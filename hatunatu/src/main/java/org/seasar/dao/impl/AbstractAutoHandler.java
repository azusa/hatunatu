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
package org.seasar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.NotSingleRowUpdatedRuntimeException;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.extension.jdbc.UpdateHandler;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.extension.jdbc.impl.BasicHandler;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.IntegerConversionUtil;
import org.seasar.framework.util.PreparedStatementUtil;
import org.seasar.framework.util.StatementUtil;

/**
 * @author higa
 * @author manhole
 */
public abstract class AbstractAutoHandler extends BasicHandler implements
        UpdateHandler {

    private BeanMetaData beanMetaData;

    private Object[] bindVariables;

    private ValueType[] bindVariableValueTypes;

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

    protected Object[] getBindVariables() {
        return bindVariables;
    }

    protected void setBindVariables(Object[] bindVariables) {
        this.bindVariables = bindVariables;
    }

    protected ValueType[] getBindVariableValueTypes() {
        return bindVariableValueTypes;
    }

    protected void setBindVariableValueTypes(ValueType[] bindVariableValueTypes) {
        this.bindVariableValueTypes = bindVariableValueTypes;
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

    public int execute(Object[] args) throws SQLRuntimeException {
        Connection connection = getConnection();
        try {
            return execute(connection, args[0]);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    public int execute(Object[] args, Class[] argTypes)
            throws SQLRuntimeException {
        return execute(args);
    }

    protected int execute(Connection connection, Object bean) {
        preUpdateBean(bean);
        setupBindVariables(bean);
        logSql(bindVariables, getArgTypes(bindVariables));
        PreparedStatement ps = prepareStatement(connection);
        int ret = -1;
        try {
            bindArgs(ps, bindVariables, bindVariableValueTypes);
            ret = PreparedStatementUtil.executeUpdate(ps);
        } finally {
            StatementUtil.close(ps);
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

    protected void preUpdateBean(Object bean) {
    }

    protected void postUpdateBean(Object bean) {
    }

    protected abstract void setupBindVariables(Object bean);

    protected void setupInsertBindVariables(Object bean) {
        List varList = new ArrayList();
        List varValueTypeList = new ArrayList();
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
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList
                .toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupUpdateBindVariables(Object bean) {
        List varList = new ArrayList();
        List varValueTypeList = new ArrayList();
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
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList
                .toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupDeleteBindVariables(Object bean) {
        List varList = new ArrayList();
        List varValueTypeList = new ArrayList();
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList
                .toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void addAutoUpdateWhereBindVariables(List varList,
            List varValueTypeList, Object bean) {
        BeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            PropertyType pt = bmd.getPropertyTypeByColumnName(bmd
                    .getPrimaryKey(i));
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