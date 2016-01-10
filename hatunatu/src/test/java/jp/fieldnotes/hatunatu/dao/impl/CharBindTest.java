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
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharBindTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this,"CharBindTest.dicon" );

    private CharTableDao charTableDao;

    @Test
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

    public static interface CharTableDao {

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
