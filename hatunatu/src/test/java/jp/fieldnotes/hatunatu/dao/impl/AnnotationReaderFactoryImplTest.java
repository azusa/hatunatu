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

import jp.fieldnotes.hatunatu.api.DaoAnnotationReader;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.dao.EmployeeDao;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationReaderFactoryImplTest {

    private AnnotationReaderFactoryImpl fieldAnnotationReaderFactory;

    private AnnotationReaderFactoryImpl annotationReaderFactory;


    @Before
    public void setUp() throws Exception {
        fieldAnnotationReaderFactory = new AnnotationReaderFactoryImpl();
        annotationReaderFactory = new AnnotationReaderFactoryImpl();
    }

    @Test
    public void testBeanAnnotationReader() throws Exception {
        // ## Arrange ##
        final Class beanClass = Employee.class;

        // ## Act ##
        final BeanAnnotationReader beanAnnotationReader = annotationReaderFactory
                .createBeanAnnotationReader(beanClass);

        // ## Assert ##
        assertEquals(fieldAnnotationReaderFactory.createBeanAnnotationReader(
                beanClass).getClass(), beanAnnotationReader.getClass());
    }

    @Test
    public void testDaoAnnotationReader() throws Exception {
        // ## Arrange ##
        final BeanDesc daoBeanDesc = BeanDescFactory
                .getBeanDesc(EmployeeDao.class);

        // ## Act ##
        final DaoAnnotationReader daoAnnotationReader = annotationReaderFactory
                .createDaoAnnotationReader(daoBeanDesc);

        // ## Assert ##
        assertEquals(fieldAnnotationReaderFactory.createDaoAnnotationReader(
                daoBeanDesc).getClass(), daoAnnotationReader.getClass());
    }

}
