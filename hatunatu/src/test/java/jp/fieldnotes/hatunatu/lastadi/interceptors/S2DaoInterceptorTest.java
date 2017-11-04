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
package jp.fieldnotes.hatunatu.lastadi.interceptors;

import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class S2DaoInterceptorTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "Interceptors.xml");

    private EmployeeDao dao;

    @Test
    public void testSelectBeanList() throws Exception {
        List employees = dao.getAllEmployees();
        for (int i = 0; i < employees.size(); ++i) {
            System.out.println(employees.get(i));
        }
        assertEquals("1", true, employees.size() > 0);
    }

    @Test
    public void testSelectBean() throws Exception {
        Employee employee = dao.getEmployee(7788);
        System.out.println(employee);
        assertEquals("1", "SCOTT", employee.getEname());
    }

    @Test
    public void testSelectDto() throws Exception {
        EmployeeDto dto = dao.findEmployeeDto(7788);
        assertEquals("SCOTT", dto.getEname());
        assertEquals("RESEARCH", dto.getDname());
    }


    @Test
    public void testSelectObject() throws Exception {
        int count = dao.getCount();
        System.out.println("count:" + count);
        assertEquals("1", true, count > 0);
    }

    @Test
    public void testUpdateTx() throws Exception {
        Employee employee = dao.getEmployee(7788);
        assertEquals("1", 1, dao.update(employee));
    }

    @Test
    public void testInsertTx() throws Exception {
        dao.insert(9999, "hoge");
    }
}