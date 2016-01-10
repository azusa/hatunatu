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

import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.PropertyTypeFactory;
import jp.fieldnotes.hatunatu.dao.PropertyTypeFactoryBuilder;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee20;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;

import java.sql.DatabaseMetaData;

import static org.junit.Assert.*;

public class PropertyTypeFactoryImplTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, getClass().getName().replace('.', '/') + ".dicon");

    private Class beanClass = Employee20.class;

    private PropertyTypeFactoryBuilder builder;

    private boolean empnoInvoked;

    private boolean managerInvoked;

    private boolean deptnoInvoked;

    private boolean dummyInvoked;

    @Test
    public void testDto() throws Exception {
        PropertyTypeFactory factory = createDtoPropertyTypeFactory();
        PropertyType[] propertyTypes = factory.createDtoPropertyTypes();
        assertNotNull(propertyTypes);
        assertEquals(5, propertyTypes.length);
    }

    @Test
    public void testBean() throws Exception {
        PropertyTypeFactory factory = createBeanPropertyTypeFactory();
        PropertyType[] propertyTypes = factory.createBeanPropertyTypes("EMP");
        assertNotNull(propertyTypes);
        assertEquals(4, propertyTypes.length);
        for (int i = 0; i < propertyTypes.length; i++) {
            PropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equals("empno")) {
                empno(pt);
            } else if (pt.getPropertyName().equals("manager")) {
                manager(pt);
            } else if (pt.getPropertyName().equals("deptno")) {
                deptno(pt);
            } else if (pt.getPropertyName().equals("dummy")) {
                dummy(pt);
            } else {
                fail();
            }
        }
        assertTrue(empnoInvoked);
        assertTrue(managerInvoked);
        assertTrue(deptnoInvoked);
        assertTrue(dummyInvoked);
    }

    private void empno(PropertyType pt) throws Exception {
        assertEquals("empno", pt.getColumnName());
        assertTrue(pt.isPrimaryKey());
        assertTrue(pt.isPersistent());
        assertEquals(ValueTypes.LONG, pt.getValueType());
        empnoInvoked = true;
    }

    private void manager(PropertyType pt) throws Exception {
        assertEquals("mgr", pt.getColumnName());
        assertFalse(pt.isPrimaryKey());
        assertTrue(pt.isPersistent());
        assertEquals(ValueTypes.SHORT, pt.getValueType());
        managerInvoked = true;
    }

    private void deptno(PropertyType pt) throws Exception {
        assertEquals("deptno", pt.getColumnName());
        assertFalse(pt.isPrimaryKey());
        assertTrue(pt.isPersistent());
        assertEquals(ValueTypes.INTEGER, pt.getValueType());
        deptnoInvoked = true;
    }

    private void dummy(PropertyType pt) throws Exception {
        assertEquals("dummy", pt.getColumnName());
        assertFalse(pt.isPrimaryKey());
        assertFalse(pt.isPersistent());
        assertEquals(ValueTypes.STRING, pt.getValueType());
        dummyInvoked = true;
    }

    private PropertyTypeFactory createDtoPropertyTypeFactory() {
        BeanAnnotationReader beanAnnotationReader = new BeanAnnotationReaderImpl(
                beanClass);
        return builder.build(beanClass, beanAnnotationReader);
    }

    private PropertyTypeFactory createBeanPropertyTypeFactory() {
        BeanAnnotationReader beanAnnotationReader = new BeanAnnotationReaderImpl(
                beanClass);
        DatabaseMetaData databaseMetaData = test.getDatabaseMetaData();
        Dbms dbms = DbmsManager.getDbms(databaseMetaData);
        return builder.build(beanClass, beanAnnotationReader, dbms,
                databaseMetaData);
    }
}
