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
package org.seasar.dao.handler;

import java.util.Date;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.util.exception.SQLRuntimeException;
import org.seasar.util.exception.SSQLException;

/**
 * @author higa
 *
 */
public class BasicUpdateHandlerTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testExecuteTx() throws Exception {
        String sql = "update emp set ename = ?, comm = ? where empno = ?";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql);
        int ret = handler.execute(new Object[] { "SCOTT", null,
                new Integer(7788) });
        assertEquals(1, ret);
    }

    /**
     * @throws Exception
     */
    public void testExecuteWithQuestionTx() throws Exception {
        String sql = "update emp set job = 'AA?A' where empno = ?";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql);
        int ret = handler.execute(new Object[] { new Integer(7788) });
        assertEquals(1, ret);
    }

    /**
     * @throws Exception
     */
    public void testExecuteWithQuestion2Tx() throws Exception {
        String sql = "update emp set job = 'AA' where empno = ?";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql);
        int ret = handler.execute(new Object[] { new Integer(7788) });
        assertEquals(1, ret);
    }

    /**
     * @throws Exception
     */
    public void testExecuteWithQuestion3Tx() throws Exception {
        String sql = "update emp set ename = ?, job = 'AA' where empno = ?";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql);
        int ret = handler.execute(new Object[] { "SCOTT", new Integer(7788) });
        assertEquals(1, ret);
    }

    /**
     * @throws Exception
     */
    public void testExceptionByBrokenSqlTx() throws Exception {
        String sql = "pdate emp set ename = ?, comm = ? where empno = ?";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql);
        try {
            handler.execute(new Object[] { "SCOTT", null, new Integer(7788) });
            fail();
        } catch (SQLRuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().indexOf(sql) > -1);
            final org.seasar.framework.exception.SSQLException cause = (org.seasar.framework.exception.SSQLException) e.getCause();
            assertEquals(sql, cause.getSql());
        }
    }

    /**
     * @throws Exception
     */
    public void testExceptionByWrongDataTypeTx() throws Exception {
        final String sql = "update emp set comm = ?";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql);
        try {
            handler.execute(new Object[] { new Date() });
            fail();
        } catch (SQLRuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().indexOf(sql) > -1);
            final org.seasar.framework.exception.SSQLException cause = (org.seasar.framework.exception.SSQLException) e.getCause();
            assertEquals(sql, cause.getSql());
        }
    }

    public void setUp() {
        include("j2ee.dicon");
    }

}
