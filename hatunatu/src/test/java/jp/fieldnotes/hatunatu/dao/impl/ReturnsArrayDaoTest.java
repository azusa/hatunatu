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

import java.util.List;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import org.apache.poi.hssf.record.formula.functions.Int;
import org.seasar.extension.unit.S2TestCase;

/**
 * https://www.seasar.org/issues/browse/DAO-19
 * 
 * @author manhole
 */
public class ReturnsArrayDaoTest extends S2TestCase {

    private EmployeeDao employeeDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("ReturnsArrayDaoTest.dicon");
    }

    public void testReturnArray() throws Exception {
        final Employee[] array = employeeDao.getAllEmployeesAsArray();
        assertEquals(Integer.toString(array.length), true, array.length > 0);
    }

    public void testReturnList() throws Exception {
        final List list = employeeDao.getAllEmployeesAsList();
        assertEquals(Integer.toString(list.size()), true, list.size() > 0);
    }


    public static interface EmployeeDao {

        EmployeeImpl[] getAllEmployeesAsArray();

        List<EmployeeImpl> getAllEmployeesAsList();
    }

    public static interface Employee {
    }

    @Bean(table="EMP")
    public static class EmployeeImpl implements Employee {

        private String ename;

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }
    }

}
