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
package org.seasar.dao.command;

import javax.sql.DataSource;

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import org.seasar.dao.handler.AbstractAutoHandler;
import org.seasar.dao.handler.AbstractBatchAutoHandler;
import org.seasar.dao.handler.DeleteBatchAutoHandler;
import org.seasar.dao.StatementFactory;

/**
 * @author higa
 * 
 */
public class DeleteBatchAutoStaticCommand extends
        AbstractBatchAutoStaticCommand {

    public DeleteBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            String[] propertyNames, boolean returningRows) {

        super(dataSource, statementFactory, beanMetaData, propertyNames, returningRows);
    }

    protected AbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

    protected AbstractBatchAutoHandler createBatchAutoHandler() {
        return new DeleteBatchAutoHandler(getDataSource(),
                getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

    protected void setupSql() {
        setupDeleteSql();
    }

    protected void setupPropertyTypes(String[] propertyNames) {
        setupDeletePropertyTypes(propertyNames);

    }
}