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

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Sql;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResultSetToBeanTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);


    // https://www.seasar.org/issues/browse/DAO-26
    @Test
    public void testMappingByPropertyNameTx() throws Exception {
        // ## Arrange ##
        final DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        {
            final SqlCommand insertCommand = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"insert"));
            Employee bean = new Employee();
            bean.setDepartmentId(new Integer(123));
            bean.setEmployeeId(new Integer(7650));
            bean.setEmployeeName("foo");
            insertCommand.execute(new Object[] { bean });
        }

        // ## Act ##
        // ## Assert ##
        {
            final SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"find1"));
            Employee bean = (Employee) command.execute(null);
            assertEquals("foo", bean.getEmployeeName());
        }
        {
            final SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"find2"));
            Employee bean = (Employee) command.execute(null);
            assertEquals("foo", bean.getEmployeeName());
        }
    }

    public static interface EmployeeDao {

        @Sql(value = "SELECT employee_name FROM EMP3")
        Employee find1();

        @Sql(value = "SELECT employee_name AS employeeName FROM EMP3")
        Employee find2();

        int insert(Employee bean);

    }

    @Bean(table = "EMP3")
    public static class Employee {

        private static final long serialVersionUID = 1L;

        private Integer employeeId;

        private String employeeName;

        private Integer departmentId;

        public Integer getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(Integer departmentId) {
            this.departmentId = departmentId;
        }

        public Integer getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Integer employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

    }

}
