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

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Argument;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Sql;

import java.util.List;

public interface EmployeeDao {

    public List<Employee> getAllEmployees();

    @Sql("select empno, ename, dname from emp, dept where empno = ? and emp.deptno = dept.deptno")
    public EmployeeDto findEmployeeDto(@Argument("empno")int empno);

    public Employee getEmployee(@Argument("empno")int empno);

    public Employee[] getEmployeesByDeptno(int deptno);

    public int getCount();


    public int insert(@Argument("empno")int empno, @Argument("ename")String ename);

    public int update(Employee employee);
}
