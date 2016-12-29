/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
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

import org.junit.Test;
import org.seasar.framework.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodUtilTest  {

    @Test
    public void testIsEqualsMethod() {
        Method equalsMethod = ClassUtil.getMethod(getClass(), "equals",
                new Class[] { Object.class });
        assertTrue("1", MethodUtil.isEqualsMethod(equalsMethod));
        Method hashCodeMethod = ClassUtil.getMethod(getClass(), "hashCode",
                new Class[0]);
        assertFalse("2", MethodUtil.isEqualsMethod(hashCodeMethod));
    }

    @Test
    public void testIsHashCodeMethod() {
        Method equalsMethod = ClassUtil.getMethod(getClass(), "equals",
                new Class[] { Object.class });
        assertFalse("1", MethodUtil.isHashCodeMethod(equalsMethod));
        Method hashCodeMethod = ClassUtil.getMethod(getClass(), "hashCode",
                new Class[0]);
        assertTrue("2", MethodUtil.isHashCodeMethod(hashCodeMethod));
    }

    @Test
    public void testIsToStringMethod() {
        Method toStringMethod = ClassUtil.getMethod(getClass(), "toString",
                new Class[0]);
        assertTrue("1", MethodUtil.isToStringMethod(toStringMethod));
        Method hashCodeMethod = ClassUtil.getMethod(getClass(), "hashCode",
                new Class[0]);
        assertFalse("2", MethodUtil.isToStringMethod(hashCodeMethod));
    }

    @Test
    public void testIsBridgeMethod() throws Exception {
        Method method = Foo.class.getMethod("foo", null);
        assertFalse(MethodUtil.isBridgeMethod(method));
    }

    @Test
    public void testIsSyntheticMethod() throws Exception {
        Method method = Foo.class.getMethod("foo", null);
        assertFalse(MethodUtil.isSyntheticMethod(method));
    }

    @Test
    public void testIsDefaultMethod() throws Exception {
        Method method = Foo.class.getMethod("foo", null);
        assertFalse(MethodUtil.isDefaultMethod(method));
    }

    public static class Foo {
        public void foo() {
        }
    }

    public interface Baz {

        List hoge(List src);

        void hoge(List src, List dest);
    }

}
