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

import java.util.List;

import org.seasar.dao.OverloadNotSupportedRuntimeException;
import org.seasar.dao.SqlCommand;
import org.seasar.dao.impl.bean.Employee;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author taedium
 * 
 */
public class OverloadNotSupportedSqlCommandTest extends S2DaoTestCase {

    public void setUp() {
        include("j2ee.dicon");
    }

    public void testExecute() {
        SqlCommand command = createDaoMetaData(MyDao.class).getSqlCommand(
                "getEmployees");
        try {
            command.execute(new Object[] { "hoge" });
            fail();
        } catch (OverloadNotSupportedRuntimeException e) {
        }
    }

    public interface MyDao {

        public Class BEAN = Employee.class;

        List getEmployees(String empName);

        List getEmployees(int empNo);

    }
}
