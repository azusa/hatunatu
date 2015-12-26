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

import java.io.Serializable;

@Bean(table = "EMP")
public class Employee20 implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String insert_NO_PERSISTENT_PROPS = "dummy";

    private Long empno;

    private Short mgr;

    private Integer deptno;

    private Department department;

    private String dummy;

    public Employee20() {
    }

    public Employee20(Long empno) {
        this.empno = empno;
    }

    @Id(value = IdType.SEQUENCE)
    public Long getEmpno() {
        return this.empno;
    }

    public void setEmpno(Long empno) {
        this.empno = empno;
    }

    public String getDummy() {
        return this.dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    @Column(value = "mgr")
    public Short getManager() {
        return this.mgr;
    }

    public void setManager(Short mgr) {
        this.mgr = mgr;
    }

    public Integer getDeptno() {
        return this.deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    @Relation(relationNo = 0, relationKey = "DEPTNO:DEPTNO")
    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}