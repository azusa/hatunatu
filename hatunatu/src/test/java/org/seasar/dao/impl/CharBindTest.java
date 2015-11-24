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

import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author manhole
 */
public class CharBindTest extends S2DaoTestCase {

    private CharTableDao charTableDao;

    protected void setUp() throws Exception {
        super.setUp();
        include("CharBindTest.dicon");
    }

    public void testCharBindTx() throws Exception {
        // ## Arrange ##
        {
            CharTable bean = new CharTable();
            bean.setId(31);
            bean.setAaa('Z');
            charTableDao.insert(bean);
        }

        // ## Act ##
        final CharTable bean = charTableDao.findById(31);

        // ## Assert ##
        assertEquals('Z', bean.getAaa());
    }

    @S2Dao(bean=CharTable.class)
    public static interface CharTableDao {

        public Class BEAN = CharTable.class;

        public CharTable findById(int id);

        public void insert(CharTable charTable);

        public void update(CharTable charTable);

    }

    @Bean(table="CHAR_TABLE")
    public static class CharTable {

        private int id;

        private char aaa;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public char getAaa() {
            return aaa;
        }

        public void setAaa(char aaa) {
            this.aaa = aaa;
        }

    }

}
