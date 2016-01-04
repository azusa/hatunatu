/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.dao.types;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.dao.util.BindVariableUtil;
import jp.fieldnotes.hatunatu.dao.util.SqlDateConversionUtil;

/**
 * java.sql.Date用の {@link ValueType}です。
 * 
 * @author higa
 * 
 */
public class SqlDateType extends AbstractValueType {

    /**
     * インスタンスを構築します。
     */
    public SqlDateType() {
        super(Types.DATE);
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return resultSet.getDate(index);
    }

    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {
        return resultSet.getDate(columnName);
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return cs.getDate(index);
    }

    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {
        return cs.getDate(parameterName);
    }

    public void bindValue(PreparedStatement ps, int index, Object value)
            throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setDate(index, toSqlDate(value));
        }
    }

    public void bindValue(CallableStatement cs, String parameterName,
            Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setDate(parameterName, toSqlDate(value));
        }
    }

    /**
     * {@link java.sql.Date}
     * 
     * @param value
     *            値
     * @return {@link java.sql.Date}
     */
    protected java.sql.Date toSqlDate(Object value) {
        return SqlDateConversionUtil.toDate(value);
    }

    public String toText(Object value) {
        if (value == null) {
            return BindVariableUtil.nullText();
        }
        return BindVariableUtil.toText(toSqlDate(value));
    }
}