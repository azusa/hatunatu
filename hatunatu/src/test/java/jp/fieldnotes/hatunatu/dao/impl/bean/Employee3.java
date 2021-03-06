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

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Column;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Relation;

import java.io.Serializable;

@Bean(table = "EMP")
public class Employee3 implements Serializable {

    private static final long serialVersionUID = -7905643078362230451L;

    private Long empno;

    private String ename;

    private String job;

    private Short mgr;

    private java.util.Date hiredate;

    private Float sal;

    private Float comm;

    private Integer deptno;

    private Department department;

    public Employee3() {
    }

    public Employee3(Long empno) {
        this.empno = empno;
    }

    public Long getEmpno() {
        return this.empno;
    }

    public void setEmpno(Long empno) {
        this.empno = empno;
    }

    public String getEname() {
        return this.ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Short getManager() {
        return this.mgr;
    }

    @Column("mgr")
    public void setManager(Short mgr) {
        this.mgr = mgr;
    }

    public java.util.Date getHiredate() {
        return this.hiredate;
    }

    public void setHiredate(java.util.Date hiredate) {
        this.hiredate = hiredate;
    }

    public Float getSal() {
        return this.sal;
    }

    public void setSal(Float sal) {
        this.sal = sal;
    }

    public Float getComm() {
        return this.comm;
    }

    public void setComm(Float comm) {
        this.comm = comm;
    }

    public Integer getDeptno() {
        return this.deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    @Relation(relationNo = 0)
    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Employee3))
            return false;
        Employee3 castOther = (Employee3) other;
        return this.getEmpno() == castOther.getEmpno();
    }

    public int hashCode() {
        return this.getEmpno().hashCode();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(empno).append(", ");
        buf.append(ename).append(", ");
        buf.append(job).append(", ");
        buf.append(mgr).append(", ");
        buf.append(hiredate).append(", ");
        buf.append(sal).append(", ");
        buf.append(comm).append(", ");
        buf.append(deptno).append(" {");
        buf.append(department).append("}");
        return buf.toString();
    }
}