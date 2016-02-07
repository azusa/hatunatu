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

import jp.fieldnotes.hatunatu.api.DaoAnnotationReader;
import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.DaoMetaDataFactory;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.pager.PagingSqlRewriter;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.misc.Disposable;
import jp.fieldnotes.hatunatu.util.misc.DisposableUtil;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DaoMetaDataFactoryImpl implements DaoMetaDataFactory, Disposable {

    protected DataSource dataSource;

    protected StatementFactory statementFactory;

    protected ResultSetFactory resultSetFactory;

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    protected BeanMetaDataFactory beanMetaDataFactory;

    protected DaoNamingConvention daoNamingConvention;

    protected ResultSetHandlerFactory resultSetHandlerFactory;

    protected DtoMetaDataFactory dtoMetaDataFactory;

    protected ProcedureMetaDataFactory procedureMetaDataFactory;

    protected PagingSqlRewriter pagingSqlRewriter;

    protected final Map<String,DaoMetaData> daoMetaDataCache = new HashMap<>();

    protected boolean initialized;

    protected boolean useDaoClassForLog;

    protected String sqlFileEncoding;

    public DaoMetaDataFactoryImpl() {
    }

    public DaoMetaDataFactoryImpl(final DataSource dataSource,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final AnnotationReaderFactory annotationReaderFactory) {

        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
        this.resultSetFactory = resultSetFactory;
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setSqlFileEncoding(final String encoding) {
        sqlFileEncoding = encoding;
    }

    public DaoMetaData getDaoMetaData(final Class daoClass) {
        if (!initialized) {
            DisposableUtil.add(this);
            initialized = true;
        }
        final String key = daoClass.getName();
        DaoMetaData dmd;
        synchronized (daoMetaDataCache) {
            dmd = daoMetaDataCache.get(key);
        }
        if (dmd != null) {
            return dmd;
        }
        final DaoMetaData instance  = createDaoMetaData(daoClass);
        synchronized (daoMetaDataCache) {
            dmd = daoMetaDataCache.get(daoClass);
            if (dmd != null) {
                return dmd;
            } else {
                daoMetaDataCache.put(key, instance);
            }
        }
        return instance;
    }

    protected DaoMetaData createDaoMetaData(final Class daoClass) {
        final BeanDesc daoBeanDesc = BeanDescFactory.getBeanDesc(daoClass);
        final DaoAnnotationReader daoAnnotationReader = annotationReaderFactory
                .createDaoAnnotationReader(daoBeanDesc);

        final DaoMetaDataImpl daoMetaData = createDaoMetaDataImpl();
        daoMetaData.setDaoClass(daoClass);
        daoMetaData.setDataSource(dataSource);
        daoMetaData.setStatementFactory(statementFactory);
        daoMetaData.setResultSetFactory(resultSetFactory);
        daoMetaData.setValueTypeFactory(valueTypeFactory);
        daoMetaData.setBeanMetaDataFactory(getBeanMetaDataFactory());
        daoMetaData.setDaoNamingConvention(getDaoNamingConvention());
        daoMetaData.setUseDaoClassForLog(useDaoClassForLog);
        daoMetaData.setDaoAnnotationReader(daoAnnotationReader);
        daoMetaData.setProcedureMetaDataFactory(procedureMetaDataFactory);
        daoMetaData.setDtoMetaDataFactory(dtoMetaDataFactory);
        daoMetaData.setResultSetHandlerFactory(resultSetHandlerFactory);
        if (sqlFileEncoding != null) {
            daoMetaData.setSqlFileEncoding(sqlFileEncoding);
        }
        if (pagingSqlRewriter != null) {
            daoMetaData.setPagingSQLRewriter(pagingSqlRewriter);
        }
        return daoMetaData;
    }

    protected DaoMetaDataImpl createDaoMetaDataImpl() {
        return new DaoMetaDataImpl();
    }

    public void setValueTypeFactory(final ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    protected BeanMetaDataFactory getBeanMetaDataFactory() {
        return beanMetaDataFactory;
    }

    public void setBeanMetaDataFactory(
            final BeanMetaDataFactory beanMetaDataFactory) {
        this.beanMetaDataFactory = beanMetaDataFactory;
    }

    public synchronized void dispose() {
        daoMetaDataCache.clear();
        initialized = false;
    }

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    public void setAnnotationReaderFactory(
            final AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setResultSetFactory(final ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
    }

    public void setStatementFactory(final StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public void setUseDaoClassForLog(final boolean userDaoClassForLog) {
        useDaoClassForLog = userDaoClassForLog;
    }

    public void setResultSetHandlerFactory(
            final ResultSetHandlerFactory resultSetHandlerFactory) {
        this.resultSetHandlerFactory = resultSetHandlerFactory;
    }

    public void setDtoMetaDataFactory(
            final DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }

    public void setProcedureMetaDataFactory(
            ProcedureMetaDataFactory procedureMetaDataFactory) {
        this.procedureMetaDataFactory = procedureMetaDataFactory;
    }

    public void setPagingSQLRewriter(final PagingSqlRewriter pagingSqlRewriter) {
        this.pagingSqlRewriter = pagingSqlRewriter;
    }

}
