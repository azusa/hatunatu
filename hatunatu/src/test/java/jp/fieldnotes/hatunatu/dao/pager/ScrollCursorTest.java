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
package jp.fieldnotes.hatunatu.dao.pager;

import jp.fieldnotes.hatunatu.api.pager.PagerCondition;
import jp.fieldnotes.hatunatu.dao.impl.BasicResultSetFactory;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScrollCursorTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    private PagerResultSetFactoryWrapper pagerResultSetFactoryWrapper;

    private PagerStatementFactory pagerStatementFactory = new PagerStatementFactory();

    @Before
    public void setUp() throws Exception {
        pagerResultSetFactoryWrapper = new PagerResultSetFactoryWrapper(
                BasicResultSetFactory.INSTANCE);
        pagerResultSetFactoryWrapper.setUseScrollCursor(true);

    }


    @Test
    public void testPageLimitTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(2);
        assertEquals(0, condition.getOffset());
        assertEquals(0, condition.getCount());

        // ## Act ##
        List employees = getEmployees(condition);

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(2, employees.size());
        assertEquals(new BigDecimal("7369"), (BigDecimal) employees.get(0));
        assertEquals(new BigDecimal("7499"), (BigDecimal) employees.get(1));
    }

    public void testOffsetTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(2);
        condition.setOffset(1);

        // ## Act ##
        List employees = getEmployees(condition);

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(2, employees.size());
        assertEquals(new BigDecimal("7499"), (BigDecimal) employees.get(0));
        assertEquals(new BigDecimal("7521"), (BigDecimal) employees.get(1));
    }

    public void testLastPageTx() throws Exception {
        // ## Arrange ##
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(5);
        condition.setOffset(10);

        // ## Act ##
        List employees = getEmployees(condition);

        // ## Assert ##
        assertEquals(14, condition.getCount());
        assertEquals(4, employees.size());
        assertEquals(new BigDecimal("7876"), (BigDecimal) employees.get(0));
        assertEquals(new BigDecimal("7900"), (BigDecimal) employees.get(1));
        assertEquals(new BigDecimal("7902"), (BigDecimal) employees.get(2));
        assertEquals(new BigDecimal("7934"), (BigDecimal) employees.get(3));
    }

    private List getEmployees(PagerCondition condition) throws SQLException {
        List result = new ArrayList();
        QueryObject queryObject = new QueryObject();
        queryObject.setSql("SELECT EMPNO FROM EMP ORDER BY EMPNO");
        queryObject.setMethodArguments(new Object[]{condition});
        try (PreparedStatement ps = pagerStatementFactory.createPreparedStatement(
                test.getConnection(), queryObject)) {
            try (ResultSet rs = pagerResultSetFactoryWrapper.createResultSet(ps, new Object[]{condition})) {
                while (rs.next()) {
                    result.add(rs.getObject(1));
                }
            }
        }
        return result;
    }

}
