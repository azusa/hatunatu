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

import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.dao.ArgumentDtoAnnotationReader;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ParameterType;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ProcedureParameter;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.ValueType;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ArgumentDtoAnnotationReaderImplTest {

    private ArgumentDtoAnnotationReader reader = new ArgumentDtoAnnotationReaderImpl();

    private BeanDesc hogeDesc = BeanDescFactory.getBeanDesc(Hoge.class);

    private BeanDesc fooDesc = BeanDescFactory.getBeanDesc(Foo.class);

    @Test
    public void testGetProcedureParameter_fieldAnnotation() throws Exception {
        Field field = hogeDesc.getFieldDesc("aaa").getField();
        String value = reader.getProcedureParameter(hogeDesc, field);
        assertEquals("in", value);
    }

    @Test
    public void testGetProcedureParameter_methodAnnoation() throws Exception {
        Field field = hogeDesc.getFieldDesc("ddd").getField();
        String value = reader.getProcedureParameter(hogeDesc, field);
        assertEquals("out", value);
    }

    @Test
    public void testGetProcedureParameter_constantAnnotation() throws Exception {
        Field field = hogeDesc.getFieldDesc("aaa").getField();
        String value = reader.getProcedureParameter(fooDesc, field);
        assertEquals("in", value);
    }

    @Test
    public void testGetProcedureParameter_none() throws Exception {
        Field field = hogeDesc.getFieldDesc("bbb").getField();
        String value = reader.getProcedureParameter(hogeDesc, field);
        assertNull(value);
    }

    @Test
    public void testGetProcedureParameter_none_constantAnnotation()
            throws Exception {
        Field field = hogeDesc.getFieldDesc("bbb").getField();
        String value = reader.getProcedureParameter(fooDesc, field);
        assertNull(value);
    }

    @Test
    public void testGetProcedureParameter_public_fieldAnnotation()
            throws Exception {
        Field field = hogeDesc.getFieldDesc("ccc").getField();
        String value = reader.getProcedureParameter(hogeDesc, field);
        assertEquals("out", value);
    }

    @Test
    public void testGetProcedureParameter_public_constantAnnotation()
            throws Exception {
        Field field = hogeDesc.getFieldDesc("ccc").getField();
        String value = reader.getProcedureParameter(fooDesc, field);
        assertEquals("out", value);
    }

    @Test
    public void testGetValueType_fieldAnnotation() throws Exception {
        Field field = hogeDesc.getFieldDesc("aaa").getField();
        assertEquals("hogeValueType", reader.getValueType(hogeDesc, field));
    }

    @Test
    public void testGetValueType_methodAnnotation() throws Exception {
        Field field = hogeDesc.getFieldDesc("ddd").getField();
        assertEquals("barValueType", reader.getValueType(hogeDesc, field));
    }

    @Test
    public void testGetValueType_constantAnnotation() throws Exception {
        Field field = hogeDesc.getFieldDesc("aaa").getField();
        assertEquals("hogeValueType", reader.getValueType(fooDesc, field));
    }

    public static class Hoge {

        @ValueType("hogeValueType")
        @ProcedureParameter()
        @SuppressWarnings("unused")
        private String aaa;

        @SuppressWarnings("unused")
        private String bbb;

        @ProcedureParameter(ParameterType.OUT)
        public String ccc;

        private String ddd;

        @ProcedureParameter(ParameterType.OUT)
        public void setDdd(String ddd) {
            this.ddd = ddd;
        }

        @ValueType("barValueType")
        public String getDdd() {
            return ddd;
        }
    }

    public static class Foo {

        public static final String PROCEDURE_PARAMETERS = null;

        public static final String aaa_VALUE_TYPE = "hogeValueType";

        public static final String aaa_PROCEDURE_PARAMETER = "in";

        public static final String ccc_PROCEDURE_PARAMETER = "out";

        @SuppressWarnings("unused")
        private String aaa;

        @SuppressWarnings("unused")
        private String bbb;

        @SuppressWarnings("unused")
        public String ccc;
    }
}
