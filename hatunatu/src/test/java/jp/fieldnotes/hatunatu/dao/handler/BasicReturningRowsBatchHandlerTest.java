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

import jp.fieldnotes.hatunatu.dao.impl.MapListResultSetHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BasicReturningRowsBatchHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);
    
    @Test
    public void testExecuteTx() throws Exception {
        String sql = "update emp set ename = ?, comm = ? where empno = ?";
        BasicReturningRowsBatchHandler handler = new BasicReturningRowsBatchHandler(
                test.getDataSource(), 2);
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[] { "aaa", null, new Integer(7369) });
        list.add(new Object[] { "bbb", new Double(100.5), new Integer(7499) });
        list.add(new Object[] { "ccc", null, new Integer(7521) });
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        int[] rows = handler.execute(queryObject, list);
        assertEquals(1, rows[0]);
        assertEquals(1, rows[1]);
        assertEquals(1, rows[2]);
        assertEquals(3, rows.length);
        String sql2 = "select empno, ename, comm from emp where empno in (7369, 7499, 7521) order by empno";
        BasicSelectHandler handler2 = new BasicSelectHandler(test.getDataSource(),
                new MapListResultSetHandler());
        queryObject = new QueryObject();
        queryObject.setSql(sql2);

        List ret2 = (List) handler2.execute(queryObject);
        Map rec = (Map) ret2.get(0);
        assertEquals("aaa", rec.get("ename"));
        rec = (Map) ret2.get(1);
        assertEquals("bbb", rec.get("ename"));
        rec = (Map) ret2.get(2);
        assertEquals("ccc", rec.get("ename"));
    }



}
