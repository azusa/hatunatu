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
package jp.fieldnotes.hatunatu.dao.command;

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.exception.UpdateFailureRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.bean.Department;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.dao.DepartmentAutoDao;
import jp.fieldnotes.hatunatu.dao.impl.dao.EmployeeAutoDao;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;

public class UpdateAutoStaticCommandTest extends S2DaoTestCase {

    public UpdateAutoStaticCommandTest(String arg0) {
        super(arg0);
    }

    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"update"));
        SqlCommand cmd2 = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee) cmd2
                .execute(new Object[] { new Integer(7788) });
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void testExecute2Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DepartmentAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(DepartmentAutoDao.class,"update"));
        Department dept = new Department();
        dept.setDeptno(10);
        Integer count = (Integer) cmd.execute(new Object[] { dept });
        assertEquals("1", new Integer(1), count);
        assertEquals("2", 1, dept.getVersionNo());
    }

    public void testExecute3Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(DepartmentAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(DepartmentAutoDao.class,"update"));
        Department dept = new Department();
        dept.setDeptno(10);
        dept.setVersionNo(-1);
        try {
            cmd.execute(new Object[] { dept });
            fail("1");
        } catch (UpdateFailureRuntimeException ex) {
            System.out.println(ex);
        }
    }

    public void testExecute4Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"update2"));
        SqlCommand cmd2 = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee) cmd2
                .execute(new Object[] { new Integer(7788) });
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void testExecute5Tx() throws Exception {
        DaoMetaData dmd = createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"update3"));
        SqlCommand cmd2 = dmd.getSqlCommand(getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee) cmd2
                .execute(new Object[] { new Integer(7788) });
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    public void setUp() {
        include("j2ee.dicon");
    }

}
