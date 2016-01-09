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

import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.apache.log4j.PropertyConfigurator;
import jp.fieldnotes.hatunatu.dao.impl.dao.EmployeeDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.jdbc.SqlLogRegistryLocator;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;

import static org.junit.Assert.assertNotNull;

public class LogCustomizeTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "jp/fieldnotes/hatunatu/dao/impl/LogCustomizeTest.dicon");

    private EmployeeDao dao;

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configure(ResourceUtil
                .getResource("logcustomize.properties"));
    }

    /*
     * EmployeeDaoのログ出力レベルはINFOなので、ここではログがでない
     */
    @Test
    public void testLogTx() throws Exception {
        dao.findAll();
        String sql = SqlLogRegistryLocator.getInstance().getLast()
                .getCompleteSql();
        assertNotNull(sql);
        System.out.println(sql);
    }

    @After
    public void tearDown() throws Exception {
        PropertyConfigurator.configure(ResourceUtil
                .getResource("log4j.properties"));
    }
}
