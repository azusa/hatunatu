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

import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.dao.BeanMetaDataFactory;
import jp.fieldnotes.hatunatu.dao.RelationPropertyTypeFactory;
import jp.fieldnotes.hatunatu.dao.RelationPropertyTypeFactoryBuilder;

import java.sql.DatabaseMetaData;

public class RelationPropertyTypeFactoryBuilderImpl implements
        RelationPropertyTypeFactoryBuilder {

    public static final String beanMetaDataFactory_BINDING = "bindingType=must";

    public static final String beanEnhancer_BINDING = "bindingType=must";

    protected BeanMetaDataFactory beanMetaDataFactory;

    public void setBeanMetaDataFactory(BeanMetaDataFactory beanMetaDataFactory) {
        this.beanMetaDataFactory = beanMetaDataFactory;
    }

    public RelationPropertyTypeFactory build(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {

        return new RelationPropertyTypeFactoryImpl(beanClass,
                beanAnnotationReader, beanMetaDataFactory, databaseMetaData,
                relationNestLevel, isStopRelationCreation);
    }

}
