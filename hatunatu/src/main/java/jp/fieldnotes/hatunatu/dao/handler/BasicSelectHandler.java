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

import jp.fieldnotes.hatunatu.dao.ResultSetFactory;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.SelectHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.BasicResultSetFactory;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.sql.StatementUtil;

import javax.sql.DataSource;
import java.sql.*;

public class BasicSelectHandler extends BasicHandler implements SelectHandler {

    private ResultSetFactory resultSetFactory = BasicResultSetFactory.INSTANCE;

    private ResultSetHandler resultSetHandler;

    private int fetchSize = 100;

    private int maxRows = -1;

    /**
     * {@link BasicSelectHandler}を作成します。
     */
    public BasicSelectHandler() {
    }

    /**
     * {@link BasicSelectHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param resultSetHandler
     *            結果セットハンドラ
     */
    public BasicSelectHandler(DataSource dataSource,
                              ResultSetHandler resultSetHandler) {

        this(dataSource, resultSetHandler, StatementFactory.INSTANCE,
                BasicResultSetFactory.INSTANCE);
    }

    /**
     * {@link BasicSelectHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param resultSetHandler
     *            結果セットハンドラ
     * @param statementFactory
     *            ステートメントファクトリ
     * @param resultSetFactory
     *            結果セットファクトリ
     */
    public BasicSelectHandler(DataSource dataSource,
                              ResultSetHandler resultSetHandler,
                              StatementFactory statementFactory, ResultSetFactory resultSetFactory) {

        setDataSource(dataSource);
        setResultSetHandler(resultSetHandler);
        setStatementFactory(statementFactory);
        setResultSetFactory(resultSetFactory);
    }

    /**
     * 結果セットファクトリを返します。
     * 
     * @return 結果セットファクトリ
     */
    public ResultSetFactory getResultSetFactory() {
        return resultSetFactory;
    }

    /**
     * 結果セットファクトリを設定します。
     * 
     * @param resultSetFactory
     *            結果セットファクトリ
     */
    public void setResultSetFactory(ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
    }

    /**
     * 結果セットハンドラを返します。
     * 
     * @return 結果セットハンドラ
     */
    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    /**
     * 結果セットハンドラを設定します。
     * 
     * @param resultSetHandler
     *            結果セットハンドラ
     */
    public void setResultSetHandler(ResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
    }

    /**
     * フェッチ数を返します。
     * 
     * @return フェッチ数
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * フェッチ数を設定します。
     * 
     * @param fetchSize
     *            フェッチ数
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * 最大行数を返します。
     * 
     * @return 最大行数
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * 最大行数を設定します。
     * 
     * @param maxRows
     *            最大行数
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    @Override
    public Object execute(QueryObject queryObject)
            throws Exception {
        try (Connection con = getConnection()) {
            return execute(con, queryObject);
        }
    }

    protected Object execute(Connection connection, QueryObject queryObject)
            throws Exception {
        logSql(queryObject);
        try (PreparedStatement ps = prepareStatement(connection, queryObject)) {
            bindArgs(ps, queryObject.getBindArguments(), queryObject.getBindTypes());
            return execute(ps, queryObject);
        }
    }

    /**
     * 引数のセットアップを行ないます。
     * 
     * @param con
     *            コネクション
     * @param args
     *            引数
     * @return セットアップ後の引数
     */
    protected Object[] setup(Connection con, Object[] args) {
        return args;
    }

    @Override
    protected PreparedStatement prepareStatement(Connection connection, QueryObject queryObject) {
        PreparedStatement ps = super.prepareStatement(connection, queryObject);
        if (fetchSize > -1) {
            StatementUtil.setFetchSize(ps, fetchSize);
        }
        if (maxRows > -1) {
            StatementUtil.setMaxRows(ps, maxRows);
        }
        return ps;
    }

    protected Object execute(PreparedStatement ps, QueryObject queryObject) throws SQLException {
        if (resultSetHandler == null) {
            throw new EmptyRuntimeException("resultSetHandler");
        }
        try (ResultSet resultSet = createResultSet(ps, queryObject.getMethodArguments())) {
            return resultSetHandler.handle(resultSet, queryObject);
        }
    }

    /**
     * データベースメタデータによるセットアップを行ないます。
     * 
     * @param dbMetaData
     *            データベースメタデータ
     */
    protected void setupDatabaseMetaData(DatabaseMetaData dbMetaData) {
    }

    /**
     * 結果セットを作成します。
     * 
     * @param ps
     *            準備されたステートメント
     * @return 結果セット
     */
    protected ResultSet createResultSet(PreparedStatement ps, Object[] methodArgument) {
        return resultSetFactory.createResultSet(ps, methodArgument);
    }
}