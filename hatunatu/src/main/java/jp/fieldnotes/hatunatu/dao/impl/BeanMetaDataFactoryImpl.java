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

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;

/**
 * @author jflute
 * @author manhole
 */
public class BeanMetaDataFactoryImpl implements BeanMetaDataFactory {

    public static final String annotationReaderFactory_BINDING = "bindingType=must";

    public static final String dataSource_BINDING = "bindingType=must";

    public static final String daoNamingConvention_BINDING = "bindingType=must";

    public static final String tableNaming_BINDING = "bindingType=must";

    public static final String propertyTypeFactoryBuilder_BINDING = "bindingType=must";

    public static final String relationPropertyTypeFactoryBuilder_BINDING = "bindingType=must";

    protected AnnotationReaderFactory annotationReaderFactory;

    protected DataSource dataSource;

    protected DaoNamingConvention daoNamingConvention;

    protected BeanEnhancer beanEnhancer;

    protected TableNaming tableNaming;

    protected PropertyTypeFactoryBuilder propertyTypeFactoryBuilder;

    protected RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder;

    public BeanMetaData createBeanMetaData(final Class daoInterface,
            final Class beanClass) {
        if (NullBean.class == beanClass) {
            return new NullBeanMetaData(daoInterface);
        }
        return createBeanMetaData(beanClass);
    }

    public BeanMetaData createBeanMetaData(final Class beanClass) {
        return createBeanMetaData(beanClass, 0);
    }

    public BeanMetaData createBeanMetaData(final Class beanClass,
            final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            final DatabaseMetaData metaData = ConnectionUtil.getMetaData(con);
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        } finally {
            ConnectionUtil.close(con);
        }
    }

    public BeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData,
            final Class beanClass, final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final Class originalBeanClass = this.beanEnhancer.getOriginalClass(beanClass);
        final Dbms dbms = getDbms(dbMetaData);
        final boolean stopRelationCreation = isLimitRelationNestLevel(relationNestLevel);
        final BeanAnnotationReader bar = annotationReaderFactory
                .createBeanAnnotationReader(originalBeanClass);
        final String versionNoPropertyName = getVersionNoPropertyName(bar);
        final String timestampPropertyName = getTimestampPropertyName(bar);
        final PropertyTypeFactory ptf = createPropertyTypeFactory(
                originalBeanClass, bar, dbMetaData, dbms);
        final RelationPropertyTypeFactory rptf = createRelationPropertyTypeFactory(
                originalBeanClass, bar, dbMetaData, relationNestLevel,
                stopRelationCreation);
        final BeanMetaDataImpl bmd = createBeanMetaDataImpl();

        bmd.setDbms(dbms);
        bmd.setBeanAnnotationReader(bar);
        bmd.setVersionNoPropertyName(versionNoPropertyName);
        bmd.setTimestampPropertyName(timestampPropertyName);
        bmd.setBeanClass(originalBeanClass);
        bmd.setTableNaming(tableNaming);
        bmd.setPropertyTypeFactory(ptf);
        bmd.setRelationPropertyTypeFactory(rptf);
        bmd.setRelationToTable(bar.getTableAnnotation() != null);
        bmd.initialize();

        final Class enhancedBeanClass = this.beanEnhancer.enhanceBeanClass(beanClass,
                versionNoPropertyName, timestampPropertyName);
        bmd.setModifiedPropertySupport(this.beanEnhancer.getSupporter());
        bmd.setBeanClass(enhancedBeanClass);

        return bmd;
    }

    protected String getVersionNoPropertyName(
            BeanAnnotationReader beanAnnotationReader) {
        final String defaultName = getDaoNamingConvention()
                .getVersionNoPropertyName();
        final String name = beanAnnotationReader.getVersionNoPropertyName();
        return name != null ? name : defaultName;
    }

    protected String getTimestampPropertyName(
            BeanAnnotationReader beanAnnotationReader) {
        final String defaultName = getDaoNamingConvention()
                .getTimestampPropertyName();
        final String name = beanAnnotationReader.getTimestampPropertyName();
        return name != null ? name : defaultName;
    }

    protected PropertyTypeFactory createPropertyTypeFactory(
            Class originalBeanClass, BeanAnnotationReader beanAnnotationReader,
            DatabaseMetaData databaseMetaData, Dbms dbms) {
        return propertyTypeFactoryBuilder.build(originalBeanClass,
                beanAnnotationReader, dbms, databaseMetaData);
    }

    protected RelationPropertyTypeFactory createRelationPropertyTypeFactory(
            Class originalBeanClass, BeanAnnotationReader beanAnnotationReader,
            DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {
        return relationPropertyTypeFactoryBuilder.build(originalBeanClass,
                beanAnnotationReader, databaseMetaData, relationNestLevel,
                isStopRelationCreation);
    }

    protected Dbms getDbms() {
        return DbmsManager.getDbms(dataSource);
    }

    protected Dbms getDbms(DatabaseMetaData dbMetaData) {
        return DbmsManager.getDbms(dbMetaData);
    }

    protected BeanMetaDataImpl createBeanMetaDataImpl() {
        return new BeanMetaDataImpl();
    }

    protected boolean isLimitRelationNestLevel(final int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }

    protected int getLimitRelationNestLevel() {
        // You can change relation creation range by changing this.
        return 1;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    public TableNaming getTableNaming() {
        return tableNaming;
    }

    public void setTableNaming(TableNaming tableNameConverter) {
        this.tableNaming = tableNameConverter;
    }

    public void setPropertyTypeFactoryBuilder(
            PropertyTypeFactoryBuilder propertyTypeFactoryBuilder) {
        this.propertyTypeFactoryBuilder = propertyTypeFactoryBuilder;
    }

    public void setRelationPropertyTypeFactoryBuilder(
            RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder) {
        this.relationPropertyTypeFactoryBuilder = relationPropertyTypeFactoryBuilder;
    }

    public void setBeanEnhancer(BeanEnhancer enhancer){
        this.beanEnhancer = enhancer;
    }
}
