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
package jp.fieldnotes.hatunatu.dao.id;

import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.dao.handler.BasicSelectHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;

public abstract class AbstractIdentifierGenerator implements
        IdentifierGenerator {

    private static final Object[] EMPTY_ARGS = new Object[0];

    protected ResultSetHandler resultSetHandler;

    protected PropertyType propertyType;

    protected Dbms dbms;

    public AbstractIdentifierGenerator(PropertyType propertyType, Dbms dbms) {
        this.propertyType = propertyType;
        this.dbms = dbms;
        resultSetHandler = new IdentifierResultSetHandler(propertyType
                .getValueType());
    }

    @Override
    public String getPropertyName() {
        return propertyType.getPropertyName();
    }

    public Dbms getDbms() {
        return dbms;
    }

    protected Object executeSql(DataSource ds, String sql, Object[] args) throws Exception {
        BasicSelectHandler handler = new BasicSelectHandler(ds,
                resultSetHandler);
        // [DAO-139]
        handler.setFetchSize(-1);
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        return handler.execute(queryObject);
    }

    protected void setIdentifier(Object bean, Object value) {
        if (propertyType == null) {
            throw new EmptyRuntimeException("propertyType");
        }
        PropertyDesc pd = propertyType.getPropertyDesc();
        pd.setValue(bean, value);
    }
}