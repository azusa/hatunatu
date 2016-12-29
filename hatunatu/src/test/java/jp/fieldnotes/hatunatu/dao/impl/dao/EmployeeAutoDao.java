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

import jp.fieldnotes.hatunatu.dao.annotation.tiger.*;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.condition.EmployeeSearchCondition;

import java.util.List;

public interface EmployeeAutoDao {


    @Query("deptno asc, empno desc")
    public List<Employee> getEmployeeByDeptno(@Argument("deptno")int deptno);

    @Query("sal BETWEEN ? AND ? ORDER BY empno")
    public List<Employee> getEmployeesBySal(Float minSal, Float maxSal);


    @Query("ename IN /*enames*/('SCOTT','MARY') AND job IN /*jobs*/('ANALYST', 'FREE')")
    public List<Employee> getEmployeesByEnameJob(@Argument("enames")List enames, @Argument( "jobs") List jobs);

    public List<Employee> getEmployeesBySearchCondition(EmployeeSearchCondition dto);

    @Query("department.dname = /*dto.department.dname*/'RESEARCH'")
    public List<Employee> getEmployeesBySearchCondition2(EmployeeSearchCondition dto);

    public List<Employee> getEmployeesByEmployee(Employee dto);


    public Employee getEmployee(@Argument("empno")int empno);

    public void insert(Employee employee);

    @NoPersistentProperty( { "job", "mgr", "hiredate", "sal", "comm", "deptno" })
    public void insert2(Employee employee);

    @PersistentProperty("deptno")
    public void insert3(Employee employee);

    public void insertBatch(Employee[] employees);

    public int[] insertBatch2(Employee[] employees);

    public void update(Employee employee);

    @NoPersistentProperty( { "job", "mgr", "hiredate", "sal", "comm", "deptno" })
    public void update2(Employee employee);

    @PersistentProperty("deptno")
    public void update3(Employee employee);

    @CheckSingleRowUpdate(false)
    public int update4(Employee employee);

    public void updateBatch(Employee[] employees);

    public int[] updateBatch2(Employee[] employees);

    public void updateBatchByList(List<Employee> employees);

    public void delete(Employee employee);

    public void deleteBatch(Employee[] employees);

    public int[] deleteBatch2(Employee[] employees);

    public void updateUnlessNull(Employee employee);
}
