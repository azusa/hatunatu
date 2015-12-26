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
package jp.fieldnotes.hatunatu.dao.parser;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import jp.fieldnotes.hatunatu.dao.CommandContext;
import jp.fieldnotes.hatunatu.dao.exception.EndCommentNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.dao.Node;
import jp.fieldnotes.hatunatu.dao.SqlParser;
import jp.fieldnotes.hatunatu.dao.exception.TokenNotClosedRuntimeException;
import jp.fieldnotes.hatunatu.dao.context.CommandContextImpl;
import jp.fieldnotes.hatunatu.dao.node.BindVariableNode;
import jp.fieldnotes.hatunatu.dao.node.IfNode;
import jp.fieldnotes.hatunatu.dao.node.SqlNode;

/**
 * @author higa
 * 
 */
public class SqlParserImplTest extends TestCase {

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public SqlParserImplTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SqlParserImplTest.class);
    }

    public void testParse() throws Exception {
        String sql = "SELECT * FROM emp";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        Node node = parser.parse();
        node.accept(ctx);
        assertEquals("1", sql, ctx.getSql());
    }

    public void testParseEndSemicolon() throws Exception {
        testParseEndSemicolon(";");
        testParseEndSemicolon(";\t");
        testParseEndSemicolon("; ");
    }

    private void testParseEndSemicolon(String endChar) {
        String sql = "SELECT * FROM emp";
        SqlParser parser = new SqlParserImpl(sql + endChar);
        CommandContext ctx = new CommandContextImpl();
        Node node = parser.parse();
        node.accept(ctx);
        assertEquals("1", sql, ctx.getSql());
    }

    public void testCommentEndNotFound() throws Exception {
        String sql = "SELECT * FROM emp/*hoge";
        SqlParser parser = new SqlParserImpl(sql);
        try {
            parser.parse();
            fail("1");
        } catch (TokenNotClosedRuntimeException ex) {
            System.out.println(ex);
        }
    }

    public void testParseBindVariable4() throws Exception {
        String sql = "SELECT * FROM emp WHERE job = #*job*#'CLERK' AND deptno = #*deptno*#20";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        String job = "CLERK";
        Integer deptno = new Integer(20);
        ctx.addArg("job", job, job.getClass());
        ctx.addArg("deptno", deptno, deptno.getClass());
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println(ctx.getSql());
    }

    public void testParseBindVariable() throws Exception {
        String sql = "SELECT * FROM emp WHERE job = /*job*/'CLERK' AND deptno = /*deptno*/20";
        String sql2 = "SELECT * FROM emp WHERE job = ? AND deptno = ?";
        String sql3 = "SELECT * FROM emp WHERE job = ";
        String sql4 = " AND deptno = ";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        String job = "CLERK";
        Integer deptno = new Integer(20);
        ctx.addArg("job", job, job.getClass());
        ctx.addArg("deptno", deptno, deptno.getClass());
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 2, vars.length);
        assertEquals("3", job, vars[0]);
        assertEquals("4", deptno, vars[1]);
        assertEquals("5", 4, root.getChildSize());
        SqlNode sqlNode = (SqlNode) root.getChild(0);
        assertEquals("6", sql3, sqlNode.getSql());
        BindVariableNode varNode = (BindVariableNode) root.getChild(1);
        assertEquals("7", "job", varNode.getExpression());
        SqlNode sqlNode2 = (SqlNode) root.getChild(2);
        assertEquals("8", sql4, sqlNode2.getSql());
        BindVariableNode varNode2 = (BindVariableNode) root.getChild(3);
        assertEquals("9", "deptno", varNode2.getExpression());
    }

    public void testParseBindVariable2() throws Exception {
        String sql = "SELECT * FROM emp WHERE job = /* job*/'CLERK'";
        String sql3 = "SELECT * FROM emp WHERE job = ";
        String sql4 = "'CLERK'";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql, ctx.getSql());
        assertEquals("2", 3, root.getChildSize());
        SqlNode sqlNode = (SqlNode) root.getChild(0);
        assertEquals("3", sql3, sqlNode.getSql());
        SqlNode sqlNode2 = (SqlNode) root.getChild(2);
        assertEquals("4", sql4, sqlNode2.getSql());
    }

    public void testParseWhiteSpace() throws Exception {
        String sql = "SELECT * FROM emp WHERE empno = /*empno*/1 AND 1 = 1";
        String sql2 = "SELECT * FROM emp WHERE empno = ? AND 1 = 1";
        String sql3 = " AND 1 = 1";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        Integer empno = new Integer(7788);
        ctx.addArg("empno", empno, empno.getClass());
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
        SqlNode sqlNode = (SqlNode) root.getChild(2);
        assertEquals("2", sql3, sqlNode.getSql());
    }

    public void testParseIf() throws Exception {
        String sql = "SELECT * FROM emp/*IF job != null*/ WHERE job = /*job*/'CLERK'/*END*/";
        String sql2 = "SELECT * FROM emp WHERE job = ?";
        String sql3 = "SELECT * FROM emp";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        String job = "CLERK";
        ctx.addArg("job", job, job.getClass());
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 1, vars.length);
        assertEquals("3", job, vars[0]);
        assertEquals("4", 2, root.getChildSize());
        SqlNode sqlNode = (SqlNode) root.getChild(0);
        assertEquals("5", sql3, sqlNode.getSql());
        IfNode ifNode = (IfNode) root.getChild(1);
        assertEquals("6", "job != null", ifNode.getExpression());
        assertEquals("7", 2, ifNode.getChildSize());
        SqlNode sqlNode2 = (SqlNode) ifNode.getChild(0);
        assertEquals("8", " WHERE job = ", sqlNode2.getSql());
        BindVariableNode varNode = (BindVariableNode) ifNode.getChild(1);
        assertEquals("9", "job", varNode.getExpression());
        CommandContext ctx2 = new CommandContextImpl();
        root.accept(ctx2);
        System.out.println(ctx2.getSql());
        assertEquals("10", sql3, ctx2.getSql());
    }

    public void testParseIf2() throws Exception {
        String sql = "/*IF aaa != null*/aaa/*IF bbb != null*/bbb/*END*//*END*/";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("1", "", ctx.getSql());
        ctx.addArg("aaa", null, String.class);
        ctx.addArg("bbb", "hoge", String.class);
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("2", "", ctx.getSql());
        ctx.addArg("aaa", "hoge", String.class);
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("3", "aaabbb", ctx.getSql());
        CommandContext ctx2 = new CommandContextImpl();
        ctx2.addArg("aaa", "hoge", String.class);
        ctx2.addArg("bbb", null, String.class);
        root.accept(ctx2);
        System.out.println("[" + ctx2.getSql() + "]");
        assertEquals("4", "aaa", ctx2.getSql());
    }

    public void testParseElse() throws Exception {
        String sql = "SELECT * FROM emp WHERE /*IF job != null*/job = /*job*/'CLERK'-- ELSE job is null/*END*/";
        String sql2 = "SELECT * FROM emp WHERE job = ?";
        String sql3 = "SELECT * FROM emp WHERE job is null";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        String job = "CLERK";
        ctx.addArg("job", job, job.getClass());
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("1", sql2, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 1, vars.length);
        assertEquals("3", job, vars[0]);
        CommandContext ctx2 = new CommandContextImpl();
        root.accept(ctx2);
        System.out.println("[" + ctx2.getSql() + "]");
        assertEquals("4", sql3, ctx2.getSql());
    }

    public void testParseElse2() throws Exception {
        String sql = "/*IF false*/aaa--ELSE bbb = /*bbb*/123/*END*/";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        Integer bbb = new Integer(123);
        ctx.addArg("bbb", bbb, bbb.getClass());
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("1", "bbb = ?", ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 1, vars.length);
        assertEquals("3", bbb, vars[0]);
    }

    public void testParseElse3() throws Exception {
        String sql = "/*IF false*/aaa--ELSE bbb/*IF false*/ccc--ELSE ddd/*END*//*END*/";
        SqlParser parser = new SqlParserImpl(sql);
        CommandContext ctx = new CommandContextImpl();
        Node root = parser.parse();
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("1", "bbbddd", ctx.getSql());
    }

    public void testElse4() throws Exception {
        String sql = "SELECT * FROM emp/*BEGIN*/ WHERE /*IF false*/aaa-- ELSE AND deptno = 10/*END*//*END*/";
        String sql2 = "SELECT * FROM emp WHERE deptno = 10";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
    }

    public void testBegin() throws Exception {
        String sql = "SELECT * FROM emp/*BEGIN*/ WHERE /*IF job != null*/job = /*job*/'CLERK'/*END*//*IF deptno != null*/ AND deptno = /*deptno*/20/*END*//*END*/";
        String sql2 = "SELECT * FROM emp";
        String sql3 = "SELECT * FROM emp WHERE job = ?";
        String sql4 = "SELECT * FROM emp WHERE job = ? AND deptno = ?";
        String sql5 = "SELECT * FROM emp WHERE deptno = ?";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());

        CommandContext ctx2 = new CommandContextImpl();
        ctx2.addArg("job", "CLERK", String.class);
        ctx2.addArg("deptno", null, Integer.class);
        root.accept(ctx2);
        System.out.println(ctx2.getSql());
        assertEquals("2", sql3, ctx2.getSql());

        CommandContext ctx3 = new CommandContextImpl();
        ctx3.addArg("job", "CLERK", String.class);
        ctx3.addArg("deptno", new Integer(20), Integer.class);
        root.accept(ctx3);
        System.out.println(ctx3.getSql());
        assertEquals("3", sql4, ctx3.getSql());

        CommandContext ctx4 = new CommandContextImpl();
        ctx4.addArg("deptno", new Integer(20), Integer.class);
        ctx4.addArg("job", null, String.class);
        root.accept(ctx4);
        System.out.println(ctx4.getSql());
        assertEquals("4", sql5, ctx4.getSql());
    }

    public void testBeginAnd() throws Exception {
        String sql = "/*BEGIN*/WHERE /*IF true*/aaa BETWEEN /*bbb*/111 AND /*ccc*/123/*END*//*END*/";
        String sql2 = "WHERE aaa BETWEEN ? AND ?";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        ctx.addArg("bbb", "111", String.class);
        ctx.addArg("ccc", "222", String.class);
        root.accept(ctx);
        System.out.println("[" + ctx.getSql() + "]");
        assertEquals("1", sql2, ctx.getSql());
    }

    public void testIn() throws Exception {
        String sql = "SELECT * FROM emp WHERE deptno IN /*deptnoList*/(10, 20) ORDER BY ename";
        String sql2 = "SELECT * FROM emp WHERE deptno IN (?, ?) ORDER BY ename";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        List deptnoList = new ArrayList();
        deptnoList.add(new Integer(10));
        deptnoList.add(new Integer(20));
        ctx.addArg("deptnoList", deptnoList, List.class);
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 2, vars.length);
        assertEquals("3", new Integer(10), vars[0]);
        assertEquals("4", new Integer(20), vars[1]);
    }

    public void testIn2() throws Exception {
        String sql = "SELECT * FROM emp WHERE deptno IN /*deptnoList*/(10, 20) ORDER BY ename";
        String sql2 = "SELECT * FROM emp WHERE deptno IN (?, ?) ORDER BY ename";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        int[] deptnoArray = { 10, 20 };
        ctx.addArg("deptnoList", deptnoArray, deptnoArray.getClass());
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 2, vars.length);
        assertEquals("3", new Integer(10), vars[0]);
        assertEquals("4", new Integer(20), vars[1]);
    }

    public void testIn3() throws Exception {
        String sql = "SELECT * FROM emp WHERE ename IN /*enames*/('SCOTT','MARY') AND job IN /*jobs*/('ANALYST', 'FREE')";
        String sql2 = "SELECT * FROM emp WHERE ename IN (?, ?) AND job IN (?, ?)";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        String[] enames = { "SCOTT", "MARY" };
        String[] jobs = { "ANALYST", "FREE" };
        ctx.addArg("enames", enames, enames.getClass());
        ctx.addArg("jobs", jobs, jobs.getClass());
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql2, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 4, vars.length);
        assertEquals("3", "SCOTT", vars[0]);
        assertEquals("4", "MARY", vars[1]);
        assertEquals("5", "ANALYST", vars[2]);
        assertEquals("6", "FREE", vars[3]);
    }

    public void testParseBindVariable3() throws Exception {
        String sql = "BETWEEN sal ? AND ?";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        ctx.addArg("$1", new Integer(0), Integer.class);
        ctx.addArg("$2", new Integer(1000), Integer.class);
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", sql, ctx.getSql());
        Object[] vars = ctx.getBindVariables();
        assertEquals("2", 2, vars.length);
        assertEquals("3", new Integer(0), vars[0]);
        assertEquals("4", new Integer(1000), vars[1]);
    }

    public void testEndNotFound() throws Exception {
        String sql = "/*BEGIN*/";
        SqlParser parser = new SqlParserImpl(sql);
        try {
            parser.parse();
            fail("1");
        } catch (EndCommentNotFoundRuntimeException ex) {
            System.out.println(ex);
        }
    }

    public void testEndParent() throws Exception {
        String sql = "INSERT INTO ITEM (ID, NUM) VALUES (/*id*/1, /*num*/20)";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        ctx.addArg("id", new Integer(0), Integer.class);
        ctx.addArg("num", new Integer(1), Integer.class);
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", true, ctx.getSql().endsWith(")"));
    }

    public void testEmbeddedValue() throws Exception {
        String sql = "/*$aaa*/";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        ctx.addArg("aaa", new Integer(0), Integer.class);
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("1", "0", ctx.getSql());
    }

    /*
     * [seasar-s2dao-dev:31]
     * 
     * https://www.seasar.org/issues/browse/DAO-43
     */
    public void testCommentAlive() throws Exception {
        String sql = "SELECT AAA,BBB -- UGO \r\nFROM HOGE WHERE AAA=/*moge*/aaa AND FUGA=/* PIRO */ccc";
        SqlParser parser = new SqlParserImpl(sql);
        Node root = parser.parse();
        CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals(
                "SELECT AAA,BBB -- UGO \nFROM HOGE WHERE AAA=? AND FUGA=/* PIRO */ccc",
                ctx.getSql());
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-43
     * 
     * Oracleのヒント句が消されないこと。
     */
    public void testCommentAlive2() throws Exception {
        final SqlParser parser = new SqlParserImpl(
                "SELECT /*+ INDEX(TABLE_NAME INDEX_NAME) */ AAA FROM HOGE");
        final Node root = parser.parse();
        final CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        assertEquals(
                "SELECT /*+ INDEX(TABLE_NAME INDEX_NAME) */ AAA FROM HOGE", ctx
                        .getSql());
    }

    /*
     * https://www.seasar.org/issues/browse/DAO-72
     * 
     */
    public void testDeleteQuestion() throws Exception {
        final SqlParser parser = new SqlParserImpl(
                "SELECT AAA \r\n--comment? \r\n--comment2? \r\nFROM HOGE");
        final Node root = parser.parse();
        final CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("SELECT AAA \n--comment \n--comment2 \nFROM HOGE", ctx
                .getSql());
    }

    public void testDeleteQuestion2() throws Exception {
        final SqlParser parser = new SqlParserImpl(
                "SELECT AAA \r--comment? \r--comment2? \rFROM HOGE");
        final Node root = parser.parse();
        final CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("SELECT AAA \n--comment \n--comment2 \nFROM HOGE", ctx
                .getSql());
    }

    public void testDeleteQuestion3() throws Exception {
        final SqlParser parser = new SqlParserImpl(
                "SELECT AAA \n--comment? \n--comment2? \nFROM HOGE");
        final Node root = parser.parse();
        final CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("SELECT AAA \n--comment \n--comment2 \nFROM HOGE", ctx
                .getSql());
    }

    /*
     * ELSEコメント内に?、その後通常コメント内に?
     */
    public void testDeleteQuestion4() throws Exception {
        final SqlParser parser = new SqlParserImpl(
                "SELECT AAA FROM HOGE where /*IF hoge != null */ aa = /*fuga*/gaga --ELSE bb = ? /*END*/ /* comment? */");
        final Node root = parser.parse();
        final CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("SELECT AAA FROM HOGE where bb = ?  /* comment */", ctx
                .getSql());
    }

    /*
     * 埋め込み変数として?、行の途中にあるラインコメントの中に?
     */
    public void testDeleteQuestion5() throws Exception {
        final SqlParser parser = new SqlParserImpl(
                "SELECT AAA FROM HOGE where aa = ?  -- comment?");
        final Node root = parser.parse();
        final CommandContext ctx = new CommandContextImpl();
        root.accept(ctx);
        System.out.println(ctx.getSql());
        assertEquals("SELECT AAA FROM HOGE where aa = ?  -- comment", ctx
                .getSql());
    }

}
