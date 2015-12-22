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
package org.seasar.dao.impl;

import org.seasar.dao.handler.BasicSelectHandler;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 * 
 */
public class BooleanToIntStatementTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testBooleanToIntTx() throws Exception {
        String sql = "update dept set active = ? where deptno = 10";
        org.seasar.dao.handler.BasicUpdateHandler handler = new org.seasar.dao.handler.BasicUpdateHandler(getDataSource(),
                sql, org.seasar.dao.impl.BooleanToIntStatementFactory.INSTANCE);
        int ret = handler.execute(new Object[] { Boolean.FALSE });
        assertEquals("1", 1, ret);

        String sql2 = "select active from dept where deptno = 10";
        org.seasar.dao.handler.BasicSelectHandler handler2 = new BasicSelectHandler(getDataSource(),
                sql2, new ObjectResultSetHandler());
        Number active = (Number) handler2.execute((Object[]) null);
        assertEquals("1", 0, active.intValue());
    }

    public void setUp() {
        include("j2ee.dicon");
    }
}