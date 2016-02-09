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
import jp.fieldnotes.hatunatu.util.convert.BigDecimalConversionUtil;
import jp.fieldnotes.hatunatu.util.convert.BigIntegerConversionUtil;

import java.math.BigDecimal;
import java.sql.*;

public class BigIntegerType extends AbstractValueType {

    /**
     * インスタンスを構築します。
     */
    public BigIntegerType() {
        super(Types.BIGINT);
    }

    @Override
    public Object getValue(final ResultSet resultSet, final int index)
            throws SQLException {
        return BigIntegerConversionUtil.toBigInteger(resultSet
                .getBigDecimal(index));
    }

    @Override
    public Object getValue(final ResultSet resultSet, final String columnName)
            throws SQLException {
        return BigIntegerConversionUtil.toBigInteger(resultSet
                .getBigDecimal(columnName));
    }

    @Override
    public Object getValue(final CallableStatement cs, final int index)
            throws SQLException {
        return BigIntegerConversionUtil.toBigInteger(cs.getBigDecimal(index));
    }

    @Override
    public Object getValue(final CallableStatement cs,
            final String parameterName) throws SQLException {
        return BigIntegerConversionUtil.toBigInteger(cs
                .getBigDecimal(parameterName));
    }

    @Override
    public void bindValue(final PreparedStatement ps, final int index,
            final Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setBigDecimal(index, BigDecimalConversionUtil
                    .toBigDecimal(value));
        }
    }

    @Override
    public void bindValue(final CallableStatement cs,
            final String parameterName, final Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setBigDecimal(parameterName, BigDecimalConversionUtil
                    .toBigDecimal(value));
        }
    }

    @Override
    public String toText(Object value) {
        if (value == null) {
            return BindVariableUtil.nullText();
        }
        BigDecimal var = BigDecimalConversionUtil.toBigDecimal(value);
        return BindVariableUtil.toText(var);
    }

}
