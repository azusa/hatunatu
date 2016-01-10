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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.exception.NotSingleResultRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.MapResultSetHandler.RestrictMapResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import static org.junit.Assert.*;

public class MapResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testHandle() throws Exception {
        ResultSetHandler handler = new MapResultSetHandler(){

            protected void handleNotSingleResult() {
                throw new AssertionError("should not be called");
            }
            
        };
        String sql = "select employee_id, employee_name from emp4 where employee_id = 7369";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Map ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Map) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(new Integer(7369), ret.get("employeeId"));
        assertEquals("SMITH", ret.get("employeeName"));
    }

    @Test
    public void testHandleNotSingleRecord() throws Exception {
        final boolean[] calls = {false};
        ResultSetHandler handler = new MapResultSetHandler(){

            protected final void handleNotSingleResult() {
                super.handleNotSingleResult();
                calls[0] = true;
            }
            
        };
        String sql = "select employee_id, employee_name from emp4";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Map ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Map) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertTrue(calls[0]);
        assertNotNull(ret);
    }

    @Test
    public void testHandle_restrict() throws Exception {
        ResultSetHandler handler = new RestrictMapResultSetHandler();
        String sql = "select employee_id, employee_name from emp4";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Map ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                try {
                    ret = (Map) handler.handle(rs);
                    fail();
                } catch(NotSingleResultRuntimeException e){
                    assertTrue(true);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNull(ret);
    }
}