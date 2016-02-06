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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.impl.RelationRowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.impl.RowCreatorImpl;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeanMetaDataResultSetHandler2Test  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    private BeanMetaData beanMetaData;

    @Test
    public void testHandle() throws Exception {
        ResultSetHandler handler = new BeanMetaDataResultSetHandler(
                beanMetaData, new RowCreatorImpl(), new RelationRowCreatorImpl());
        String sql = "select empno, dept.dname as d_name from emp, dept where empno = 7788 and emp.deptno = dept.deptno";
        Connection con = test.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        MyEmp ret = null;
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ret = (MyEmp) handler.handle(rs, test.getQueryObject());
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        assertNotNull("1", ret);
        System.out.println(ret.getEmpno());
        assertEquals("2", "RESEARCH", ret.getDname());
    }

    @Before
    public void setUp() throws Throwable {
        beanMetaData = test.createBeanMetaData(MyEmp.class);
    }

    public static class MyEmp {
        private int empno;

        private String dname;

        /**
         * @return Returns the dname.
         */
        public String getDname() {
            return dname;
        }

        /**
         * @param dname
         *            The dname to set.
         */
        public void setDname(String dname) {
            this.dname = dname;
        }

        /**
         * @return Returns the empno.
         */
        public int getEmpno() {
            return empno;
        }

        /**
         * @param empno
         *            The empno to set.
         */
        public void setEmpno(int empno) {
            this.empno = empno;
        }
    }
}