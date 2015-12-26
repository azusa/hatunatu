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
import jp.fieldnotes.hatunatu.dao.impl.BooleanToIntPreparedStatement;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author manhole
 * @author azusa
 */
public class PagerStatementFactoryTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        PagerContext.start();
    }

    protected void tearDown() throws Exception {
        PagerContext.end();
        super.tearDown();
    }

    /**
     * Pagerで無い場合は引数1つのprepareStatementを呼ぶこと。
     */
    public void testCreatePreparedStatement_NoPager() throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        pagerContext.pushArgs(new Object[] { new Integer(1) });
        final boolean[] calls = { false };

        Connection con = mock(Connection.class);

//        final NullConnection con = new NullConnection() {
//            public PreparedStatement prepareStatement(String sql)
//                    throws SQLException {
//                calls[0] = true;
//                return null;
//            }
//        };
        PreparedStatement pstmt = null;
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            pstmt = statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        verify(con).prepareStatement("aaaa");
    }

    /**
     * Pagerの場合は引数3つのprepareStatementを呼ぶこと。
     */
    public void testCreatePreparedStatement_Pager() throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        DefaultPagerCondition pagerCondition = new DefaultPagerCondition();
        pagerCondition.setLimit(10);
        pagerContext.pushArgs(new Object[] { pagerCondition });
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = null;
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        verify(con).prepareStatement(eq("aaaa"), anyInt(), anyInt());
    }

    /**
     * Pagerでもlimitが-1の場合は引数1つのprepareStatementを呼ぶこと。
     */
    public void testCreatePreparedStatement_Pager_NoneLimit() throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        pagerContext.pushArgs(new Object[] { new DefaultPagerCondition() });
        Connection con = mock(Connection.class);

        PreparedStatement pstmt = null;
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            pstmt = statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        verify(con).prepareStatement("aaaa");
    }

    /**
     * Pagerでlimitが-1でもoffsetが設定されている場合は引数3つのprepareStatementを呼ぶこと。
     */
    public void testCreatePreparedStatement_Pager_NoneLimitAndOffSet()
            throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        DefaultPagerCondition pagerCondition = new DefaultPagerCondition();
        pagerCondition.setOffset(10);
        pagerContext.pushArgs(new Object[] { pagerCondition });
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = null;
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            pstmt = statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        verify(con).prepareStatement(eq("aaaa"), anyInt(), anyInt());
    }

    /**
     * Pagerで無い場合は引数1つのprepareStatementを呼ぶこと。
     * booleanToIntプロパティがtrueの場合はBooleanToIntPreparedStatementでラップする。
     */
    public void testCreatePreparedStatement_NoPager_BooleanToInt()
            throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        pagerContext.pushArgs(new Object[] { new Integer(1) });
        Connection con = mock(Connection.class);
        PreparedStatement stmt = null;
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            statementFactory.setBooleanToInt(true);
            // 例外にならなければOK
            stmt = statementFactory.createPreparedStatement(con, "aaaa");

            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        verify(con).prepareStatement("aaaa");
        assertTrue(stmt instanceof BooleanToIntPreparedStatement);
    }

    /**
     * Pagerの場合は引数3つのprepareStatementを呼ぶこと。
     * booleanToIntプロパティがtrueの場合はBooleanToIntPreparedStatementでラップする。 
     */
    public void testCreatePreparedStatement_Pager_BooleanToInt()
            throws Exception {
        // ## Arrange ##
        final PagerContext pagerContext = PagerContext.getContext();
        DefaultPagerCondition pagerCondition = new DefaultPagerCondition();
        pagerCondition.setLimit(10);
        pagerContext.pushArgs(new Object[] { pagerCondition });
        Connection con = mock(Connection.class);

        PreparedStatement stmt = null;
        try {
            // ## Act ##
            final PagerStatementFactory statementFactory = new PagerStatementFactory();
            // 例外にならなければOK
            statementFactory.setBooleanToInt(true);
            stmt = statementFactory.createPreparedStatement(con, "aaaa");
            // ## Assert ##
        } finally {
            pagerContext.popArgs();
        }
        verify(con).prepareStatement(eq("aaaa"), anyInt(), anyInt());
        assertTrue(stmt instanceof BooleanToIntPreparedStatement);
    }

}
