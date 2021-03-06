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
package jp.fieldnotes.hatunatu.lastadi.interceptors;

import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class S2DaoInterceptor3Test {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "Interceptors.xml");

    private DepartmentAutoDao dao;

    @Test
    public void testUpdateTx() throws Exception {
        Department dept = new Department();
        dept.setDeptno(10);
        assertEquals("1", 1, dao.update(dept));
    }

    @Test
    public void testDeleteTx() throws Exception {
        Department dept = new Department();
        dept.setDeptno(10);
        assertEquals("1", 1, dao.delete(dept));
    }

}