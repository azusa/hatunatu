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

import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.jdbc.SqlLog;
import org.seasar.extension.jdbc.SqlLogRegistry;
import org.seasar.extension.jdbc.SqlLogRegistryLocator;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BasicHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testLogSql() throws Exception {
        final String sql = "update emp set ename = ?, comm = ? where empno = ?";
        Object[] args = new Object[] { "hoge", new BigDecimal("100.5"),
                new Integer(7788) };
        Class[] argTypes = new Class[] { String.class, BigDecimal.class,
                Integer.class };
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setBindArguments(args);
        queryObject.setBindTypes(argTypes);
        BasicHandler handler = new BasicHandler() {
        };
        handler.setDataSource(test.getDataSource());
        assertTrue(handler.getLoggerClass() == BasicHandler.class);

        handler.logSql(queryObject);

        assertSqlLog(sql, args, argTypes);
        handler.setLoggerClass(BasicHandlerTest.class);
        handler.logSql(queryObject);
        assertSqlLog(sql, args, argTypes);
        assertTrue(handler.getLoggerClass() == BasicHandlerTest.class);
    }

    private void assertSqlLog(final String sql, Object[] args, Class[] argTypes) {
        SqlLogRegistry registry = SqlLogRegistryLocator.getInstance();
        SqlLog sqlLog = registry.getLast();
        assertEquals(sql, sqlLog.getRawSql());
        assertEquals(
                "update emp set ename = 'hoge', comm = 100.5 where empno = 7788",
                sqlLog.getCompleteSql());
        assertSame(args, sqlLog.getBindArgs());
        assertSame(argTypes, sqlLog.getBindArgTypes());
    }

}
