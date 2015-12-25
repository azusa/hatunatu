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
import org.seasar.dao.handler.AbstractBatchAutoHandler;
import org.seasar.dao.StatementFactory;

/**
 * @author higa
 * 
 */
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

    public Object execute(Object[] args) {
        AbstractBatchAutoHandler handler = createBatchAutoHandler();
        injectDaoClass(handler);
        handler.setSql(getSql());
        if (this.returningRows) {
            return handler.executeBatch(args);
        } else {
            int updatedRows = handler.execute(args);
            return new Integer(updatedRows);
        }
    }
}