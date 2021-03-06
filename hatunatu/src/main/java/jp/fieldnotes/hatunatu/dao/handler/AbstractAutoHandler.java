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
package jp.fieldnotes.hatunatu.dao.handler;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.UpdateHandler;
import jp.fieldnotes.hatunatu.dao.exception.NotSingleRowUpdatedRuntimeException;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.convert.IntegerConversionUtil;
import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.log.Logger;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractAutoHandler extends BasicHandler implements
        UpdateHandler {

    private BeanMetaData beanMetaData;

    private Timestamp timestamp;

    private Integer versionNo;

    private PropertyType[] propertyTypes;

    private boolean checkSingleRowUpdate;

    public AbstractAutoHandler(DataSource dataSource,
                               StatementFactory statementFactory, BeanMetaData beanMetaData,
                               PropertyType[] propertyTypes, boolean checkSingleRowUpdate) {

        setDataSource(dataSource);
        setStatementFactory(statementFactory);
        this.beanMetaData = beanMetaData;
        this.propertyTypes = propertyTypes;
        this.checkSingleRowUpdate = checkSingleRowUpdate;
    }

    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    protected Logger getLogger() {
        return Logger.getLogger(loggerClass);
    }

    protected Timestamp getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    protected Integer getVersionNo() {
        return versionNo;
    }

    protected void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    protected PropertyType[] getPropertyTypes() {
        return propertyTypes;
    }

    protected void setPropertyTypes(PropertyType[] propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    @Override
    public int execute(QueryObject queryObject) throws Exception {
        try (Connection connection = getConnection()) {
            return execute(connection, queryObject);
        }
    }

    protected int execute(Connection connection, QueryObject queryObject) throws Exception {
        Object bean = queryObject.getMethodArguments()[0];
        preUpdateBean(bean);
        setupBindVariables(bean, queryObject);
        logSql(queryObject);
        int ret = -1;
        try (PreparedStatement ps = prepareStatement(connection, queryObject)) {
            bindArgs(ps, queryObject.getBindArguments(), queryObject.getBindVariableValueTypes());
            ret = PreparedStatementUtil.executeUpdate(ps);
        }
        if (checkSingleRowUpdate && ret != 1) {
            throw new NotSingleRowUpdatedRuntimeException(bean, ret);
        }
        postUpdateBean(bean);
        return ret;
    }

    protected void bindArgs(PreparedStatement ps, Object[] args,
            ValueType[] valueTypes) {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            ValueType valueType = valueTypes[i];
            try {
                valueType.bindValue(ps, i + 1, args[i]);
            } catch (SQLException ex) {
                throw new SQLRuntimeException(ex);
            }
        }
    }

    protected void preUpdateBean(Object bean) throws Exception {
    }

    protected void postUpdateBean(Object bean) throws Exception {
    }

    protected abstract void setupBindVariables(Object bean, QueryObject queryObject);

    protected void setupInsertBindVariables(Object bean, QueryObject queryObject) {
        List<Object> varList = new ArrayList<>();
        List<ValueType> varValueTypeList = new ArrayList<>();
        final BeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(new Timestamp(new Date().getTime()));
                varList.add(getTimestamp());
            } else if (pt.getPropertyName().equals(versionNoPropertyName)) {
                setVersionNo(new Integer(0));
                varList.add(getVersionNo());
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        queryObject.setBindArguments(varList.toArray());
        queryObject.setBindVariableValueTypes(varValueTypeList
                .toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupUpdateBindVariables(Object bean, QueryObject queryObject) {
        List<Object> varList = new ArrayList<>();
        List<ValueType> varValueTypeList = new ArrayList<>();
        final BeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(new Timestamp(new Date().getTime()));
                varList.add(getTimestamp());
            } else if (pt.getPropertyName().equals(versionNoPropertyName)) {
                Object value = pt.getPropertyDesc().getValue(bean);
                int intValue = IntegerConversionUtil.toPrimitiveInt(value) + 1;
                setVersionNo(new Integer(intValue));
                varList.add(getVersionNo());
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        queryObject.setBindArguments(varList.toArray());
        queryObject.setBindVariableValueTypes(varValueTypeList
                .toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupDeleteBindVariables(Object bean, QueryObject queryObject) {
        List<Object> varList = new ArrayList<>();
        List<ValueType> varValueTypeList = new ArrayList<>();
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        queryObject.setBindArguments(varList.toArray());
        queryObject.setBindVariableValueTypes(varValueTypeList
                .toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void addAutoUpdateWhereBindVariables(List varList,
                                                   List<ValueType> varValueTypeList, Object bean) {
        BeanMetaData bmd = getBeanMetaData();
        for (String key : bmd.getPrimaryKeys()) {
            PropertyType pt = bmd.getPropertyTypeByColumnName(key);
            PropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (bmd.hasVersionNoPropertyType()) {
            PropertyType pt = bmd.getVersionNoPropertyType();
            PropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (bmd.hasTimestampPropertyType()) {
            PropertyType pt = bmd.getTimestampPropertyType();
            PropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
    }

    protected void updateTimestampIfNeed(Object bean) {
        if (getTimestamp() != null) {
            PropertyDesc pd = getBeanMetaData().getTimestampPropertyType()
                    .getPropertyDesc();
            pd.setValue(bean, getTimestamp());
        }
    }

    protected void updateVersionNoIfNeed(Object bean) {
        if (getVersionNo() != null) {
            PropertyDesc pd = getBeanMetaData().getVersionNoPropertyType()
                    .getPropertyDesc();
            pd.setValue(bean, getVersionNo());
        }
    }

}