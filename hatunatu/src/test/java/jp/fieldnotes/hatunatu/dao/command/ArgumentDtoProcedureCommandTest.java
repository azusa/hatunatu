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
package jp.fieldnotes.hatunatu.dao.command;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ParameterType;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ProcedureCall;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ProcedureParameter;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.util.exception.SIllegalArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.dao.impl.Procedures;

import static org.junit.Assert.*;

public class ArgumentDtoProcedureCommandTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "jdbc-derby.dicon");

    @Before
    public void setUp() throws Exception {
        Procedures.params = new HashMap();
    }

    @After
    public void tearDown() throws Exception {
        Procedures.params = null;
    }

    @Test
    public void testOutParameterTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class,"executeAaa1"));
        Aaa1 dto = new Aaa1();
        command.execute(new Object[] { dto });
        assertNotNull(dto.getFoo());
    }

    @Test
    public void testMultiOutParametersTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "executeAaa2"));
        Aaa2 dto = new Aaa2();
        command.execute(new Object[] { dto });
        assertNotNull(dto.getBbb());
        assertNotNull(dto.getCcc());
    }

    @Test
    public void testEmptyArgumentTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "executeAaa3"));
        command.execute(new Object[] {});
        assertTrue(Procedures.isAaa3Invoked);
    }

    @Test
    public void testInParameterTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class,"executeBbb1"));
        Bbb1 dto = new Bbb1();
        dto.setCcc("hoge");
        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
    }

    @Test
    public void testMultiInParametersTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "executeBbb2"));
        Bbb2 dto = new Bbb2();
        dto.setCcc("hoge");
        dto.setDdd(new BigDecimal("10"));
        dto.setXxx(Timestamp.valueOf("2007-08-26 14:30:00"));
        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertEquals(Timestamp.valueOf("2007-08-26 14:30:00"),
                (Timestamp) Procedures.params.get("eee"));
    }

    @Test
    public void testInOutMixedParametersTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class,"executeCcc1"));
        Ccc1 dto = new Ccc1();
        dto.setCcc("hoge");
        dto.setDdd(new BigDecimal("10"));
        command.execute(new Object[] { dto });
        assertEquals("hoge", Procedures.params.get("ccc"));
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertNotNull(dto.getEee());
    }

    @Test
    public void testNullArgumentTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "executeCcc1"));
        try {
            command.execute(new Object[] { null });
            fail();
        } catch (SIllegalArgumentException e) {
            assertEquals("EDAO0029", e.getMessageCode());
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testInOutMixedParameters2Tx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "executeCcc2"));
        Ccc2 dto = new Ccc2();
        dto.setDdd(new BigDecimal("10"));
        command.execute(new Object[] { dto });
        assertNotNull(dto.getCcc());
        assertEquals(new BigDecimal("10"), (BigDecimal) Procedures.params
                .get("ddd"));
        assertNotNull(dto.getEee());
    }

    @Test
    public void testInOutParameterTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "executeDdd1"));
        Ddd1 dto = new Ddd1();
        dto.setCcc("ab");
        command.execute(new Object[] { dto });
        assertEquals("abcd", dto.getCcc());
    }

    @Test
    public void testReturnParameterTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Dao.class);
        SqlCommand command = dmd.getSqlCommand(test.getSingleDaoMethod(Dao.class, "max"));
        MaxDto dto = new MaxDto();
        dto.setBbb(5d);
        dto.setCcc(10d);
        command.execute(new Object[] { dto });
        assertEquals(10d, dto.getAaa(), 0);
    }

    public static interface Dao {

        @ProcedureCall(value = "PROCEDURE_TEST_AAA1")
        void executeAaa1(Aaa1 aaa1);

        @ProcedureCall(value = "PROCEDURE_TEST_AAA2")
        void executeAaa2(Aaa2 aaa2);

        @ProcedureCall(value = "PROCEDURE_TEST_AAA3")
        void executeAaa3();

        @ProcedureCall(value = "PROCEDURE_TEST_BBB1")
        void executeBbb1(Bbb1 bbb1);

        @ProcedureCall(value = "PROCEDURE_TEST_BBB2")
        void executeBbb2(Bbb2 bbb2);

        @ProcedureCall(value = "PROCEDURE_TEST_CCC1")
        void executeCcc1(Ccc1 ccc1);

        @ProcedureCall(value = "PROCEDURE_TEST_CCC2")
        void executeCcc2(Ccc2 ccc2);

        @ProcedureCall(value = "PROCEDURE_TEST_DDD1")
        void executeDdd1(Ddd1 ddd1);

        @ProcedureCall(value = "FUNCTION_TEST_MAX")
        double max(MaxDto maxDto);
    }

    public static class Aaa1 {

        private String foo;

        @ProcedureParameter(value = ParameterType.OUT)
        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    public static class Aaa2 {

        private String bbb;

        private Timestamp ccc;

        @ProcedureParameter(value = ParameterType.OUT)
        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        @ProcedureParameter(value = ParameterType.OUT)
        public Timestamp getCcc() {
            return ccc;
        }

        public void setCcc(Timestamp ccc) {
            this.ccc = ccc;
        }
    }

    public static class Bbb1 {

        private String ccc;

        @ProcedureParameter(value = ParameterType.IN)
        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }
    }

    public static class Bbb2 {

        private String ccc;

        private BigDecimal ddd;

        private Timestamp xxx;

        @ProcedureParameter(value = ParameterType.IN)
        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        @ProcedureParameter(value = ParameterType.IN)
        public BigDecimal getDdd() {
            return ddd;
        }

        public void setDdd(BigDecimal ddd) {
            this.ddd = ddd;
        }

        @ProcedureParameter(value = ParameterType.IN)
        public Timestamp getXxx() {
            return xxx;
        }

        public void setXxx(Timestamp xxx) {
            this.xxx = xxx;
        }
    }

    public static class Ccc1 {

        private String ccc;

        private BigDecimal ddd;

        private String eee;

        @ProcedureParameter(value = ParameterType.IN)
        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        @ProcedureParameter(value = ParameterType.IN)
        public BigDecimal getDdd() {
            return ddd;
        }

        public void setDdd(BigDecimal ddd) {
            this.ddd = ddd;
        }

        @ProcedureParameter(value = ParameterType.OUT)
        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Ccc2 {

        public static String ccc_PROCEDURE_PARAMETER = "out";

        public static String ddd_PROCEDURE_PARAMETER = "in";

        public static String eee_PROCEDURE_PARAMETER = "out";

        private String ccc;

        private BigDecimal ddd;

        private String eee;

        @ProcedureParameter(value = ParameterType.OUT)
        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        @ProcedureParameter(value = ParameterType.IN)
        public BigDecimal getDdd() {
            return ddd;
        }

        public void setDdd(BigDecimal ddd) {
            this.ddd = ddd;
        }

        @ProcedureParameter(value = ParameterType.OUT)
        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }

    public static class Ddd1 {

        private String ccc;

        @ProcedureParameter(value = ParameterType.INOUT)
        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }
    }

    public static class MaxDto {

        private double aaa;

        private double bbb;

        private double ccc;

        @ProcedureParameter(value = ParameterType.RETURN)
        public double getAaa() {
            return aaa;
        }

        public void setAaa(double aaa) {
            this.aaa = aaa;
        }

        @ProcedureParameter(value = ParameterType.IN)
        public double getBbb() {
            return bbb;
        }

        public void setBbb(double bbb) {
            this.bbb = bbb;
        }

        @ProcedureParameter(value = ParameterType.IN)
        public double getCcc() {
            return ccc;
        }

        public void setCcc(double ccc) {
            this.ccc = ccc;
        }

    }
}
