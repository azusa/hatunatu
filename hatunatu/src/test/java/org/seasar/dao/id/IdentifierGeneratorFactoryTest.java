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
package org.seasar.dao.id;

import org.seasar.dao.Dbms;
import org.seasar.dao.IdentifierGenerator;
import org.seasar.dao.dbms.HSQL;
import org.seasar.dao.PropertyType;
import org.seasar.dao.impl.PropertyTypeImpl;
import org.seasar.dao.types.ValueTypes;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.util.beans.BeanDesc;
import org.seasar.util.beans.PropertyDesc;
import org.seasar.util.beans.factory.BeanDescFactory;

/**
 * @author higa
 * 
 */
public class IdentifierGeneratorFactoryTest extends S2TestCase {

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public IdentifierGeneratorFactoryTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IdentifierGeneratorFactoryTest.class);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

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