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
import jp.fieldnotes.hatunatu.dao.dbms.Oracle;
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
import jp.fieldnotes.hatunatu.dao.resultset.*;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.TextUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DaoMetaDataImplTest extends S2DaoTestCase {

    @Override
    public void setUp(){
        include("j2ee.dicon");
    }

    public void testSelectBeanList() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getAllEmployees"));
        assertNotNull("1", cmd);
        assertEquals("2", "SELECT * FROM emp", cmd.getSql());
        BeanListMetaDataResultSetHandler rsh = (BeanListMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals("3", true, Employee.class.isAssignableFrom(
                rsh.getBeanMetaData().getBeanClass()));
    }

    public void testSelectBeanArray() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getAllEmployeeArray"));
        assertNotNull("1", cmd);
        BeanArrayMetaDataResultSetHandler rsh = (BeanArrayMetaDataResultSetHandler) cmd
                .getResultSetHandler();
        assertEquals("2", true, Employee.class.isAssignableFrom(
                rsh.getBeanMetaData().getBeanClass()));
    }

    public void testSelectMapArray() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getAllEmployeeMap"));
        assertNotNull("1", cmd);
        assertEquals(MapArrayResultSetHandler.class, cmd.getResultSetHandler()
                .getClass());
    }

    public void testSelectDtoArray() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"findAll"));
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

        final Class daoClass = Employee8Manager.class;
        final DaoMetaDataImpl dmd = createDaoMetaData(daoClass);

        InsertAutoDynamicCommand cmd1 = (InsertAutoDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee8Manager.class,"generate"));
        assertNotNull(cmd1);
        // System.out.println(cmd.getSql());
        UpdateAutoStaticCommand cmd2 = (UpdateAutoStaticCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee8Manager.class,"change"));
        assertNotNull(cmd2);
        DeleteAutoStaticCommand cmd3 = (DeleteAutoStaticCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee8Manager.class,"terminate"));
        assertNotNull(cmd3);
        System.out.println(cmd3.getSql());
    }

    public void testSelectBean() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getEmployee"));
        assertNotNull("1", cmd);
        assertEquals("2", BeanMetaDataResultSetHandler.class, cmd
                .getResultSetHandler().getClass());
        assertEquals("3", "empno", cmd.getArgNames()[0]);
    }

    public void testSelectObject() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getCount"));
        assertNotNull("1", cmd);
        assertEquals("2", ObjectResultSetHandler.class, cmd
                .getResultSetHandler().getClass());
        assertEquals("3", "SELECT COUNT(*) FROM emp", cmd.getSql());
    }

    public void testUpdate() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        UpdateDynamicCommand cmd = (UpdateDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"update"));
        assertNotNull("1", cmd);
        assertEquals("2", "employee", cmd.getArgNames()[0]);
    }

    public void testInsertAutoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"insert"));
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

    public void testUpdateAutoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class, "update"));
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee)cmd2.execute(new Object[] { new Integer(7788) });
        emp.setEname("hoge2");;
        cmd.execute(new Object[] { emp });
    }

    public void testUpdateNoCheckTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"update4"));
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee)cmd2.execute(new Object[] { new Integer(7788) });
        emp.setEname("hoge4");
        emp.setTimestamp(Timestamp
                .valueOf("1995-01-23 01:23:45.678"));
        Object obj2 = cmd.execute(new Object[] { emp });
        assertTrue("2", obj2 instanceof Integer);
        int ret = ((Integer) obj2).intValue();
        assertEquals("3", 0, ret);
    }

    public void testDeleteAutoTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"delete"));
        assertNotNull("1", cmd);
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Object emp = cmd2.execute(new Object[] { new Integer(7788) });
        cmd.execute(new Object[] { emp });
    }

    public void testIllegalAutoUpdateMethod() throws Exception {
        try {
            DaoMetaData dmd = createDaoMetaData(IllegalEmployeeAutoDao.class);
            SqlCommand command = dmd.getSqlCommand(getSingleDaoMethod(IllegalEmployeeAutoDao.class, "insertIllegal"));
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
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeeByDeptno"));
        System.out.println(cmd.getSql());
    }

    public void testInsertBatchAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        InsertBatchAutoStaticCommand cmd = (InsertBatchAutoStaticCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"insertBatch"));
        assertNotNull("1", cmd);
    }

    public void testUpdateBatchAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        UpdateBatchAutoStaticCommand cmd = (UpdateBatchAutoStaticCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class, "updateBatch"));
        assertNotNull("1", cmd);
    }

    public void testDeleteBatchAuto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        DeleteBatchAutoStaticCommand cmd = (DeleteBatchAutoStaticCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"deleteBatch"));
        assertNotNull("1", cmd);
    }

    public void testCreateFindCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.createFindCommand(null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.createFindCommand(null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand3() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindCommand("select * from emp");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand4() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindCommand("order by empno");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
    }

    public void testCreateFindCommand5() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(EmployeeAutoDao.class);

        dmd.setDbms(new Oracle());
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .createFindCommand("empno = ?");
        System.out.println(cmd.getSql());
        assertTrue("1", cmd.getSql().endsWith(" AND empno = ?"));
    }

    public void testCreateFindCommandByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class, null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindCommand2ByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class, null);
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindCommand3ByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class,
                "select * from emp");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindCommand4ByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindCommand(EmployeeDto.class,
                "order by empno");
        List employees = (List) cmd.execute(null);
        System.out.println(employees);
        assertTrue("1", employees.size() > 0);
        assertTrue("2", employees.get(0) instanceof EmployeeDto);
    }

    public void testCreateFindBeanCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindBeanCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
    }



    public void testCreateFindBeanCommandByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindBeanCommand(EmployeeDto.class,
                "empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof EmployeeDto);
    }

    public void testCreateFindMapCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindMapCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof Map);
    }

    public void testCreateFindMapListCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData((EmployeeAutoDao.class));

        SqlCommand cmd = dmd.createFindMapListCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof List);
        assertTrue("3", ((List) employee).get(0) instanceof Map);
    }

    public void testCreateFindMapArrayCommand() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);

        SqlCommand cmd = dmd.createFindMapArrayCommand("empno = ?");
        Object employee = cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        assertNotNull("1", employee);
        assertTrue("2", employee instanceof Map[]);
    }

    public void testSelectAutoByQuery() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySal"));
        List employees = (List) cmd.execute(new Object[] { new Integer(0),
                new Integer(1000) });
        System.out.println(employees);
        assertEquals("1", 2, employees.size());
    }

    public void testSelectAutoByQueryMultiIn() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesByEnameJob"));
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
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getCount"));
        Object obj = cmd.execute(new Object[] {});
        assertTrue("1", obj instanceof Integer);
        int ret = ((Integer) obj).intValue();
        assertEquals("2", 14, ret);
    }

    public void testSelectCountBySqlFile2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeDao.class,"getCount2"));
        Object obj = cmd.execute(new Object[] {});
        assertTrue("1", obj instanceof Integer);
        int ret = ((Integer) obj).intValue();
        assertEquals("2", 14, ret);
    }

    public void testRelation1() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee2Dao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(Employee2Dao.class,"getAllEmployees"));
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
        DaoMetaData dmd = createDaoMetaData(Employee2Dao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(Employee2Dao.class,"getAllEmployeesOnly"));
        List emps = (List) cmd.execute(null);
        System.out.println(emps);
        assertTrue("1", emps.size() > 0);
        for (Iterator it = emps.iterator(); it.hasNext();) {
            Object emp = it.next();
            assertNull("2:" + emp.toString(), getProperty(emp, "department2"));
        }
    }

    public void testRelation3Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        {
            SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"insert"));
            Employee emp = new Employee();
            emp.setEmpno(new Long(9999));
            emp.setEname("test");
            // Department:50 does not exist.
            emp.setDeptno(new Integer(50));
            cmd.execute(new Object[] { emp });
        }
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
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
        DaoMetaDataImpl dmd = createDaoMetaData(Employee8Manager.class);
        assertEquals("1", EmployeeDao.class, dmd
                .getDaoInterface(EmployeeDao.class));
    }

    public void testAutoSelectSqlByDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySearchCondition"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        EmployeeSearchCondition dto = new EmployeeSearchCondition();
        dto.setDname("RESEARCH");
        List employees = (List) cmd.execute(new Object[] { dto });
        assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto2() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesByEmployee"));
        assertNotNull("1", cmd);
        Employee dto = new Employee();
        dto.setJob("MANAGER");
        List employees = (List) cmd.execute(new Object[] { dto });
        // assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto3() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee3Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee3Dao.class, "getEmployees"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Employee3 dto = new Employee3();
        dto.setManager((short) 7902);
        List employees = (List) cmd.execute(new Object[] { dto });
        System.out.println(employees);
        assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto4() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee3Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee3Dao.class,"getEmployees2"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        assertTrue("2", cmd.getSql().endsWith(" ORDER BY empno"));
    }

    public void testAutoSelectSqlByDto5() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySearchCondition2"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        EmployeeSearchCondition dto = new EmployeeSearchCondition();
        Department department =new Department();
        department.setDname("RESEARCH");
        dto.setDepartment(department);
        List employees = (List) cmd.execute(new Object[] { dto });
        assertTrue("2", employees.size() > 0);
    }

    public void testAutoSelectSqlByDto6() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployeesBySearchCondition2"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        EmployeeSearchCondition dto = new EmployeeSearchCondition();
        dto.setDepartment(null);
        List employees = (List) cmd.execute(new Object[] { dto });
        assertEquals("2", 0, employees.size());
    }

    public void testSelfReference() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee4Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee4Dao.class,"getEmployee"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        Employee4 employee = (Employee4)cmd.execute(new Object[] { new Integer(7788) });
        System.out.println(employee);
        Employee4 parent = employee.getParent();
        assertEquals("2", new Long(7566), parent.getEmpno());
    }

    public void testSelfMultiPk() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee5Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee5Dao.class,"getEmployee"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
    }

    public void testNotHavePrimaryKey() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DepartmentTotalSalaryDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(DepartmentTotalSalaryDao.class,"getTotalSalaries"));
        assertNotNull("1", cmd);
        System.out.println(cmd.getSql());
        List result = (List) cmd.execute(null);
        System.out.println(result);
    }

    public void testSelectAutoFullColumnName() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        System.out.println(cmd.getSql());
    }

    public void testStartsWithOrderBy() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(Employee6Dao.class);
        EmployeeSearchCondition condition = new EmployeeSearchCondition();
        condition.setDname("RESEARCH");;
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee6Dao.class,"getEmployees"));
        cmd.execute(new Object[] { condition });
        condition.setOrderByString("ENAME");
        cmd.execute(new Object[] { condition });
    }

    public void testStartsWithBeginComment() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee8Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee8Dao.class,"getEmployees"));
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

    public void testQueryAnnotationTx() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(Employee7Dao.class);
        SelectDynamicCommand cmd1 = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee7Dao.class,"getCount"));
        UpdateDynamicCommand cmd2 = (UpdateDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee7Dao.class,"deleteEmployee"));
        System.out.println(cmd1.getSql());
        System.out.println(cmd2.getSql());
        assertEquals(new Integer(14), cmd1.execute(null));
        assertEquals(new Integer(1), cmd2.execute(new Object[] { new Integer(
                7369) }));
        assertEquals(new Integer(13), cmd1.execute(null));
    }



    public void testDaoExtend2() throws Exception {
        DaoMetaDataImpl dmd = createDaoMetaData(EmployeeExDao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(EmployeeExDao.class,"getEmployee"));
        final String expected = TextUtil.readText(EmployeeDao.class
                .getPackage().getName().replace('.', '/')
                + "/" + "EmployeeDao_getEmployee.sql");
        assertEquals(expected, cmd.getSql());
    }

    public void testUsingColumnAnnotationForSql_Update() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee9Dao.class);
        UpdateAutoStaticCommand cmd = (UpdateAutoStaticCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee9Dao.class,"update"));
        final String sql = cmd.getSql();
        System.out.println(sql);
        assertEquals(sql, true, sql.indexOf("eNaMe") > -1);
    }

    public void testUsingColumnAnnotationForSql_Select() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee9Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee9Dao.class, "findBy"));
        final String sql = cmd.getSql();
        System.out.println(sql);
        final int pos = sql.indexOf("WHERE");
        final String before = sql.substring(0, pos);
        final String after = sql.substring(pos);
        assertEquals(before, true, before.indexOf("EMP.eNaMe") > -1);
        assertEquals(after, true, after.indexOf("EMP.eNaMe") > -1);
    }

    public void testUsingColumnAnnotationForSql_SelectDto() throws Exception {
        DaoMetaData dmd = createDaoMetaData(Employee9Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee9Dao.class,"findByEname"));
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
        DaoMetaData dmd = createDaoMetaData(Employee10Dao.class);
        SelectDynamicCommand cmd = (SelectDynamicCommand) dmd
                .getSqlCommand(getSingleDaoMethod(Employee10Dao.class,"getEmployeesByJob"));
        final List emps = (List) cmd.execute(new Object[] { null });
        assertEquals(14, emps.size());
    }

    public void testNoSqlFile() throws Exception {
        // ## Arrange ##
        final Class daoClass = Employee11Dao.class;

        // ## Act ##
        // ## Assert ##
        try {
            DaoMetaData dmd = createDaoMetaData(daoClass);
            SqlCommand command = dmd.getSqlCommand(getSingleDaoMethod(Employee11Dao.class, "insert"));
            fail();
        } catch (final MethodSetupFailureRuntimeException e) {
            System.out.println(e.getMessage());
            final SqlFileNotFoundRuntimeException cause = (SqlFileNotFoundRuntimeException) e
                    .getCause();
            assertEquals("EDAO0025", cause.getMessageCode());
        }

    }

    public void testDeleteByQuery() throws Exception {
        final Class daoClass = Employee12Dao.class;
        DaoMetaData metaData = createDaoMetaData(daoClass);
        UpdateDynamicCommand cmd = (UpdateDynamicCommand) metaData
                .getSqlCommand(getSingleDaoMethod(Employee12Dao.class,"delete"));
        assertEquals("DELETE FROM EMP WHERE EMPNO = /*no*/1111", cmd.getSql());

        cmd = (UpdateDynamicCommand) metaData.getSqlCommand(getSingleDaoMethod(Employee12Dao.class,"deleteNoWhere"));
        assertEquals("DELETE FROM EMP WHERE EMPNO = ?", cmd.getSql());

    }

    public void testAssertAnnotation() throws Exception {
        final Class daoClass = Employee13Dao.class;
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
        final Class daoClass = Employee14Dao.class;
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
