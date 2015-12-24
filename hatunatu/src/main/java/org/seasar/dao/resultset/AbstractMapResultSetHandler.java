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
package org.seasar.dao.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.dao.PropertyType;
import org.seasar.dao.ResultSetHandler;
import org.seasar.dao.ValueType;
import org.seasar.dao.impl.PropertyTypeImpl;
import org.seasar.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.util.collection.CaseInsensitiveMap;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;

public abstract class AbstractMapResultSetHandler implements ResultSetHandler {

    public AbstractMapResultSetHandler() {
    }

    protected Map createRow(ResultSet rs, PropertyType[] propertyTypes)
            throws SQLException {

        Map row = new CaseInsensitiveMap();
        for (int i = 0; i < propertyTypes.length; ++i) {
            Object value = propertyTypes[i].getValueType().getValue(rs, i + 1);
            row.put(propertyTypes[i].getPropertyName(), value);
        }
        return row;
    }

    protected PropertyType[] createPropertyTypes(ResultSetMetaData rsmd)
            throws SQLException {

        int count = rsmd.getColumnCount();
        PropertyType[] propertyTypes = new PropertyType[count];
        for (int i = 0; i < count; ++i) {
            String propertyName = StringUtil.replace(
                    rsmd.getColumnLabel(i + 1), "_", "");
            ValueType valueType = ValueTypes.getValueType(rsmd
                    .getColumnType(i + 1));
            propertyTypes[i] = new PropertyTypeImpl(propertyName, valueType);
        }
        return propertyTypes;
    }
}