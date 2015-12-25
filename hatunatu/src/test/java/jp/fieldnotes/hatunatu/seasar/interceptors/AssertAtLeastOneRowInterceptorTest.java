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

import jp.fieldnotes.hatunatu.seasar.exception.NoRowsUpdatedRuntimeException;
import org.seasar.dao.annotation.tiger.Arguments;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.annotation.tiger.Sql;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author manhole
 */
public class AssertAtLeastOneRowInterceptorTest extends S2TestCase {

    private EmployeeDao employeeDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("AssertAtLeastOneRowInterceptorTest.dicon");
    }

    public void testMoreThanOneRowTx() throws Exception {
        final int ret = employeeDao.updateSal("A%");
        assertEquals(2, ret);
    }

    public void testNoRowTx() throws Exception {
        try {
            final int ret = employeeDao.updateSal("ZZ%");
            fail("count: " + ret);
        } catch (NoRowsUpdatedRuntimeException e) {
            assertEquals("EDAO0015", e.getMessageCode());
        }
    }

    @S2Dao(bean=Employee.class)
    public static interface EmployeeDao {

        @Sql("update EMP set SAL = SAL * 2 where ENAME LIKE /*ename*/'ABC'")
        @Arguments({"ename"})
        public int updateSal(String name);

    }

}
