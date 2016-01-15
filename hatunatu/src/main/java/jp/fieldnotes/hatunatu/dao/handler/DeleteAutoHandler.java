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
package jp.fieldnotes.hatunatu.dao.handler;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;

public class DeleteAutoHandler extends AbstractAutoHandler {

    public DeleteAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            PropertyType[] propertyTypes, boolean checkSingleRowUpdate) {

        super(dataSource, statementFactory, beanMetaData, propertyTypes,
                checkSingleRowUpdate);
    }

    @Override
    protected void setupBindVariables(Object bean, QueryObject queryObject) {
        setupDeleteBindVariables(bean, queryObject);
    }
}