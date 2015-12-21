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

import org.seasar.dao.*;
import org.seasar.dao.dbms.Oracle;
import org.seasar.dao.impl.bean.Department;
import org.seasar.dao.impl.bean.Employee;
import org.seasar.dao.impl.bean.Employee3;
import org.seasar.dao.impl.bean.Employee9;
import org.seasar.dao.impl.condition.EmployeeSearchCondition;
import org.seasar.dao.impl.dao.*;
import org.seasar.dao.impl.dto.EmployeeDto;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.TextUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author higa
 * @author manhole
 */
public class DaoMetaDataImplTest extends S2DaoTestCase {

    @Override
    public void setUp(){
        include("j2ee.dicon");
    }


    protected Class getBeanClass(String className) {
        if (className.equals("Employee")) {
            return Employee.class;
        }
        fail(className);
        return null;
    }

    protected Object getBean(String className) {
        if (className.equals("Employee")) {
            return new Employee();
        } else if (className.equals("Employee3")) {
            return new Employee3();
        } else if (className.equals("Employee9")) {
            return new Employee9();
        } else if (className.equals("EmployeeSearchCondition")) {
            return new EmployeeSearchCondition();
        } else if (className.equals("Department")) {
            return new Department();
        }
        fail(className);
        return null;
    }

    protected Class getDaoClass(String className) {
        if (className.equals("EmployeeDao")) {
            return EmployeeDao.class;
        } else if (className.equals("EmployeeAutoDao")) {
            return EmployeeAutoDao.class;
        } else if (className.equals("IllegalEmployeeAutoDao")) {
            return IllegalEmployeeAutoDao.class;
        } else if (className.equals("Employee2Dao")) {
            return Employee2Dao.class;
        } else if (className.equals("Employee3Dao")) {
            return Employee3Dao.class;
        } else if (className.equals("Employee4Dao")) {
            return Employee4Dao.class;
        } else if (className.equals("Employee5Dao")) {
            return Employee5Dao.class;
        } else if (className.equals("Employee6Dao")) {
            return Employee6Dao.class;
        } else if (className.equals("Employee7Dao")) {
            return Employee7Dao.class;
        } else if (className.equals("Employee8Dao")) {
            return Employee8Dao.class;
        } else if (className.equals("Employee9Dao")) {
            return Employee9Dao.class;
        } else if (className.equals("Employee10Dao")) {
            return Employee10Dao.class;
        } else if (className.equals("Employee11Dao")) {
            return Employee11Dao.class;
        } else if (className.equals("Employee12Dao")) {
            return Employee12Dao.class;
        } else if (className.equals("Employee13Dao")) {
            return Employee13Dao.class;
        } else if (className.equals("Employee14Dao")) {
            return Employee14Dao.class;
        } else if (className.equals("Employee8Manager")) {
            return Employee8Manager.class;
        } else if (className.equals("DepartmentTotalSalaryDao")) {
            return DepartmentTotalSalaryDao.class;
        } else if (className.equals("EmployeeExDao")) {
            return EmployeeExDao.class;
        }
        fail();
        return null;
    }

    public void testSelectBeanList() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getAllEmployees");
        assertNotNull("1", cmd);
        assertEquals("2", "SELECT * FROM emp", cmd.getSql());
        BeanListMetaDataResultSetHandler rsh = (BeanListMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals("3", true, getBeanClass("Employee").isAssignableFrom(
                rsh.getBeanMetaData().getBeanClass()));
    }

    public void testSelectBeanArray() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getAllEmployeeArray");
        assertNotNull("1", cmd);
        BeanArrayMetaDataResultSetHandler rsh = (BeanArrayMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals("2", true, getBeanClass("Employee").isAssignableFrom(
                rsh.getBeanMetaData().getBeanClass()));
    }

