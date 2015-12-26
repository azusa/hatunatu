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
import jp.fieldnotes.hatunatu.api.beans.MethodDesc;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.command.*;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.exception.*;
import jp.fieldnotes.hatunatu.dao.handler.ProcedureHandlerImpl;
import jp.fieldnotes.hatunatu.dao.pager.NullPagingSqlRewriter;
import jp.fieldnotes.hatunatu.dao.pager.PagingSqlRewriter;
import jp.fieldnotes.hatunatu.dao.resultset.*;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;
import jp.fieldnotes.hatunatu.dao.util.FetchHandlerUtil;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.exception.MethodNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.util.exception.NoSuchMethodRuntimeException;
import jp.fieldnotes.hatunatu.util.io.ResourceUtil;
import jp.fieldnotes.hatunatu.util.lang.ClassUtil;
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

    protected Class beanClass;

    protected BeanMetaData beanMetaData;

    protected Map<Method, SqlCommand> sqlCommands = new HashMap<>();

    protected ValueTypeFactory valueTypeFactory;

    protected BeanMetaDataFactory beanMetaDataFactory;

    protected DtoMetaDataFactory dtoMetaDataFactory;

    protected ResultSetHandlerFactory resultSetHandlerFactory;

    protected DaoNamingConvention daoNamingConvention;

    protected boolean useDaoClassForLog = false;

    protected PagingSqlRewriter pagingSqlRewriter = new NullPagingSqlRewriter();

    protected ProcedureMetaDataFactory procedureMetaDataFactory;

    protected boolean checkSingleRowUpdateForAll = true;

    public DaoMetaDataImpl() {
    }

    public void initialize() {
        beanClass = daoAnnotationReader.getBeanClass();
        daoInterface = getDaoInterface(daoClass);
        daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        final Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            final DatabaseMetaData dbMetaData = ConnectionUtil.getMetaData(con);
            dbms = DbmsManager.getDbms(dbMetaData);
        } finally {
            ConnectionUtil.close(con);
        }
        this.beanMetaData = beanMetaDataFactory.createBeanMetaData(
                daoInterface, beanClass);
        checkSingleRowUpdateForAll = daoAnnotationReader
                .isCheckSingleRowUpdate();
    }

    protected void setupSqlCommand() {
        final BeanDesc idbd = BeanDescFactory.getBeanDesc(daoInterface);
        for (String methodNames : idbd.getMethodNames()){
            for (MethodDesc methodDesc : idbd.getMethodDescs(methodNames)) {
                if (MethodUtil.isAbstract(methodDesc.getMethod())) {
                    setupMethod(methodDesc.getMethod());
                }
            }
        }
    }

    protected void setupMethod(final Method method) {
        setupMethod(daoInterface, method);
    }

    protected void setupMethod(final Class daoInterface, final Method method) {
        try {
            assertAnnotation(method);

            setupMethodByAnnotation(daoInterface, method);

            if (!completedSetupMethod(method)) {
                setupMethodBySqlFile(daoInterface, method);
            }

            if (!completedSetupMethod(method)) {
                setupMethodByInterfaces(daoInterface, method);
            }

            if (!completedSetupMethod(method)) {
                setupMethodBySuperClass(daoInterface, method);
            }

            if (!completedSetupMethod(method)
                    && daoAnnotationReader.isSqlFile(method)) {
                String fileName = getSqlFilePath(daoInterface, method) + ".sql";
                throw new SqlFileNotFoundRuntimeException(daoInterface, method,
                        fileName);
            }

            if (!completedSetupMethod(method)) {
                setupMethodByAuto(method);
            }
        } catch (final Exception e) {
            throw new MethodSetupFailureRuntimeException(
                    daoInterface.getName(), method.getName(), e);
        }
    }

    protected void setupMethodByAnnotation(final Class daoInterface,
            final Method method) {
        final String sql = daoAnnotationReader.getSQL(method, dbms.getSuffix());
        if (sql != null) {
            setupMethodByManual(method, sql);
            return;
        }
        final String procedureCallName = daoAnnotationReader
                .getProcedureCallName(method);
        if (procedureCallName != null) {
            setupProcedureCallMethod(method, procedureCallName);
            return;
        }
        final String procedureName = daoAnnotationReader
                .getStoredProcedureName(method);
        if (procedureName != null) {
            setupProcedureMethod(method, procedureName);
            return;
        }
    }

    protected void assertAnnotation(final Method method) {
        if (isInsert(method.getName()) || isUpdate(method.getName())) {
            if (daoAnnotationReader.getQuery(method) != null) {
                throw new IllegalAnnotationRuntimeException("Query");
            }
        }
    }

    protected void setupProcedureMethod(final Method method,
            final String procedureName) {

        final ProcedureHandlerImpl handler = new ProcedureHandlerImpl();
        handler.setDataSource(dataSource);
        handler.setDbms(dbms);
        handler.setDaoMethod(method);
        handler.setDaoAnnotationReader(daoAnnotationReader);
        handler.setBeanMetaData(beanMetaData);
        handler.setProcedureName(procedureName);
        handler.setResultSetHandlerFactory(resultSetHandlerFactory);
        handler.setStatementFactory(statementFactory);
        handler.initialize();
        final SqlCommand command = new StaticStoredProcedureCommand(handler);
        putSqlCommand(method, command);
    }

    protected void setupProcedureCallMethod(final Method method,
            final String procedureName) {

        final ProcedureMetaData metaData = procedureMetaDataFactory
                .createProcedureMetaData(procedureName, method);
        final ResultSetHandler resultSetHandler = createResultSetHandler(method);
        final SqlCommand command = new ArgumentDtoProcedureCommand(dataSource,
                resultSetHandler, statementFactory, resultSetFactory, metaData);

        putSqlCommand(method, command);
    }

    protected String readText(final String path) throws URISyntaxException, IOException {
        URL url = ResourceUtil.getResource(path, null);
        return new String(Files.readAllBytes(Paths.get(url.toURI())), sqlFileEncoding);
    }

    protected void setupMethodBySqlFile(final Class daoInterface,
            final Method method) throws IOException, URISyntaxException {
        final String base = getSqlFilePath(daoInterface, method);
        final String dbmsPath = base + dbms.getSuffix() + ".sql";
        final String standardPath = base + ".sql";
        if (ResourceUtil.isExist(dbmsPath)) {
            final String sql = readText(dbmsPath);
            setupMethodByManual(method, sql);
        } else if (ResourceUtil.isExist(standardPath)) {
            final String sql = readText(standardPath);
            setupMethodByManual(method, sql);
        } else if (isDelete(method.getName())) {
            final String query = daoAnnotationReader.getQuery(method);
            if (StringUtil.isNotBlank(query)) {
                if (query.trim().toUpperCase().startsWith("WHERE")) {
                    setupMethodByManual(method, "DELETE FROM "
                            + beanMetaData.getTableName() + " " + query);
                } else {
                    setupMethodByManual(method, "DELETE FROM "
                            + beanMetaData.getTableName() + " WHERE " + query);
                }
            }
        }
    }

    /**
     * @param daoInterface
     * @param method
     * @return
     */
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
            final Method method) {
        final Class[] interfaces = daoInterface.getInterfaces();
        if (interfaces == null) {
            return;
        }
        for (int i = 0; i < interfaces.length; i++) {
            final Method interfaceMethod = getSameSignatureMethod(
                    interfaces[i], method);
            if (interfaceMethod != null) {
                setupMethod(interfaces[i], interfaceMethod);
            }
        }
    }

    protected void setupMethodBySuperClass(final Class daoInterface,
            final Method method) {
        final Class superDaoClass = daoInterface.getSuperclass();
        if (superDaoClass != null && !Object.class.equals(superDaoClass)) {
            final Method superClassMethod = getSameSignatureMethod(
                    superDaoClass, method);
            if (superClassMethod != null) {
                setupMethod(superDaoClass, method);
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

    protected void setupMethodByManual(final Method method, final String sql) {
        if (isSelect(method)) {
            setupSelectMethodByManual(method, sql);
        } else {
            setupUpdateMethodByManual(method, sql);
        }
    }

    protected void setupMethodByAuto(final Method method) {
        if (isInsert(method.getName())) {
            setupInsertMethodByAuto(method);
        } else if (isUpdate(method.getName())) {
            setupUpdateMethodByAuto(method);
        } else if (isDelete(method.getName())) {
            setupDeleteMethodByAuto(method);
        } else {
            setupSelectMethodByAuto(method);
        }
    }

    protected void setupSelectMethodByManual(final Method method,
            final String sql) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(createResultSetHandler(method));
        cmd.setSql(sql);
        cmd.setArgNames(daoAnnotationReader.getArgNames(method));
        cmd.setArgTypes(method.getParameterTypes());
        putSqlCommand(method, cmd);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            final ResultSetHandler rsh) {
        return new SelectDynamicCommand(dataSource, statementFactory, rsh,
                resultSetFactory, pagingSqlRewriter);
    }

    protected SelectDynamicCommand createSelectDynamicCommand(
            final ResultSetHandler resultSetHandler, final String query) {

        final SelectDynamicCommand cmd = createSelectDynamicCommand(resultSetHandler);
        final StringBuilder buf = new StringBuilder(255);
        if (startsWithSelect(query)) {
            buf.append(query);
        } else {
            final String sql = dbms.getAutoSelectSql(getBeanMetaData());
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
        if (m.lookingAt()) {
            return true;
        }
        return false;
    }

    protected boolean startsWithBeginComment(final String query) {
        final Matcher m = startWithBeginCommentPattern.matcher(query);
        if (m.lookingAt()) {
            return true;
        }
        return false;
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

    protected ResultSetHandler createResultSetHandler(final Method method) {
        return resultSetHandlerFactory.getResultSetHandler(daoAnnotationReader,
                beanMetaData, method);
    }

    protected boolean isBeanClassAssignable(final Class clazz) {
        return beanClass.isAssignableFrom(clazz)
                || clazz.isAssignableFrom(beanClass);
    }

    // update & insert & delete
    protected void setupUpdateMethodByManual(final Method method,
            final String sql) {
        final UpdateDynamicCommand cmd = new UpdateDynamicCommand(dataSource,
                statementFactory);
        cmd.setSql(sql);
        String[] argNames = daoAnnotationReader.getArgNames(method);
        if (argNames.length == 0 && isUpdateSignatureForBean(method)) {
            argNames = new String[] { StringUtil.decapitalize(ClassUtil
                    .getShortClassName(beanClass.getName())) };
        }
        cmd.setArgNames(argNames);
        cmd.setArgTypes(method.getParameterTypes());
        cmd
                .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
        putSqlCommand(method, cmd);
    }

    protected boolean isUpdateSignatureForBean(final Method method) {
        return method.getParameterTypes().length == 1
                && isBeanClassAssignable(method.getParameterTypes()[0]);
    }

    protected Class getNotSingleRowUpdatedExceptionClass(final Method method) {
        final Class[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes != null) {
            for (int i = 0; i < exceptionTypes.length; ++i) {
                final Class exceptionType = exceptionTypes[i];
                if (exceptionType.getName().indexOf(NOT_SINGLE_ROW_UPDATED) >= 0) {
                    return exceptionType;
                }
            }
        }
        return null;
    }

    protected void setupInsertMethodByAuto(final Method method) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method);
        final SqlCommand command;
        if (isUpdateSignatureForBean(method)) {
            final InsertAutoDynamicCommand cmd = new InsertAutoDynamicCommand();
            cmd.setBeanMetaData(getBeanMetaData());
            cmd.setDataSource(dataSource);
            cmd
                    .setNotSingleRowUpdatedExceptionClass(getNotSingleRowUpdatedExceptionClass(method));
            cmd.setPropertyNames(propertyNames);
            cmd.setStatementFactory(statementFactory);
            cmd.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
            command = cmd;
        } else {
            boolean returningRows = false;
            if (int[].class.isAssignableFrom(method.getReturnType())) {
                returningRows = true;
            }
            final InsertBatchAutoStaticCommand cmd = new InsertBatchAutoStaticCommand(
                    dataSource, statementFactory, getBeanMetaData(),
                    propertyNames, returningRows);
            command = cmd;
        }
        putSqlCommand(method, command);
    }

    // update
    protected void setupUpdateMethodByAuto(final Method method) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method);
        AbstractSqlCommand cmd;
        if (isUpdateSignatureForBean(method)) {
            if (isUnlessNull(method.getName())) {
                cmd = createUpdateAutoDynamicCommand(method, propertyNames);
            } else if (isModifiedOnly(method.getName())) {
                cmd = createUpdateModifiedOnlyCommand(method, propertyNames);
            } else {
                cmd = createUpdateAutoStaticCommand(method, propertyNames);
            }
        } else {
            boolean returningRows = false;
            if (int[].class.isAssignableFrom(method.getReturnType())) {
                returningRows = true;
            }
            cmd = createUpdateBatchAutoStaticCommand(method, propertyNames,
                    returningRows);
        }
        putSqlCommand(method, cmd);
    }

    protected UpdateAutoStaticCommand createUpdateAutoStaticCommand(
            final Method method, final String[] propertyNames) {
        UpdateAutoStaticCommand cmd = new UpdateAutoStaticCommand(dataSource,
                statementFactory, beanMetaData, propertyNames);
        cmd.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
        return cmd;
    }

    /**
     * @param method
     * @param propertyNames
     * @return
     */
    protected AbstractSqlCommand createUpdateAutoDynamicCommand(
            final Method method, final String[] propertyNames) {
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
            final Method method, final String[] propertyNames) {
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
            final Method method, final String[] propertyNames,
            boolean returningRows) {
        return new UpdateBatchAutoStaticCommand(dataSource, statementFactory,
                beanMetaData, propertyNames, returningRows);
    }

    protected void setupDeleteMethodByAuto(final Method method) {
        checkAutoUpdateMethod(method);
        final String[] propertyNames = getPersistentPropertyNames(method);
        SqlCommand cmd = null;
        if (isUpdateSignatureForBean(method)) {
            cmd = createDeleteAutoStaticCommand(method, propertyNames);
        } else {
            boolean returningRows = false;
            if (int[].class.isAssignableFrom(method.getReturnType())) {
                returningRows = true;
            }
            cmd = createDeleteBatchAutoStaticCommand(method, propertyNames,
                    returningRows);
        }
        putSqlCommand(method, cmd);
    }

    protected DeleteAutoStaticCommand createDeleteAutoStaticCommand(
            final Method method, final String[] propertyNames) {
        DeleteAutoStaticCommand cmd = new DeleteAutoStaticCommand(dataSource,
                statementFactory, beanMetaData, propertyNames);
        cmd.setCheckSingleRowUpdate(isCheckSingleRowUpdate(method));
        return cmd;
    }

    protected DeleteBatchAutoStaticCommand createDeleteBatchAutoStaticCommand(
            final Method method, final String[] propertyNames,
            boolean returningRows) {
        return new DeleteBatchAutoStaticCommand(dataSource, statementFactory,
                beanMetaData, propertyNames, returningRows);
    }

    protected String[] getPersistentPropertyNames(final Method method) {
        final List names = new ArrayList();
        String[] props = daoAnnotationReader.getNoPersistentProps(method);
        if (props != null) {
            for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
                final PropertyType pt = beanMetaData.getPropertyType(i);
                if (pt.isPersistent()
                        && !isPropertyExist(props, pt.getPropertyName())) {
                    names.add(pt.getPropertyName());
                }
            }
        } else {
            props = daoAnnotationReader.getPersistentProps(method);
            if (props != null) {
                names.addAll(Arrays.asList(props));
                for (int i = 0; i < beanMetaData.getPrimaryKeySize(); ++i) {
                    final String pk = beanMetaData.getPrimaryKey(i);
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
        if (names.size() == 0) {
            for (int i = 0; i < beanMetaData.getPropertyTypeSize(); ++i) {
                final PropertyType pt = beanMetaData.getPropertyType(i);
                if (pt.isPersistent()) {
                    names.add(pt.getPropertyName());
                }
            }
        }
        return (String[]) names.toArray(new String[names.size()]);
    }

    protected boolean isPropertyExist(final String[] props,
            final String propertyName) {
        for (int i = 0; i < props.length; ++i) {
            if (props[i].equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        return false;
    }

    protected void setupSelectMethodByAuto(final Method method) {
        final ResultSetHandler handler = createResultSetHandler(method);
        final String[] argNames = daoAnnotationReader.getArgNames(method);
        final String query = daoAnnotationReader.getQuery(method);
        SelectDynamicCommand cmd = null;
        if (query != null && !startsWithOrderBy(query)) {
            cmd = setupQuerySelectMethodByAuto(method, handler, argNames, query);
        } else {
            cmd = setupNonQuerySelectMethodByAuto(method, handler, argNames,
                    query);
        }
        putSqlCommand(method, cmd);
    }

    protected boolean isQuerySelectMethodByAuto(final Method method,
            final String query) {
        return query != null && !startsWithOrderBy(query);
    }

    protected SelectDynamicCommand setupQuerySelectMethodByAuto(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        final Class[] types = method.getParameterTypes();
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler,
                query);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByAuto(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        if (isAutoSelectSqlByDto(method, argNames)) {
            return setupNonQuerySelectMethodByDto(method, handler, argNames,
                    query);
        } else {
            return setupNonQuerySelectMethodByArgs(method, handler, argNames,
                    query);
        }
    }

    protected boolean isAutoSelectSqlByDto(final Method method,
            final String[] argNames) {
        if (argNames.length == 0) {
            if (method.getParameterTypes().length == 1) {
                return true;
            } else if (method.getParameterTypes().length == 2) {
                Class clazz = method.getParameterTypes()[1];
                if (FetchHandlerUtil.isFetchHandler(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected SelectDynamicCommand setupNonQuerySelectMethodByDto(
            final Method method, final ResultSetHandler handler,
            final String[] argNames, final String query) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler);
        Class clazz = method.getParameterTypes()[0];
        if (isUpdateSignatureForBean(method)) {
            clazz = beanClass;
        }
        final Class[] types = new Class[] { clazz };
        String sql = createAutoSelectSqlByDto(clazz);
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
            final String[] argNames, final String query) {
        final SelectDynamicCommand cmd = createSelectDynamicCommand(handler);
        final Class[] types = method.getParameterTypes();
        String sql = createAutoSelectSql(argNames);
        if (query != null) {
            sql = sql + " " + query;
        }
        cmd.setSql(sql);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(types);
        return cmd;
    }

    protected String createAutoSelectSqlByDto(final Class dtoClass) {
        final String sql = dbms.getAutoSelectSql(getBeanMetaData());
        final StringBuilder buf = new StringBuilder(sql);
        // TODO どうするか要検討
        if (dtoClass.isPrimitive()) {
            return sql;
        }
        final DtoMetaData dmd = createDtoMetaData(dtoClass);
        boolean began = false;
        if (!(sql.lastIndexOf("WHERE") > 0)) {
            buf.append("/*BEGIN*/ WHERE ");
            began = true;
        }
        for (int i = 0; i < dmd.getPropertyTypeSize(); ++i) {
            final PropertyType pt = dmd.getPropertyType(i);
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
            if (!began || i != 0) {
                buf.append("AND ");
            }
            buf.append(columnName);
            buf.append(" = /*");
            buf.append(propertyName);
            buf.append("*/null");
            buf.append("/*END*/");
        }
        if (began) {
            buf.append("/*END*/");
        }
        return buf.toString();
    }

    private DtoMetaData createDtoMetaData(final Class dtoClass) {
        final DtoMetaData dtoMetaData = dtoMetaDataFactory
                .getDtoMetaData(dtoClass);
        for (int i = 0; i < beanMetaData.getPropertyTypeSize(); i++) {
            final PropertyType master = beanMetaData.getPropertyType(i);
            final String name = master.getPropertyName();
            if (dtoMetaData.hasPropertyType(name)) {
                final PropertyType slave = dtoMetaData.getPropertyType(name);
                slave.setColumnName(master.getColumnName());
            }
        }
        return dtoMetaData;
    }

    protected String createAutoSelectSql(final String[] argNames) {
        final String sql = dbms.getAutoSelectSql(getBeanMetaData());
        final StringBuilder buf = new StringBuilder(sql);
        if (argNames.length != 0) {
            boolean began = false;
            if (!(sql.lastIndexOf("WHERE") > 0)) {
                buf.append("/*BEGIN*/ WHERE ");
                began = true;
            }
            for (int i = 0; i < argNames.length; ++i) {
                final String columnName = beanMetaData
                        .convertFullColumnName(argNames[i]);
                buf.append("/*IF ");
                buf.append(argNames[i]);
                buf.append(" != null*/");
                buf.append(" ");
                if (!began || i != 0) {
                    buf.append("AND ");
                }
                buf.append(columnName);
                buf.append(" = /*");
                buf.append(argNames[i]);
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
                || !isBeanClassAssignable(method.getParameterTypes()[0])
                && !method.getParameterTypes()[0].isAssignableFrom(List.class)
                && !method.getParameterTypes()[0].isArray()) {
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
        final String[] insertPrefixes = getDaoNamingConvention()
                .getInsertPrefixes();
        for (int i = 0; i < insertPrefixes.length; ++i) {
            if (methodName.startsWith(insertPrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUpdate(final String methodName) {
        final String[] updatePrefixes = getDaoNamingConvention()
                .getUpdatePrefixes();
        for (int i = 0; i < updatePrefixes.length; ++i) {
            if (methodName.startsWith(updatePrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDelete(final String methodName) {
        final String[] deletePrefixes = getDaoNamingConvention()
                .getDeletePrefixes();
        for (int i = 0; i < deletePrefixes.length; ++i) {
            if (methodName.startsWith(deletePrefixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUnlessNull(final String methodName) {
        final String[] unlessNullSuffixes = getDaoNamingConvention()
                .getUnlessNullSuffixes();
        for (int i = 0; i < unlessNullSuffixes.length; i++) {
            if (methodName.endsWith(unlessNullSuffixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected boolean isModifiedOnly(final String methodName) {
        final String[] modifiedOnlySuffixes = getDaoNamingConvention()
                .getModifiedOnlySuffixes();
        for (int i = 0; i < modifiedOnlySuffixes.length; i++) {
            if (methodName.endsWith(modifiedOnlySuffixes[i])) {
                return true;
            }
        }
        return false;
    }

    protected void putSqlCommand(Method method, SqlCommand cmd) {
        if (useDaoClassForLog) {
            if (cmd instanceof InjectDaoClassSupport) {
                ((InjectDaoClassSupport) cmd).setDaoClass(daoClass);
            }
        }
        sqlCommands.put(method, cmd);
    }

    /**
     * メソッドおよびDao全体のいずれもSingleRowUpdateチェックが有効になっているかどうかを返します。
     *
     * <p>
     * メソッドまたはDao全体のいずれもチェックが有効として設定されている場合のみ、<code>true</code>を返します。
     * どちらか一方でもチェック無効に設定されていれば、 falseを返します。
     * </p>
     *
     * @param method
     *            チェック対象のメソッド
     * @return 指定されたメソッドの実行時チェックが有効なら<code>true</code>を返す。
     */
    protected boolean isCheckSingleRowUpdate(Method method) {
        return checkSingleRowUpdateForAll
                & daoAnnotationReader.isCheckSingleRowUpdate(method);
    }

    @Override
    public Class getBeanClass() {
        return beanClass;
    }

    protected void setBeanClass(final Class beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    @Override
    public synchronized SqlCommand getSqlCommand(final Method method)
            throws MethodNotFoundRuntimeException {

        SqlCommand cmd = (SqlCommand) sqlCommands.get(method);
        if (cmd == null) {
            if (MethodUtil.isAbstract(method)) {
                setupMethod(method);
            }
            cmd = (SqlCommand) sqlCommands.get(method);
            if (cmd == null) {
                throw new MethodNotFoundRuntimeException(daoClass, method.getName(), method.getParameterTypes());
            }
        }
        return cmd;

    }

    @Override
    public boolean hasSqlCommand(final Method method) {
        return sqlCommands.containsKey(method);
    }

    @Override
    public SqlCommand createFindCommand(final String query) {
        return createSelectDynamicCommand(new BeanListMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator()),
                query);
    }

    @Override
    public SqlCommand createFindCommand(Class dtoClass, String query) {
        return createSelectDynamicCommand(
                new DtoListMetaDataResultSetHandler(dtoMetaDataFactory
                        .getDtoMetaData(dtoClass), createRowCreator()), query);
    }

    @Override
    public SqlCommand createFindBeanCommand(final String query) {
        return createSelectDynamicCommand(new BeanMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator()),
                query);
    }

    @Override
    public SqlCommand createFindBeanCommand(Class dtoClass, String query) {
        return createSelectDynamicCommand(
                new DtoMetaDataResultSetHandler(dtoMetaDataFactory
                        .getDtoMetaData(dtoClass), createRowCreator()), query);
    }

    @Override
    public SqlCommand createFindMapCommand(String query) {
        return createSelectDynamicCommand(new MapResultSetHandler(), query);
    }

    @Override
    public SqlCommand createFindMapListCommand(String query) {
        return createSelectDynamicCommand(new MapListResultSetHandler(), query);
    }

    @Override
    public SqlCommand createFindMapArrayCommand(String query) {
        return createSelectDynamicCommand(new MapArrayResultSetHandler(), query);
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
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

    protected String getSqlFileEncoding() {
        return sqlFileEncoding;
    }

    public void setSqlFileEncoding(final String sencoding) {
        this.sqlFileEncoding = sencoding;
    }

    public ValueTypeFactory getValueTypeFactory() {
        return valueTypeFactory;
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Class getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(final Class daoClass) {
        this.daoClass = daoClass;
    }

    public void setBeanMetaDataFactory(
            final BeanMetaDataFactory beanMetaDataFactory) {
        this.beanMetaDataFactory = beanMetaDataFactory;
    }

    public DtoMetaDataFactory getDtoMetaDataFactory() {
        return dtoMetaDataFactory;
    }

    public void setDtoMetaDataFactory(
            final DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    public boolean isUseDaoClassForLog() {
        return useDaoClassForLog;
    }

    public void setUseDaoClassForLog(final boolean setUserDaoClassForLog) {
        this.useDaoClassForLog = setUserDaoClassForLog;
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

    public void setPagingSQLRewriter(final PagingSqlRewriter pagingSqlRewriter) {
        this.pagingSqlRewriter = pagingSqlRewriter;
    }

    public void setProcedureMetaDataFactory(
            ProcedureMetaDataFactory procedureMetaDataFactory) {
        this.procedureMetaDataFactory = procedureMetaDataFactory;
    }

}
