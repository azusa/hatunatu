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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.seasar.dao.RelationRowCreator;
import org.seasar.dao.RowCreator;
import org.seasar.dao.unit.S2DaoTestCase;
import org.seasar.extension.jdbc.ResultSetHandler;

public class BeanArrayMetaDataResultSetHandlerTest extends S2DaoTestCase {

    public BeanArrayMetaDataResultSetHandlerTest(String arg0) {
        super(arg0);
    }

    public void testHandle() throws Exception {
        ResultSetHandler handler = new BeanArrayMetaDataResultSetHandler(
                createBeanMetaData(Employee.class), createRowCreator(),
                createRelationRowCreator());
        String sql = "select * from emp";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee[] ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee[]) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        for (int i = 0; i < ret.length; ++i) {
            Employee emp = ret[i];
            System.out.println(emp.getEmpno() + "," + emp.getEname());
        }
    }

    protected RowCreator createRowCreator() {// [DAO-118] (2007/08/25)
        return new RowCreatorImpl();
    }

    protected RelationRowCreator createRelationRowCreator() {
        return new RelationRowCreatorImpl();
    }

    public void setUp() {
        include("j2ee.dicon");
    }

    protected void setUpAfterContainerInit() throws Throwable {
        super.setUpAfterContainerInit();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner
                .run(BeanArrayMetaDataResultSetHandlerTest.class);
    }

}