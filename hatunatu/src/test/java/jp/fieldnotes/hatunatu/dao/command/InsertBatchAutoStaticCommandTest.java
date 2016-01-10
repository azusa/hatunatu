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
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.dao.EmployeeAutoDao;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InsertBatchAutoStaticCommandTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testExecuteTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(EmployeeAutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"insertBatch"));
        Employee emp = new Employee();
        emp.setEmpno(99);
        emp.setEname("hoge");
        Employee emp2 = new Employee();
        emp2.setEmpno(98);
        emp2.setEname("hoge2");
        Integer count = (Integer) cmd.execute(new Object[] { new Employee[] {
                emp, emp2 } });
        assertEquals("1", new Integer(2), count);

        SqlCommand cmd2 = dmd.getSqlCommand(test.getSingleDaoMethod(EmployeeAutoDao.class,"insertBatch2"));
        Employee emp3 = new Employee();
        emp3.setEmpno(97);
        emp3.setEname("hoge3");
        Employee emp4 = new Employee();
        emp4.setEmpno(96);
        emp4.setEname("hoge2");
        int[] ret = (int[]) cmd2.execute(new Object[] { new Employee[] { emp3,
                emp4 } });
        assertEquals("2", 2, ret.length);
        assertEquals("3", 1, ret[0]);
        assertEquals("4", 1, ret[1]);
    }

}