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

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PkOnlyTableTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "PkOnlyTableTest.dicon");

    /*
     * https://www.seasar.org/issues/browse/DAO-16
     */
    @Test
    public void testInsertTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(PkOnlyTableDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(PkOnlyTableDao.class,"insert"));
        PkOnlyTable data = new PkOnlyTable();
        data.setAaa("value");
        Integer i = (Integer) cmd.execute(new Object[] { data });
        assertEquals(1, i.intValue());
    }

    @Bean(table="PKONLYTABLE")
    public class PkOnlyTable {

        private String aaa;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }
    }

    public interface PkOnlyTableDao {

        int insert(PkOnlyTable data);

    }

}
