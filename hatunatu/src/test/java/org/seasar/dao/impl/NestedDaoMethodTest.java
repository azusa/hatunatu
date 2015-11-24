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
package org.seasar.dao.impl;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.EntityManager;
import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author manhole
 */
public class NestedDaoMethodTest extends S2DaoTestCase {

    private EmpDao empDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("NestedDaoMethodTest.dicon");
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-83
     * Daoメソッドから他のDaoメソッドを呼ぶとNPEに。
     */
    public void test1() throws Exception {
        final Emp emp = empDao.findById(7782);
        assertEquals("CLARK", emp.getEname());
        final Dept dept = emp.getDept();
        assertEquals("ACCOUNTING", dept.getDname());
    }

    @Bean(table = "EMP")
    public static class Emp {

        private int empno;

        private int deptno;

        private String ename;

        private Dept dept;

        public int getEmpno() {
            return this.empno;
        }

        public void setEmpno(int empno) {
            this.empno = empno;
        }

        public String getEname() {
            return this.ename;
        }

        public void setEname(final String ename) {
            this.ename = ename;
        }

        public int getDeptno() {
            return deptno;
        }

        public void setDeptno(final int deptno) {
            this.deptno = deptno;
        }

        public Dept getDept() {
            return dept;
        }

        public void setDept(final Dept dept) {
            this.dept = dept;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(empno).append(", ");
            buf.append(ename).append(", ");
            buf.append(deptno).append(", ");
            buf.append(dept);
            return buf.toString();
        }

    }

    @Bean(table = "DEPT")
    public static class Dept {

        private int deptno;

        private String dname;

        public int getDeptno() {
            return this.deptno;
        }

        public void setDeptno(int deptno) {
            this.deptno = deptno;
        }

        public String getDname() {
            return this.dname;
        }

        public void setDname(String dname) {
            this.dname = dname;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(deptno).append(", ");
            buf.append(dname);
            return buf.toString();
        }

    }

    @S2Dao(bean=Emp.class)
    public static interface EmpDao {

        Emp findById(int empno);

    }

    @S2Dao(bean=Dept.class)
    public static interface DeptDao {

        public Class BEAN = Dept.class;

        public static String findById_ARGS = "deptno";

        Dept findById(int deptno);

    }

    public static abstract class EmpDaoImpl extends AbstractDao implements
            EmpDao {

        private DeptDao deptDao;

        public EmpDaoImpl(final DaoMetaDataFactory daoMetaDataFactory) {
            super(daoMetaDataFactory);
        }

        public Emp findById(int empno) {
            final EntityManager em = getEntityManager();
            final Emp emp = (Emp) em.findBean("empno = ?", new Integer(empno));
            final int deptno = emp.getDeptno();
            final Dept dept = deptDao.findById(deptno);
            emp.setDept(dept);
            return emp;
        }

        public void setDeptDao(final DeptDao deptDao) {
            this.deptDao = deptDao;
        }

    }

}
