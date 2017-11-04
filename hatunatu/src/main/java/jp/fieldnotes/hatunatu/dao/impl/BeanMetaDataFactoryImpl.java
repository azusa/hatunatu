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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class BeanMetaDataFactoryImpl implements BeanMetaDataFactory {

    protected AnnotationReaderFactory annotationReaderFactory;

    protected DataSource dataSource;

    protected DaoNamingConvention daoNamingConvention = new DaoNamingConventionImpl();

    protected ModifiedPropertySupport beanEnhancer = new ModifiedPropertySupport();

    protected TableNaming tableNaming;

    protected PropertyTypeFactoryBuilder propertyTypeFactoryBuilder;

    protected RelationPropertyTypeFactoryBuilder relationPropertyTypeFactoryBuilder;

    @Override
    public BeanMetaData createBeanMetaData(final Class daoInterface,
                                           final Class beanClass) throws SQLException {
        if (NullBean.class == beanClass) {
            return new NullBeanMetaData(daoInterface);
        }
        return createBeanMetaData(beanClass);
    }

    @Override
    public BeanMetaData createBeanMetaData(final Class beanClass) throws SQLException {
        return createBeanMetaData(beanClass, 0);
    }

    @Override
    public BeanMetaData createBeanMetaData(final Class beanClass,
                                           final int relationNestLevel) throws SQLException {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        try (Connection con = DataSourceUtil.getConnection(dataSource)) {
            final DatabaseMetaData metaData = ConnectionUtil.getMetaData(con);
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        }
    }

    public BeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData,
            final Class beanClass, final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final Dbms dbms = getDbms(dbMetaData);
        final boolean stopRelationCreation = isLimitRelationNestLevel(relationNestLevel);
        final BeanAnnotationReader bar = annotationReaderFactory
                .createBeanAnnotationReader(beanClass);
        final String versionNoPropertyName = getVersionNoPropertyName(bar);
        final String timestampPropertyName = getTimestampPropertyName(bar);
        final PropertyTypeFactory ptf = createPropertyTypeFactory(
                beanClass, bar, dbMetaData, dbms);
        final RelationPropertyTypeFactory rptf = createRelationPropertyTypeFactory(
                beanClass, bar, dbMetaData, relationNestLevel,
                stopRelationCreation);
        final BeanMetaDataImpl bmd = createBeanMetaDataImpl();

        bmd.setDbms(dbms);
        bmd.setBeanAnnotationReader(bar);
        bmd.setVersionNoPropertyName(versionNoPropertyName);
        bmd.setTimestampPropertyName(timestampPropertyName);
        bmd.setBeanClass(beanClass);
        bmd.setTableNaming(tableNaming);
        bmd.setPropertyTypeFactory(ptf);
        bmd.setRelationPropertyTypeFactory(rptf);
        bmd.setRelationToTable(bar.getTableAnnotation() != null);
        bmd.initialize();

        bmd.setModifiedPropertySupport(this.beanEnhancer);
        bmd.setBeanClass(beanClass);

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

    protected Dbms getDbms() throws SQLException {
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

}
