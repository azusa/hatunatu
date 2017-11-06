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

import jp.fieldnotes.hatunatu.dao.*;

import java.sql.DatabaseMetaData;

public class FastPropertyTypeFactoryBuilder implements
        PropertyTypeFactoryBuilder {

    protected ValueTypeFactory valueTypeFactory = new ValueTypeFactoryImpl();

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

        return new FastPropertyTypeFactory(beanClass, beanAnnotationReader,
                valueTypeFactory, columnNaming);
    }

    public PropertyTypeFactory build(Class beanClass,
            BeanAnnotationReader beanAnnotationReader, Dbms dbms,
            DatabaseMetaData databaseMetaData) {

        return new FastPropertyTypeFactory(beanClass, beanAnnotationReader,
                valueTypeFactory, columnNaming, dbms);
    }

}
