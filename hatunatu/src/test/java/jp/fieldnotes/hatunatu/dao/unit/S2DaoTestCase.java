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

import jp.fieldnotes.hatunatu.api.*;
import jp.fieldnotes.hatunatu.api.beans.MethodDesc;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.impl.*;
import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.unit.S2TestCase;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.util.List;

public abstract class S2DaoTestCase extends S2TestCase {

    private ValueTypeFactory valueTypeFactory;

    private AnnotationReaderFactory annotationReaderFactory;

    private BeanMetaDataFactory beanMetaDataFactory;

    private DaoNamingConvention daoNamingConvention;

    private Dbms dbms;

    private ResultSetHandlerFactory resultSetHandlerFactory;

    private DtoMetaDataFactory dtoMetaDataFactory;

    private PropertyTypeFactoryBuilder propertyTypeFactoryBuilder;

    private RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder;

    private TableNaming tableNaming;

    private ColumnNaming columnNaming;

    private ProcedureMetaDataFactory procedureMetaDataFactory;

    protected void tearDown() throws Exception {
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
        super.tearDown();
    }

    protected void assertBeanEquals(final String message,
            final DataSet expected, final Object bean) {

        final S2DaoBeanReader reader = new S2DaoBeanReader(bean,
                createBeanMetaData(bean.getClass()));
        assertEquals(message, expected, reader.read());
    }

    protected void assertBeanListEquals(final String message,
            final DataSet expected, final List list) {

        final S2DaoBeanListReader reader = new S2DaoBeanListReader(list,
                createBeanMetaData(list.get(0).getClass()));
        assertEquals(message, expected, reader.read());
    }

    protected BeanMetaData createBeanMetaData(final Class beanClass) {
        final BeanMetaDataFactory factory = getBeanMetaDataFactory();
        return factory.createBeanMetaData(beanClass);
    }

    protected DtoMetaDataImpl createDtoMetaData(final Class dtoClass) {
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

    protected DaoMetaDataImpl createDaoMetaData(final Class daoClass) {
        final DaoMetaDataImpl dmd = new DaoMetaDataImpl();
        final BeanDesc daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        final jp.fieldnotes.hatunatu.api.DaoAnnotationReader daoAnnotationReader = getAnnotationReaderFactory()
                .createDaoAnnotationReader(daoBeanDesc);
        final BeanMetaDataFactory bmdf = getBeanMetaDataFactory();
        final DtoMetaDataFactory dmdf = getDtoMetaDataFactory();

        dmd.setDaoClass(daoClass);
        dmd.setDataSource(getDataSource());
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

    protected BeanMetaDataFactory getBeanMetaDataFactory() {
        if (beanMetaDataFactory == null) {
            final BeanMetaDataFactoryImpl impl = new BeanMetaDataFactoryImpl() {
                protected Dbms getDbms() {
                    return S2DaoTestCase.this.getDbms();
                }
            };
            impl.setAnnotationReaderFactory(getAnnotationReaderFactory());
            impl.setDataSource(getDataSource());
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

    protected Method getSingleDaoMethod(Class daoClass, String methodName) {
        MethodDesc[] methodDesc = BeanDescFactory.getBeanDesc(daoClass).getMethodDescs(methodName);
        if (methodDesc.length >= 2){
            throw new AssertionError(methodName + "is overloaded.");
        }
        return methodDesc[0].getMethod();
    }

    protected Dbms getDbms() {
        if (dbms == null) {
            final DatabaseMetaData dbMetaData = getDatabaseMetaData();
            dbms = DbmsManager.getDbms(dbMetaData);
        }
        return dbms;
    }

    protected void setDbms(final Dbms dbms) {
        this.dbms = dbms;
    }

    protected AnnotationReaderFactory getAnnotationReaderFactory() {
        if (annotationReaderFactory == null) {
            annotationReaderFactory = new AnnotationReaderFactoryImpl();
        }
        return annotationReaderFactory;
    }

    protected void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    protected ValueTypeFactory getValueTypeFactory() {
        if (valueTypeFactory == null) {
            final ValueTypeFactoryImpl impl = new ValueTypeFactoryImpl();
            valueTypeFactory = impl;
        }
        return valueTypeFactory;
    }

    protected void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    protected DaoNamingConvention getDaoNamingConvention() {
        if (daoNamingConvention == null) {
            daoNamingConvention = new DaoNamingConventionImpl();
        }
        return daoNamingConvention;
    }

    protected void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
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

}