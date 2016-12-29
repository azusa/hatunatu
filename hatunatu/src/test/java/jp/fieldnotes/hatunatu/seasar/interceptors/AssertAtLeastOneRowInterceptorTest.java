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
package jp.fieldnotes.hatunatu.seasar.interceptors;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Argument;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Sql;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.seasar.exception.NoRowsUpdatedRuntimeException;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AssertAtLeastOneRowInterceptorTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this,"AssertAtLeastOneRowInterceptorTest.dicon");

    private EmployeeDao employeeDao;


    @Test
    public void testMoreThanOneRowTx() throws Exception {
        final int ret = employeeDao.updateSal("A%");
        assertEquals(2, ret);
    }

    @Test
    public void testNoRowTx() throws Exception {
        try {
            final int ret = employeeDao.updateSal("ZZ%");
            fail("count: " + ret);
        } catch (NoRowsUpdatedRuntimeException e) {
            assertEquals("EDAO0015", e.getMessageCode());
        }
    }

    public static interface EmployeeDao {

        @Sql("update EMP set SAL = SAL * 2 where ENAME LIKE /*ename*/'ABC'")
        public int updateSal(@Argument("ename")String name);

    }

}
