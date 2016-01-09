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
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.MethodDesc;
import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.impl.*;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import org.junit.rules.ExternalResource;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.SqlTableReader;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.seasar.framework.container.ContainerConstants;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.env.Env;
import org.seasar.framework.unit.UnitClassLoader;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HatunatuTest extends ExternalResource {


    public ValueTypeFactory valueTypeFactory;

    public AnnotationReaderFactory annotationReaderFactory;

    public BeanMetaDataFactory beanMetaDataFactory;

    public DaoNamingConvention daoNamingConvention;

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

    private S2Container container;

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

    public HatunatuTest(Object instance){
        this(instance, "j2ee.dicon");
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
        PagerContext.end();

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DataTable readDbByTable(String table) {
        return readDbByTable(table, null);
    }

    public DataTable readDbByTable(String table, String condition) {
        SqlTableReader reader = new SqlTableReader(getDataSource());
        reader.setTable(table, condition);
        return reader.read();
    }

    public void assertBeanEquals(final String message,
                                    final DataSet expected, final Object bean) {

        final S2DaoBeanReader reader = new S2DaoBeanReader(bean,
                createBeanMetaData(bean.getClass()));
        assertThat(message, reader.read(), is(expected));
    }

    public void assertBeanListEquals(final String message,
                                        final DataSet expected, final List list) {

        final S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
                createBeanMetaData(list.get(0).getClass()));
        assertThat(message, reader.read(), is(expected));
    }

    public BeanMetaData createBeanMetaData(final Class beanClass) {
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
        dmd.setStatementFactory(BasicStatementFactory.INSTANCE);
        dmd.setResultSetFactory(BasicResultSetFactory.INSTANCE);
        dmd.setValueTypeFactory(getValueTypeFactory());
        dmd.setBeanMetaDataFactory(bmdf);
        dmd.setDaoNamingConvention(getDaoNamingConvention());
        dmd.setDaoAnnotationReader(daoAnnotationReader);
        dmd.setProcedureMetaDataFactory(getProcedureMetaDataFactory());
        dmd.setDtoMetaDataFactory(dmdf);
        dmd.setResultSetHandlerFactory(getResultSetHandlerFactory());
        dmd.initialize();
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
            impl.setDaoNamingConvention(getDaoNamingConvention());
            impl.setPropertyTypeFactoryBuilder(getPropertyTypeFactoryBuilder());
            impl
                    .setRelationPropertyTypeFactoryBuilder(getRelationPropertyTypeFactoryBuilder(impl));
            impl.setTableNaming(getTableNaming());
            NullBeanEnhancer enhancer = new NullBeanEnhancer();
            enhancer.setDaoNamingConvention(getDaoNamingConvention());
            impl.setBeanEnhancer(enhancer);
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

    public DaoNamingConvention getDaoNamingConvention() {
        if (daoNamingConvention == null) {
            daoNamingConvention = new DaoNamingConventionImpl();
        }
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
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

    protected ResultSetHandlerFactory getResultSetHandlerFactory() {
        if (resultSetHandlerFactory == null) {
            final ResultSetHandlerFactorySelector factory = new ResultSetHandlerFactorySelector();
            factory.setDtoMetaDataFactory(getDtoMetaDataFactory());
            factory.init();
            resultSetHandlerFactory = factory;
        }
        return resultSetHandlerFactory;
    }

    protected void setResultSetHandlerFactory(
            final ResultSetHandlerFactory resultSetHandlerFactory) {
        this.resultSetHandlerFactory = resultSetHandlerFactory;
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

    protected void setDtoMetaDataFactory(
            final DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }

    protected ColumnNaming getColumnNaming() {
        if (columnNaming == null) {
            columnNaming = new DefaultColumnNaming();
        }
        return columnNaming;
    }

    protected void setColumnNaming(final ColumnNaming columnNaming) {
        this.columnNaming = columnNaming;
    }

    protected PropertyTypeFactoryBuilder getPropertyTypeFactoryBuilder() {
        if (propertyTypeFactoryBuilder == null) {
            final PropertyTypeFactoryBuilderImpl builder = new PropertyTypeFactoryBuilderImpl();
            builder.setColumnNaming(getColumnNaming());
            builder.setDaoNamingConvention(getDaoNamingConvention());
            builder.setValueTypeFactory(getValueTypeFactory());
            propertyTypeFactoryBuilder = builder;
        }
        return propertyTypeFactoryBuilder;
    }

    protected void setPropertyTypeFactoryBuilder(
            final PropertyTypeFactoryBuilder propertyTypeFactoryBuilder) {
        this.propertyTypeFactoryBuilder = propertyTypeFactoryBuilder;
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

    protected void setRelationPropertyTypeFactoryBuilder(
            final RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder) {
        this.relationPropertyTypeFactoryBuilder = relationPropertyTypeFactoryBuilder;
    }

    protected TableNaming getTableNaming() {
        if (tableNaming == null) {
            tableNaming = new DefaultTableNaming();
        }
        return tableNaming;
    }

    protected void setTableNaming(final TableNaming tableNaming) {
        this.tableNaming = tableNaming;
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

    protected void setProcedureMetaDataFactory(
            final ProcedureMetaDataFactory procedureMetaDataFactory) {
        this.procedureMetaDataFactory = procedureMetaDataFactory;
    }

    private S2Container getContainer() {
        return container;
    }

    private void include(String path) {
        S2ContainerFactory.include(container, ResourceUtil.convertPath(path, instance.getClass()));
    }

    private Object getComponent(Class componentClass) {
        return container.getComponent(componentClass);
    }

    private void setupDataSource() {
        S2Container container = getContainer();
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
        Env.setFilePath(ENV_PATH);
        Env.setValueIfAbsent(ENV_VALUE);
        originalClassLoader = getOriginalClassLoader();
        unitClassLoader = new UnitClassLoader(originalClassLoader);
        Thread.currentThread().setContextClassLoader(unitClassLoader);
        container = S2ContainerFactory.create();
        SingletonS2ContainerFactory.setContainer(container);
    }

    private ClassLoader getOriginalClassLoader() {
        S2Container configurationContainer = S2ContainerFactory
                .getConfigurationContainer();
        if (configurationContainer != null
                && configurationContainer.hasComponentDef(ClassLoader.class)) {
            return (ClassLoader) configurationContainer
                    .getComponent(ClassLoader.class);
        }
        return Thread.currentThread().getContextClassLoader();
    }



}