    public void testSelectMapArray() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getAllEmployeeMap");
        assertNotNull("1", cmd);
        assertEquals(MapArrayResultSetHandler.class, cmd.getResultSetHandler()
                .getClass());
    }

    public void testSelectDtoArray() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("findAll");
        assertNotNull("1", cmd);
        DtoArrayMetaDataResultSetHandler rsh = (DtoArrayMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals(EmployeeDto.class, rsh.getDtoMetaData().getBeanClass());
    }

    public void testPrefixTest() {
        final DaoNamingConventionImpl daoNamingConvention = new DaoNamingConventionImpl();
        daoNamingConvention.setDaoSuffixes(new String[] { "Manager" });
        daoNamingConvention.setInsertPrefixes(new String[] { "generate" });
        daoNamingConvention.setUpdatePrefixes(new String[] { "change" });
        daoNamingConvention.setDeletePrefixes(new String[] { "terminate" });
        setDaoNamingConvention(daoNamingConvention);

        final Class daoClass = getDaoClass("Employee8Manager");
        final DaoMetaDataImpl dmd = createDaoMetaData(daoClass);

        InsertAutoDynamicCommand cmd1 = (InsertAutoDynamicCommand) dmd
                .getSqlCommand("generate");
        assertNotNull(cmd1);
        // System.out.println(cmd.getSql());
        UpdateAutoStaticCommand cmd2 = (UpdateAutoStaticCommand) dmd
                .getSqlCommand("change");
        assertNotNull(cmd2);
        System.out.println(cmd2.getSql());
        DeleteAutoStaticCommand cmd3 = (DeleteAutoStaticCommand) dmd
                .getSqlCommand("terminate");
        assertNotNull(cmd3);
        System.out.println(cmd3.getSql());
    }

    public void testSelectBean() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        assertNotNull("1", cmd);
        assertEquals("2", BeanMetaDataResultSetHandler.class, cmd
                .getResultSetHandler().getClass());
        assertEquals("3", "empno", cmd.getArgNames()[0]);
    }

    public void testSelectObject() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getCount");
        assertNotNull("1", cmd);
        assertEquals("2", ObjectResultSetHandler.class, cmd
                .getResultSetHandler().getClass());
        assertEquals("3", "SELECT COUNT(*) FROM emp", cmd.getSql());
    }

    public void testUpdate() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        UpdateDynamicCommand cmd = (UpdateDynamicCommand) dmd
                .getSqlCommand("update");
        assertNotNull("1", cmd);
        assertEquals("2", "employee", cmd.getArgNames()[0]);
    }

    public void testInsertAutoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.getSqlCommand("insert");
        assertNotNull("1", cmd);
        Object emp = getBean("Employee");
        setProperty(emp, "empno", new Integer(99));
        setProperty(emp, "ename", "hoge");
        cmd.execute(new Object[] { emp });
    }

    protected void setProperty(Object obj, String name, Object value) {
        BeanDesc desc = BeanDescFactory.getBeanDesc(obj.getClass());
        PropertyDesc propertyDesc = desc.getPropertyDesc(name);
        propertyDesc.setValue(obj, value);
    }

    protected Object getProperty(Object obj, String name) {
        BeanDesc desc = BeanDescFactory.getBeanDesc(obj.getClass());
        PropertyDesc propertyDesc = desc.getPropertyDesc(name);
        return propertyDesc.getValue(obj);
    }

    public void testUpdateAutoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.getSqlCommand("update");
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        Object emp = cmd2.execute(new Object[] { new Integer(7788) });
        setProperty(emp, "ename", "hoge2");
        cmd.execute(new Object[] { emp });
    }

    public void testUpdateNoCheckTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.getSqlCommand("update4");
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        Object emp = cmd2.execute(new Object[] { new Integer(7788) });
        setProperty(emp, "ename", "hoge4");
        setProperty(emp, "timestamp", Timestamp
                .valueOf("1995-01-23 01:23:45.678"));
        Object obj2 = cmd.execute(new Object[] { emp });
        assertTrue("2", obj2 instanceof Integer);
        int ret = ((Integer) obj2).intValue();
        assertEquals("3", 0, ret);
    }

    public void testDeleteAutoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.getSqlCommand("delete");
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        Object emp = cmd2.execute(new Object[] { new Integer(7788) });
        cmd.execute(new Object[] { emp });
    }

    public void testIllegalAutoUpdateMethod() throws Exception {
        try {
            createDaoMetaData(getDaoClass("IllegalEmployeeAutoDao"));
            fail("1");
        } catch (MethodSetupFailureRuntimeException ex) {
            assertTrue("1",
                    ex.getCause() instanceof IllegalSignatureRuntimeException);
            System.out.println(((IllegalSignatureRuntimeException) ex
                    .getCause()).getSignature());
            System.out.println(ex);
        }
    }

    public void testSelectAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeeByDeptno");
        System.out.println(cmd.getSql());
    }

    public void testInsertBatchAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        InsertBatchAutoStaticCommand cmd = (InsertBatchAutoStaticCommand) dmd
                .getSqlCommand("insertBatch");
        assertNotNull("1", cmd);
    }

    public void testUpdateBatchAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        UpdateBatchAutoStaticCommand cmd = (UpdateBatchAutoStaticCommand) dmd
                .getSqlCommand("updateBatch");
        assertNotNull("1", cmd);
    }

    public void testDeleteBatchAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        DeleteBatchAutoStaticCommand cmd = (DeleteBatchAutoStaticCommand) dmd
                .getSqlCommand("deleteBatch");
        assertNotNull("1", cmd);
    }

    public void testCreateFindCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.createFindCommand(null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.createFindCommand(null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand3() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindCommand("select * from emp");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand4() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindCommand("order by empno");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand5() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        dmd.setDbms(new Oracle());
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .createFindCommand("empno = ?");
        System.out.println(cmd.getSql());
        assertTrue("1", cmd.getSql().endsWith(" AND empno = ?"));
    }

    public void testCreateFindCommandByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class, null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindCommand2ByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class, null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindCommand3ByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class,
                "select * from emp");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindCommand4ByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class,
                "order by empno");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindBeanCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindBeanCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
    }

    public void testCreateObjectBeanCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd
                .createFindObjectCommand("select count(*) from emp");
        Long count = (Long) cmd.execute(null);
        assertEquals("1", 14L, count.intValue());
    }

    public void testCreateFindBeanCommandByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindBeanCommand(EmployeeDto.class,
                "empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof EmployeeDto);
    }

    public void testCreateFindMapCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindMapCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof Map);
    }

    public void testCreateFindMapListCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindMapListCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof List);
        assertTrue("3", ((List) employee).get(0) instanceof Map);
    }

    public void testCreateFindMapArrayCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));

        SqlCommand cmd = dmd.createFindMapArrayCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof Map[]);
    }

    public void testSelectAutoByQuery() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SqlCommand cmd = dmd.getSqlCommand("getEmployeesBySal");
        List employees = (List) cmd.execute(new Object[] { new Integer(0),
                new Integer(1000) });
        System.out.println(employees);
        assertEquals("1", 2, employees.size());
    }

    public void testSelectAutoByQueryMultiIn() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeesByEnameJob");
        System.out.println(cmd.getSql());
        List enames = new ArrayList();
        enames.add("SCOTT");
        enames.add("MARY");
        List jobs = new ArrayList();
        jobs.add("ANALYST");
        jobs.add("FREE");
        List employees = (List) cmd.execute(new Object[] { enames, jobs });
        System.out.println(employees);
        assertEquals("1", 1, employees.size());
    }

    public void testSelectCountBySqlFile1() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getCount");
        Object obj = cmd.execute(new Object[] {});
        assertTrue("1", obj instanceof Integer);
        int ret = ((Integer) obj).intValue();
        assertEquals("2", 14, ret);
    }

    public void testSelectCountBySqlFile2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getCount2");
        Object obj = cmd.execute(new Object[] {});
        assertTrue("1", obj instanceof Integer);
        int ret = ((Integer) obj).intValue();
        assertEquals("2", 14, ret);
    }

    public void testRelation1() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee2Dao"));
        SqlCommand cmd = dmd.getSqlCommand("getAllEmployees");
        List emps = (List) cmd.execute(null);
        System.out.println(emps);
        assertTrue("1", emps.size() > 0);
        for (Iterator it = emps.iterator(); it.hasNext();) {
            Object emp = it.next();
            assertNotNull("2:" + emp.toString(),
                    getProperty(emp, "department2"));
        }
    }

    // [Seasar-user:3605][DAO-7]
    public void testRelation2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee2Dao"));
        SqlCommand cmd = dmd.getSqlCommand("getAllEmployeesOnly");
        List emps = (List) cmd.execute(null);
        System.out.println(emps);
        assertTrue("1", emps.size() > 0);
        for (Iterator it = emps.iterator(); it.hasNext();) {
            Object emp = it.next();
            assertNull("2:" + emp.toString(), getProperty(emp, "department2"));
        }
    }

    public void testRelation3Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        {
            SqlCommand cmd = dmd.getSqlCommand("insert");
            Object emp = getBean("Employee");
            setProperty(emp, "empno", new Integer(9999));
            setProperty(emp, "ename", "test");
            // Department:50 does not exist.
            setProperty(emp, "deptno", new Integer(50));
            cmd.execute(new Object[] { emp });
        }
        SqlCommand cmd = dmd.getSqlCommand("getEmployee");
        {
            Object emp = cmd.execute(new Object[] { new Integer(7369) });
            System.out.println(emp);
            assertNotNull(getProperty(emp, "department"));
        }
        {
            Object emp = cmd.execute(new Object[] { new Integer(9999) });
            System.out.println(emp);
            assertNotNull(getProperty(emp, "department"));
        }
    }

    public void testGetDaoInterface() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(getDaoClass("Employee8Manager"));
        assertEquals("1", EmployeeDao.class, dmd
                .getDaoInterface(EmployeeDao.class));
    }

    public void testAutoSelectSqlByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeesBySearchCondition");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Object dto = getBean("EmployeeSearchCondition");
        setProperty(dto, "dname", "RESEARCH");
        List employees = (List) cmd.execute(new Object[] { dto });
        assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeesByEmployee");
        assertNotNull("1", cmd);
        Object dto = getBean("Employee");
        setProperty(dto, "job", "MANAGER");
        List employees = (List) cmd.execute(new Object[] { dto });
        // assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto3() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee3Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployees");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Object dto = getBean("Employee3");
        BeanDesc desc = BeanDescFactory.getBeanDesc(dto.getClass());
        PropertyDesc propertyDesc = desc.getPropertyDesc("manager");
        propertyDesc.setValue(dto, (new Short((short) 7902)));
        List employees = (List) cmd.execute(new Object[] { dto });
        System.out.println(employees);
        assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto4() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee3Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployees2");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        assertTrue("2", cmd.getSql().endsWith(" ORDER BY empno"));
    }

    public void testAutoSelectSqlByDto5() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeesBySearchCondition2");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Object dto = getBean("EmployeeSearchCondition");
        Object department = getBean("Department");
        setProperty(department, "dname", "RESEARCH");
        setProperty(dto, "department", department);
        List employees = (List) cmd.execute(new Object[] { dto });
        assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto6() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeesBySearchCondition2");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Object dto = getBean("EmployeeSearchCondition");
        setProperty(dto, "department", null);
        List employees = (List) cmd.execute(new Object[] { dto });
        assertEquals("2", 0, employees.size());
    }

    public void testSelfReference() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee4Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        Object parent = getProperty(employee, "parent");
        assertEquals("2", new Long(7566), getProperty(parent, "empno"));
    }

    public void testSelfMultiPk() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee5Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
    }

    public void testNotHavePrimaryKey() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("DepartmentTotalSalaryDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getTotalSalaries");
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        List result = (List) cmd.execute(null);
        System.out.println(result);
    }

    public void testSelectAutoFullColumnName() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("EmployeeAutoDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        System.out.println(cmd.getSql());
    }

    public void testStartsWithOrderBy() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(getDaoClass("Employee6Dao"));
        Object condition = getBean("EmployeeSearchCondition");
        setProperty(condition, "dname", "RESEARCH");
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployees");
        System.out.println(cmd.getSql());
        cmd.execute(new Object[] { condition });
        setProperty(condition, "orderByString", "ENAME");
        cmd.execute(new Object[] { condition });
    }

    public void testStartsWithBeginComment() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee8Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployees");
        System.out.println(cmd.getSql());
        {
            Object emp = getBean("Employee");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(14, results.size());
        }
        {
            Object emp = getBean("Employee");
            setProperty(emp, "ename", "SMITH");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(1, results.size());
        }
        {
            Object emp = getBean("Employee");
            setProperty(emp, "job", "SALESMAN");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(4, results.size());
        }
        {
            Object emp = getBean("Employee");
            setProperty(emp, "ename", "SMITH");
            setProperty(emp, "job", "CLERK");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(1, results.size());
        }
        {
            Object emp = getBean("Employee");
            setProperty(emp, "ename", "a");
            setProperty(emp, "job", "b");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(0, results.size());
        }
    }

    public void testQueryAnnotationTx() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(getDaoClass("Employee7Dao"));
        SelectDynamicCommand cmd1 = (SelectDynamicCommand) dmd
                .getSqlCommand("getCount");
        UpdateDynamicCommand cmd2 = (UpdateDynamicCommand) dmd
                .getSqlCommand("deleteEmployee");
        System.out.println(cmd1.getSql());
        System.out.println(cmd2.getSql());
        assertEquals(new Integer(14), cmd1.execute(null));
        assertEquals(new Integer(1), cmd2.execute(new Object[] { new Integer(
                7369) }));
        assertEquals(new Integer(13), cmd1.execute(null));
    }



    public void testDaoExtend2() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(getDaoClass("EmployeeExDao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployee");
        final String expected = TextUtil.readText(EmployeeDao.class
                .getPackage().getName().replace('.', '/')
                + "/" + "EmployeeDao_getEmployee.sql");
        assertEquals(expected, cmd.getSql());
    }

    public void testUsingColumnAnnotationForSql_Insert() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee9Dao"));
        InsertAutoDynamicCommand cmd = (InsertAutoDynamicCommand) dmd
                .getSqlCommand("insert");
        Object bean = getBean("Employee9");
        setProperty(bean, "empno", new Integer(321));
        setProperty(bean, "ename", "foo");
        final PropertyType[] propertyTypes = cmd.createInsertPropertyTypes(cmd
                .getBeanMetaData(), bean, cmd.getPropertyNames());
        final String sql = cmd.createInsertSql(cmd.getBeanMetaData(),
                propertyTypes);
        System.out.println(sql);
        assertEquals(sql, true, sql.indexOf("eNaMe") > -1);
    }

    public void testUsingColumnAnnotationForSql_Update() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee9Dao"));
        UpdateAutoStaticCommand cmd = (UpdateAutoStaticCommand) dmd
                .getSqlCommand("update");
        final String sql = cmd.getSql();
        System.out.println(sql);
        assertEquals(sql, true, sql.indexOf("eNaMe") > -1);
    }

    public void testUsingColumnAnnotationForSql_Select() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee9Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("findBy");
        final String sql = cmd.getSql();
        System.out.println(sql);
        final int pos = sql.indexOf("WHERE");
        final String before = sql.substring(0, pos);
        final String after = sql.substring(pos);
        assertEquals(before, true, before.indexOf("EMP.eNaMe") > -1);
        assertEquals(after, true, after.indexOf("EMP.eNaMe") > -1);
    }

    public void testUsingColumnAnnotationForSql_SelectDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee9Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("findByEname");
        final String sql = cmd.getSql();
        System.out.println(sql);
        final int pos = sql.indexOf("WHERE");
        final String before = sql.substring(0, pos);
        final String after = sql.substring(pos);
        assertEquals(before, true, before.indexOf("EMP.eNaMe") > -1);
        assertEquals(after, true, after.indexOf("EMP.eName") > -1);
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-32
     */
    public void testSelectWithNullArgs() throws Exception {
        DaoMetaData dmd = createDaoMetaData(getDaoClass("Employee10Dao"));
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand("getEmployeesByJob");
        final List emps = (List) cmd.execute(new Object[] { null });
        assertEquals(14, emps.size());
    }

    public void testNoSqlFile() throws Exception {
        // ## Arrange ##
        final Class daoClass = getDaoClass("Employee11Dao");

        // ## Act ##
        // ## Assert ##
        try {
            createDaoMetaData(daoClass);
            fail();
        } catch (final MethodSetupFailureRuntimeException e) {
            System.out.println(e.getMessage());
            final SqlFileNotFoundRuntimeException cause = (SqlFileNotFoundRuntimeException) e
                    .getCause();
            assertEquals("EDAO0025", cause.getMessageCode());
        }

    }

    public void testDeleteByQuery() throws Exception {
        final Class daoClass = getDaoClass("Employee12Dao");
        DaoMetaData metaData = createDaoMetaData(daoClass);
        UpdateDynamicCommand cmd = (UpdateDynamicCommand) metaData
                .getSqlCommand("delete");
        assertEquals("DELETE FROM EMP WHERE EMPNO = /*no*/1111", cmd.getSql());

        cmd = (UpdateDynamicCommand) metaData.getSqlCommand("deleteNoWhere");
        assertEquals("DELETE FROM EMP WHERE EMPNO = ?", cmd.getSql());

    }

    public void testAssertAnnotation() throws Exception {
        final Class daoClass = getDaoClass("Employee13Dao");
        try {
            createDaoMetaData(daoClass);
        } catch (MethodSetupFailureRuntimeException e) {
            IllegalAnnotationRuntimeException cause = (IllegalAnnotationRuntimeException) e
                    .getCause();
            System.out.println(cause);
            assertEquals("EDAO0026", cause.getMessageCode());
        }

    }

    public void testAssertAnnotation2() throws Exception {
        final Class daoClass = getDaoClass("Employee14Dao");
        try {
            createDaoMetaData(daoClass);
        } catch (MethodSetupFailureRuntimeException e) {
            IllegalAnnotationRuntimeException cause = (IllegalAnnotationRuntimeException) e
                    .getCause();
            System.out.println(cause);
            assertEquals("EDAO0026", cause.getMessageCode());
        }

    }

}
