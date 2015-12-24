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

import org.seasar.dao.exception.MethodSetupFailureRuntimeException;
import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.extension.unit.S2TestCase;
import jp.fieldnotes.hatunatu.util.exception.SRuntimeException;

/**
 * [DAO-150]
 * 
 * https://www.seasar.org/issues/browse/DAO-150
 * 
 * 
 * @author azusa
 * 
 */
public class NoPropertyForUpdateTest extends S2TestCase {

    private NoPropertyForUpdateDao noPropertyForUpdateDao;

    protected void setUp() throws Exception {
        include("NoPropertyForUpdate.dicon");
    }

    public void testExceptionOnUpdate() {
        try {
            noPropertyForUpdateDao.delete(new NoPropertyForUpdate());
            fail();
        } catch (MethodSetupFailureRuntimeException e) {
            SRuntimeException cause = (SRuntimeException) e.getCause();
            assertEquals("EDAO0035", cause.getMessageCode());
            e.printStackTrace();
        }
    }

    @S2Dao(bean=NoPropertyForUpdate.class)
    public static interface NoPropertyForUpdateDao {

        void update(NoPropertyForUpdate noPropertyForUpdate);

        void delete(NoPropertyForUpdate noPropertyForUpdate);

    }

    @Bean(table = "EMP")
    private static class NoPropertyForUpdate {

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