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

import jp.fieldnotes.hatunatu.util.convert.CalendarConversionUtil;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class CalendarTimestampType extends TimestampType {

    @Override
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return toCalendar(super.getValue(resultSet, index));
    }

    @Override
    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {
        return toCalendar(super.getValue(resultSet, columnName));
    }

    @Override
    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toCalendar(super.getValue(cs, index));
    }

    @Override
    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {
        return toCalendar(super.getValue(cs, parameterName));
    }

    /**
     * {@link Calendar}に変換します。
     * 
     * @param value
     *            値
     * @return {@link Calendar}
     */
    protected Calendar toCalendar(Object value) {
        return CalendarConversionUtil.toCalendar(value);
    }
}
