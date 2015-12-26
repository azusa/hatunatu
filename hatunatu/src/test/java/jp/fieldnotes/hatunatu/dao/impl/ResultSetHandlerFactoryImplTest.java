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

import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.impl.MapResultSetHandler.RestrictMapResultSetHandler;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.resultset.BeanMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.DtoMetaDataResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.ObjectResultSetHandler;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;

/**
 * @author azusa
 * 
 */
public class ResultSetHandlerFactoryImplTest extends S2DaoTestCase {

    private ResultSetHandlerFactoryImpl resultSetHandlerFactoryImpl;

    protected void setUp() throws Exception {
        include("j2ee.dicon");
        resultSetHandlerFactoryImpl = new ResultSetHandlerFactoryImpl();
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
    }

    public void testCreateMapResultSetHandler_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl.createMapResultSetHandler() instanceof RestrictMapResultSetHandler);
    }

    public void testCreateMapResultSetHandler() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        assertFalse(resultSetHandlerFactoryImpl.createMapResultSetHandler() instanceof RestrictMapResultSetHandler);
    }

    public void testCreateBeanMetaDataResultSetHandler_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createBeanMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class)) instanceof BeanMetaDataResultSetHandler.RestrictBeanMetaDataResultSetHandler);
    }

    public void testCreateBeanMetaDataResultSetHandler() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createBeanMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class));
        assertTrue(handler instanceof BeanMetaDataResultSetHandler);

        assertFalse(handler instanceof BeanMetaDataResultSetHandler.RestrictBeanMetaDataResultSetHandler);
    }

    public void testCreateDtoMetaDataResultSet_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createDtoMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class)) instanceof DtoMetaDataResultSetHandler.RestrictDtoMetaDataResultSetHandler);
    }

    public void testCreateDtoMetaDataResultSet() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createDtoMetaDataResultSetHandler(getBeanMetaDataFactory()
                        .createBeanMetaData(Employee.class));
        assertTrue(handler instanceof DtoMetaDataResultSetHandler);

        assertFalse(handler instanceof DtoMetaDataResultSetHandler.RestrictDtoMetaDataResultSetHandler);
    }

    public void testCreateObjectMetaDataResultSet_restrict() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(true);
        assertTrue(resultSetHandlerFactoryImpl
                .createObjectResultSetHandler(null) instanceof ObjectResultSetHandler.RestrictObjectResultSetHandler);
    }

    public void testCreateObjectMetaDataResultSet() {
        resultSetHandlerFactoryImpl.setRestrictNotSingleResult(false);
        ResultSetHandler handler = resultSetHandlerFactoryImpl
                .createObjectResultSetHandler(null);
        assertTrue(handler instanceof ObjectResultSetHandler);
        assertFalse(handler instanceof ObjectResultSetHandler.RestrictObjectResultSetHandler);
    }

}
