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

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.handler.AbstractAutoHandler;
import org.seasar.dao.handler.AbstractBatchAutoHandler;
import org.seasar.dao.handler.InsertBatchAutoHandler;
import org.seasar.dao.StatementFactory;

/**
 * @author higa
 */
public class InsertBatchAutoStaticCommand extends
        AbstractBatchAutoStaticCommand {

    public InsertBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, BeanMetaData beanMetaData,
            String[] propertyNames, boolean returningRows) {

        super(dataSource, statementFactory, beanMetaData, propertyNames, returningRows);
    }

    protected AbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

    protected AbstractBatchAutoHandler createBatchAutoHandler() {
        return new InsertBatchAutoHandler(getDataSource(),
                getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

    protected void setupSql() {
        setupInsertSql();
    }

    protected void setupPropertyTypes(String[] propertyNames) {
        setupInsertPropertyTypes(propertyNames);
    }

}
