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
import jp.fieldnotes.hatunatu.util.convert.BooleanConversionUtil;

import java.sql.*;

public class BooleanIntegerType extends AbstractValueType {

    /**
     * インスタンスを構築します。
     */
    public BooleanIntegerType() {
        super(Types.INTEGER);
    }

    @Override
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return BooleanConversionUtil.toBoolean(resultSet.getObject(index));
    }

    @Override
    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {

        return BooleanConversionUtil.toBoolean(resultSet.getObject(columnName));
    }

    @Override
    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return BooleanConversionUtil.toBoolean(cs.getObject(index));
    }

    @Override
    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {

        return BooleanConversionUtil.toBoolean(cs.getObject(parameterName));
    }

    @Override
    public void bindValue(PreparedStatement ps, int index, Object value)
            throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setInt(index, toInt(value));
        }
    }

    @Override
    public void bindValue(CallableStatement cs, String parameterName,
            Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setInt(parameterName, toInt(value));
        }
    }

    @Override
    public String toText(Object value) {
        if (value == null) {
            return BindVariableUtil.nullText();
        }
        int var = toInt(value);
        return BindVariableUtil.toText(new Integer(var));
    }

    /**
     * <code>int</code>に変換します。
     * 
     * @param value
     *            値
     * @return <code>int</code>
     */
    protected int toInt(Object value) {
        return BooleanConversionUtil.toPrimitiveBoolean(value) ? 1 : 0;
    }
}