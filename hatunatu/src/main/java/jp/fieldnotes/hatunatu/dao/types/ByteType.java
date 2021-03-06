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
import jp.fieldnotes.hatunatu.util.convert.ByteConversionUtil;

import java.sql.*;

public class ByteType extends AbstractValueType {

    /**
     * インスタンスを構築します。
     */
    public ByteType() {
        super(Types.SMALLINT);
    }

    @Override
    public Object getValue(final ResultSet resultSet, final int index)
            throws SQLException {
        return ByteConversionUtil.toByte(resultSet.getObject(index));
    }

    @Override
    public Object getValue(final ResultSet resultSet, final String columnName)
            throws SQLException {
        return ByteConversionUtil.toByte(resultSet.getObject(columnName));
    }

    @Override
    public Object getValue(final CallableStatement cs, final int index)
            throws SQLException {
        return ByteConversionUtil.toByte(cs.getObject(index));
    }

    @Override
    public Object getValue(final CallableStatement cs,
            final String parameterName) throws SQLException {
        return ByteConversionUtil.toByte(cs.getObject(parameterName));
    }

    @Override
    public void bindValue(final PreparedStatement ps, final int index,
            final Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setByte(index, ByteConversionUtil.toPrimitiveByte(value));
        }
    }

    @Override
    public void bindValue(final CallableStatement cs,
            final String parameterName, final Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs
                    .setByte(parameterName, ByteConversionUtil
                            .toPrimitiveByte(value));
        }
    }

    @Override
    public String toText(Object value) {
        if (value == null) {
            return BindVariableUtil.nullText();
        }
        Byte var = ByteConversionUtil.toByte(value);
        return BindVariableUtil.toText(var);
    }
}
