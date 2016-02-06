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
package jp.fieldnotes.hatunatu.dao.resultset;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.RelationRowCreator;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.RowCreator;
import jp.fieldnotes.hatunatu.dao.impl.RelationRowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.impl.RowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.impl.bean.Department;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee23;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.Assert.*;

public class BeanListMetaDataResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testHandle() throws Exception {
        BeanMetaData beanMetaData = test.createBeanMetaData(Employee.class);
        ResultSetHandler handler = new BeanListMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select * from emp";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        List ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (List) handler.handle(rs, test.getQueryObject());
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        for (int i = 0; i < ret.size(); ++i) {
            Employee emp = (Employee) ret.get(i);
            System.out.println(emp.getEmpno() + "," + emp.getEname());
        }
    }

    @Test
    public void testHandle2() throws Exception {
        BeanMetaData beanMetaData = test.createBeanMetaData(Employee.class);
        ResultSetHandler handler = new BeanListMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.*, dept.dname as dname_0 from emp, dept where emp.deptno = dept.deptno and emp.deptno = 20";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        List ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (List) handler.handle(rs, test.getQueryObject());
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        for (int i = 0; i < ret.size(); ++i) {
            Employee emp = (Employee) ret.get(i);
            System.out.println(emp);
            Department dept = emp.getDepartment();
            assertNotNull("2", dept);
            assertEquals("3", emp.getDeptno(), dept.getDeptno());
            assertNotNull("4", dept.getDname());
        }
    }

    @Test
    public void testHandle3() throws Exception {
        BeanMetaData beanMetaData = test.createBeanMetaData(Employee.class);
        ResultSetHandler handler = new BeanListMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.*, dept.deptno as deptno_0, dept.dname as dname_0 from emp, dept where dept.deptno = 20 and emp.deptno = dept.deptno";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        List ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (List) handler.handle(rs, test.getQueryObject());
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        Employee emp = (Employee) ret.get(0);
        Employee emp2 = (Employee) ret.get(1);
        assertSame("1", emp.getDepartment(), emp2.getDepartment());
    }

    @Test
    public void testHandle_relationship() throws Exception {
        BeanMetaData beanMetaData = test.createBeanMetaData(Employee23.class);
        ResultSetHandler handler = new BeanListMetaDataResultSetHandler(
                beanMetaData, createRowCreator(), createRelationRowCreator());
        String sql = "select emp.empno, emp.ename, emp.deptno, department.deptno as deptno_0, department.dname as dname_0 from EMP5 emp LEFT OUTER JOIN DEPT department on emp.deptno = department.deptno order by emp.empno";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        List ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (List) handler.handle(rs, test.getQueryObject());
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        Employee23 emp = (Employee23) ret.get(0);
        assertNull(emp.getDepartment());
        Employee23 emp2 = (Employee23) ret.get(1);

        Department dept2 = emp2.getDepartment();
        assertNotNull(dept2);
        assertEquals(10, dept2.getDeptno());
        assertEquals("ACCOUNTING", dept2.getDname());
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

}
