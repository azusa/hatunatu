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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleResultSetType extends AbstractValueType {

    /**
     * カーソル用のタイプです。
     */
    protected static int CURSOR = -10;

    /**
     * インスタンスを構築します。
     */
    public OracleResultSetType() {
        super(CURSOR);
    }

    @Override
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return cs.getObject(index);
    }

    @Override
    public Object getValue(CallableStatement cs, String parameterName)
            throws SQLException {
        return cs.getObject(parameterName);
    }

    @Override
    public void bindValue(PreparedStatement ps, int index, Object value)
            throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public void bindValue(CallableStatement cs, String parameterName,
            Object value) throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public String toText(Object value) {
        return BindVariableUtil.nullText();
    }

}