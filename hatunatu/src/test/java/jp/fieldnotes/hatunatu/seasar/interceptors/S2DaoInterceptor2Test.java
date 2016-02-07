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
package jp.fieldnotes.hatunatu.seasar.interceptors;

import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class S2DaoInterceptor2Test  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "EmployeeAutoDao.dicon");

    private EmployeeAutoDao dao;

    @Test
    public void testInsertTx() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("hoge");
        assertEquals("1", 1, dao.insert(emp));
    }

    @Test
    public void testSelect() throws Exception {
        Employee emp = dao.getEmployee(7788);
        System.out.println(emp);
        assertEquals("1", 7788, emp.getEmpno());
    }

    @Test
    public void testSelectQuery() throws Exception {
        List employees = dao.getEmployeesBySal(0, 1000);
        System.out.println(employees);
        assertEquals("1", 2, employees.size());
    }

    @Test
    public void testInsertBatchTx() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("hoge");
        Employee emp2 = new Employee();
        emp2.setEmpno(98);
        emp2.setEname("hoge2");
        assertArrayEquals(new int[]{1, 1}, dao.insertBatch(new Employee[]{emp, emp2}));
    }

    @Test
    public void testFullWidthTildaTx() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("～");
        dao.insert(emp);
        Employee emp2 = dao.getEmployee(99);
        assertEquals("1", emp.getEname(), emp2.getEname());
    }
}