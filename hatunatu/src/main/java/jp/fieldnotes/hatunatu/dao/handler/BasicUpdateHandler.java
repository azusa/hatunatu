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
package jp.fieldnotes.hatunatu.dao.handler;

import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.UpdateHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BasicUpdateHandler extends BasicHandler implements UpdateHandler {

    /**
     * {@link BasicUpdateHandler}を作成します。
     */
    public BasicUpdateHandler() {
    }


    /**
     * {@link BasicUpdateHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param statementFactory
     *            ステートメントファクトリ
     */
    public BasicUpdateHandler(DataSource dataSource,
                              StatementFactory statementFactory) {

        super(dataSource, statementFactory);
    }

    @Override
    public int execute(QueryObject queryObject) throws Exception {
        return execute(queryObject, queryObject.getBindArguments(), getArgTypes(queryObject.getBindTypes()));
    }

    protected int execute(QueryObject queryObject, Object[] args, Class[] argTypes)
            throws SQLException {
        try (Connection connection = getConnection()) {
            return execute(connection, queryObject, args, argTypes);
        }
    }

    /**
     * SQL文を実行します。
     * 
     * @param connection
     *            コネクション
     * @param args
     *            引数
     * @param argTypes
     *            引数の型
     * @return 更新した行数
     */
    protected int execute(Connection connection, QueryObject queryObject, Object[] args, Class[] argTypes) throws SQLException {
        logSql(queryObject);
        try (PreparedStatement ps = prepareStatement(connection, queryObject)) {
            bindArgs(ps, args, argTypes);
            return PreparedStatementUtil.executeUpdate(ps);
        }
    }
}