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

import jp.fieldnotes.hatunatu.dao.ReturningRowsBatchHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.impl.BasicStatementFactory;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BasicReturningRowsBatchHandler extends BasicHandler implements
        ReturningRowsBatchHandler {

    private static final int[] EMPTY_ARRAY = new int[0];

    private int batchSize = -1;

    /**
     * {@link BasicReturningRowsBatchHandler}を作成します。
     */
    public BasicReturningRowsBatchHandler() {
    }

    /**
     * {@link BasicReturningRowsBatchHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     */
    public BasicReturningRowsBatchHandler(final DataSource dataSource
    ) {
        this(dataSource, -1);
    }

    /**
     * {@link BasicReturningRowsBatchHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param batchSize
     *            バッチ数
     */
    public BasicReturningRowsBatchHandler(final DataSource dataSource,
                                          final int batchSize) {
        this(dataSource, batchSize, BasicStatementFactory.INSTANCE);
    }

    /**
     * {@link BasicReturningRowsBatchHandler}を作成します。
     * 
     * @param dataSource
     *            データソース
     * @param batchSize
     *            バッチ数
     * @param statementFactory
     *            ステートメントファクトリ
     */
    public BasicReturningRowsBatchHandler(final DataSource dataSource,
                                          final int batchSize,
                                          final StatementFactory statementFactory) {

        setDataSource(dataSource);
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
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public int[] execute(final QueryObject queryObject, final List<Object[]> list) throws Exception {
        if (list.size() == 0) {
            return EMPTY_ARRAY;
        }
        final Object[] args = list.get(0);
        return execute(queryObject, list, getArgTypes(args));
    }

    protected int[] execute(final QueryObject queryObject, final List<Object[]> list, final Class[] argTypes) throws SQLException {
        try (Connection connection = getConnection()) {
            return execute(connection, queryObject, list, argTypes);
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
     * @return バッチ内のコマンドごとに 1 つの要素が格納されている更新カウントの配列。
     *         配列の要素はコマンドがバッチに追加された順序で並べられる
     */
    protected int[] execute(final Connection connection, final QueryObject queryObject, final List<Object[]> list,
                            final Class[] argTypes) throws SQLException {
        final int size = batchSize > 0 ? Math.min(batchSize, list.size())
                : list.size();
        if (size == 0) {
            return EMPTY_ARRAY;
        }
        try (PreparedStatement ps = prepareStatement(connection, queryObject)) {
            for (int i = 0; i < list.size(); ++i) {
                final Object[] args = list.get(i);
                logSql(queryObject);
                bindArgs(ps, args, argTypes);
                PreparedStatementUtil.addBatch(ps);
            }
            return PreparedStatementUtil.executeBatch(ps);
        }
    }

}