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
package jp.fieldnotes.hatunatu.dao.impl.dao;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Argument;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Sql;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.SqlFile;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;

import java.util.List;

public interface EmployeeDao {

    public List<Employee> getAllEmployees();

    public Employee[] getAllEmployeeArray();


    public Employee selectByEmpno( @Argument("empno")long empno);

    @Sql(value = "SELECT empno, ename, dname FROM emp, dept where emp.deptno = dept.deptno")
    public EmployeeDto[] findAll();

    /**
     * @param empno
     * @return
     */

    public Employee getEmployee(@Argument("empno")int empno);

    @SqlFile
    public int getCount();

    @SqlFile("jp/fieldnotes/hatunatu/dao/impl/sqlfile/getCount.sql")
    public int getCount2();

    public void update(Employee employee);

    public Employee[] getEmployeesByDeptno(int deptno);


    public int fetchByDeptno(@Argument("deptno")int deptno);


}
