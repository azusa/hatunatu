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

        handler.setLoggerClass(BasicHandlerTest.class);
        handler.logSql(queryObject);
        assertTrue(handler.getLoggerClass() == BasicHandlerTest.class);
    }

}
