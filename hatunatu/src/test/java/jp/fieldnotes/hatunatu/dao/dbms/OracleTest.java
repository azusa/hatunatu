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
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OracleTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);


    @Test
    public void testCreateAutoSelectList() throws Exception {
        Dbms dbms = new Oracle();
        test.setDbms(dbms);
        BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        String sql = dbms.getAutoSelectSql(bmd);
        System.out.println(sql);
    }

    @Test
    public void testCreateAutoSelectList2() throws Exception {
        Dbms dbms = new Oracle();
        test.setDbms(dbms);
        BeanMetaData bmd = test.createBeanMetaData(Department.class);
        String sql = dbms.getAutoSelectSql(bmd);
        System.out.println(sql);
        assertTrue("1", sql.endsWith("FROM DEPT"));
    }

}