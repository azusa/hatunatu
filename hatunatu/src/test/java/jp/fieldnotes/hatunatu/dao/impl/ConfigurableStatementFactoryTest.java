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
import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ConfigurableStatementFactoryTest {

    @Test
    public void testCunstructorWithNull() throws Exception {
        try {
            new ConfigurableStatementFactory(null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testCreatePreparedStatement() throws Exception {
        // ## Arrange ##
        final Connection mockConnection = mock(Connection.class);
        final PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        final StatementFactory mockStatementFactory = mock(StatementFactory.class);


        ConfigurableStatementFactory statementFactory = spy(new ConfigurableStatementFactory(mockStatementFactory));

        when(mockStatementFactory.createPreparedStatement(anyObject(), anyString())).thenReturn(mockPreparedStatement);

        // ## Act ##
        PreparedStatement result = statementFactory.createPreparedStatement(mockConnection,"some sql");

        assertSame(mockPreparedStatement, result);

        verify(mockStatementFactory).createPreparedStatement(mockConnection, "some sql");
    }



    @Test
    public void testCreateCallableStatement() throws Exception {
        final Connection mockConnection = mock(Connection.class);
        final CallableStatement mockCallableStatement = mock(CallableStatement.class);

        final StatementFactory mockStatementFactory = mock(StatementFactory.class);

        when(mockStatementFactory.createCallableStatement(anyObject(), anyString())).thenReturn(mockCallableStatement);

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(mockStatementFactory);

        // ## Act ##
        CallableStatement callableStatement = statementFactory
                .createCallableStatement(mockConnection, "some sql");

        // ## Assert ##
        assertSame(mockCallableStatement, callableStatement);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testConfigurePreparedStatement() throws Exception {

        Connection con = mock(Connection.class);

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        StatementFactory mockStatementFactory = mock(StatementFactory.class);

        when(mockStatementFactory.createPreparedStatement(anyObject(), anyString())).thenReturn(mockPreparedStatement);

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(mockStatementFactory);

        statementFactory.setFetchSize(new Integer(123));
        statementFactory.setMaxRows(new Integer(221));
        statementFactory.setQueryTimeout(new Integer(321));

        statementFactory.createPreparedStatement(con,
                "select ...");

        verify(mockPreparedStatement).setFetchSize(123);
        verify(mockPreparedStatement).setMaxRows(221);
        verify(mockPreparedStatement).setQueryTimeout(321);

    }

    /**
     * @throws Exception
     */
    @Test
    public void testConfigureCallableStatement() throws Exception {

        Connection con = mock(Connection.class);

        CallableStatement mockCallableStatement = mock(CallableStatement.class);

        StatementFactory mockStatementFactory = mock(StatementFactory.class);

        when(mockStatementFactory.createCallableStatement(anyObject(), anyString())).thenReturn(mockCallableStatement);

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(mockStatementFactory);

        statementFactory.setFetchSize(new Integer(1123));
        statementFactory.setMaxRows(new Integer(5535));

        // ## Act ##
        statementFactory.createCallableStatement(mock(Connection.class),
                "select ...");

        // ## Assert ##
        verify(mockCallableStatement).setFetchSize(1123);
        verify(mockCallableStatement).setMaxRows(5535);
    }




}
