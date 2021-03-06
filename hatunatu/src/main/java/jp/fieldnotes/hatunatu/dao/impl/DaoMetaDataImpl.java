/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.*;
import jp.fieldnotes.hatunatu.api.DaoAnnotationReader;
import jp.fieldnotes.hatunatu.api.DtoMetaData;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.command.*;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.exception.*;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;
import jp.fieldnotes.hatunatu.dao.util.TypeUtil;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.exception.MethodNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.util.exception.NoSuchMethodRuntimeException;
import jp.fieldnotes.hatunatu.util.io.ResourceUtil;
import jp.fieldnotes.hatunatu.util.lang.ClassUtil;
import jp.fieldnotes.hatunatu.util.lang.GenericsUtil;
import jp.fieldnotes.hatunatu.util.lang.MethodUtil;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link DaoMetaData}.
 */
public class DaoMetaDataImpl implements DaoMetaData {

    private static final Pattern startWithOrderByPattern = Pattern.compile(
            "(/\\*[^*]+\\*/)*order by", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithSelectPattern = Pattern.compile(
            "^\\s*select\\s", Pattern.CASE_INSENSITIVE);

    private static final Pattern beginCommentPattern = Pattern.compile(
            "/\\*BEGIN\\*/\\s*WHERE", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithBeginCommentPattern = Pattern
            .compile("/\\*BEGIN\\*/\\s*WHERE .+", Pattern.CASE_INSENSITIVE);

    private static final Pattern startWithIfCommentPattern = Pattern.compile(
            "/\\*IF .+", Pattern.CASE_INSENSITIVE);

    private static final String NOT_SINGLE_ROW_UPDATED = "NotSingleRowUpdated";

    protected Class daoClass;

    protected Class daoInterface;

    protected BeanDesc daoBeanDesc;

    protected DataSource dataSource;

    protected DaoAnnotationReader daoAnnotationReader;

    protected StatementFactory statementFactory;

    protected ResultSetFactory resultSetFactory;

    protected String sqlFileEncoding = "UTF-8";

    protected Dbms dbms;

    protected Map<Class, BeanMetaData> beanMetaDataCache = new HashMap<>();

    protected Map<Method, SqlCommand> sqlCommands = new HashMap<>();

    protected ValueTypeFactory valueTypeFactory;

    protected BeanMetaDataFactory beanMetaDataFactory;

    protected DtoMetaDataFactory dtoMetaDataFactory;

    protected ResultSetHandlerFactory resultSetHandlerFactory;

    protected DaoNamingConvention daoNamingConvention = DaoNamingConvention.INSTASNCE;

    protected ProcedureMetaDataFactory procedureMetaDataFactory;

    protected boolean checkSingleRowUpdateForAll = true;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public DaoMetaDataImpl() {
    }

    @Override
    public synchronized SqlCommand getSqlCommand(final Method method) {
        try {
            if (!initialized.get()) {
                daoInterface = getDaoInterface(daoClass);
                daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
                try (Connection con = DataSourceUtil.getConnection(dataSource)) {
                    final DatabaseMetaData dbMetaData = ConnectionUtil.getMetaData(con);
                    dbms = DbmsManager.getDbms(dbMetaData);
                }
                checkSingleRowUpdateForAll = daoAnnotationReader
                        .isCheckSingleRowUpdate();
                initialized.set(true);
            }

            SqlCommand cmd = sqlCommands.get(method);
            if (cmd == null) {
                if (MethodUtil.isAbstract(method)) {
                    Class<?> beanClass;
                    if (isSelect(method)) {
                        beanClass = daoAnnotationReader.getBeanClass(method);
                    } else {
                        if (method.getParameterTypes().length >= 1) {
                            if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
                                beanClass = GenericsUtil.getRawClass(GenericsUtil.getGenericParameter(method.getGenericParameterTypes()[0], 0));
                            } else if (method.getParameterTypes()[0].isArray()) {
                                beanClass = method.getParameterTypes()[0].getComponentType();
                            } else {
                                beanClass = method.getParameterTypes()[0];
                            }
                        } else {
                            beanClass = NullBean.class;
                        }
                    }
                    BeanMetaData beanMetaData;
                    if (beanMetaDataCache.containsKey(beanClass)) {
                        beanMetaData = beanMetaDataCache.get(beanClass);
                    } else {
                        beanMetaData = beanMetaDataFactory.createBeanMetaData(beanClass);
                        beanMetaDataCache.put(beanClass, beanMetaData);
                    }
                    setupMethod(method, beanMetaData);
                }
                cmd = sqlCommands.get(method);
                if (cmd == null) {
                    throw new MethodNotFoundRuntimeException(daoClass, method.getName(), method.getParameterTypes());
                }
            }
            return cmd;
        } catch (final Exception e) {
            throw new MethodSetupFailureRuntimeException(
                    daoInterface.getName(), method.getName(), e);
        }


    }

    protected void setupMethod(final Method method, BeanMetaData beanMetaData) throws IOException, URISyntaxException {
        setupMethod(daoInterface, method, beanMetaData);
    }

    protected void setupMethod(final Class daoInterface, final Method method, BeanMetaData beanMetaData) throws IOException, URISyntaxException {
        assertAnnotation(method);

        setupMethodByAnnotation(method, beanMetaData);

        if (!completedSetupMethod(method)) {
            setupMethodBySqlFile(daoInterface, method, beanMetaData);
        }

        if (!completedSetupMethod(method)) {
            setupMethodByInterfaces(daoInterface, method, beanMetaData);
        }

        if (!completedSetupMethod(method)) {
            setupMethodBySuperClass(daoInterface, method, beanMetaData);
        }

        if (!completedSetupMethod(method)
                && daoAnnotationReader.isSqlFile(method)) {
            String fileName = getSqlFilePath(daoInterface, method) + ".sql";
            throw new SqlFileNotFoundRuntimeException(daoInterface, method,
                    fileName);
        }

        if (!completedSetupMethod(method)) {
            setupMethodByAuto(method, beanMetaData);
        }
    }

    protected void setupMethodByAnnotation(final Method method, final BeanMetaData beanMetaData) {
        final String sql = daoAnnotationReader.getSQL(method, dbms.getSuffix());
        if (sql != null) {
            setupMethodByManual(method, sql, beanMetaData);
            return;
        }
        final String procedureCallName = daoAnnotationReader
                .getProcedureCallName(method);
        if (procedureCallName != null) {
            setupProcedureCallMethod(method, procedureCallName, beanMetaData);
        }
    }

    protected void assertAnnotation(final Method method) {
        if (isInsert(method.getName()) || isUpdate(method.getName()) || isDelete(method.getName())) {
            if (daoAnnotationReader.getQuery(method) != null) {
                throw new IllegalAnnotationRuntimeException("Query");
            }
        }
    }


    protected void setupProcedureCallMethod(final Method method,
                                            final String procedureName, final BeanMetaData beanMetaData) {

        final ProcedureMetaData metaData = procedureMetaDataFactory
                .createProcedureMetaData(procedureName, method);
        final ResultSetHandler resultSetHandler = createResultSetHandler(method, beanMetaData);
        final SqlCommand command = new ArgumentDtoProcedureCommand(dataSource,
                resultSetHandler, statementFactory, resultSetFactory, metaData);

        putSqlCommand(method, command);
    }

    protected String readText(final String path) throws URISyntaxException, IOException {
        URL url = ResourceUtil.getResource(path, null);
        return new String(Files.readAllBytes(Paths.get(url.toURI())), sqlFileEncoding);
    }

    protected void setupMethodBySqlFile(final Class daoInterface,
                                        final Method method, final BeanMetaData beanMetaData) throws IOException, URISyntaxException {
        final String base = getSqlFilePath(daoInterface, method);
        final String dbmsPath = base + dbms.getSuffix() + ".sql";
        final String standardPath = base + ".sql";
        if (ResourceUtil.isExist(dbmsPath)) {
            final String sql = readText(dbmsPath);
            setupMethodByManual(method, sql, beanMetaData);
        } else if (ResourceUtil.isExist(standardPath)) {
            final String sql = readText(standardPath);
            setupMethodByManual(method, sql, beanMetaData);
        }
    }

    protected String getSqlFilePath(final Class daoInterface,
                                    final Method method) {
        String base;
        String fileByAnnotation = daoAnnotationReader.getSqlFilePath(method);
        if (StringUtil.isEmpty(fileByAnnotation)) {
            base = daoInterface.getName().replace('.', '/') + "_"
                    + method.getName();
        } else {
            base = fileByAnnotation.replaceAll(".sql$", "");
        }
        return base;
    }

    protected void setupMethodByInterfaces(final Class daoInterface,
                                           final Method method, BeanMetaData beanMetaData) throws IOException, URISyntaxException {
        final Class[] interfaces = daoInterface.getInterfaces();
        if (interfaces == null) {
            return;
        }
        for (Class anInterface : interfaces) {
            final Method interfaceMethod = getSameSignatureMethod(
                    anInterface, method);
            if (interfaceMethod != null) {
                setupMethod(anInterface, interfaceMethod, beanMetaData);
            }
        }
    }

    protected void setupMethodBySuperClass(final Class daoInterface,
                                           final Method method, BeanMetaData beanMetaData) throws IOException, URISyntaxException {
        final Class superDaoClass = daoInterface.getSuperclass();
        if (superDaoClass != null && !Object.class.equals(superDaoClass)) {
            final Method superClassMethod = getSameSignatureMethod(
                    superDaoClass, method);
            if (superClassMethod != null) {
                setupMethod(superDaoClass, method, beanMetaData);
            }
        }
    }

    protected boolean completedSetupMethod(final Method method) {
        return hasSqlCommand(method);
    }

    private Method getSameSignatureMethod(final Class clazz, final Method method) {
        try {
            final String methodName = method.getName();
            final Class[] parameterTypes = method.getParameterTypes();
            return ClassUtil.getMethod(clazz, methodName, parameterTypes);
        } catch (final NoSuchMethodRuntimeException e) {
            return null;
        }
    }

    protected void setupMethodByManual(final Method method, final String sql, BeanMetaData beanMetaData) {
        if (isSelect(method)) {
            setupSelectMethodByManual(method, sql, beanMetaData);
        } else {
            setupUpdateMethodByManual(method, sql, beanMetaData);
        }
    }

    protected void setupMethodByAuto(final Method method, BeanMetaData beanMetaData) {
        if (isInsert(method.getName())) {
            setupInsertMethodByAuto(method, beanMetaData);
        } else if (isUpdate(method.getName())) {
            setupUpdateMethodByAuto(method, beanMetaData);
        } else if (isDelete(method.getName())) {
            setupDeleteMethodByAuto(method, beanMetaData);
        } else {
            setupSelectMethodByAuto(method, beanMetaData);
        }
    }

    protected void setupSelectMethodByManual(final Method method,
                                             final String sql, BeanMetaData beanMetaData) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(createResultSetHandler(method, beanMetaData));
        cmd.setSql(sql);
        cmd.setArgNames(daoAnnotationReader.getArgNames(method));
        cmd.setArgTypes(method.getParameterTypes());
        putSqlCommand(method, cmd);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            final ResultSetHandler rsh) {
        return new SelectDynamicCommand(dataSource, statementFactory, rsh,
                resultSetFactory);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            final ResultSetHandler resultSetHandler, final String query, BeanMetaData beanMetaData) {

        final SelectDynamicCommand cmd = createSelectDynamicCommand(resultSetHandler);
        final StringBuilder buf = new StringBuilder(255);
        if (startsWithSelect(query)) {
            buf.append(query);
        } else {
            final String sql = dbms.getAutoSelectSql(beanMetaData);
            buf.append(sql);
            if (query != null) {
                String adjustedQuery = query;
                boolean began = false;
                boolean whereContained = sql.lastIndexOf("WHERE") > 0;
                if (startsWithOrderBy(query)) {
                    buf.append(" ");
                } else if (startsWithBeginComment(query)) {
                    buf.append(" ");
                    if (whereContained) {
                        final Matcher matcher = beginCommentPattern
                                .matcher(query);
                        adjustedQuery = matcher.replaceFirst("/*BEGIN*/AND");
                    }
                } else if (!whereContained) {
                    if (startsWithIfComment(query)) {
                        buf.append("/*BEGIN*/");
                        began = true;
                    }
                    buf.append(" WHERE ");
                } else {
                    if (startsWithIfComment(query)) {
                        buf.append("/*BEGIN*/");
                        began = true;
                    }
                    buf.append(" AND ");
                }
                buf.append(adjustedQuery);
                if (began) {
                    buf.append("/*END*/");
                }
            }
        }
        cmd.setSql(buf.toString());
        return cmd;
    }

