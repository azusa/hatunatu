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

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Argument;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Query;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListBindTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this,"ListBindTest.xml" );

    private EmployeeDao employeeDao;


    @Test
    public void testListBindTx() throws Exception {
        // ## Arrange ##
        final List param = Arrays.asList(new Integer[] { new Integer(7566),
                new Integer(7900) });

        // ## Act ##
        final List employees = employeeDao.findByIdList(param);

        // ## Assert ##
        assertEquals(2, employees.size());
        {
            final Employee emp = (Employee) employees.get(0);
            assertEquals("JONES", emp.getEname());
        }
        {
            final Employee emp = (Employee) employees.get(1);
            assertEquals("JAMES", emp.getEname());
        }
    }

    @Test
    public void testArrayBindTx() throws Exception {
        // ## Arrange ##
        final Integer[] param = new Integer[] { new Integer(7900),
                new Integer(7902) };

        // ## Act ##
        final List employees = employeeDao.findByIdArray(param);

        // ## Assert ##
        assertEquals(2, employees.size());
        {
            final Employee emp = (Employee) employees.get(0);
            assertEquals("JAMES", emp.getEname());
        }
        {
            final Employee emp = (Employee) employees.get(1);
            assertEquals("FORD", emp.getEname());
        }
    }

    public static interface EmployeeDao {



        public Employee findById(@Argument("empno")int empno);


        @Query( "/*BEGIN*/ WHERE "
                + "/*IF empno != null*/ empno IN /*empno*/('aaa', 'bbb')/*END*/"
                + " /*END*/")
        public List<Employee> findByIdList(@Argument("empno")List empnos);


        @Query("/*BEGIN*/ WHERE "
                + "/*IF empno != null*/ empno IN /*empno*/('aaa')/*END*/"
                + " /*END*/")
        public List<Employee> findByIdArray(@Argument("empno")Integer[] empnos);

        public void insert(Employee employee);

        public void update(Employee employee);

    }

}
