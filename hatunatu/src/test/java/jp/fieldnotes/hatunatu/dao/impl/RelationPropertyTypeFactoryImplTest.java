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

import java.sql.DatabaseMetaData;

import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.dao.BeanMetaDataFactory;
import jp.fieldnotes.hatunatu.api.RelationPropertyType;
import jp.fieldnotes.hatunatu.dao.impl.bean.Employee20;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;

public class RelationPropertyTypeFactoryImplTest extends S2DaoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        include("j2ee.dicon");
    }

    public void test() throws Exception {
        RelationPropertyTypeFactoryImpl factory = createRelationPropertyTypeFactoryImpl();
        RelationPropertyType[] types = factory.createRelationPropertyTypes();
        assertNotNull(types);
        assertEquals(1, types.length);
    }

    private RelationPropertyTypeFactoryImpl createRelationPropertyTypeFactoryImpl() {
        Class beanClass = Employee20.class;
        BeanAnnotationReader beanAnnotationReader = new BeanAnnotationReaderImpl(
                beanClass);
        BeanMetaDataFactory beanMetaDataFactory = getBeanMetaDataFactory();
        DatabaseMetaData databaseMetaData = getDatabaseMetaData();
        int relationNestLevel = 0;
        boolean isStopRelationCreation = false;
        return new RelationPropertyTypeFactoryImpl(beanClass,
                beanAnnotationReader, beanMetaDataFactory, databaseMetaData,
                relationNestLevel, isStopRelationCreation);
    }
}
