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

import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.resultset.BeanMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.DtoMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.ObjectResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResultSetHandlerFactoryImplTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    private ResultSetHandlerFactoryImpl resultSetHandlerFactoryImpl;

    @Before
    public void setUp() throws Exception {
        resultSetHandlerFactoryImpl = new ResultSetHandlerFactoryImpl();
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
    }

    @Test
    public void testCreateBeanMetaDataResultSetHandler_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createBeanMetaDataResultSetHandler(test.getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class)) instanceof BeanMetaDataResultSetHandler.RestrictBeanMetaDataResultSetHandler);
    }

    @Test
    public void testCreateBeanMetaDataResultSetHandler() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createBeanMetaDataResultSetHandler(test.getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class));
        assertTrue(handler instanceof BeanMetaDataResultSetHandler);

        assertFalse(handler instanceof BeanMetaDataResultSetHandler.RestrictBeanMetaDataResultSetHandler);
    }

    @Test
    public void testCreateDtoMetaDataResultSet_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createDtoMetaDataResultSetHandler(test.getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class)) instanceof DtoMetaDataResultSetHandler.RestrictDtoMetaDataResultSetHandler);
    }

    @Test
    public void testCreateDtoMetaDataResultSet() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createDtoMetaDataResultSetHandler(test.getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class));
        assertTrue(handler instanceof DtoMetaDataResultSetHandler);

        assertFalse(handler instanceof DtoMetaDataResultSetHandler.RestrictDtoMetaDataResultSetHandler);
    }

    @Test
    public void testCreateObjectMetaDataResultSet_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createObjectResultSetHandler(null) instanceof ObjectResultSetHandler.RestrictObjectResultSetHandler);
    }

    @Test
    public void testCreateObjectMetaDataResultSet() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createObjectResultSetHandler(null);
        assertTrue(handler instanceof ObjectResultSetHandler);
        assertFalse(handler instanceof ObjectResultSetHandler.RestrictObjectResultSetHandler);
    }

}
