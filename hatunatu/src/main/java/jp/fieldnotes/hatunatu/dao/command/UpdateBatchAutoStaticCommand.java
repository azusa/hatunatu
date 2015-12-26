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
package jp.fieldnotes.hatunatu.dao.command;

import javax.sql.DataSource;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.handler.AbstractAutoHandler;
import jp.fieldnotes.hatunatu.dao.handler.AbstractBatchAutoHandler;
import jp.fieldnotes.hatunatu.dao.handler.UpdateBatchAutoHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;

/**
 * @author higa
 * 
 */
public class UpdateBatchAutoStaticCommand extends
        AbstractBatchAutoStaticCommand {

    public UpdateBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            String[] propertyNames, boolean returningRows) {

        super(dataSource, statementFactory, beanMetaData, propertyNames, returningRows);
    }

    protected AbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

    protected AbstractBatchAutoHandler createBatchAutoHandler() {
        return new UpdateBatchAutoHandler(getDataSource(),
                getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

    protected void setupSql() {
        setupUpdateSql();
    }

    protected void setupPropertyTypes(String[] propertyNames) {
        setupUpdatePropertyTypes(propertyNames);

    }
}