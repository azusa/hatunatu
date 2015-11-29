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

import org.seasar.dao.*;
import org.seasar.dao.impl.bean.Employee;
import org.seasar.dao.impl.dao.EmployeeDao;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.util.beans.BeanDesc;
import org.seasar.util.beans.factory.BeanDescFactory;

import java.lang.reflect.Method;

/**
 * @author jundu
 * 
 */
public class ResultSetHandlerFactorySelectorTest extends S2DaoTestCase {

    ResultSetHandlerFactory resultSetHandlerFactory;

    AnnotationReaderFactory annotationReaderFactory;

    BeanMetaDataFactory beanMetaDataFactory;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        include("dao.dicon");
    }

    /**
     * Test method for
     * {@link ResultSetHandlerFactorySelector#getResultSetHandler(DaoAnnotationReader, BeanMetaData, Method)}.
     */
    public void testGetResultSetHandler() {
        BeanDesc daoBeanDesc = BeanDescFactory.getBeanDesc(EmployeeDao.class);
        DaoAnnotationReader daoAnnotationReader = annotationReaderFactory
                .createDaoAnnotationReader(daoBeanDesc);
        BeanMetaData beanMetaData = beanMetaDataFactory
                .createBeanMetaData(Employee.class);
        Method[] methods = EmployeeDao.class.getMethods();
        Method method = findMethod(methods, "fetchAll");
        ResultSetHandler resultSetHandler = resultSetHandlerFactory
                .getResultSetHandler(daoAnnotationReader, beanMetaData, method);
        assertTrue(resultSetHandler instanceof FetchResultSetHandler);
    }

    protected Method findMethod(Method[] methods, String name) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
}
