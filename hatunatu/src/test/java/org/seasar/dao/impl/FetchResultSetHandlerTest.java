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

import org.seasar.dao.DtoMetaDataFactory;
import org.seasar.dao.FetchHandler;
import org.seasar.dao.impl.*;
import org.seasar.dao.impl.bean.Employee;
import org.seasar.dao.impl.dto.EmployeeDto;
import org.seasar.dao.pager.PagerContext;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

import java.util.Map;

/**
 * @author jundo
 * 
 */
public class FetchResultSetHandlerTest extends S2DaoTestCase {

    DtoMetaDataFactory dtoMetaDataFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        include("dao.dicon");
        PagerContext.start();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        PagerContext.end();
    }

    /**
     * Test method for
     * {@link FetchResultSetHandler#createResultSetHandler(FetchHandler, java.lang.Class)}.
     */
    public void testCreateResultSetHandler() {
        FetchResultSetHandler resultSetHandler = new FetchResultSetHandler(
                Employee.class, createBeanMetaData(Employee.class),
                dtoMetaDataFactory);
        {
            FetchHandler fetchHandler = new FetchHandler<Employee>() {
                public boolean execute(Employee bean) {
                    return false;
                }
            };
            ResultSetHandler handler = resultSetHandler
                    .createResultSetHandler(fetchHandler);
            assertTrue(handler instanceof FetchBeanMetaDataResultSetHandler);
        }
        {
            FetchHandler fetchHandler = new FetchHandler<EmployeeDto>() {
                public boolean execute(EmployeeDto bean) {
                    return false;
                }
            };
            ResultSetHandler handler = resultSetHandler
                    .createResultSetHandler(fetchHandler);
            assertTrue(handler instanceof FetchDtoMetaDataResultSetHandler);
        }
        {
            FetchHandler fetchHandler = new FetchHandler<Map>() {
                public boolean execute(Map bean) {
                    return false;
                }
            };
            ResultSetHandler handler = resultSetHandler
                    .createResultSetHandler(fetchHandler);
            assertTrue(handler instanceof FetchMapResultSetHandler);
        }
        {
            FetchHandler fetchHandler = new FetchHandler<String>() {
                public boolean execute(String ename) {
                    return false;
                }
            };
            ResultSetHandler handler = resultSetHandler
                    .createResultSetHandler(fetchHandler);
            assertTrue(handler instanceof FetchObjectResultSetHandler);
        }
    }

    /**
     * Test method for
     * {@link FetchResultSetHandler#getParameterClass(FetchHandler)}.
     */
    public void testGetParameterClass() {
        FetchResultSetHandler resultSetHandler = new FetchResultSetHandler(
                Employee.class, createBeanMetaData(Employee.class),
                dtoMetaDataFactory);
        FetchHandler fetchHandler = new FetchHandler<Employee>() {
            public boolean execute(Employee bean) {
                return false;
            }
        };
        Class clazz = resultSetHandler.getParameterClass(fetchHandler);
        assertEquals(Employee.class, clazz);
    }

    /**
     * Test method for
     * {@link FetchResultSetHandler#getFetchHandler()}.
     */
    public void testGetFetchHandler() {
        FetchResultSetHandler resultSetHandler = new FetchResultSetHandler(
                Employee.class, createBeanMetaData(Employee.class),
                dtoMetaDataFactory);
        FetchHandler fetchHandler = new FetchHandler<Employee>() {
            public boolean execute(Employee bean) {
                return false;
            }
        };
        {
            Object[] args = new Object[] { "aaa", fetchHandler };
            PagerContext.getContext().pushArgs(args);
            FetchHandler aqtual = resultSetHandler.getFetchHandler();
            PagerContext.getContext().popArgs();
            assertEquals(fetchHandler, aqtual);
        }
        {
            Object[] args = new Object[] { "aaa" };
            PagerContext.getContext().pushArgs(args);
            try {
                FetchHandler aqtual = resultSetHandler.getFetchHandler();
                fail();
            } catch (IllegalArgumentException e) {
            }
            PagerContext.getContext().popArgs();
        }
        {
            Object[] args = new Object[] { "aaa", fetchHandler, "bbb" };
            PagerContext.getContext().pushArgs(args);
            try {
                FetchHandler aqtual = resultSetHandler.getFetchHandler();
                fail();
            } catch (IllegalArgumentException e) {
            }
            PagerContext.getContext().popArgs();
        }
    }

}
