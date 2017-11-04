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

import jp.fieldnotes.hatunatu.dao.impl.bean.Employee20;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee22;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ModifiedPropertySupportTest {

    private ModifiedPropertySupport target;

    @Before
    public void setUp() throws Exception {
        target = new ModifiedPropertySupport();
    }

    @Test
    public void testGetModifiedPropertyNames() {
        Employee22 emp = new Employee22();
        emp.setEmpno(new Long(1000));
        emp.setDeptno(new Integer(1));
        Set names = target.getModifiedPropertyNames(emp);
        assertNotNull(names);
        assertEquals("1", 2, names.size());
        assertTrue("2", names.contains("empno"));
        assertTrue("3", names.contains("deptno"));
        assertFalse("4", names.contains("dummy"));
    }

    @Test
    public void testGetModifiedPropertyNamesFromNoPropertyBean() {
        Employee20 emp = new Employee20();
        emp.setEmpno(new Long(1000));
        emp.setDeptno(new Integer(1));
        Set names = target.getModifiedPropertyNames(emp);
        assertNotNull(names);
        assertEquals("1", 0, names.size());
    }

}
