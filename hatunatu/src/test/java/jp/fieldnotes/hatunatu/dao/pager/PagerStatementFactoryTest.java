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

import jp.fieldnotes.hatunatu.dao.impl.BooleanToIntPreparedStatement;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PagerStatementFactoryTest {

    /**
     * Pagerで無い場合は引数1つのprepareStatementを呼ぶこと。
     */
    @Test
    public void testCreatePreparedStatement_NoPager() throws Exception {
        // ## Arrange ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("aaaa");
        queryObject.setMethodArguments(new Object[]{new Integer(1)});

        final boolean[] calls = {false};

        Connection con = mock(Connection.class);

        // ## Act ##
        final PagerStatementFactory statementFactory = new PagerStatementFactory();
        // 例外にならなければOK
        PreparedStatement pstmt = statementFactory.createPreparedStatement(con, queryObject);

        // ## Assert ##
        verify(con).prepareStatement("aaaa");
    }

    /**
     * Pagerの場合は引数3つのprepareStatementを呼ぶこと。
     */
    @Test
    public void testCreatePreparedStatement_Pager() throws Exception {
        // ## Arrange ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("aaaa");
        DefaultPagerCondition pagerCondition = new DefaultPagerCondition();
        pagerCondition.setLimit(10);

        queryObject.setMethodArguments(new Object[]{pagerCondition});

        Connection con = mock(Connection.class);
        // ## Act ##
        final PagerStatementFactory statementFactory = new PagerStatementFactory();
        // 例外にならなければOK
        statementFactory.createPreparedStatement(con, queryObject);

        // ## Assert ##
        verify(con).prepareStatement(eq("aaaa"), anyInt(), anyInt());
    }

    /**
     * Pagerでもlimitが-1の場合は引数1つのprepareStatementを呼ぶこと。
     */
    @Test
    public void testCreatePreparedStatement_Pager_NoneLimit() throws Exception {
        // ## Arrange ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("aaaa");

        queryObject.setMethodArguments(new Object[]{new DefaultPagerCondition()});
        Connection con = mock(Connection.class);

        // ## Act ##
        final PagerStatementFactory statementFactory = new PagerStatementFactory();
        // 例外にならなければOK
        PreparedStatement pstmt = statementFactory.createPreparedStatement(con, queryObject);

        // ## Assert ##
        verify(con).prepareStatement("aaaa");
    }

    /**
     * Pagerでlimitが-1でもoffsetが設定されている場合は引数3つのprepareStatementを呼ぶこと。
     */
    @Test
    public void testCreatePreparedStatement_Pager_NoneLimitAndOffSet()
            throws Exception {
        // ## Arrange ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("aaaa");
        DefaultPagerCondition pagerCondition = new DefaultPagerCondition();
        pagerCondition.setOffset(10);
        queryObject.setMethodArguments(new Object[]{pagerCondition});
        Connection con = mock(Connection.class);
        // ## Act ##
        final PagerStatementFactory statementFactory = new PagerStatementFactory();
        // 例外にならなければOK
        PreparedStatement pstmt = statementFactory.createPreparedStatement(con, queryObject);

        // ## Assert ##
        verify(con).prepareStatement(eq("aaaa"), anyInt(), anyInt());
    }

    /**
     * Pagerで無い場合は引数1つのprepareStatementを呼ぶこと。
     * booleanToIntプロパティがtrueの場合はBooleanToIntPreparedStatementでラップする。
     */
    @Test
    public void testCreatePreparedStatement_NoPager_BooleanToInt()
            throws Exception {
        // ## Arrange ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("aaaa");
        queryObject.setMethodArguments(new Object[]{new Integer(1)});

        Connection con = mock(Connection.class);
        // ## Act ##
        final PagerStatementFactory statementFactory = new PagerStatementFactory();
        statementFactory.setBooleanToInt(true);
        // 例外にならなければOK
        PreparedStatement stmt = statementFactory.createPreparedStatement(con, queryObject);

        // ## Assert ##
        verify(con).prepareStatement("aaaa");
        assertThat(stmt, is(instanceOf(BooleanToIntPreparedStatement.class)));
    }

    /**
     * Pagerの場合は引数3つのprepareStatementを呼ぶこと。
     * booleanToIntプロパティがtrueの場合はBooleanToIntPreparedStatementでラップする。
     */
    public void testCreatePreparedStatement_Pager_BooleanToInt()
            throws Exception {
        // ## Arrange ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("aaaa");
        DefaultPagerCondition pagerCondition = new DefaultPagerCondition();
        pagerCondition.setLimit(10);
        queryObject.setMethodArguments(new Object[]{pagerCondition});
        Connection con = mock(Connection.class);

        // ## Act ##
        final PagerStatementFactory statementFactory = new PagerStatementFactory();
        // 例外にならなければOK
        statementFactory.setBooleanToInt(true);
        PreparedStatement stmt = statementFactory.createPreparedStatement(con, queryObject);
        // ## Assert ##
        verify(con).prepareStatement(eq("aaaa"), anyInt(), anyInt());
        assertThat(stmt, is(instanceOf(BooleanToIntPreparedStatement.class)));
    }

}
