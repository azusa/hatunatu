/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.util.sql;

import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.log.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static jp.fieldnotes.hatunatu.util.misc.AssertionUtil.assertArgumentNotEmpty;
import static jp.fieldnotes.hatunatu.util.misc.AssertionUtil.assertArgumentNotNull;


/**
 * {@link Statement}用のユーティリティクラスです。
 * 
 * @author higa
 */
public abstract class StatementUtil {

    private static final Logger logger = Logger.getLogger(StatementUtil.class);

    /**
     * SQLを実行します。
     * 
     * @param statement
     *            {@link Statement}。{@literal null}であってはいけません
     * @param sql
     *            SQL文字列。{@literal null}や空文字列であってはいけません
     * @return 実行した結果
     * @see Statement#execute(String)
     */
    public static boolean execute(final Statement statement, final String sql) {
        assertArgumentNotNull("statement", statement);
        assertArgumentNotEmpty("sql", sql);

        try {
            return statement.execute(sql);
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * フェッチサイズを設定します。
     * 
     * @param statement
     *            {@link Statement}。{@literal null}であってはいけません
     * @param fetchSize
     *            フェッチサイズ
     * @see Statement#setFetchSize(int)
     */
    public static void setFetchSize(final Statement statement,
            final int fetchSize) {
        assertArgumentNotNull("statement", statement);

        try {
            statement.setFetchSize(fetchSize);
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * 最大行数を設定します。
     * 
     * @param statement
     *            {@link Statement}。{@literal null}であってはいけません
     * @param maxRows
     *            最大の行数
     * @see Statement#setMaxRows(int)
     */
    public static void setMaxRows(final Statement statement, final int maxRows) {
        assertArgumentNotNull("statement", statement);

        try {
            statement.setMaxRows(maxRows);
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * クエリタイムアウトを設定します。
     * 
     * @param statement
     *            {@link Statement}。{@literal null}であってはいけません
     * @param queryTimeout
     *            クエリタイムアウト
     * @see Statement#setQueryTimeout(int)
     */
    public static void setQueryTimeout(final Statement statement,
            final int queryTimeout) {
        assertArgumentNotNull("statement", statement);

        try {
            statement.setQueryTimeout(queryTimeout);
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * 結果セットを返します。
     * 
     * @param statement
     *            {@link Statement}。{@literal null}であってはいけません
     * @return 結果セット
     */
    public static ResultSet getResultSet(final Statement statement) {
        assertArgumentNotNull("statement", statement);

        try {
            return statement.getResultSet();
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

    /**
     * 更新カウントを返します。
     * 
     * @param statement
     *            {@link Statement}。{@literal null}であってはいけません
     * @return 更新カウント
     */
    public static int getUpdateCount(final Statement statement) {
        assertArgumentNotNull("statement", statement);

        try {
            return statement.getUpdateCount();
        } catch (final SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }

}
