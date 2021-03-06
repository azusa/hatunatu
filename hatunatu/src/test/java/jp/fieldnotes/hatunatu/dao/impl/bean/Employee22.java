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
package jp.fieldnotes.hatunatu.dao.impl.bean;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jundu
 *
 */
@Bean(table = "EMP", noPersistentProperty={"dummy"})
public class Employee22 {

    private static final long serialVersionUID = 1L;

    public static final String TABLE = "EMP";

    public static final int department_RELNO = 0;

    public static final String manager_COLUMN = "mgr";

    public static final String insert_NO_PERSISTENT_PROPS = "dummy";

    private Long empno;

    private Short mgr;

    private Integer deptno;

    private Department department;

    private String dummy;
    
    private Set modifiedPropertySet = new HashSet();

    public Employee22() {
    }

    public Employee22(Long empno) {
        this.empno = empno;
    }

    @Id(value = IdType.SEQUENCE)
    public Long getEmpno() {
        return this.empno;
    }

    public void setEmpno(Long empno) {
        this.modifiedPropertySet.add("empno");
        this.empno = empno;
    }

    public String getDummy() {
        return this.dummy;
    }

    public void setDummy(String dummy) {
        this.modifiedPropertySet.add("dummy");
        this.dummy = dummy;
    }

    @Column(value = "mgt")
    public Short getManager() {
        return this.mgr;
    }

    public void setManager(Short mgr) {
        this.modifiedPropertySet.add("mgr");
        this.mgr = mgr;
    }

    public Integer getDeptno() {
        return this.deptno;
    }

    public void setDeptno(Integer deptno) {
        this.modifiedPropertySet.add("deptno");
        this.deptno = deptno;
    }

    @Relation(relationNo = 0, relationKey = "DEPTNO:DEPTNO")
    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set getModifiedPropertyNames() {
        return this.modifiedPropertySet;
    }

}
