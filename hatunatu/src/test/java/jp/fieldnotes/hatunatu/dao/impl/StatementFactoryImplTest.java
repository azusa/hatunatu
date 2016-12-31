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

import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.pager.DefaultPagerCondition;
import org.junit.Before;
import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class StatementFactoryImplTest {

    private Connection mockConnection;

    private PreparedStatement mockPreparedStatement;

    private CallableStatement mockCallableStatement;

    private StatementFactoryImpl statementFactory;

    @Before
    public void before() throws Exception {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockCallableStatement = mock(CallableStatement.class);
        statementFactory = spy(new StatementFactoryImpl());
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareCall(anyString())).thenReturn(mockCallableStatement);
    }

    @Test
    public void testCreatePreparedStatement() throws Exception {

        // ## Act ##
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("some sql");
        PreparedStatement result = statementFactory.createPreparedStatement(mockConnection, queryObject);

        assertSame(mockPreparedStatement, result);

        verify(statementFactory).createPreparedStatement(mockConnection, queryObject);
    }



    @Test
    public void testCreateCallableStatement() throws Exception {

        // ## Act ##
        CallableStatement callableStatement = statementFactory
                .createCallableStatement(mockConnection, "some sql");

        // ## Assert ##
        assertSame(mockCallableStatement, callableStatement);
    }

    @Test
    public void testConfigurePreparedStatement() throws Exception {

        statementFactory.setFetchSize(123);
        statementFactory.setMaxRows(221);
        statementFactory.setQueryTimeout(321);

        QueryObject queryObject = new QueryObject();
        queryObject.setSql("select ...");
        statementFactory.createPreparedStatement(mockConnection,
                queryObject);

        verify(mockPreparedStatement).setFetchSize(123);
        verify(mockPreparedStatement).setMaxRows(221);
        verify(mockPreparedStatement).setQueryTimeout(321);

    }

    @Test
    public void testConfigureCallableStatement() throws Exception {

        statementFactory.setFetchSize(1123);
        statementFactory.setMaxRows(5535);

        // ## Act ##
        statementFactory.createCallableStatement(mockConnection,
                "select ...");

        // ## Assert ##
        verify(mockCallableStatement).setFetchSize(1123);
        verify(mockCallableStatement).setMaxRows(5535);
    }

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
        // 例外にならなければOK
        PreparedStatement pstmt = statementFactory.createPreparedStatement(mockConnection, queryObject);

        // ## Assert ##
        verify(mockConnection).prepareStatement("aaaa");
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

        // 例外にならなければOK
        statementFactory.createPreparedStatement(mockConnection, queryObject);

        // ## Assert ##
        verify(mockConnection).prepareStatement(eq("aaaa"), anyInt(), anyInt());
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
        // 例外にならなければOK
        PreparedStatement pstmt = statementFactory.createPreparedStatement(mockConnection, queryObject);

        // ## Assert ##
        verify(mockConnection).prepareStatement("aaaa");
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
        // 例外にならなければOK
        PreparedStatement pstmt = statementFactory.createPreparedStatement(mockConnection, queryObject);

        // ## Assert ##
        verify(mockConnection).prepareStatement(eq("aaaa"), anyInt(), anyInt());
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
        statementFactory.setBooleanToInt(true);
        // 例外にならなければOK
        PreparedStatement stmt = statementFactory.createPreparedStatement(mockConnection, queryObject);

        // ## Assert ##
        verify(mockConnection).prepareStatement("aaaa");
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
        // 例外にならなければOK
        statementFactory.setBooleanToInt(true);
        PreparedStatement stmt = statementFactory.createPreparedStatement(mockConnection, queryObject);
        // ## Assert ##
        verify(mockConnection).prepareStatement(eq("aaaa"), anyInt(), anyInt());
        assertThat(stmt, is(instanceOf(BooleanToIntPreparedStatement.class)));
    }




}
