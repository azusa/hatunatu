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

import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.util.sql.StatementUtil;

import java.sql.*;

public class StatementFactoryImpl implements StatementFactory {

    /**
     * フェッチサイズです。
     */
    private Integer fetchSize;

    /**
     * 最大行数です。
     */
    private Integer maxRows;

    /**
     * クエリのタイムアウトです。
     */
    private Integer queryTimeout;

    private boolean booleanToInt = false;

    /**
     * Constructor.
     */
    public StatementFactoryImpl() {
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con, QueryObject queryObject) {
        PreparedStatement pstmt = null;
        if (PagerContext.isPagerCondition(queryObject.getMethodArguments())) {
            try {
                pstmt = con.prepareStatement(queryObject.getSql(),
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
            return createPreparedStatement(pstmt, queryObject.getSql());
        }
        pstmt = ConnectionUtil.prepareStatement(con, queryObject.getSql());
        return createPreparedStatement(pstmt, queryObject.getSql());
    }

    @Override
    public CallableStatement createCallableStatement(Connection con, String sql) {
        CallableStatement pstmt = ConnectionUtil.prepareCall(con, sql);
        configurePreparedStatement(pstmt);
        return pstmt;
    }

    private PreparedStatement createPreparedStatement(PreparedStatement pstmt,
                                                      String sql) {
        configurePreparedStatement(pstmt);
        if (booleanToInt) {
            return new BooleanToIntPreparedStatement(pstmt, sql);
        } else {
            return pstmt;
        }
    }

    /**
     * Customize {@link PreparedStatement}.
     *
     * @param ps {@link PreparedStatement}
     */
    protected void configurePreparedStatement(PreparedStatement ps) {
        if (fetchSize != null) {
            StatementUtil.setFetchSize(ps, fetchSize.intValue());
        }
        if (maxRows != null) {
            StatementUtil.setMaxRows(ps, maxRows.intValue());
        }
        if (queryTimeout != null) {
            StatementUtil.setQueryTimeout(ps, queryTimeout.intValue());
        }
    }

    /**
     * フェッチサイズを設定します。
     * 
     * @param fetchSize
     */
    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * 最大行数を設定します。
     * 
     * @param maxRows
     */
    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * クエリタイムアウトを設定します。
     *
     * @param queryTimeout Timeout for query(seconds).
     */
    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public void setBooleanToInt(boolean b) {
        booleanToInt = b;
    }


}
