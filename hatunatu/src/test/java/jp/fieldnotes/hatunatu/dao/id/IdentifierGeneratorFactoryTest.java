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
import jp.fieldnotes.hatunatu.dao.annotation.tiger.IdType;
import jp.fieldnotes.hatunatu.dao.dbms.HSQL;
import jp.fieldnotes.hatunatu.dao.impl.Identifier;
import jp.fieldnotes.hatunatu.dao.impl.PropertyTypeImpl;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdentifierGeneratorFactoryTest {

    private Dbms dbms;

    private Hoge hoge;

    private PropertyType propertyType;

    @Before
    public void before() {
        dbms = new HSQL();
        hoge = new Hoge();
        hoge.setId(1);
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);
        PropertyDesc propertyDesc = beanDesc.getPropertyDesc("id");
        propertyType = new PropertyTypeImpl(propertyDesc,
                ValueTypes.getValueType(int.class));
    }


    @Test
    public void testCreateAssinedIdentifierGenerator() throws Exception {
        IdentifierGenerator generator = IdentifierGeneratorFactory
                .createIdentifierGenerator(propertyType, dbms, null);
        assertEquals(AssignedIdentifierGenerator.class, generator
                .getClass());
    }

    @Test
    public void testCreateIdentityIdentifierGenerator2() throws Exception {
        Identifier identifier = new Identifier();
        identifier.setIdType(IdType.IDENTITY);
        IdentifierGenerator generator = IdentifierGeneratorFactory.createIdentifierGenerator(
                propertyType, dbms, identifier);
        assertEquals("2", IdentityIdentifierGenerator.class, generator
                .getClass());
    }

    @Test
    public void testCreateSequenceIdentifierGenerator() throws Exception {
        Identifier identifier = new Identifier();
        identifier.setIdType(IdType.SEQUENCE);
        identifier.setSequenceName("myseq");

        IdentifierGenerator generator = IdentifierGeneratorFactory.createIdentifierGenerator(
                propertyType, dbms, identifier);
        assertEquals("myseq", ((SequenceIdentifierGenerator) generator)
                .getSequenceName());
    }

    @Test
    public void testCreateSequenceAllocationSizeIdentifierGenerator4() throws Exception {
        Identifier identifier = new Identifier();
        identifier.setIdType(IdType.SEQUENCE);
        identifier.setSequenceName("myseq");
        identifier.setAllocationSize(10);

        IdentifierGenerator generator = IdentifierGeneratorFactory.createIdentifierGenerator(
                propertyType, dbms,
                identifier);
    }

}