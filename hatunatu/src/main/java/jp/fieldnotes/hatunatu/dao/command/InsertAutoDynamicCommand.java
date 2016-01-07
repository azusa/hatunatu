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
package jp.fieldnotes.hatunatu.dao.command;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.dao.InjectDaoClassSupport;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.handler.InsertAutoHandler;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.BasicHandler;
import jp.fieldnotes.hatunatu.util.exception.SRuntimeException;

public class InsertAutoDynamicCommand implements SqlCommand,
        InjectDaoClassSupport {

    private DataSource dataSource;

    private StatementFactory statementFactory;

    private BeanMetaData beanMetaData;

    private String[] propertyNames;

    private Class notSingleRowUpdatedExceptionClass;

    private Class daoClass;

    private boolean checkSingleRowUpdate = true;

    public InsertAutoDynamicCommand() {
    }

    public Object execute(Object[] args) {
        final Object bean = args[0];
        final BeanMetaData bmd = getBeanMetaData();
        final PropertyType[] propertyTypes = createInsertPropertyTypes(bmd,
                bean, getPropertyNames());
        final String sql = createInsertSql(bmd, propertyTypes);

        InsertAutoHandler handler = new InsertAutoHandler(getDataSource(),
                getStatementFactory(), bmd, propertyTypes,
                isCheckSingleRowUpdate());
        injectDaoClass(handler);
        handler.setSql(sql);
        int rows = handler.execute(args);
        return new Integer(rows);
    }

    public void setDaoClass(Class clazz) {
        daoClass = clazz;
    }

    protected void injectDaoClass(BasicHandler handler) {
        if (daoClass != null) {
            handler.setLoggerClass(daoClass);
        }
    }

    protected String createInsertSql(BeanMetaData bmd,
            PropertyType[] propertyTypes) {
        StringBuilder buf = new StringBuilder(100);
        buf.append("INSERT INTO ");
        buf.append(bmd.getTableName());
        buf.append(" (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            final String columnName = pt.getColumnName();
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(columnName);
        }
        buf.append(") VALUES (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append("?");
        }
        buf.append(")");
        return buf.toString();
    }

    protected PropertyType[] createInsertPropertyTypes(BeanMetaData bmd,
            Object bean, String[] propertyNames) {

        if (0 == propertyNames.length) {
            throw new SRuntimeException("EDAO0024", new Object[] { bean
                    .getClass().getName() });
        }
        List types = new ArrayList();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();

        for (int i = 0; i < propertyNames.length; ++i) {
            PropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                final IdentifierGenerator generator = bmd
                        .getIdentifierGenerator(pt.getPropertyName());
                if (!generator.isSelfGenerate()) {
                    continue;
                }
            } else {
                if (pt.getPropertyDesc().getValue(bean) == null) {
                    final String propertyName = pt.getPropertyName();
                    if (!propertyName.equalsIgnoreCase(timestampPropertyName)
                            && !propertyName
                                    .equalsIgnoreCase(versionNoPropertyName)) {
                        continue;
                    }
                }
            }
            types.add(pt);
        }
        if (types.isEmpty()) {
            throw new SRuntimeException("EDAO0014");
        }
        PropertyType[] propertyTypes = (PropertyType[]) types
                .toArray(new PropertyType[types.size()]);
        return propertyTypes;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Class getNotSingleRowUpdatedExceptionClass() {
        return notSingleRowUpdatedExceptionClass;
    }

    public void setNotSingleRowUpdatedExceptionClass(
            Class notSingleRowUpdatedExceptionClass) {
        this.notSingleRowUpdatedExceptionClass = notSingleRowUpdatedExceptionClass;
    }

    protected StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    protected BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    public void setBeanMetaData(BeanMetaData beanMetaData) {
        this.beanMetaData = beanMetaData;
    }

    protected String[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }

    public boolean isCheckSingleRowUpdate() {
        return checkSingleRowUpdate;
    }

    public void setCheckSingleRowUpdate(boolean checkSingleRowUpdate) {
        this.checkSingleRowUpdate = checkSingleRowUpdate;
    }

}
