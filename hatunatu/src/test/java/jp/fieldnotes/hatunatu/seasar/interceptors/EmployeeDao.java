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

import org.seasar.dao.annotation.tiger.Arguments;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.annotation.tiger.Sql;

import java.util.List;
import java.util.Map;

@S2Dao(bean= Employee.class)
public interface EmployeeDao {

    public List getAllEmployees();

    public String findEmployeeDto_SQL = "select empno, ename, dname from emp, dept where empno = ? and emp.deptno = dept.deptno";

    @Sql("select empno, ename, dname from emp, dept where empno = ? and emp.deptno = dept.deptno")
    @Arguments("empno")
    public EmployeeDto findEmployeeDto(int empno);

    @Sql("select empno as value, ename as label from emp")
    public Map[] getLabelValue();

    @Arguments("empno")
    public Employee getEmployee(int empno);

    public Employee[] getEmployeesByDeptno(int deptno);

    public int getCount();

    @Arguments({"empno","ename"})
    public int insert(int empno, String ename);

    public int update(Employee employee);
}
