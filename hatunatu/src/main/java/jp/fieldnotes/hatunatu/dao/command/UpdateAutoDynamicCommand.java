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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.exception.NoUpdatePropertyTypeRuntimeException;
import jp.fieldnotes.hatunatu.dao.handler.UpdateAutoHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class UpdateAutoDynamicCommand extends AbstractSqlCommand {

    private BeanMetaData beanMetaData;

    private String[] propertyNames;

    private boolean checkSingleRowUpdate = true;

    public UpdateAutoDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    @Override
    protected Object doExecute(Object[] args) throws Exception {
        final Object bean = args[0];
        final BeanMetaData bmd = getBeanMetaData();
        final PropertyType[] propertyTypes = createUpdatePropertyTypes(bmd,
                bean, getPropertyNames());

        UpdateAutoHandler handler = new UpdateAutoHandler(getDataSource(),
                getStatementFactory(), bmd, propertyTypes,
                isCheckSingleRowUpdate());
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(createUpdateSql(bmd, propertyTypes));
        queryObject.setMethodArguments(args);

        int i = handler.execute(queryObject);
        return new Integer(i);
    }

    protected PropertyType[] createUpdatePropertyTypes(BeanMetaData bmd,
            Object bean, String[] propertyNames) {
        List types = new ArrayList();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyNames.length; ++i) {
            PropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey() == false) {
                String propertyName = pt.getPropertyName();
                if (propertyName.equalsIgnoreCase(timestampPropertyName)
                        || propertyName.equalsIgnoreCase(versionNoPropertyName)
                        || pt.getPropertyDesc().getValue(bean) != null) {
                    types.add(pt);
                }
            }
        }
        if (types.isEmpty()) {
            throw new NoUpdatePropertyTypeRuntimeException();
        }
        PropertyType[] propertyTypes = (PropertyType[]) types
                .toArray(new PropertyType[types.size()]);
        return propertyTypes;
    }

    protected String createUpdateSql(BeanMetaData bmd,
            PropertyType[] propertyTypes) {
        StringBuilder buf = new StringBuilder(100);
        buf.append("UPDATE ");
        buf.append(bmd.getTableName());
        buf.append(" SET ");
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            final String columnName = pt.getColumnName();
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(columnName);
            buf.append(" = ?");
        }

        buf.append(" WHERE ");
        for (String key : bmd.getPrimaryKeys()) {
            buf.append(key);
            buf.append(" = ? AND ");
        }
        buf.setLength(buf.length() - 5);
        if (bmd.hasVersionNoPropertyType()) {
            PropertyType pt = bmd.getVersionNoPropertyType();
            buf.append(" AND ");
            buf.append(pt.getColumnName());
            buf.append(" = ?");
        }
        if (bmd.hasTimestampPropertyType()) {
            PropertyType pt = bmd.getTimestampPropertyType();
            buf.append(" AND ");
            buf.append(pt.getColumnName());
            buf.append(" = ?");
        }

        return buf.toString();
    }

    /**
     * @return Returns the beanMetaData.
     */
    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    /**
     * @param beanMetaData
     *            The beanMetaData to set.
     */
    public void setBeanMetaData(BeanMetaData beanMetaData) {
        this.beanMetaData = beanMetaData;
    }

    /**
     * @return Returns the propertyNames.
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * @param propertyNames
     *            The propertyNames to set.
     */
    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }

    public boolean isCheckSingleRowUpdate() {
        return checkSingleRowUpdate;
    }

    public void setCheckSingleRowUpdate(boolean resultCheck) {
        this.checkSingleRowUpdate = resultCheck;
    }

}
