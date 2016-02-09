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
import jp.fieldnotes.hatunatu.util.io.InputStreamUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;

public class BinaryStreamType extends AbstractValueType {

    /**
     * インスタンスを構築します。
     */
    public BinaryStreamType() {
        super(Types.BINARY);
    }

    @Override
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return resultSet.getBinaryStream(index);
    }

    @Override
    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {

        return resultSet.getBinaryStream(columnName);
    }

    @Override
    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toBinaryStream(cs.getBytes(index));
    }

    @Override
    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {

        return toBinaryStream(cs.getBytes(parameterName));
    }

    private InputStream toBinaryStream(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void bindValue(PreparedStatement ps, int index, Object value)
            throws SQLException {

        if (value == null) {
            setNull(ps, index);
        } else if (value instanceof InputStream) {
            InputStream is = (InputStream) value;
            ps.setBinaryStream(index, is, InputStreamUtil.available(is));
        } else {
            ps.setObject(index, value);
        }
    }

    @Override
    public void bindValue(CallableStatement cs, String parameterName,
            Object value) throws SQLException {

        if (value == null) {
            setNull(cs, parameterName);
        } else if (value instanceof InputStream) {
            InputStream is = (InputStream) value;
            cs
                    .setBinaryStream(parameterName, is, InputStreamUtil
                            .available(is));
        } else {
            cs.setObject(parameterName, value);
        }
    }

    @Override
    public String toText(Object value) {
        if (value == null) {
            return BindVariableUtil.nullText();
        }
        return BindVariableUtil.toText(value);
    }
}