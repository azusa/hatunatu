/*
 * Copyright 2015 the original author or authors.
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
package jp.fieldnotes.hatunatu.dao.pager;

import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.BooleanToIntPreparedStatement;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;

import java.sql.*;

public class PagerStatementFactory implements StatementFactory {

    protected boolean booleanToInt = false;

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

    private PreparedStatement createPreparedStatement(PreparedStatement pstmt,
            String sql) {
        if (booleanToInt) {
            return new BooleanToIntPreparedStatement(pstmt, sql);
        } else {
            return pstmt;
        }
    }

    @Override
    public CallableStatement createCallableStatement(Connection con, String sql) {
        return ConnectionUtil.prepareCall(con, sql);
    }

    public void setBooleanToInt(boolean b) {
        booleanToInt = b;
    }

}
