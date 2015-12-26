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

import java.io.Serializable;

import jp.fieldnotes.hatunatu.dao.annotation.tiger.Arguments;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.S2Dao;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ValueType;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author manhole
 */
public class ClobTest extends S2TestCase {

    private LargeTextDao largeTextDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("ClobTest.dicon");
    }

    public void test1Tx() throws Exception {
        assertNotNull(largeTextDao);
        final LargeText largeText = largeTextDao.getLargeText(123);
        assertEquals(null, largeText);
    }

    public void test2Tx() throws Exception {
        {
            LargeText largeText = new LargeText();
            largeText.setId(1);
            largeText.setLargeString("abc1");
            largeTextDao.insert(largeText);
        }
        {
            final LargeText largeText = largeTextDao.getLargeText(1);
            assertEquals("abc1", largeText.getLargeString());
            assertEquals(0, largeText.getVersionNo());

            largeText.setLargeString("ABCDEFG");
            largeTextDao.update(largeText);
        }
        {
            final LargeText largeText = largeTextDao.getLargeText(1);
            assertEquals("ABCDEFG", largeText.getLargeString());
            assertEquals(1, largeText.getVersionNo());
        }
    }

    @S2Dao(bean=LargeText.class)
    public static interface LargeTextDao {

        @Arguments({"id"})
        public LargeText getLargeText(int id);

        public void insert(LargeText largeText);

        public void update(LargeText largeText);

    }

    @Bean(table="LARGE_TEXT")
    public static class LargeText implements Serializable {

        private static final long serialVersionUID = 1L;

        private int id;

        @ValueType(value =  "stringClobType")
        private String largeString;

        private int versionNo;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLargeString() {
            return largeString;
        }

        public void setLargeString(String largeString) {
            this.largeString = largeString;
        }

        public int getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(int versionNo) {
            this.versionNo = versionNo;
        }
    }

}
