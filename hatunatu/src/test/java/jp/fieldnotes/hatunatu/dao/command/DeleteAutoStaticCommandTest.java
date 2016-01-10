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
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DeleteAutoStaticCommandTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"delete"));

        SqlCommand cmd2 = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"getEmployee"));
        Employee emp = (Employee) cmd2
                .execute(new Object[] { new Integer(7788) });
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

    @Test
    public void testExecute2Tx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(DepartmentAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(DepartmentAutoDao.class,"delete"));
        Department dept = new Department();
        dept.setDeptno(10);
        Integer count = (Integer) cmd.execute(new Object[] { dept });
        assertEquals("1", new Integer(1), count);
    }

    @Test
    public void testExecute3Tx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(DepartmentAutoDao.class);
        DeleteAutoStaticCommand cmd = (DeleteAutoStaticCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(DepartmentAutoDao.class,"delete"));
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

}