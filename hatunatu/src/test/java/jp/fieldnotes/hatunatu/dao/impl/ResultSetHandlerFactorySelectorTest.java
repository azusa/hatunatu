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

import jp.fieldnotes.hatunatu.api.*;
import jp.fieldnotes.hatunatu.dao.AnnotationReaderFactory;
import jp.fieldnotes.hatunatu.dao.BeanMetaDataFactory;
import jp.fieldnotes.hatunatu.dao.ResultSetHandlerFactory;
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.dao.EmployeeDao;
import jp.fieldnotes.hatunatu.dao.resultset.FetchResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

public class ResultSetHandlerFactorySelectorTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "dao.dicon");

    ResultSetHandlerFactory resultSetHandlerFactory;

    AnnotationReaderFactory annotationReaderFactory;

    BeanMetaDataFactory beanMetaDataFactory;

    /**
     * Test method for
     * {@link ResultSetHandlerFactorySelector#getResultSetHandler(jp.fieldnotes.hatunatu.api.DaoAnnotationReader, BeanMetaData, Method)}.
     */
    @Test
    public void testGetResultSetHandler() {
        BeanDesc daoBeanDesc = BeanDescFactory.getBeanDesc(EmployeeDao.class);
        jp.fieldnotes.hatunatu.api.DaoAnnotationReader daoAnnotationReader = annotationReaderFactory
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
