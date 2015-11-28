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
package org.seasar.dao.issue;

import org.seasar.dao.annotation.tiger.Arguments;
import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.framework.util.ClassUtil;

/**
 * @author manhole
 */
public class Jira101Test extends S2DaoTestCase {

    private Jira101Dao dao;

    protected void setUp() throws Exception {
        super.setUp();
        include(ClassUtil.getShortClassName(Jira101Test.class) + ".dicon");
    }

    public void testJira101Tx() throws Exception {
        {
            final Jira101Entity entity = new Jira101Entity();
            entity.setFooId(777);
            entity.setAaa("a1");
            entity.setBbb("b1");
            dao.insert(entity);
        }
        {
            final Jira101Entity entity = dao.findById(777);
            //entity.setAaa("a2");
            dao.updateModifiedOnly(entity);
        }
    }

    @S2Dao(bean=Jira101Entity.class)
    public static interface Jira101Dao {
        @Arguments("FOO_ID")
        Jira101Entity findById(int id);

        int insert(Jira101Entity entity);

        int updateModifiedOnly(Jira101Entity entity);
    }

    @Bean(table="JIRA101_TABLE")
    public static class Jira101Entity {

        private int fooId;

        private String aaa;

        private String bbb;

        public int getFooId() {
            return fooId;
        }

        public void setFooId(final int id) {
            this.fooId = id;
        }

        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

    }

}
