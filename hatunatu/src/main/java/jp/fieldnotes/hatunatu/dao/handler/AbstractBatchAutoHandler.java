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
import jp.fieldnotes.hatunatu.dao.ReturningRowsBatchHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.sql.PreparedStatementUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractBatchAutoHandler extends AbstractAutoHandler
        implements ReturningRowsBatchHandler {

    public AbstractBatchAutoHandler(DataSource dataSource,
                                    StatementFactory statementFactory, BeanMetaData beanMetaData,
                                    PropertyType[] propertyTypes) {

        super(dataSource, statementFactory, beanMetaData, propertyTypes, false);
    }

    @Override
    public int[] execute(QueryObject queryObject, List<Object[]> list) throws Exception {
        if (list == null) {
            throw new IllegalArgumentException("list");
        }
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = prepareStatement(connection, queryObject)) {
                for (Object bean : list) {
                    execute(queryObject, ps, bean);
                }
                return PreparedStatementUtil.executeBatch(ps);
            }
        }
    }


    public int[] executeBatch(QueryObject queryObject, Object[] args) throws Exception {
        List list = null;
        if (args[0] instanceof Object[]) {
            list = Arrays.asList((Object[]) args[0]);
        } else if (args[0] instanceof List) {
            list = (List) args[0];
        }
        if (list == null) {
            throw new IllegalArgumentException("args[0]");
        }
        return execute(queryObject, list);
    }

    protected void execute(QueryObject queryObject, PreparedStatement ps, Object bean) {
        setupBindVariables(bean, queryObject);
        logSql(queryObject);
        bindArgs(ps, queryObject.getBindArguments(), queryObject.getBindVariableValueTypes());
        PreparedStatementUtil.addBatch(ps);
    }
}