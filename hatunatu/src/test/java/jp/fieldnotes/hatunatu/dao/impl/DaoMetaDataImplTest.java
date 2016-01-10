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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.command.*;
import jp.fieldnotes.hatunatu.dao.exception.IllegalAnnotationRuntimeException;
import jp.fieldnotes.hatunatu.dao.exception.IllegalSignatureRuntimeException;
import jp.fieldnotes.hatunatu.dao.exception.MethodSetupFailureRuntimeException;
import jp.fieldnotes.hatunatu.dao.exception.SqlFileNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.bean.Department;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee3;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee4;
import jp.fieldnotes.hatunatu.dao.impl.condition.EmployeeSearchCondition;
import jp.fieldnotes.hatunatu.dao.impl.dao.*;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;
import jp.fieldnotes.hatunatu.dao.resultset.BeanArrayMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.BeanListMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.BeanMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.ObjectResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.TextUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class DaoMetaDataImplTest  {
    
    @Rule
    public HatunatuTest test = new HatunatuTest(this);


    @Test
    public void testSelectBeanList() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"getAllEmployees"));
        assertNotNull("1", cmd);
        assertEquals("2", "SELECT * FROM emp", cmd.getSql());
        BeanListMetaDataResultSetHandler rsh = (BeanListMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals("3", true, Employee.class.isAssignableFrom(
                rsh.getBeanMetaData().getBeanClass()));
        assertNotNull("4:can get from emp.", cmd.execute(new Object[0]));
    }

    @Test
    public void testSelectBeanArray() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"getAllEmployeeArray"));
        assertNotNull("1", cmd);
        BeanArrayMetaDataResultSetHandler rsh = (BeanArrayMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals("2", true, Employee.class.isAssignableFrom(
                rsh.getDtoMetaData().getBeanClass()));
    }

    @Test
    public void testSelectDtoArray() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"findAll"));
        assertNotNull("1", cmd);
        BeanArrayMetaDataResultSetHandler rsh = (BeanArrayMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals(EmployeeDto.class, rsh.getBeanMetaData().getBeanClass());
    }

    @Test
    public void testPrefixTest() {
        final DaoNamingConventionImpl daoNamingConvention = new DaoNamingConventionImpl();
        daoNamingConvention.setDaoSuffixes(new String[] { "Manager" });
        daoNamingConvention.setInsertPrefixes(new String[] { "generate" });
        daoNamingConvention.setUpdatePrefixes(new String[] { "change" });
        daoNamingConvention.setDeletePrefixes(new String[] { "terminate" });
        test.setDaoNamingConvention(daoNamingConvention);

        final Class daoClass = Employee8Manager.class;
        final DaoMetaDataImpl dmd = test.createDaoMetaData(daoClass);

        InsertAutoDynamicCommand cmd1 = (InsertAutoDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee8Manager.class,"generate"));
        assertNotNull(cmd1);
        // System.out.println(cmd.getSql());
        UpdateAutoStaticCommand cmd2 = (UpdateAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee8Manager.class,"change"));
        assertNotNull(cmd2);
        DeleteAutoStaticCommand cmd3 = (DeleteAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee8Manager.class,"terminate"));
        assertNotNull(cmd3);
        System.out.println(cmd3.getSql());
    }

    @Test
    public void testSelectBean() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"getEmployee"));
        assertNotNull("1", cmd);
        assertEquals("2", BeanMetaDataResultSetHandler.class, cmd
                .getResultSetHandler().getClass());
        assertEquals("3", "empno", cmd.getArgNames()[0]);
    }

    @Test
    public void testSelectObject() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"getCount"));
        assertNotNull("1", cmd);
        assertEquals("2", ObjectResultSetHandler.class, cmd
                .getResultSetHandler().getClass());
        assertEquals("3", "SELECT COUNT(*) FROM emp", cmd.getSql());
    }

    @Test
    public void testUpdate() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        UpdateDynamicCommand cmd = (UpdateDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"update"));
        assertNotNull("1", cmd);
        assertEquals("2", "employee", cmd.getArgNames()[0]);
    }

    @Test
    public void testInsertAutoTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"insert"));
        assertNotNull("1", cmd);
        Employee emp = new Employee();
        emp.setEmpno(new Integer(99));
        emp.setEname("hoge");
        cmd.execute(new Object[] { emp });
    }

    protected Object getProperty(Object obj, String name) {
        BeanDesc desc = BeanDescFactory.getBeanDesc(obj.getClass());
        PropertyDesc propertyDesc = desc.getPropertyDesc(name);
        return propertyDesc.getValue(obj);
    }

    @Test
    public void testUpdateAutoTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class, "update"));
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee)cmd2.execute(new Object[] { new Integer(7788) });
        emp.setEname("hoge2");;
        cmd.execute(new Object[] { emp });
    }

    @Test
    public void testUpdateNoCheckTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"update4"));
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee)cmd2.execute(new Object[] { new Integer(7788) });
        emp.setEname("hoge4");
        emp.setTimestamp(Timestamp
                .valueOf("1995-01-23 01:23:45.678"));
        Object obj2 = cmd.execute(new Object[] { emp });
        assertTrue("2", obj2 instanceof Integer);
        int ret = ((Integer) obj2).intValue();
        assertEquals("3", 0, ret);
    }

    @Test
    public void testDeleteAutoTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"delete"));
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Object emp = cmd2.execute(new Object[] { new Integer(7788) });
        cmd.execute(new Object[] { emp });
    }

    @Test
    public void testIllegalAutoUpdateMethod() throws Exception {
        try {
            DaoMetaData dmd = test.createDaoMetaData(IllegalEmployeeAutoDao.class);
            SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(IllegalEmployeeAutoDao.class, "insertIllegal"));
            fail("1");
        } catch (MethodSetupFailureRuntimeException ex) {
            assertTrue("1",
                    ex.getCause() instanceof IllegalSignatureRuntimeException);
            System.out.println(((IllegalSignatureRuntimeException) ex
                    .getCause()).getSignature());
            System.out.println(ex);
        }
    }

    @Test
    public void testSelectAuto() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeeByDeptno"));
        System.out.println(cmd.getSql());
    }

    @Test
    public void testInsertBatchAuto() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        InsertBatchAutoStaticCommand cmd = (InsertBatchAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"insertBatch"));
        assertNotNull("1", cmd);
    }

    @Test
    public void testUpdateBatchAuto() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        UpdateBatchAutoStaticCommand cmd = (UpdateBatchAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class, "updateBatch"));
        assertNotNull("1", cmd);
    }

    @Test
    public void testDeleteBatchAuto() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        DeleteBatchAutoStaticCommand cmd = (DeleteBatchAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"deleteBatch"));
        assertNotNull("1", cmd);
    }

    @Test
    public void testSelectAutoByQuery() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySal"));
        List employees = (List) cmd.execute(new Object[] { new Integer(0),
                new Integer(1000) });
        System.out.println(employees);
        assertEquals("1", 2, employees.size());
    }

    @Test
    public void testSelectAutoByQueryMultiIn() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesByEnameJob"));
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

    @Test
    public void testSelectCountBySqlFile1() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"getCount"));
        Object obj = cmd.execute(new Object[] {});
        System.out.println(obj.getClass());
        assertTrue("1", obj instanceof Integer);
        int ret = ((Integer) obj).intValue();
        assertEquals("2", 14, ret);
    }

    @Test
    public void testSelectCountBySqlFile2() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeDao.class,"getCount2"));
        Object obj = cmd.execute(new Object[] {});
        assertTrue("1", obj instanceof Integer);
        int ret = ((Integer) obj).intValue();
        assertEquals("2", 14, ret);
    }

    @Test
    public void testRelation1() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee2Dao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(Employee2Dao.class,"getAllEmployees"));
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
    @Test
    public void testRelation2() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee2Dao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(Employee2Dao.class,"getAllEmployeesOnly"));
        List emps = (List) cmd.execute(null);
        System.out.println(emps);
        assertTrue("1", emps.size() > 0);
        for (Iterator it = emps.iterator(); it.hasNext();) {
            Object emp = it.next();
            assertNull("2:" + emp.toString(), getProperty(emp, "department2"));
        }
    }

    @Test
    public void testRelation3Tx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        {
            SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"insert"));
            Employee emp = new Employee();
            emp.setEmpno(new Long(9999));
            emp.setEname("test");
            // Department:50 does not exist.
            emp.setDeptno(new Integer(50));
            cmd.execute(new Object[] { emp });
        }
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
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

    @Test
    public void testGetDaoInterface() throws Exception {
        DaoMetaDataImpl dmd = test.createDaoMetaData(Employee8Manager.class);
        assertEquals("1", EmployeeDao.class, dmd
                .getDaoInterface(EmployeeDao.class));
    }

    @Test
    public void testAutoSelectSqlByDto() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySearchCondition"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        EmployeeSearchCondition dto = new EmployeeSearchCondition();
        dto.setDname("RESEARCH");
        List employees = (List) cmd.execute(new Object[] { dto });
        assertTrue("2", employees.size() > 0);
    }

    @Test
    public void testAutoSelectSqlByDto2() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesByEmployee"));
        assertNotNull("1", cmd);
        Employee dto = new Employee();
        dto.setJob("MANAGER");
        List employees = (List) cmd.execute(new Object[] { dto });
        // assertTrue("2", employees.size() > 0);
    }

    @Test
    public void testAutoSelectSqlByDto3() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee3Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee3Dao.class, "getEmployees"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Employee3 dto = new Employee3();
        dto.setManager((short) 7902);
        List employees = (List) cmd.execute(new Object[] { dto });
        System.out.println(employees);
        assertTrue("2", employees.size() > 0);
    }

    @Test
    public void testAutoSelectSqlByDto4() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee3Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee3Dao.class,"getEmployees2"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        assertTrue("2", cmd.getSql().endsWith(" ORDER BY empno"));
    }

    @Test
    public void testAutoSelectSqlByDto5() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySearchCondition2"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        EmployeeSearchCondition dto = new EmployeeSearchCondition();
        Department department =new Department();
        department.setDname("RESEARCH");
        dto.setDepartment(department);
        List employees = (List) cmd.execute(new Object[] { dto });
        assertTrue("2", employees.size() > 0);
    }

    @Test
    public void testAutoSelectSqlByDto6() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySearchCondition2"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        EmployeeSearchCondition dto = new EmployeeSearchCondition();
        dto.setDepartment(null);
        List employees = (List) cmd.execute(new Object[] { dto });
        assertEquals("2", 0, employees.size());
    }

    @Test
    public void testSelfReference() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee4Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee4Dao.class,"getEmployee"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Employee4 employee = (Employee4)cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        Employee4 parent = employee.getParent();
        assertEquals("2", new Long(7566), parent.getEmpno());
    }

    @Test
    public void testSelfMultiPk() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee5Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee5Dao.class,"getEmployee"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
    }

    @Test
    public void testNotHavePrimaryKey() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(DepartmentTotalSalaryDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(DepartmentTotalSalaryDao.class,"getTotalSalaries"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        List result = (List) cmd.execute(null);
        System.out.println(result);
    }

    @Test
    public void testSelectAutoFullColumnName() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        System.out.println(cmd.getSql());
    }

    @Test
    public void testStartsWithOrderBy() throws Exception {
        DaoMetaDataImpl dmd = test.createDaoMetaData(Employee6Dao.class);
        EmployeeSearchCondition condition = new EmployeeSearchCondition();
        condition.setDname("RESEARCH");;
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee6Dao.class,"getEmployees"));
        cmd.execute(new Object[] { condition });
        condition.setOrderByString("ENAME");
        cmd.execute(new Object[] { condition });
    }

    @Test
    public void testStartsWithBeginComment() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee8Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee8Dao.class,"getEmployees"));
        System.out.println(cmd.getSql());
        {
            Employee emp = new Employee();
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(14, results.size());
        }
        {
            Employee emp = new Employee();;
            emp.setEname("SMITH");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(1, results.size());
        }
        {
            Employee emp = new Employee();;
            emp.setJob("SALESMAN");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(4, results.size());
        }
        {
            Employee emp = new Employee();
            emp.setEname("SMITH");
            emp.setJob("CLERK");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(1, results.size());
        }
        {
            Employee emp = new Employee();;
            emp.setEname("a");
            emp.setJob("b");
            List results = (List) cmd.execute(new Object[] { emp });
            assertEquals(0, results.size());
        }
    }

    @Test
    public void testQueryAnnotationTx() throws Exception {
        DaoMetaDataImpl dmd = test.createDaoMetaData(Employee7Dao.class);
        SelectDynamicCommand cmd1 = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee7Dao.class,"getCount"));
        UpdateDynamicCommand cmd2 = (UpdateDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee7Dao.class,"deleteEmployee"));
        System.out.println(cmd1.getSql());
        System.out.println(cmd2.getSql());
        assertEquals(new Integer(14), cmd1.execute(null));
        assertEquals(new Integer(1), cmd2.execute(new Object[] { new Integer(
                7369) }));
        assertEquals(new Integer(13), cmd1.execute(null));
    }


    @Test
    public void testDaoExtend2() throws Exception {
        DaoMetaDataImpl dmd = test.createDaoMetaData(EmployeeExDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(EmployeeExDao.class,"getEmployee"));
        final String expected = TextUtil.readText(EmployeeDao.class
                .getPackage().getName().replace('.', '/')
                + "/" + "EmployeeDao_getEmployee.sql");
        assertEquals(expected, cmd.getSql());
    }

    @Test
    public void testUsingColumnAnnotationForSql_Update() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee9Dao.class);
        UpdateAutoStaticCommand cmd = (UpdateAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee9Dao.class,"update"));
        final String sql = cmd.getSql();
        System.out.println(sql);
        assertEquals(sql, true, sql.indexOf("eNaMe") > -1);
    }

    @Test
    public void testUsingColumnAnnotationForSql_Select() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee9Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee9Dao.class, "findBy"));
        final String sql = cmd.getSql();
        System.out.println(sql);
        final int pos = sql.indexOf("WHERE");
        final String before = sql.substring(0, pos);
        final String after = sql.substring(pos);
        assertEquals(before, true, before.indexOf("EMP.eNaMe") > -1);
        assertEquals(after, true, after.indexOf("EMP.eNaMe") > -1);
    }

    @Test
    public void testUsingColumnAnnotationForSql_SelectDto() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee9Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee9Dao.class,"findByEname"));
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
    @Test
    public void testSelectWithNullArgs() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Employee10Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Employee10Dao.class,"getEmployeesByJob"));
        final List emps = (List) cmd.execute(new Object[] { null });
        assertEquals(14, emps.size());
    }

    @Test
    public void testNoSqlFile() throws Exception {
        // ## Arrange ##
        final Class daoClass = Employee11Dao.class;

        // ## Act ##
        // ## Assert ##
        try {
            DaoMetaData dmd = test.createDaoMetaData(daoClass);
            SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Employee11Dao.class, "insert"));
            fail();
        } catch (final MethodSetupFailureRuntimeException e) {
            System.out.println(e.getMessage());
            final SqlFileNotFoundRuntimeException cause = (SqlFileNotFoundRuntimeException) e
                    .getCause();
            assertEquals("EDAO0025", cause.getMessageCode());
        }

    }

    @Test
    public void testDeleteByQuery() throws Exception {
        final Class daoClass = Employee12Dao.class;
        DaoMetaData metaData = test.createDaoMetaData(daoClass);
        UpdateDynamicCommand cmd = (UpdateDynamicCommand) metaData
                .getSqlCommand(test.getSingleDaoMethod(Employee12Dao.class,"delete"));
        assertEquals("DELETE FROM EMP WHERE EMPNO = /*no*/1111", cmd.getSql());

        cmd = (UpdateDynamicCommand) metaData.getSqlCommand(test.getSingleDaoMethod(Employee12Dao.class,"deleteNoWhere"));
        assertEquals("DELETE FROM EMP WHERE EMPNO = ?", cmd.getSql());

    }

    @Test
    public void testAssertAnnotation() throws Exception {
        final Class daoClass = Employee13Dao.class;
        try {
            test.createDaoMetaData(daoClass);
        } catch (MethodSetupFailureRuntimeException e) {
            IllegalAnnotationRuntimeException cause = (IllegalAnnotationRuntimeException) e
                    .getCause();
            System.out.println(cause);
            assertEquals("EDAO0026", cause.getMessageCode());
        }

    }

    @Test
    public void testAssertAnnotation2() throws Exception {
        final Class daoClass = Employee14Dao.class;
        try {
            test.createDaoMetaData(daoClass);
        } catch (MethodSetupFailureRuntimeException e) {
            IllegalAnnotationRuntimeException cause = (IllegalAnnotationRuntimeException) e
                    .getCause();
            System.out.println(cause);
            assertEquals("EDAO0026", cause.getMessageCode());
        }

    }

}
