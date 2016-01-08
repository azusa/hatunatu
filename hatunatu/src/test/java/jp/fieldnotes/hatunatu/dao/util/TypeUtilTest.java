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
package jp.fieldnotes.hatunatu.dao.util;

import java.lang.reflect.Field;
import java.util.Map;

import jp.fieldnotes.hatunatu.dao.util.TypeUtil;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeUtilTest  {

    @Test
    public void testIsSimpleType() throws Exception {
        assertFalse(TypeUtil.isSimpleType(Map.class));
        assertTrue(TypeUtil.isSimpleType(int.class));
    }

    @Test
    public void testGetDeclaredFields() throws Exception {
        Field[] fields = TypeUtil.getDeclaredFields(TestClass.class);
        assertEquals(5, fields.length);
        assertEquals("aaa", fields[0].getName());
        assertEquals("bbb", fields[1].getName());
        assertEquals("ccc", fields[2].getName());
        assertEquals("ddd", fields[3].getName());
        assertEquals("eee", fields[4].getName());
    }

    public static class TestClass {
        int aaa;

        int bbb;

        int ccc;

        int ddd;

        int eee;
    }
}
