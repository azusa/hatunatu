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

import jp.fieldnotes.hatunatu.dao.NullBean;
import jp.fieldnotes.hatunatu.dao.exception.BeanNotFoundRuntimeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NullBeanMetaDataTest {

    @Test
    public void testException() throws Exception {
        NullBeanMetaData metaData = new NullBeanMetaData(HogeDao.class);
        try {
            metaData.getTableName();
            fail();
        } catch (BeanNotFoundRuntimeException ignore) {
            System.out.println(ignore.getMessage());
        }
    }

    @Test
    public void testGetBeanClass() throws Exception {
        NullBeanMetaData metaData = new NullBeanMetaData(HogeDao.class);
        assertEquals(NullBean.class, metaData.getBeanClass());
    }

    public static interface HogeDao {
    }
}
