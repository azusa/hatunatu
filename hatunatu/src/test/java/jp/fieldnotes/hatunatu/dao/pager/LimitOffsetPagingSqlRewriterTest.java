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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.dataset.DataSet;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LimitOffsetPagingSqlRewriterTest extends S2DaoTestCase {
    private static final int TEST_OFFSET = 18;

    private static final int TEST_LIMIT = 12;

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "LimitOffsetTest.dicon");

    CustomerDao dao;

    LimitOffsetPagingSqlRewriter rewriter =  new LimitOffsetPagingSqlRewriter();


    @Test
    public void testMakeCountSql() {
        assertEquals("count(*)で全件数を取得するSQLを生成",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT) AS total",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT"));
        assertEquals("count(*)で全件数を取得するSQLを生成(order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
        assertEquals("count(*)で全件数を取得するSQLを生成(ORDER BY 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT ORDER BY id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(whitespace付きorder by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT\n) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT\norder by\n    id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE name like '%order by%' ) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE name like '%order by%' order by id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE name='aaa'/*order by*/) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE name='aaa'/*order by*/order by id"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(途中のorder byは除去しない)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT WHERE\n--order by\nname=1\n) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT WHERE\n--order by\nname=1\norder by id"));
        assertEquals("count(*)で全件数を取得するSQLを生成(order by除去 UNICODE)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT order by ＮＯ"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 UNICODE)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by 名前, 組織_ID"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 ASC,DESC)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order by 名前 ASC\n, 組織_ID DESC"));
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(order by除去 ASC,DESC+空行)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter
                        .makeCountSql("SELECT * FROM DEPARTMENT order\n\tby\n\n 名前 \n\tASC \n\n\n, 組織_ID \n\tDESC \n"));
    }

    @Test
    public void testSetChopOrderByAndMakeCountSql() throws Exception {
        assertEquals("count(*)で全件数を取得するSQLを生成(chopOrderBy=true, order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT ) AS total",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
        rewriter.setChopOrderBy(false);
        assertEquals(
                "count(*)で全件数を取得するSQLを生成(chopOrderBy=false, order by 除去)",
                "SELECT count(*) FROM (SELECT * FROM DEPARTMENT order by id) AS total",
                rewriter.makeCountSql("SELECT * FROM DEPARTMENT order by id"));
    }

    /*
     * public void testMakeBaseSql() throws Exception { try {
     * PagerContext.getContext().pushArgs(createNormalArgs()); assertEquals(
     * "SELECTの前のネイティブSQLを除去", "SELECT * FROM DEPARTMENT",
     * wrapper.makeBaseSql("native sql ... SELECT * FROM DEPARTMENT"));
     * assertEquals( "ネイティブSQLが存在しない場合、元のSQLも変化なし", "SELECT * FROM DEPARTMENT",
     * wrapper.makeBaseSql("SELECT * FROM DEPARTMENT")); } finally {
     * PagerContext.getContext().popArgs(); } }
     */
    @Test
    public void testLimitOffsetSql() throws Exception {
        assertEquals("指定されたlimit offsetが付加されたSQLを生成",
                "SELECT * FROM DEPARTMENT LIMIT 10 OFFSET 55", rewriter
                        .makeLimitOffsetSql("SELECT * FROM DEPARTMENT", 10, 55));
    }

    @Test
    public void testPagingTx() throws Exception {
        readXlsAllReplaceDb("PagerTestData.xls");
        DefaultPagerCondition condition = new DefaultPagerCondition();
        condition.setLimit(TEST_LIMIT);
        condition.setOffset(TEST_OFFSET);
        Customer[] test = dao.getPagedRow(condition);
        assertNotNull(test);
        assertEquals(TEST_LIMIT, test.length);
        for (int i = 0; i < TEST_LIMIT; i++) {
            assertEquals(TEST_OFFSET + i + 1, test[i].getPriority());
        }
    }

    @Test
    public void testWithSqlCommentTx() throws Exception {
        readXlsAllReplaceDb("PagerTestData.xls");
        DataSet expected = readXls("PagingData01.xls");
        PagerTestCondition condition = new PagerTestCondition();
        condition.setStartDate((new SimpleDateFormat("yyyy/MM/dd"))
                .parse("2006/01/01"));
        condition.setSortKey("PRIORITY");
        condition.setLimit(TEST_LIMIT);
        condition.setOffset(TEST_OFFSET);
        Customer[] actual = dao.getPagedRow2(condition);
        assertNotNull(actual);
        assertDataSetEquals("PagerTestData", expected, actual);
        assertEquals(81, condition.getCount());
    }

    @Test
    public void testNonPagingCompatibityFalseTx() throws Exception {
        readXlsAllReplaceDb("PagerTestData.xls");
        DataSet expected = readXls("PagerTestData.xls");
        Customer[] actual = dao.getAll();
        assertNotNull(actual);
        assertDataSetEquals("PagerTestData",expected, actual);
    }

    @Override
    protected DataSource getDataSource() {
        return test.getDataSource();
    }

    @Override
    protected BeanMetaData createBeanMetaData(final Class beanClass) throws SQLException {
        return test.createBeanMetaData(beanClass);
    }

}
