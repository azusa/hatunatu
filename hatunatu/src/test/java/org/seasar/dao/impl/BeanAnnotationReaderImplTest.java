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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.annotation.tiger.*;
import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.dbms.MySQL;
import org.seasar.dao.dbms.Oracle;
import org.seasar.dao.dbms.PostgreSQL;
import org.seasar.dao.impl.bean.Department;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import java.util.Date;

/**
 * @author manhole
 * @author azusa
 */
public class BeanAnnotationReaderImplTest extends TestCase {

    protected BeanAnnotationReader createBeanAnnotationReader(Class clazz) {
        return new BeanAnnotationReaderImpl(clazz);
    }

    protected Class getBeanClass(String className) {
        if ("AnnotationTestBean1".equals(className)) {
            return AnnotationTestBean1.class;
        } else if ("AnnotationTestBean2".equals(className)) {
            return AnnotationTestBean2.class;
        } else if ("AnnotationTestBean3".equals(className)) {
            return AnnotationTestBean3.class;
        } else if ("AnnotationTestBean4".equals(className)) {
            return AnnotationTestBean4.class;
        } else if ("AnnotationTestBean5".equals(className)) {
            return AnnotationTestBean5.class;
        } else if ("AnnotationTestBean6".equals(className)) {
            return AnnotationTestBean6.class;
        }

        throw new AssertionFailedError(className);
    }

    @Bean(table = "TABLE", noPersistentProperty = "prop2", timeStampProperty = "myTimestamp", versionNoProperty = "myVersionNo")
    public static class AnnotationTestBean1 {

        private Department department;

        private Date myTimestamp;

        public int getProp1() {
            return 0;
        }

        @Id(value = IdType.SEQUENCE, sequenceName = "myseq")
        @Column("Cprop1")
        public void setProp1(int i) {
        }

        public int getProp2() {
            return 0;
        }

        public void setProp2(int i) {
        }

        public Date getMyTimestamp() {
            return myTimestamp;
        }

        public void setMyTimestamp(Date myTimestamp) {
            this.myTimestamp = myTimestamp;
        }

        public Department getDepartment() {
            return department;
        }

        @Relation(relationNo = 0, relationKey = "DEPTNUM:DEPTNO")
        public void setDepartment(Department department) {
            this.department = department;
        }

    }

    public static class AnnotationTestBean2 {

        public int getProp1() {
            return 0;
        }

        @Column("Cprop1")
        public void setProp1(int i) {
        }

        public int getProp2() {
            return 0;
        }

        public void setProp2(int i) {
        }
    }

    public static class AnnotationTestBean3 {

        private String aaa;

        private String bbb;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        @ValueType("fooType")
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
    }

    public static class AnnotationTestBean4 {

        private String aaa;

        private String bbb;

        @Ids( {
                @Id(value = IdType.IDENTITY, dbms = "oracle"),
                @Id(value = IdType.SEQUENCE, sequenceName = "myseq", dbms = "mysql"),
                @Id(value = IdType.SEQUENCE, sequenceName = "myseq_2", allocationSize = 10) })
        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        @ValueType("fooType")
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
    }

    public static class AnnotationTestBean5 {

        private String aaa;

        private String bbb;

        @Id(value = IdType.IDENTITY, dbms = "oracle")
        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        @ValueType("fooType")
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
    }

    public static class AnnotationTestBean6 {

        private String aaa;

        private String bbb;

        @Id(value = IdType.IDENTITY)
        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        @ValueType("fooType")
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
    }


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
