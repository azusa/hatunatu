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

import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicUpdateHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testExecuteTx() throws Exception {
        String sql = "update emp set ename = ?, comm = ? where empno = ?";
        BasicUpdateHandler handler = new BasicUpdateHandler(test.getDataSource(),
                StatementFactory.INSTANCE);
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setBindArguments(new Object[]{"SCOTT", null,
                new Integer(7788) });
        queryObject.setBindTypes(new Class[]{String.class, null, Integer.class});
        int ret = handler.execute(queryObject);
        assertEquals(1, ret);
    }

    @Test
    public void testExecuteWithQuestionTx() throws Exception {
        String sql = "update emp set job = 'AA?A' where empno = ?";
        BasicUpdateHandler handler = new BasicUpdateHandler(test.getDataSource(),
                StatementFactory.INSTANCE);
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setBindArguments(new Object[]{new Integer(7788)});
        queryObject.setBindTypes(new Class[]{Integer.class});


        int ret = handler.execute(queryObject);
        assertEquals(1, ret);
    }

    @Test
    public void testExecuteWithQuestion2Tx() throws Exception {
        String sql = "update emp set job = 'AA' where empno = ?";
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setBindArguments(new Object[]{new Integer(7788)});
        queryObject.setBindTypes(new Class[]{Integer.class});


        BasicUpdateHandler handler = new BasicUpdateHandler(test.getDataSource(),
                StatementFactory.INSTANCE);
        int ret = handler.execute(queryObject);
        assertEquals(1, ret);
    }

    @Test
    public void testExecuteWithQuestion3Tx() throws Exception {
        String sql = "update emp set ename = ?, job = 'AA' where empno = ?";
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setBindArguments(new Object[]{"SCOTT", new Integer(7788)});
        queryObject.setBindTypes(new Class[]{String.class, Integer.class});


        BasicUpdateHandler handler = new BasicUpdateHandler(test.getDataSource(),
                StatementFactory.INSTANCE);
        int ret = handler.execute(queryObject);
        assertEquals(1, ret);
    }


}
