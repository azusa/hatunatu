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

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Sql;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NoPkTableTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this,"NoPkTableTest.xml");

    private NoPkTableDao noPkTableDao;

    @Test
    public void testCRUDTx() throws Exception {
        {
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(0, beans.length);
        }
        {
            // insert
            NoPkTable bean = new NoPkTable();
            bean.setAaa("a");
            bean.setBbb(new Integer(1));
            noPkTableDao.insert(bean);
        }
        {
            // select
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(1, beans.length);
            assertEquals("a", beans[0].getAaa());
            assertEquals(1, beans[0].getBbb().intValue());

            // update
            beans[0].setAaa("a2");
            noPkTableDao.update(beans[0]);
        }
        {
            // select
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(1, beans.length);
            assertEquals("a2", beans[0].getAaa());
            assertEquals(1, beans[0].getBbb().intValue());
        }
        // delete
        {
            noPkTableDao.delete("hoge");
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(1, beans.length);
        }
        {
            noPkTableDao.delete("a2");
            final NoPkTable[] beans = noPkTableDao.findAll();
            assertEquals(0, beans.length);
        }
    }

    public static interface NoPkTableDao {

        NoPkTable[] findAll();


        int insert(NoPkTable noPkTable);

        int update(NoPkTable noPkTable);

        @Sql("DELETE FROM NO_PK_TABLE WHERE AAA = ?")
        int delete(String aaa);

    }

    @Bean(table = "NO_PK_TABLE")
    public static class NoPkTable {

        private String aaa;

        private Integer bbb;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public Integer getBbb() {
            return bbb;
        }

        public void setBbb(Integer bbb) {
            this.bbb = bbb;
        }

    }
}
