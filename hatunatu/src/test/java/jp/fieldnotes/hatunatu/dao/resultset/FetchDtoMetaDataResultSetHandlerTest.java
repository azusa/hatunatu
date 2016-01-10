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

import jp.fieldnotes.hatunatu.dao.FetchHandler;
import jp.fieldnotes.hatunatu.dao.RowCreator;
import jp.fieldnotes.hatunatu.dao.impl.RowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto3;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FetchDtoMetaDataResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);


    @Test
    public void testHandle() throws Exception {
        String sql = "select empno, ename, dname from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        final List<EmployeeDto> ret = new ArrayList<EmployeeDto>();
        ResultSetHandler handler = new FetchDtoMetaDataResultSetHandler(
                test.createDtoMetaData(EmployeeDto.class), createRowCreator(),
                new FetchHandler<EmployeeDto>() {
                    public boolean execute(EmployeeDto bean) {
                        ret.add(bean);
                        return true;
                    };
                });
        try {
            ResultSet rs = ps.executeQuery();
            try {
                handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(1, ret.size());
        EmployeeDto dto = (EmployeeDto) ret.get(0);
        assertEquals(7788, dto.getEmpno());
        assertEquals("SCOTT", dto.getEname());
        assertEquals("RESEARCH", dto.getDname());
    }

    @Test
    public void testHandle2() throws Exception {
        String sql = "select employee_id, employee_name from emp4 where employee_id = 7369";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        final List<EmployeeDto3> ret = new ArrayList<EmployeeDto3>();
        ResultSetHandler handler = new FetchDtoMetaDataResultSetHandler(
                test.createDtoMetaData(EmployeeDto3.class), createRowCreator(),
                new FetchHandler<EmployeeDto3>() {
                    public boolean execute(EmployeeDto3 bean) {
                        ret.add(bean);
                        return true;
                    };
                });
        try {
            ResultSet rs = ps.executeQuery();
            try {
                handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull(ret);
        assertEquals(1, ret.size());
        EmployeeDto3 dto = (EmployeeDto3) ret.get(0);
        assertEquals(7369, dto.getEmployeeId());
        assertEquals("SMITH", dto.getEmployeeName());
    }

    protected RowCreator createRowCreator() {
        return new RowCreatorImpl();
    }

}
