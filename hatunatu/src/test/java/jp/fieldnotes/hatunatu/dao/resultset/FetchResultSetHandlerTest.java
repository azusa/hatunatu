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
package jp.fieldnotes.hatunatu.dao.resultset;

import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.DtoMetaDataFactory;
import jp.fieldnotes.hatunatu.dao.FetchHandler;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class FetchResultSetHandlerTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "dao.dicon");

    DtoMetaDataFactory dtoMetaDataFactory;

    @Before
    public void setUp() throws Exception {
        PagerContext.start();
    }

    @After
    public void tearDown() throws Exception {
        PagerContext.end();
    }

    @Test
    public void testCreateResultSetHandler() {
        FetchResultSetHandler resultSetHandler = new FetchResultSetHandler(
                Employee.class, test.createBeanMetaData(Employee.class),
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

    public void testGetParameterClass() {
        FetchResultSetHandler resultSetHandler = new FetchResultSetHandler(
                Employee.class, test.createBeanMetaData(Employee.class),
                dtoMetaDataFactory);
        FetchHandler fetchHandler = new FetchHandler<Employee>() {
            public boolean execute(Employee bean) {
                return false;
            }
        };
        Class clazz = resultSetHandler.getParameterClass(fetchHandler);
        assertEquals(Employee.class, clazz);
    }

    public void testGetFetchHandler() {
        FetchResultSetHandler resultSetHandler = new FetchResultSetHandler(
                Employee.class, test.createBeanMetaData(Employee.class),
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
