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
package jp.fieldnotes.hatunatu.dao.resultset;

import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.exception.NotSingleResultRuntimeException;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

public class ObjectResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testHandle() throws Exception {
        ResultSetHandler handler = new ObjectResultSetHandler(null);
        String sql = "select ename from emp where empno = 7788";
        Connection con = test.getConnection();
        String ret = null;
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();
        ) {
            ret = (String) handler.handle(rs, test.getQueryObject());
        }
        assertEquals("SCOTT", ret);
    }

    @Test
    public void testHandle_restrict() throws Exception {
        ResultSetHandler handler = new ObjectResultSetHandler.RestrictObjectResultSetHandler(null);
        String sql = "select ename from emp";
        Connection con = test.getConnection();
        String ret = null;
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();
        ) {
                try{
                    ret = (String) handler.handle(rs, test.getQueryObject());
                    fail();
                } catch(NotSingleResultRuntimeException e){
                    assertTrue(true);
                }
        }
        assertNull(ret);
    }

}
