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
package jp.fieldnotes.hatunatu.dao;

import jp.fieldnotes.hatunatu.api.BeanMetaData;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public interface BeanMetaDataFactory {

    BeanMetaData createBeanMetaData(Class daoInterface, Class beanClass) throws SQLException;

    BeanMetaData createBeanMetaData(Class beanClass) throws SQLException;

    BeanMetaData createBeanMetaData(Class beanClass, int relationNestLevel) throws SQLException;

    BeanMetaData createBeanMetaData(DatabaseMetaData dbMetaData,
            Class beanClass, int relationNestLevel);
}
