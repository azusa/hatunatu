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
import jp.fieldnotes.hatunatu.dao.RowCreator;
import jp.fieldnotes.hatunatu.dao.exception.NotSingleResultRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.RowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

public class DtoMetaDataResultSetHandlerTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testHandle() throws Exception {
        ResultSetHandler handler = new DtoMetaDataResultSetHandler(
                test.createDtoMetaData(EmployeeDto.class), createRowCreator());
        String sql = "select empno, ename, dname from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        EmployeeDto dto = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                dto = (EmployeeDto) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(dto);
        assertEquals(7788, dto.getEmpno());
        assertEquals("SCOTT", dto.getEname());
        assertEquals("RESEARCH", dto.getDname());
    }

    @Test
    public void testHandleRestrict() throws Exception {
        ResultSetHandler handler = new DtoMetaDataResultSetHandler.RestrictDtoMetaDataResultSetHandler(
                test.createDtoMetaData(EmployeeDto.class), createRowCreator());
        String sql = "select empno, ename, dname from emp, dept where emp.deptno = dept.deptno";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        EmployeeDto dto = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                try {
                    dto = (EmployeeDto) handler.handle(rs);
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
        assertNull(dto);
    }
    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }
}