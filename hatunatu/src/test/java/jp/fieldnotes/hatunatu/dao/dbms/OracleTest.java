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
package jp.fieldnotes.hatunatu.dao.dbms;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;

/**
 * @author higa
 * @author manhole
 */
public class OracleTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
    }

    public void testCreateAutoSelectList() throws Exception {
        Dbms dbms = new Oracle();
        setDbms(dbms);
        BeanMetaData bmd = createBeanMetaData(Employee.class);
        String sql = dbms.getAutoSelectSql(bmd);
        System.out.println(sql);
    }

    public void testCreateAutoSelectList2() throws Exception {
        Dbms dbms = new Oracle();
        setDbms(dbms);
        BeanMetaData bmd = createBeanMetaData(Department.class);
        String sql = dbms.getAutoSelectSql(bmd);
        System.out.println(sql);
        assertTrue("1", sql.endsWith("FROM DEPT"));
    }

}