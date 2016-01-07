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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import jp.fieldnotes.hatunatu.dao.BatchHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.impl.BasicStatementFactory;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;
import jp.fieldnotes.hatunatu.util.sql.StatementUtil;

/**
 * @deprecated  use {@link BasicReturningRowsBatchHandler}
 */
public class BasicBatchHandler extends BasicHandler implements BatchHandler {

    private int batchSize = -1;

    /**
     * {@link BasicBatchHandler}を作成します。
     */
    public BasicBatchHandler() {
    }

    /**
     * {@link BasicBatchHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param sql
     *            SQL
     */
    public BasicBatchHandler(DataSource dataSource, String sql) {
        this(dataSource, sql, -1);
    }

    /**
     * {@link BasicBatchHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param sql
     *            SQL
     * @param batchSize
     *            バッチ数
     */
    public BasicBatchHandler(DataSource dataSource, String sql, int batchSize) {
        this(dataSource, sql, batchSize, BasicStatementFactory.INSTANCE);
    }

    /**
     * {@link BasicBatchHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param sql
     *            SQL
     * @param batchSize
     *            バッチ数
     * @param statementFactory
     *            ステートメントファクトリ
     */
    public BasicBatchHandler(DataSource dataSource, String sql, int batchSize,
            StatementFactory statementFactory) {

        setDataSource(dataSource);
        setSql(sql);
        setBatchSize(batchSize);
        setStatementFactory(statementFactory);
    }

    /**
     * バッチ数を返します。
     * 
     * @return バッチ数
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * バッチ数を設定します。
     * 
     * @param batchSize
     *            バッチ数
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int execute(List list) throws SQLRuntimeException {
        if (list.size() == 0) {
            return 0;
        }
        Object[] args = (Object[]) list.get(0);
        return execute(list, getArgTypes(args));
    }

    public int execute(List list, Class[] argTypes) throws SQLRuntimeException {
        Connection connection = getConnection();
        try {
            return execute(connection, list, argTypes);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    /**
     * 更新を実行します。
     * 
     * @param connection
     *            コネクション
     * @param list
     *            バッチ対象のデータ
     * @param argTypes
     *            引数の型のリスト
     * @return 対象のデータの行数
     */
    protected int execute(Connection connection, List list, Class[] argTypes) {
        PreparedStatement ps = prepareStatement(connection);
        int size = batchSize > 0 ? batchSize : list.size();
        try {
            for (int i = 0, j = 0; i < list.size(); ++i) {
                Object[] args = (Object[]) list.get(i);
                logSql(args, argTypes);
                bindArgs(ps, args, argTypes);
                PreparedStatementUtil.addBatch(ps);
                if (j == size - 1 || i == list.size() - 1) {
                    PreparedStatementUtil.executeBatch(ps);
                    j = 0;
                } else {
                    ++j;
                }
            }
            return list.size();
        } finally {
            StatementUtil.close(ps);
        }
    }
}