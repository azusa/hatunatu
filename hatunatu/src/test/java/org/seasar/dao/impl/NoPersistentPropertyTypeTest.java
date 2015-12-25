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

import java.util.Iterator;
import java.util.List;

import org.seasar.dao.exception.MethodSetupFailureRuntimeException;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.annotation.tiger.Sql;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * https://www.seasar.org/issues/browse/DAO-20
 * 
 * @author manhole
 */
public class NoPersistentPropertyTypeTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
    }

    public void testNoPersistentPropertyTypeException1() throws Exception {
        try {
            final DaoMetaDataImpl dmd = createDaoMetaData(Foo1Dao.class);
            final SqlCommand command = dmd.getSqlCommand("findAll");
            command.execute(null);
            fail();
        } catch (MethodSetupFailureRuntimeException e) {
            e.printStackTrace();
            assertEquals(true, -1 < e.getMessage().indexOf("EDAO0019"));
            final String message = e.getCause().getMessage();
            assertEquals(true, -1 < message.indexOf("EDAO0017"));
        }
    }

    public void testNoPersistentPropertyTypeException2() throws Exception {
        // ## Arrange ##
        final DaoMetaDataImpl dmd = createDaoMetaData(Foo2Dao.class);

        // ## Act ##
        final SqlCommand command = dmd.getSqlCommand("findAll");

        // ## Assert ##
        final List result = (List) command.execute(null);
        assertEquals(false, result.isEmpty());
        for (Iterator it = result.iterator(); it.hasNext();) {
            FooDto a = (FooDto) it.next();
            assertNotNull(a.getEname());
        }
    }

    @S2Dao(bean=FooDto.class)
    public static interface Foo1Dao {

        List findAll();
    }

    @S2Dao(bean=FooDto.class)
    public static interface Foo2Dao {

        @Sql("SELECT * FROM EMP")
        List findAll();
    }

    @Bean(table = "WRONG_TABLE_NAME")
    public static class FooDto {

        private String ename;

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }
    }

}
