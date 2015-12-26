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

import jp.fieldnotes.hatunatu.dao.impl.RelationKey;
import junit.framework.TestCase;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Column;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.Relation;

/**
 * @author higa
 * 
 */
public class RelationKeyTest extends TestCase {

    protected void tearDown() throws Exception {
    }

    public void testEquals() throws Exception {
        Object[] values = new Object[] { "1", "2" };
        RelationKey pk = new RelationKey(values);
        assertEquals("1", pk, pk);
        assertEquals("2", pk, new RelationKey(values));
        assertEquals("3", false, new RelationKey(new Object[] { "1" })
                .equals(pk));
    }

    public void testHashCode() throws Exception {
        Object[] values = new Object[] { "1", "2" };
        RelationKey pk = new RelationKey(values);
        assertEquals("1", "1".hashCode() + "2".hashCode(), pk.hashCode());
    }

    @Bean(table="MyBean")
    public static class MyBean {



        private Integer aaa;

        private String bbb;

        private Ccc ccc;

        private Integer ddd;

        public Integer getAaa() {
            return aaa;
        }

        public void setAaa(Integer aaa) {
            this.aaa = aaa;
        }

        @Column(value = "myBbb")
        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        @Relation(relationNo = 0, relationKey = "ddd:id")
        public Ccc getCcc() {
            return ccc;
        }

        public void setCcc(Ccc ccc) {
            this.ccc = ccc;
        }

        public Integer getDdd() {
            return ddd;
        }

        public void setDdd(Integer ddd) {
            this.ddd = ddd;
        }
    }

    public static class Ccc {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}