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
package jp.fieldnotes.hatunatu.dao.id;

import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.dbms.HSQL;
import jp.fieldnotes.hatunatu.dao.impl.PropertyTypeImpl;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdentifierGeneratorFactoryTest {

    @Test
    public void testCreateIdentifierGenerator() throws Exception {
        Dbms dbms = new HSQL();
        Hoge hoge = new Hoge();
        hoge.setId(1);
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);
        PropertyDesc propertyDesc = beanDesc.getPropertyDesc("id");
        PropertyType propertyType = new PropertyTypeImpl(propertyDesc,
                ValueTypes.getValueType(int.class));
        IdentifierGenerator generator = IdentifierGeneratorFactory
                .createIdentifierGenerator(propertyType, dbms, null);
        assertEquals("1", AssignedIdentifierGenerator.class, generator
                .getClass());
        generator = IdentifierGeneratorFactory.createIdentifierGenerator(
                propertyType, dbms, "identity");
        assertEquals("2", IdentityIdentifierGenerator.class, generator
                .getClass());
        generator = IdentifierGeneratorFactory.createIdentifierGenerator(
                propertyType, dbms, "sequence, sequenceName = myseq");
        assertEquals("3", "myseq", ((SequenceIdentifierGenerator) generator)
                .getSequenceName());
        generator = IdentifierGeneratorFactory.createIdentifierGenerator(
                propertyType, dbms,
                "sequence, sequenceName = myseq, allocationSize = 10");
        assertEquals("4", 10, ((SequenceIdentifierGenerator) generator)
                .getAllocationSize());
    }
}