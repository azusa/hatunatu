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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.dao.ResultSetFactory;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;
import jp.fieldnotes.hatunatu.util.sql.StatementUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BasicResultSetFactory implements ResultSetFactory {

    /**
     * 自分自身のインスタンスです。
     */
    public static final ResultSetFactory INSTANCE = new BasicResultSetFactory();

    @Override
    public ResultSet getResultSet(Statement statement) {
        return StatementUtil.getResultSet(statement);
    }

    @Override
    public ResultSet createResultSet(PreparedStatement ps, Object[] methodArgument) {
        return PreparedStatementUtil.executeQuery(ps);
    }

}
