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
package jp.fieldnotes.hatunatu.examples.dao;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Argument;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Query;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Sql;

import java.util.List;

public interface EmployeeDao {

    public List<Employee> getAllEmployees();

    public List<Employee> getEmps(EmployeeSearchCondition dto);


    public Employee getEmployee(@Argument("empno")int empno);

    @Sql("SELECT COUNT(*) FROM EMP")
    public int getCount();

    public List<Employee> getEmployeeByJobDeptno(@Argument("job")String job, @Argument("deptno")Integer deptno);

    @Query("/*IF deptno != null*/deptno = /*deptno*/123\n"
            + "-- ELSE 1=1\n" + "/*END*/")
    public List<Employee> getEmployeeByDeptno(@Argument("deptno")Integer deptno);

    public int update(Employee employee);

    @Query("SELECT empno FROM emp")
    public int[] getAllEmployeeNumbers();

}
