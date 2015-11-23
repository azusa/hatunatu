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

import junit.framework.TestCase;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.dbms.MySQL;
import org.seasar.dao.dbms.Oracle;
import org.seasar.dao.dbms.PostgreSQL;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author manhole
 * @author azusa
 */
public abstract class AbstractBeanAnnotationReaderTest extends TestCase {

    protected abstract Class getBeanClass(String className);

    protected abstract BeanAnnotationReader createBeanAnnotationReader(
            Class clazz);

    public void testGetColumnAnnotation() {
        Class clazz = getBeanClass("AnnotationTestBean1");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
        BeanAnnotationReader reader = createBeanAnnotationReader(clazz);
        assertEquals("1", "Cprop1", reader.getColumnAnnotation(beanDesc
                .getPropertyDesc("prop1")));
        assertEquals("2", (String) null, reader.getColumnAnnotation(beanDesc
                .getPropertyDesc("prop2")));
    }

    public void testGetTableAnnotation() {
        Class clazz1 = getBeanClass("AnnotationTestBean1");
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        assertEquals("1", "TABLE", reader1.getTableAnnotation());
        Class clazz2 = getBeanClass("AnnotationTestBean2");
        BeanAnnotationReader reader2 = createBeanAnnotationReader(clazz2);
        assertNull("2", reader2.getTableAnnotation());
    }

    public void testGetVersionNoProteryNameAnnotation() {
        Class clazz1 = getBeanClass("AnnotationTestBean1");
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        String str1 = reader1.getVersionNoPropertyName();
        assertEquals("1", "myVersionNo", str1);
        Class clazz2 = getBeanClass("AnnotationTestBean2");
        BeanAnnotationReader reader2 = createBeanAnnotationReader(clazz2);
        String str2 = reader2.getVersionNoPropertyName();
        assertNull("1", str2);
    }

    public void testGetTimestampPropertyName() {
        Class clazz1 = getBeanClass("AnnotationTestBean1");
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        String str1 = reader1.getTimestampPropertyName();
        assertEquals("1", "myTimestamp", str1);
        Class clazz2 = getBeanClass("AnnotationTestBean2");
        BeanAnnotationReader reader2 = createBeanAnnotationReader(clazz2);
        String str2 = reader2.getTimestampPropertyName();
        assertNull("1", str2);
    }

    public void testGetIds() {
        Class clazz1 = getBeanClass("AnnotationTestBean4");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz1);
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        String str1 = reader1.getId(beanDesc.getPropertyDesc("aaa"),
                new Oracle());
        assertEquals("1", "identity", str1);
        String str2 = reader1.getId(beanDesc.getPropertyDesc("aaa"),
                new MySQL());
        assertEquals("2", "sequence, sequenceName=myseq, allocationSize=0",
                str2);
        String str3 = reader1.getId(beanDesc.getPropertyDesc("aaa"),
                new PostgreSQL());
        assertEquals("3", "sequence, sequenceName=myseq_2, allocationSize=10",
                str3);
        String str4 = reader1.getId(beanDesc.getPropertyDesc("bbb"),
                new MySQL());
        assertNull("4", str4);
    }

    public void testGetId1() {
        Class clazz1 = getBeanClass("AnnotationTestBean5");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz1);
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        String str1 = reader1.getId(beanDesc.getPropertyDesc("aaa"),
                new Oracle());
        assertEquals("1", "identity", str1);
        String str2 = reader1
                .getId(beanDesc.getPropertyDesc("aaa"), new HSQL());
        assertNull("2", str2);
        String str3 = reader1.getId(beanDesc.getPropertyDesc("bbb"),
                new Oracle());
        assertNull("3", str3);
    }

    public void testGetId2() {
        Class clazz1 = getBeanClass("AnnotationTestBean6");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz1);
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        String str1 = reader1.getId(beanDesc.getPropertyDesc("aaa"),
                new Oracle());
        assertEquals("1", "identity", str1);
        String str3 = reader1.getId(beanDesc.getPropertyDesc("bbb"),
                new Oracle());
        assertNull("2", str3);
    }

    public void testGetNoPersisteneProps() {
        Class clazz1 = getBeanClass("AnnotationTestBean1");
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        String[] strings1 = reader1.getNoPersisteneProps();
        assertEquals("1", "prop2", strings1[0]);
        Class clazz2 = getBeanClass("AnnotationTestBean2");
        BeanAnnotationReader reader2 = createBeanAnnotationReader(clazz2);
        String[] strings2 = reader2.getNoPersisteneProps();
        assertNull("1", strings2);
    }

    public void testGetRelationKey() {
        Class clazz1 = getBeanClass("AnnotationTestBean1");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz1);
        BeanAnnotationReader reader1 = createBeanAnnotationReader(clazz1);
        PropertyDesc pd = beanDesc.getPropertyDesc("department");
        assertTrue("1", reader1.hasRelationNo(pd));
        assertEquals("1", 0, reader1.getRelationNo(pd));
        assertEquals("1", "DEPTNUM:DEPTNO", reader1.getRelationKey(pd));
        assertFalse("1", reader1.hasRelationNo(beanDesc
                .getPropertyDesc("prop2")));
    }

    public void testGetValueType() throws Exception {
        Class clazz = getBeanClass("AnnotationTestBean3");
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
        BeanAnnotationReader annotationReader = createBeanAnnotationReader(clazz);
        PropertyDesc aaaPd = beanDesc.getPropertyDesc("aaa");
        assertEquals((String) null, annotationReader.getValueType(aaaPd));

        PropertyDesc bbbPd = beanDesc.getPropertyDesc("bbb");
        assertEquals("fooType", annotationReader.getValueType(bbbPd));
    }

}
