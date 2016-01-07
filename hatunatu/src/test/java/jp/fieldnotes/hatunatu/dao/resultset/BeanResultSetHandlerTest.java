/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.impl.Employee;
import org.seasar.extension.unit.S2TestCase;

public class BeanResultSetHandlerTest extends S2TestCase {

    /**
     * @throws Exception
     */
    public void testHandle() throws Exception {
        ResultSetHandler handler = new BeanResultSetHandler(Employee.class);
        String sql = "select * from emp where empno = 7788";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        System.out.println(ret.getEmpno() + "," + ret.getEname());
    }

    /**
     * @throws Exception
     */
    public void testHandle2() throws Exception {
        ResultSetHandler handler = new BeanResultSetHandler(Employee.class);
        String sql = "select ename, job from emp where empno = 7788";
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        Employee ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (Employee) handler.handle(rs);
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        System.out.println(ret.getEname() + "," + ret.getJob());
    }

    public void setUp() {
        include("j2ee.dicon");
    }
}