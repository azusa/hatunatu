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
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.util.misc.DisposableUtil;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DaoMetaDataFactoryImplTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "dao.dicon");

    private DaoMetaDataFactoryImpl daoMetaDataFactory;

    /*
     * https://www.seasar.org/issues/browse/DAO-17
    */
    @Test
    public void testDispose1() throws Exception {
        // ## Arrange ##
        assertEquals(0, daoMetaDataFactory.daoMetaDataCache.size());

        // ## Act ##
        final DaoMetaData dmd = daoMetaDataFactory.getDaoMetaData(FooDao.class);
        assertNotNull(dmd);

        // ## Assert ##
        assertEquals(1, daoMetaDataFactory.daoMetaDataCache.size());
        DisposableUtil.dispose();
        assertEquals(0, daoMetaDataFactory.daoMetaDataCache.size());
    }

    @Test
    public void testDispose2() throws Exception {
        // ## Arrange ##
        assertEquals(0, daoMetaDataFactory.daoMetaDataCache.size());

        // ## Act ##
        // ## Assert ##
        {
            final DaoMetaData dmd = daoMetaDataFactory
                    .getDaoMetaData(FooDao.class);
            assertNotNull(dmd);
        }

        assertEquals(1, daoMetaDataFactory.daoMetaDataCache.size());
        DisposableUtil.dispose();
        assertEquals(0, daoMetaDataFactory.daoMetaDataCache.size());
        {
            final DaoMetaData dmd = daoMetaDataFactory
                    .getDaoMetaData(FooDao.class);
            assertNotNull(dmd);
        }
        assertEquals(1, daoMetaDataFactory.daoMetaDataCache.size());
        DisposableUtil.dispose();
        assertEquals(0, daoMetaDataFactory.daoMetaDataCache.size());
    }

    public static interface FooDao {

        List<Foo> findAll();
    }

    @Bean(table="EMP")
    public static class Foo {

        private String ename;

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }
    }

}
