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

import java.lang.reflect.Method;
import java.util.List;

import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

public class OverloadSqlCommandTest extends S2DaoTestCase {

    public void setUp() {
        include("j2ee.dicon");
    }

    public void testExecute_WithStringArguments() {
        Method method = BeanDescFactory.getBeanDesc(MyDao.class).getMethodDesc("getEmployees", String.class).getMethod();
        SqlCommand command = createDaoMetaData(MyDao.class).getSqlCommand(
                method);
        command.execute(new Object[] { "hoge" });
        assertTrue(true);
    }

    public void testExecute_WithIntArguments() {
        Method method = BeanDescFactory.getBeanDesc(MyDao.class).getMethodDesc("getEmployees", Integer.TYPE).getMethod();
        SqlCommand command = createDaoMetaData(MyDao.class).getSqlCommand(
                method);
        command.execute(new Object[] { 7369 });
        assertTrue(true);
    }
    public interface MyDao {

        List<Employee> getEmployees(String empName);

        List<Employee> getEmployees(int empNo);

    }
}
