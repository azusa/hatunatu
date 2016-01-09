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

import jp.fieldnotes.hatunatu.dao.exception.IllegalSignatureRuntimeException;
import jp.fieldnotes.hatunatu.dao.ProcedureMetaData;
import jp.fieldnotes.hatunatu.dao.ProcedureParameterType;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ParameterType;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ProcedureParameter;
import jp.fieldnotes.hatunatu.dao.impl.AnnotationReaderFactoryImpl;
import jp.fieldnotes.hatunatu.dao.impl.ProcedureMetaDataFactoryImpl;
import jp.fieldnotes.hatunatu.dao.impl.ValueTypeFactoryImpl;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.unit.S2TestCase;

import static org.junit.Assert.*;

public class ProcedureMetaDataFactoryImplTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "jdbc-derby.dicon");


    @Test
    public void testCreateProcedureMetaData() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setAnnotationReaderFactory(new AnnotationReaderFactoryImpl());
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.initialize();
        ProcedureMetaData metaData = factory.createProcedureMetaData(name,
                Dao.class.getMethod("execute", new Class[] { Hoge.class }));

        assertNotNull(metaData);
        assertEquals(3, metaData.getParameterTypeSize());

        ProcedureParameterType ppt = metaData.getParameterType("ccc");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());

        ppt = metaData.getParameterType("ddd");
        assertTrue(ppt.isInType());
        assertEquals(ValueTypes.INTEGER, ppt.getValueType());

        ppt = metaData.getParameterType("eee");
        assertTrue(ppt.isOutType());
        assertEquals(ValueTypes.STRING, ppt.getValueType());
    }

    @Test
    public void testCreateProcedureMetaData_noParameter() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new AnnotationReaderFactoryImpl());
        factory.initialize();
        ProcedureMetaData metaData = factory.createProcedureMetaData(name,
                Dao.class.getMethod("executeWithNoParameter", new Class[] {}));
        assertNotNull(metaData);
        assertEquals(0, metaData.getParameterTypeSize());
    }

    @Test
    public void testCreateProcedureMetaData_simpleParameter() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new AnnotationReaderFactoryImpl());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao.class.getMethod(
                    "executeWithSimpleParameter", new Class[] { int.class }));
            fail();
        } catch (IllegalSignatureRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateProcedureMetaData_multiParameters() throws Exception {
        String name = "PROCEDURE_TEST_CCC2";
        ProcedureMetaDataFactoryImpl factory = new ProcedureMetaDataFactoryImpl();
        factory.setValueTypeFactory(new ValueTypeFactoryImpl());
        factory.setAnnotationReaderFactory(new AnnotationReaderFactoryImpl());
        factory.initialize();
        try {
            factory.createProcedureMetaData(name, Dao.class.getMethod(
                    "executeWithMultiParameters", new Class[] { Hoge.class,
                            int.class }));
            fail();
        } catch (IllegalSignatureRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public interface Dao {
        void execute(Hoge hoge);

        void executeWithNoParameter();

        void executeWithSimpleParameter(int aaa);

        void executeWithMultiParameters(Hoge hoge, int aaa);
    }

    public static class Hoge {

        @ProcedureParameter(ParameterType.OUT)
        private String ccc;

        @ProcedureParameter(ParameterType.IN)
        private int ddd;

        @ProcedureParameter(ParameterType.OUT)
        private String eee;

        public String getCcc() {
            return ccc;
        }

        public void setCcc(String ccc) {
            this.ccc = ccc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public String getEee() {
            return eee;
        }

        public void setEee(String eee) {
            this.eee = eee;
        }
    }
}
