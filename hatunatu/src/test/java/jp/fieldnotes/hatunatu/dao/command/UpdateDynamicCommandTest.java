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

import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class UpdateDynamicCommandTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testExecuteTx() throws Exception {
        UpdateDynamicCommand cmd = new UpdateDynamicCommand(test.getDataSource(),
                StatementFactory.INSTANCE);
        cmd
                .setSql("UPDATE emp SET ename = /*employee.ename*/'HOGE' WHERE empno = /*employee.empno*/1234");
        cmd.setArgNames(Arrays.asList("employee" ));

        Employee emp = new Employee();
        emp.setEmpno(7788);
        emp.setEname("SCOTT");
        Integer count = (Integer) cmd.execute(new Object[] { emp });
        assertEquals("1", new Integer(1), count);
    }

}