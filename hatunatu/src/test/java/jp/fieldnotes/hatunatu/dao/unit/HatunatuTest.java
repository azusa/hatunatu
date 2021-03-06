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

package jp.fieldnotes.hatunatu.dao.unit;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.MethodDesc;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.dataset.DataReader;
import jp.fieldnotes.hatunatu.dao.dataset.DataSet;
import jp.fieldnotes.hatunatu.dao.dataset.DataTable;
import jp.fieldnotes.hatunatu.dao.dataset.impl.SqlTableReader;
import jp.fieldnotes.hatunatu.dao.dataset.impl.SqlWriter;
import jp.fieldnotes.hatunatu.dao.dataset.impl.XlsReader;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.impl.*;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.io.ResourceUtil;
import jp.fieldnotes.hatunatu.util.lang.FieldUtil;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;
import org.junit.rules.ExternalResource;
import org.lastaflute.di.core.ContainerConstants;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HatunatuTest extends ExternalResource {


    public ValueTypeFactory valueTypeFactory;

    public AnnotationReaderFactory annotationReaderFactory;

    public BeanMetaDataFactory beanMetaDataFactory;

    public Dbms dbms;

    public ResultSetHandlerFactory resultSetHandlerFactory;

    public DtoMetaDataFactory dtoMetaDataFactory;

    public PropertyTypeFactoryBuilder propertyTypeFactoryBuilder;

    public RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder;

    public TableNaming tableNaming;

    public ColumnNaming columnNaming;

    public ProcedureMetaDataFactory procedureMetaDataFactory;

    private static final String ENV_PATH = "env_ut.txt";

    private static final String ENV_VALUE = "ut";

    private LaContainer container;

    private ClassLoader originalClassLoader;

    private UnitClassLoader unitClassLoader;

    private static final String DATASOURCE_NAME = "j2ee"
            + ContainerConstants.NS_SEP + "dataSource";

    private DataSource dataSource;

    private Connection connection;

    private DatabaseMetaData dbMetaData;

    private TransactionManager tm;

    private List<Field> boundFields = new ArrayList<>();

    private Object instance;

    private String dataSourceDiconName;

    private QueryObject queryObject = new QueryObject();

    public HatunatuTest(Object instance){
        this(instance, "jdbc.xml");
    }

    public HatunatuTest(Object instance, String dataSourceDiconName){
        this.instance = instance;
        this.dataSourceDiconName = dataSourceDiconName;
    }


    @Override
    protected void before() throws Throwable {
        setUpContainer();
        include(dataSourceDiconName);
        bindFields();
        setupDataSource();
        tm = (TransactionManager) getComponent(TransactionManager.class);
        tm.begin();

        List<String> migrationScript = Files.readAllLines(Paths.get(ResourceUtil.getResource("migration/script.sql").toURI()), Charset.forName("UTF-8"));
        for (String script : migrationScript){
            if (script.isEmpty()){
                continue;
            }

            try (Statement stmt = this.dataSource.getConnection().createStatement()){
                stmt.executeUpdate(script);
            }
        }
    }

    @Override
    protected void after() {

        if (tm != null) {
            try {
                tm.rollback();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
        unbindFields();
        tearDownDataSource();
        valueTypeFactory = null;
        annotationReaderFactory = null;
        beanMetaDataFactory = null;
        dbms = null;
        resultSetHandlerFactory = null;
        dtoMetaDataFactory = null;
        propertyTypeFactoryBuilder = null;
        relationPropertyTypeFactoryBuilder = null;
        tableNaming = null;
        columnNaming = null;
        procedureMetaDataFactory = null;

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DataTable readDbByTable(String table) throws Exception {
        return readDbByTable(table, null);
    }

    public DataTable readDbByTable(String table, String condition) throws Exception {
        SqlTableReader reader = new SqlTableReader(getDataSource());
        reader.setTable(table, condition);
        return reader.read();
    }

    public void readXlsAllReplaceDb(String path) throws Exception {
        DataSet dataSet = readXls(path);
        for (int i = dataSet.getTableSize() - 1; i >= 0; --i) {
            deleteTable(dataSet.getTable(i).getTableName());
        }
        writeDb(dataSet);
    }

    private void deleteTable(String tableName) {
        BasicUpdateHandler handler = new BasicUpdateHandler(getDataSource(),
                "DELETE FROM " + tableName);
        handler.execute(null);
    }

    public DataSet readXls(String path) {
        DataReader reader = new XlsReader(ResourceUtil.convertPath(path, instance.getClass()), true);
        return reader.read();
    }

    private void writeDb(DataSet dataSet) throws Exception {
        SqlWriter writer = new SqlWriter(this.getDataSource());
        writer.write(dataSet);
    }

    public BeanMetaData createBeanMetaData(final Class beanClass) throws SQLException {
        final BeanMetaDataFactory factory = getBeanMetaDataFactory();
        return factory.createBeanMetaData(beanClass);
    }

    public DtoMetaDataImpl createDtoMetaData(final Class dtoClass) {
        final DtoMetaDataImpl dmd = new DtoMetaDataImpl();
        final BeanAnnotationReader reader = getAnnotationReaderFactory()
                .createBeanAnnotationReader(dtoClass);
        final PropertyTypeFactoryBuilder builder = getPropertyTypeFactoryBuilder();
        final PropertyTypeFactory propertyTypeFactory = builder.build(dtoClass,
                reader);
        dmd.setBeanClass(dtoClass);
        dmd.setBeanAnnotationReader(getAnnotationReaderFactory()
                .createBeanAnnotationReader(dtoClass));
        dmd.setPropertyTypeFactory(propertyTypeFactory);
        dmd.initialize();
        return dmd;
    }

    public DaoMetaDataImpl createDaoMetaData(final Class daoClass) {
        final DaoMetaDataImpl dmd = new DaoMetaDataImpl();
        final BeanDesc daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        final jp.fieldnotes.hatunatu.api.DaoAnnotationReader daoAnnotationReader = getAnnotationReaderFactory()
                .createDaoAnnotationReader(daoBeanDesc);
        final BeanMetaDataFactory bmdf = getBeanMetaDataFactory();
        final DtoMetaDataFactory dmdf = getDtoMetaDataFactory();

        dmd.setDaoClass(daoClass);
        dmd.setDataSource(this.dataSource);
        dmd.setStatementFactory(StatementFactory.INSTANCE);
        dmd.setResultSetFactory(BasicResultSetFactory.INSTANCE);
        dmd.setValueTypeFactory(getValueTypeFactory());
        dmd.setBeanMetaDataFactory(bmdf);
        dmd.setDaoAnnotationReader(daoAnnotationReader);
        dmd.setProcedureMetaDataFactory(getProcedureMetaDataFactory());
        dmd.setDtoMetaDataFactory(dmdf);
        dmd.setResultSetHandlerFactory(getResultSetHandlerFactory());
        return dmd;
    }

    public BeanMetaDataFactory getBeanMetaDataFactory() {
        if (beanMetaDataFactory == null) {
            final BeanMetaDataFactoryImpl impl = new BeanMetaDataFactoryImpl() {
                protected Dbms getDbms() {
                    return this.getDbms();
                }
            };
            impl.setAnnotationReaderFactory(getAnnotationReaderFactory());
            impl.setDataSource(this.dataSource);
            impl.setPropertyTypeFactoryBuilder(getPropertyTypeFactoryBuilder());
            impl
                    .setRelationPropertyTypeFactoryBuilder(getRelationPropertyTypeFactoryBuilder(impl));
            impl.setTableNaming(new DefaultTableNaming());
            beanMetaDataFactory = impl;
        }
        return beanMetaDataFactory;
    }

    public Method getSingleDaoMethod(Class daoClass, String methodName) {
        MethodDesc[] methodDesc = BeanDescFactory.getBeanDesc(daoClass).getMethodDescs(methodName);
        if (methodDesc.length >= 2){
            throw new AssertionError(methodName + "is overloaded.");
        }
        return methodDesc[0].getMethod();
    }

    public Dbms getDbms() {
        if (dbms == null) {
            final DatabaseMetaData dbMetaData = getDatabaseMetaData();
            dbms = DbmsManager.getDbms(dbMetaData);
        }
        return dbms;
    }

    public void setDbms(final Dbms dbms) {
        this.dbms = dbms;
    }

    public AnnotationReaderFactory getAnnotationReaderFactory() {
        if (annotationReaderFactory == null) {
            annotationReaderFactory = new AnnotationReaderFactoryImpl();
        }
        return annotationReaderFactory;
    }


    public ValueTypeFactory getValueTypeFactory() {
        if (valueTypeFactory == null) {
            final ValueTypeFactoryImpl impl = new ValueTypeFactoryImpl();
            valueTypeFactory = impl;
        }
        return valueTypeFactory;
    }

    public DatabaseMetaData getDatabaseMetaData() {
        if (dbMetaData != null) {
            return dbMetaData;
        }
        dbMetaData = ConnectionUtil.getMetaData(getConnection());
        return dbMetaData;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        connection = DataSourceUtil.getConnection(this.dataSource);
        return connection;
    }

    public QueryObject getQueryObject() {
        return this.queryObject;
    }

    protected ResultSetHandlerFactory getResultSetHandlerFactory() {
        if (resultSetHandlerFactory == null) {
            final ResultSetHandlerFactorySelector factory = new ResultSetHandlerFactorySelector();
            factory.setDtoMetaDataFactory(getDtoMetaDataFactory());
            factory.init();
            resultSetHandlerFactory = factory;
        }
        return resultSetHandlerFactory;
    }


    protected DtoMetaDataFactory getDtoMetaDataFactory() {
        if (dtoMetaDataFactory == null) {
            final DtoMetaDataFactoryImpl factory = new DtoMetaDataFactoryImpl();
            factory.setAnnotationReaderFactory(getAnnotationReaderFactory());
            factory
                    .setPropertyTypeFactoryBuilder(getPropertyTypeFactoryBuilder());
            dtoMetaDataFactory = factory;
        }
        return dtoMetaDataFactory;
    }

    protected void setColumnNaming(final ColumnNaming columnNaming) {
        this.columnNaming = columnNaming;
    }

    protected PropertyTypeFactoryBuilder getPropertyTypeFactoryBuilder() {
        if (propertyTypeFactoryBuilder == null) {
            final PropertyTypeFactoryBuilderImpl builder = new PropertyTypeFactoryBuilderImpl();
            builder.setColumnNaming(new DefaultColumnNaming());
            builder.setValueTypeFactory(getValueTypeFactory());
            propertyTypeFactoryBuilder = builder;
        }
        return propertyTypeFactoryBuilder;
    }

    protected RelationPropertyTypeFactoryBuilder getRelationPropertyTypeFactoryBuilder(
            final BeanMetaDataFactory beanMetaDataFactory) {
        if (relationPropertyTypeFactoryBuilder == null) {
            final RelationPropertyTypeFactoryBuilderImpl builder = new RelationPropertyTypeFactoryBuilderImpl();
            builder.setBeanMetaDataFactory(beanMetaDataFactory);
            relationPropertyTypeFactoryBuilder = builder;
        }
        return relationPropertyTypeFactoryBuilder;
    }


    protected ProcedureMetaDataFactory getProcedureMetaDataFactory() {
        if (procedureMetaDataFactory == null) {
            final ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
            factory.setValueTypeFactory(valueTypeFactory);
            factory.setAnnotationReaderFactory(annotationReaderFactory);
            factory.initialize();
            procedureMetaDataFactory = factory;
        }
        return procedureMetaDataFactory;
    }


    private LaContainer getContainer() {
        return container;
    }

    private void include(String path) {
        LaContainerFactory.include(container, ResourceUtil.convertPath(path, instance.getClass()));
    }

    private Object getComponent(Class componentClass) {
        return container.getComponent(componentClass);
    }

    private void setupDataSource() {
        LaContainer container = getContainer();
            if (container.hasComponentDef(DATASOURCE_NAME)) {
                dataSource = (DataSource) container
                        .getComponent(DATASOURCE_NAME);
            } else if (container.hasComponentDef(DataSource.class)) {
                dataSource = (DataSource) container
                        .getComponent(DataSource.class);
            }
    }

    private void bindFields() throws Throwable {
        boundFields = new ArrayList<Field>();
        Field[] fields = instance.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            bindField(fields[i]);
        }
    }

    private void bindField(Field field) {
        if (isAutoBindable(field)) {
            field.setAccessible(true);
            if (FieldUtil.get(field, instance) != null) {
                return;
            }
            String name = normalizeName(field.getName());
            Object component = null;
            if (getContainer().hasComponentDef(name)) {
                Class componentClass = container.getComponentDef(name)
                        .getComponentClass();
                if (componentClass == null) {
                    component = container.getComponent(name);
                    if (component != null) {
                        componentClass = component.getClass();
                    }
                }
                if (componentClass != null
                        && field.getType().isAssignableFrom(componentClass)) {
                    if (component == null) {
                        component = container.getComponent(name);
                    }
                } else {
                    component = null;
                }
            }
            if (component == null
                    && getContainer().hasComponentDef(field.getType())) {
                component = getComponent(field.getType());
            }
            if (component != null) {
                FieldUtil.set(field, instance, component);
                boundFields.add(field);
            }
        }
    }

    private boolean isAutoBindable(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)
                && !field.getType().isPrimitive();
    }

    private String normalizeName(String name) {
        return StringUtil.replace(name, "_", "");
    }

    private void unbindFields() {
        for (int i = 0; i < boundFields.size(); ++i) {
            Field field = (Field) boundFields.get(i);
            try {
                field.set(instance, null);
            } catch (IllegalArgumentException e) {
                System.err.println(e);
            } catch (IllegalAccessException e) {
                System.err.println(e);
            }
        }
        boundFields = null;
    }

    private void tearDownDataSource() {
        dbMetaData = null;
        if (connection != null) {
            ConnectionUtil.close(connection);
            connection = null;
        }
        dataSource = null;
    }

    private void setUpContainer() throws Throwable {
        originalClassLoader = getOriginalClassLoader();
        unitClassLoader = new UnitClassLoader(originalClassLoader);
        Thread.currentThread().setContextClassLoader(unitClassLoader);
        container = LaContainerFactory.create("app.xml");
        SingletonLaContainerFactory.setContainer(container);
    }

    private ClassLoader getOriginalClassLoader() {
        LaContainer configurationContainer = LaContainerFactory
                .getConfigurationContainer();
        if (configurationContainer != null
                && configurationContainer.hasComponentDef(ClassLoader.class)) {
            return (ClassLoader) configurationContainer
                    .getComponent(ClassLoader.class);
        }
        return Thread.currentThread().getContextClassLoader();
    }

    private static class BasicUpdateHandler {

        private DataSource ds;

        private String sql;

        /**
         * {@link org.seasar.extension.jdbc.impl.BasicUpdateHandler}を作成します。
         */
        public BasicUpdateHandler() {
        }

        /**
         * {@link org.seasar.extension.jdbc.impl.BasicUpdateHandler}を作成します。
         *
         * @param dataSource データソース
         * @param sql        SQL
         */
        public BasicUpdateHandler(DataSource dataSource, String sql) {
            this.ds = dataSource;
            this.sql = sql;
        }

        public int execute(Object[] args) throws SQLRuntimeException {
            return execute(args, getArgTypes(args));
        }

        public int execute(Object[] args, Class[] argTypes)
                throws SQLRuntimeException {

            try (Connection connection = ds.getConnection()) {
                return execute(connection, args, argTypes);
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }

        /**
         * SQL文を実行します。
         *
         * @param connection コネクション
         * @param args       引数
         * @param argTypes   引数の型
         * @return 更新した行数
         */
        public int execute(Connection connection, Object[] args, Class[] argTypes) {

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                bindArgs(ps, args, argTypes);
                return PreparedStatementUtil.executeUpdate(ps);
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }

        private void bindArgs(PreparedStatement ps, Object[] args,
                              Class[] argTypes) {

            if (args == null) {
                return;
            }
            for (int i = 0; i < args.length; ++i) {
                ValueType valueType = getValueType(argTypes[i]);
                try {
                    valueType.bindValue(ps, i + 1, args[i]);
                } catch (SQLException ex) {
                    throw new SQLRuntimeException(ex);
                }
            }
        }

        private ValueType getValueType(Class clazz) {
            return ValueTypes.getValueType(clazz);
        }

        private Class[] getArgTypes(Object[] args) {
            if (args == null) {
                return null;
            }
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; ++i) {
                Object arg = args[i];
                if (arg != null) {
                    argTypes[i] = arg.getClass();
                }
            }
            return argTypes;
        }

    }


}
