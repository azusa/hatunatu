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
import jp.fieldnotes.hatunatu.dao.ColumnNaming;
import jp.fieldnotes.hatunatu.dao.DaoNamingConvention;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.PropertyTypeFactory;
import jp.fieldnotes.hatunatu.dao.PropertyTypeFactoryBuilder;
import jp.fieldnotes.hatunatu.dao.ValueTypeFactory;

public class PropertyTypeFactoryBuilderImpl implements
        PropertyTypeFactoryBuilder {

    public static final String valueTypeFactory_BINDING = "bindingType=must";

    public static final String columnNaming_BINDING = "bindingType=must";

    public static final String daoNamingConvention_BINDING = "bindingType=must";

    protected ValueTypeFactory valueTypeFactory;

    protected ColumnNaming columnNaming;

    protected DaoNamingConvention daoNamingConvention;

    public void setColumnNaming(ColumnNaming columnNaming) {
        this.columnNaming = columnNaming;
    }

    public void setDaoNamingConvention(DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }

    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public PropertyTypeFactory build(Class beanClass,
            BeanAnnotationReader beanAnnotationReader) {

        return new PropertyTypeFactoryImpl(beanClass, beanAnnotationReader,
                valueTypeFactory, columnNaming);
    }

    public PropertyTypeFactory build(Class beanClass,
            BeanAnnotationReader beanAnnotationReader, Dbms dbms,
            DatabaseMetaData databaseMetaData) {

        return new PropertyTypeFactoryImpl(beanClass, beanAnnotationReader,
                valueTypeFactory, columnNaming, dbms, databaseMetaData);
    }

}
