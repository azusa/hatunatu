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

import jp.fieldnotes.hatunatu.dao.dbms.HSQL;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.impl.PropertyTypeImpl;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.unit.S2TestCase;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import static org.junit.Assert.assertTrue;


public class SequenceIdentifierGeneratorTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(getClass());

    @Test
    public void testGenerateTx() throws Exception {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);
        PropertyDesc propertyDesc = beanDesc.getPropertyDesc("id");
        PropertyType propertyType = new PropertyTypeImpl(propertyDesc,
                ValueTypes.getValueType(int.class));
        SequenceIdentifierGenerator generator = new SequenceIdentifierGenerator(
                propertyType, new HSQL());
        generator.setSequenceName("myseq");
        Hoge hoge = new Hoge();
        generator.setIdentifier(hoge, test.getDataSource());
        System.out.println(hoge.getId());
        assertTrue("1", hoge.getId() > 0);
    }

    @Test
    public void testGenerate_allocationSizeTx() throws Exception {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);
        PropertyDesc propertyDesc = beanDesc.getPropertyDesc("id");
        PropertyType propertyType = new PropertyTypeImpl(propertyDesc,
                ValueTypes.getValueType(int.class));
        SequenceIdentifierGenerator generator = new SequenceIdentifierGenerator(
                propertyType, new HSQL());
        generator.setSequenceName("myseq2");
        generator.setAllocationSize(10L);
        Hoge hoge = new Hoge();
        generator.setIdentifier(hoge, test.getDataSource());
        System.out.println(hoge.getId());
        assertTrue(hoge.getId() > 0);
        int prev = hoge.getId();
        for (int i = 0; i < 31; i++) {
            generator.setIdentifier(hoge, test.getDataSource());
            System.out.println(hoge.getId());
            assertTrue(hoge.getId() > prev);
            prev = hoge.getId();
        }
    }

}