    protected boolean startsWithIfComment(final String query) {
        final Matcher m = startWithIfCommentPattern.matcher(query);
        return m.lookingAt();
    }

    protected boolean startsWithBeginComment(final String query) {
        final Matcher m = startWithBeginCommentPattern.matcher(query);
        return m.lookingAt();
    }

    protected static boolean startsWithSelect(final String query) {
        if (query != null) {
            final Matcher m = startWithSelectPattern.matcher(query);
            if (m.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    protected static boolean startsWithOrderBy(final String query) {
        if (query != null) {
            final Matcher m = startWithOrderByPattern.matcher(query);
            if (m.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    protected ResultSetHandler createResultSetHandler(final Method method, BeanMetaData beanMetaData) {
        return resultSetHandlerFactory.getResultSetHandler(daoAnnotationReader,
                beanMetaData, method);
    }

    // update & insert & delete
    protected void setupUpdateMethodByManual(final Method method,
                                             final String sql, BeanMetaData beanMetaData) {
        final UpdateDynamicCommand cmd = new UpdateDynamicCommand(dataSource,
                statementFactory);
        cmd.setSql(sql);
        List<String> argNames = daoAnnotationReader.getArgNames(method);
        if (argNames.isEmpty() && isUpdateSignatureForBean(method)) {
            argNames = Arrays.asList(StringUtil.decapitalize(ClassUtil.getShortClassName(beanMetaData.getBeanClass().getName())));
        }
        cmd.setArgNames(argNames);
        cmd.setArgTypes(method.getParameterTypes());
        cmd
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        putSqlCommand(method, cmd);
    }

    protected boolean isUpdateSignatureForBean(final Method method) {
        if (isInsert(method.getName()) || isUpdate(method.getName()) || isDelete(method.getName())) {
            if (method.getParameterTypes().length == 1) {
                if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    return false;
                }
                return !method.getParameterTypes()[0].isArray();
            }
        }
        return false;
    }

    protected Class getNotSingleRowUpdatedExceptionClass(final Method method) {
        final Class[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes != null) {
            for (final Class exceptionType : exceptionTypes) {
                if (exceptionType.getName().contains(NOT_SINGLE_ROW_UPDATED)) {
                    return exceptionType;
                }
            }
        }
        return null;
    }

    protected void setupInsertMethodByAuto(final Method method, BeanMetaData beanMetaData) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method, beanMetaData);
        final SqlCommand command;
        if (isUpdateSignatureForBean(method)) {
            final InsertAutoDynamicCommand cmd = new InsertAutoDynamicCommand(dataSource, statementFactory);
            cmd.setBeanMetaData(beanMetaData);
            cmd
                    .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
            cmd.setPropertyNames(propertyNames);
            cmd.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
            command = cmd;
        } else {
            boolean returningRows = false;
            if (int[].class.isAssignableFrom(method.getReturnType())) {
                returningRows = true;
            }
            command = new InsertBatchAutoStaticCommand(
                    dataSource, statementFactory, beanMetaData,
                    propertyNames, returningRows);
        }
        putSqlCommand(method, command);
    }

    // update
    protected void setupUpdateMethodByAuto(final Method method, final BeanMetaData beanMetaData) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method, beanMetaData);
        AbstractSqlCommand cmd;
        if (isUpdateSignatureForBean(method)) {
            if (isModifiedOnly(method.getName())) {
                cmd = createUpdateModifiedOnlyCommand(method, propertyNames, beanMetaData);
            } else {
                cmd = createUpdateAutoStaticCommand(method, propertyNames, beanMetaData);
            }
        } else {
            boolean returningRows = false;
            if (int[].class.isAssignableFrom(method.getReturnType())) {
                returningRows = true;
            }
            cmd = createUpdateBatchAutoStaticCommand(propertyNames,
                    returningRows, beanMetaData);
        }
        putSqlCommand(method, cmd);
    }

    protected UpdateAutoStaticCommand createUpdateAutoStaticCommand(
            final Method method, final String[] propertyNames, BeanMetaData beanMetaData) {
        UpdateAutoStaticCommand cmd = new UpdateAutoStaticCommand(dataSource,
                statementFactory, beanMetaData, propertyNames);
        cmd.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
        return cmd;
    }

    protected AbstractSqlCommand createUpdateAutoDynamicCommand(
            final Method method, final String[] propertyNames, BeanMetaData beanMetaData) {
        AbstractSqlCommand cmd;
        final UpdateAutoDynamicCommand uac = new UpdateAutoDynamicCommand(
                dataSource, statementFactory);
        uac.setBeanMetaData(beanMetaData);
        uac.setPropertyNames(propertyNames);
        uac
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        uac.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
        cmd = uac;
        return cmd;
    }

    protected AbstractSqlCommand createUpdateModifiedOnlyCommand(
            final Method method, final String[] propertyNames, BeanMetaData beanMetaData) {
        final UpdateModifiedOnlyCommand uac = new UpdateModifiedOnlyCommand(
                dataSource, statementFactory);
        uac.setBeanMetaData(beanMetaData);
        uac.setPropertyNames(propertyNames);
        uac
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        uac.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
        return uac;
    }

    protected UpdateBatchAutoStaticCommand createUpdateBatchAutoStaticCommand(
            final String[] propertyNames,
            boolean returningRows, BeanMetaData beanMetaData) {
        return new UpdateBatchAutoStaticCommand(dataSource, statementFactory,
                beanMetaData, propertyNames, returningRows);
    }

    protected void setupDeleteMethodByAuto(final Method method, final BeanMetaData beanMetaData) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method, beanMetaData);
        SqlCommand cmd;
        if (isUpdateSignatureForBean(method)) {
            cmd = createDeleteAutoStaticCommand(method, propertyNames, beanMetaData);
        } else {
            boolean returningRows = false;
            if (int[].class.isAssignableFrom(method.getReturnType())) {
                returningRows = true;
            }
            cmd = createDeleteBatchAutoStaticCommand(propertyNames,
                    returningRows, beanMetaData);
        }
        putSqlCommand(method, cmd);
    }

    protected DeleteAutoStaticCommand createDeleteAutoStaticCommand(
            final Method method, final String[] propertyNames, BeanMetaData beanMetaData) {
        DeleteAutoStaticCommand cmd = new DeleteAutoStaticCommand(dataSource,
                statementFactory, beanMetaData, propertyNames);
        cmd.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
        return cmd;
    }

    protected DeleteBatchAutoStaticCommand createDeleteBatchAutoStaticCommand(
            final String[] propertyNames,
            boolean returningRows, BeanMetaData beanMetaData) {
        return new DeleteBatchAutoStaticCommand(dataSource, statementFactory,
                beanMetaData, propertyNames, returningRows);
    }

    protected String[] getPersistentPropertyNames(final Method method, final BeanMetaData beanMetaData) {
        final List<String> names = new ArrayList<>();
        String[] props = daoAnnotationReader.getNoPersistentProps(method);
        if (props != null) {
            for (PropertyType pt : beanMetaData.getPropertyTypes()) {
                if (pt.isPersistent()
                        && !isPropertyExist(props, pt.getPropertyName())) {
                    names.add(pt.getPropertyName());
                }
            }
        } else {
            props = daoAnnotationReader.getPersistentProps(method);
            if (props != null) {
                names.addAll(Arrays.asList(props));
                for (String pk : beanMetaData.getPrimaryKeys()) {
                    final PropertyType pt = beanMetaData
                            .getPropertyTypeByColumnName(pk);
                    names.add(pt.getPropertyName());
                }
                if (beanMetaData.hasVersionNoPropertyType()) {
                    names.add(beanMetaData.getVersionNoPropertyName());
                }
                if (beanMetaData.hasTimestampPropertyType()) {
                    names.add(beanMetaData.getTimestampPropertyName());
                }
            }
        }
        if (names.isEmpty()) {
            for (PropertyType pt : beanMetaData.getPropertyTypes()) {
                if (pt.isPersistent()) {
                    names.add(pt.getPropertyName());
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

    protected boolean isPropertyExist(final String[] props,
                                      final String propertyName) {
        for (String prop : props) {
            if (prop.equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        return false;
    }

    protected void setupSelectMethodByAuto(final Method method, final BeanMetaData beanMetaData) {
        final ResultSetHandler handler = createResultSetHandler(method, beanMetaData);
        final List<String> argNames = daoAnnotationReader.getArgNames(method);
        final String query = daoAnnotationReader.getQuery(method);
        SelectDynamicCommand cmd;
        if (query != null && !startsWithOrderBy(query)) {
            cmd = setupQuerySelectMethodByAuto(method, handler, argNames, query, beanMetaData);
        } else {
            cmd = setupNonQuerySelectMethodByAuto(method, handler, argNames,
                    query, beanMetaData);
        }
        putSqlCommand(method, cmd);
    }


    protected SelectDynamicCommand setupQuerySelectMethodByAuto(
            final Method method, final ResultSetHandler handler,
            final List<String> argNames, final String query, final BeanMetaData beanMetaData) {
        final Class[] types = method.getParameterTypes();
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler,
                query, beanMetaData);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByAuto(
            final Method method, final ResultSetHandler handler,
            final List<String> argNames, final String query, final BeanMetaData beanMetaData) {
        if (isAutoSelectSqlByDto(method, argNames)) {
            return setupNonQuerySelectMethodByDto(method, handler, argNames,
                    query, beanMetaData);
        } else {
            return setupNonQuerySelectMethodByArgs(method, handler, argNames,
                    query, beanMetaData);
        }
    }

    protected boolean isAutoSelectSqlByDto(final Method method,
                                           final List<String> argNames) {
        if (argNames.isEmpty()) {
            if (method.getParameterTypes().length == 1) {
                return true;
            }
        }
        return false;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByDto(
            final Method method, final ResultSetHandler handler,
            final List<String> argNames, final String query, final BeanMetaData beanMetaData) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler);
        Class clazz = method.getParameterTypes()[0];
        if (isUpdateSignatureForBean(method)) {
            clazz = beanMetaData.getBeanClass();
        }
        final Class[] types = new Class[]{clazz};
        String sql = createAutoSelectSqlByDto(clazz, beanMetaData);
        if (query != null) {
            sql = sql + " " + query;
        }
        cmd.setSql(sql);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByArgs(
            final Method method, final ResultSetHandler handler,
            final List<String> argNames, final String query, final BeanMetaData beanMetaData) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler);
        final Class[] types = method.getParameterTypes();
        String sql = createAutoSelectSql(argNames, beanMetaData);
        if (query != null) {
            sql = sql + " " + query;
        }
        cmd.setSql(sql);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected String createAutoSelectSqlByDto(final Class dtoClass, final BeanMetaData beanMetaData) {
        final String sql = dbms.getAutoSelectSql(beanMetaData);
        final StringBuilder buf = new StringBuilder(sql);
        // TODO どうするか要検討
        if (dtoClass.isPrimitive()) {
            return sql;
        }
        final DtoMetaData dmd = createDtoMetaData(dtoClass, beanMetaData);
        boolean began = false;
        if (!(sql.lastIndexOf("WHERE") > 0)) {
            buf.append("/*BEGIN*/ WHERE ");
            began = true;
        }
        boolean started = false;
        for (PropertyType pt : dmd.getPropertyTypes()) {
            final String aliasName = pt.getColumnName();
            if (!beanMetaData.hasPropertyTypeByAliasName(aliasName)) {
                continue;
            }
            if (!beanMetaData.getPropertyTypeByAliasName(aliasName)
                    .isPersistent()) {
                continue;
            }
            final String columnName = beanMetaData
                    .convertFullColumnName(aliasName);
            final String propertyName = "dto." + pt.getPropertyName();
            buf.append("/*IF ");
            buf.append(propertyName);
            buf.append(" != null*/");
            buf.append(" ");
            if (!began || started) {
                buf.append("AND ");
            }
            buf.append(columnName);
            buf.append(" = /*");
            buf.append(propertyName);
            buf.append("*/null");
            buf.append("/*END*/");
            started = true;
        }
        if (began) {
            buf.append("/*END*/");
        }
        return buf.toString();
    }

    private DtoMetaData createDtoMetaData(final Class dtoClass, BeanMetaData beanMetaData) {
        final DtoMetaData dtoMetaData = dtoMetaDataFactory
                .getDtoMetaData(dtoClass);
        for (PropertyType master : beanMetaData.getPropertyTypes()) {
            final String name = master.getPropertyName();
            if (dtoMetaData.hasPropertyType(name)) {
                final PropertyType slave = dtoMetaData.getPropertyType(name);
                slave.setColumnName(master.getColumnName());
            }
        }
        return dtoMetaData;
    }

    protected String createAutoSelectSql(final List<String> argNames, BeanMetaData beanMetaData) {
        final String sql = dbms.getAutoSelectSql(beanMetaData);
        final StringBuilder buf = new StringBuilder(sql);
        if (!argNames.isEmpty()) {
            boolean began = false;
            if (!(sql.lastIndexOf("WHERE") > 0)) {
                buf.append("/*BEGIN*/ WHERE ");
                began = true;
            }
            for (int i = 0; i < argNames.size(); ++i) {
                final String columnName = beanMetaData
                        .convertFullColumnName(argNames.get(i));
                buf.append("/*IF ");
                buf.append(argNames.get(i));
                buf.append(" != null*/");
                buf.append(" ");
                if (!began || i != 0) {
                    buf.append("AND ");
                }
                buf.append(columnName);
                buf.append(" = /*");
                buf.append(argNames.get(i));
                buf.append("*/null");
                buf.append("/*END*/");
            }
            if (began) {
                buf.append("/*END*/");
            }
        }
        return buf.toString();
    }

    protected void checkAutoUpdateMethod(final Method method) {
        if (method.getParameterTypes().length != 1
                && !method.getParameterTypes()[0].isAssignableFrom(List.class)
                && !method.getParameterTypes()[0].isArray()
                || TypeUtil.isSimpleType(method.getParameterTypes()[0])
                ) {
            throw new IllegalSignatureRuntimeException("EDAO0006", method
                    .toString());
        }
    }

    protected boolean isSelect(final Method method) {
        if (isInsert(method.getName())) {
            return false;
        }
        if (isUpdate(method.getName())) {
            return false;
        }
        if (isDelete(method.getName())) {
            return false;
        }
        return true;
    }

    protected boolean isInsert(final String methodName) {
        final String[] insertPrefixes = this.daoNamingConvention
                .getInsertPrefixes();
        for (String insertPrefix : insertPrefixes) {
            if (methodName.startsWith(insertPrefix)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUpdate(final String methodName) {
        final String[] updatePrefixes = this.daoNamingConvention
                .getUpdatePrefixes();
        for (String updatePrefixe : updatePrefixes) {
            if (methodName.startsWith(updatePrefixe)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDelete(final String methodName) {
        final String[] deletePrefixes = this.daoNamingConvention
                .getDeletePrefixes();
        for (String deletePrefix : deletePrefixes) {
            if (methodName.startsWith(deletePrefix)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isModifiedOnly(final String methodName) {
        final String[] modifiedOnlySuffixes = this.daoNamingConvention
                .getModifiedOnlySuffixes();
        for (String modifiedOnlySuffix : modifiedOnlySuffixes) {
            if (methodName.endsWith(modifiedOnlySuffix)) {
                return true;
            }
        }
        return false;
    }

    protected void putSqlCommand(Method method, SqlCommand cmd) {
        sqlCommands.put(method, cmd);
    }

    /**
     * メソッドおよびDao全体のいずれもSingleRowUpdateチェックが有効になっているかどうかを返します。
     * <p/>
     * <p>
     * メソッドまたはDao全体のいずれもチェックが有効として設定されている場合のみ、<code>true</code>を返します。
     * どちらか一方でもチェック無効に設定されていれば、 falseを返します。
     * </p>
     *
     * @param method チェック対象のメソッド
     * @return 指定されたメソッドの実行時チェックが有効なら<code>true</code>を返す。
     */
    protected boolean isCheckSingleRowUpdate(Method method) {
        return checkSingleRowUpdateForAll
                & daoAnnotationReader.isCheckSingleRowUpdate(method);
    }

    @Override
    public boolean hasSqlCommand(final Method method) {
        return sqlCommands.containsKey(method);
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    public Class getDaoInterface(final Class clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        throw new DaoNotFoundRuntimeException(clazz);
    }

    public void setDbms(final Dbms dbms) {
        this.dbms = dbms;
    }

    public void setResultSetFactory(final ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
    }

    public void setStatementFactory(final StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }


    public void setSqlFileEncoding(final String sencoding) {
        this.sqlFileEncoding = sencoding;
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDaoClass(final Class daoClass) {
        this.daoClass = daoClass;
    }

    public void setBeanMetaDataFactory(
            final BeanMetaDataFactory beanMetaDataFactory) {
        this.beanMetaDataFactory = beanMetaDataFactory;
    }

    public void setDtoMetaDataFactory(
            final DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }


    public void setResultSetHandlerFactory(
            final ResultSetHandlerFactory resultSetHandlerFactory) {
        this.resultSetHandlerFactory = resultSetHandlerFactory;
    }

    @Override
    public DaoAnnotationReader getDaoAnnotationReader() {
        return daoAnnotationReader;
    }

    public void setDaoAnnotationReader(
            final DaoAnnotationReader daoAnnotationReader) {
        this.daoAnnotationReader = daoAnnotationReader;
    }


    public void setProcedureMetaDataFactory(
            ProcedureMetaDataFactory procedureMetaDataFactory) {
        this.procedureMetaDataFactory = procedureMetaDataFactory;
    }

}
