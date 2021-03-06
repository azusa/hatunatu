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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.AbstractBatchAutoHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;

public abstract class AbstractBatchAutoStaticCommand extends
        AbstractAutoStaticCommand {

    protected final boolean returningRows;

    public AbstractBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            String[] propertyNames, boolean returningRows) {

        super(dataSource, statementFactory, beanMetaData, propertyNames);
        this.returningRows = returningRows;
    }

    protected abstract AbstractBatchAutoHandler createBatchAutoHandler();

    @Override
    protected Object doExecute(Object[] args) throws Exception {
        AbstractBatchAutoHandler handler = createBatchAutoHandler();
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(getSql());
        queryObject.setMethodArguments(args);
        return handler.executeBatch(queryObject, args);
    }
}