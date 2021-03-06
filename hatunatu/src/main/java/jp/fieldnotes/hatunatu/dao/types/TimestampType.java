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

import jp.fieldnotes.hatunatu.dao.util.BindVariableUtil;
import jp.fieldnotes.hatunatu.dao.util.DateConversionUtil;
import jp.fieldnotes.hatunatu.dao.util.TimestampConversionUtil;
import jp.fieldnotes.hatunatu.util.exception.ParseRuntimeException;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimestampType extends AbstractValueType {

    /**
     * インスタンスを構築します。
     */
    public TimestampType() {
        super(Types.TIMESTAMP);
    }

    @Override
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return resultSet.getTimestamp(index);
    }

    @Override
    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {
        return resultSet.getTimestamp(columnName);
    }

    @Override
    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return cs.getTimestamp(index);
    }

    @Override
    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {
        return cs.getTimestamp(parameterName);
    }

    @Override
    public void bindValue(PreparedStatement ps, int index, Object value)
            throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setTimestamp(index, toTimestamp(value));
        }
    }

    @Override
    public void bindValue(CallableStatement cs, String parameterName,
            Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setTimestamp(parameterName, toTimestamp(value));
        }
    }

    /**
     * {@link Timestamp}に変換します。
     * 
     * @param value
     *            値
     * @return {@link Timestamp}
     */
    protected Timestamp toTimestamp(Object value) {
        if (value instanceof Date || value instanceof Calendar) {
            return TimestampConversionUtil.toTimestamp(value);
        }
        try {
        return TimestampConversionUtil.toTimestamp(value,
                TimestampConversionUtil.getPattern(Locale.getDefault()));
        } catch (ParseRuntimeException e) {
            return TimestampConversionUtil.toTimestamp(value,
                    DateConversionUtil.getPattern(Locale.getDefault()));
        }
    }

    @Override
    public String toText(Object value) {
        if (value == null) {
            return BindVariableUtil.nullText();
        }
        return BindVariableUtil.toText(toTimestamp(value));
    }
}