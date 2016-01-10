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

import jp.fieldnotes.hatunatu.dao.impl.BasicStatementFactory;
import jp.fieldnotes.hatunatu.dao.impl.OracleResultSetFactory;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.jdbc.impl.ObjectResultSetHandler;

import static org.junit.Assert.assertEquals;

public class BasicSelectHandlerForOracleTest  {
    /**
     * 
     */
    public static final String WAVE_DASH = "\u301C";

    /**
     * 
     */
    public static final String FULL_WIDTH_TILDE = "\uFF5E";

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testExecuteTx() throws Exception {
        String sql = "insert into emp(empno, ename) values(99, ?)";
        BasicUpdateHandler handler = new BasicUpdateHandler(test.getDataSource(),
                sql);
        handler.execute(new Object[] { WAVE_DASH });

        String sql2 = "select ename from emp where empno = 99";
        BasicSelectHandler handler2 = new BasicSelectHandler(test.getDataSource(),
                sql2, new ObjectResultSetHandler(),
                BasicStatementFactory.INSTANCE, new OracleResultSetFactory());
        String ret = (String) handler2.execute((Object[]) null);
        System.out.println(ret);
        assertEquals("1", FULL_WIDTH_TILDE, ret);
    }
}