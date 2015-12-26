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
import jp.fieldnotes.hatunatu.dao.handler.BasicUpdateHandler;
import jp.fieldnotes.hatunatu.dao.impl.PropertyTypeImpl;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import org.seasar.extension.unit.S2TestCase;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;


/**
 * @author higa
 * 
 */
public class IdentityIdentifierGeneratorTest extends S2TestCase {

    /**
     * Constructor for InvocationImplTest.
     * 
     * @param arg0
     */
    public IdentityIdentifierGeneratorTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IdentityIdentifierGeneratorTest.class);
    }

    protected void setUp() throws Exception {
        include("j2ee.dicon");
    }

    protected void tearDown() throws Exception {
    }

    public void testGetGeneratedValueTx() throws Exception {
        BasicUpdateHandler updateHandler = new BasicUpdateHandler(
                getDataSource(),
                "insert into identitytable(id_name) values('hoge')");
        updateHandler.execute(null);
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(Hoge.class);
        PropertyDesc propertyDesc = beanDesc.getPropertyDesc("id");
        PropertyType propertyType = new PropertyTypeImpl(propertyDesc,
                ValueTypes.getValueType(int.class));
        IdentityIdentifierGenerator generator = new IdentityIdentifierGenerator(
                propertyType, new HSQL());
        Hoge hoge = new Hoge();
        generator.setIdentifier(hoge, getDataSource());
        System.out.println(hoge.getId());
        assertTrue("1", hoge.getId() >= 0);
    }
}