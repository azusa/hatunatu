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

import jp.fieldnotes.hatunatu.dao.util.DateConversionUtil;
import jp.fieldnotes.hatunatu.dao.util.TimestampConversionUtil;
import jp.fieldnotes.hatunatu.util.exception.ParseRuntimeException;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimestampType extends TimestampType {

    @Override
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return toDate(super.getValue(resultSet, index));
    }

    @Override
    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {
        return toDate(super.getValue(resultSet, columnName));
    }

    @Override
    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toDate(super.getValue(cs, index));
    }

    @Override
    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {
        return toDate(super.getValue(cs, parameterName));
    }

    /**
     * {@link Date}に変換します。
     * 
     * @param value
     *            値
     * @return {@link Date}
     */
    protected Date toDate(Object value) {
        if (value instanceof Date || value instanceof Calendar) {
            return DateConversionUtil.toDate(value);
        }
        try {
            return DateConversionUtil.toDate(value, TimestampConversionUtil
                    .getPattern(Locale.getDefault()));
        } catch (ParseRuntimeException e) {
            return DateConversionUtil.toDate(value);
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
        return new Timestamp(toDate(value).getTime());
    }

}
