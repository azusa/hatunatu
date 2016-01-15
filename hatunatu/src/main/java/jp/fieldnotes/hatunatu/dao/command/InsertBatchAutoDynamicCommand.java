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
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.AbstractAutoHandler;
import jp.fieldnotes.hatunatu.dao.handler.InsertBatchAutoHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;
import java.util.List;

public class InsertBatchAutoDynamicCommand extends InsertAutoDynamicCommand {

    public InsertBatchAutoDynamicCommand(DataSource dataSource,
                                         StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    @Override
    public Object doExecute(Object[] args) throws Exception {
        Object[] beans = null;
        if (args[0] instanceof Object[]) {
            beans = (Object[]) args[0];
        } else if (args[0] instanceof List) {
            beans = ((List) args[0]).toArray();
        }
        if (beans == null) {
            throw new IllegalArgumentException("args[0]");
        }

        final BeanMetaData bmd = getBeanMetaData();
        final PropertyType[] propertyTypes = createInsertPropertyTypes(bmd,
                beans[0], getPropertyNames());
        final String sql = createInsertSql(bmd, propertyTypes);

        AbstractAutoHandler handler = new InsertBatchAutoHandler(
                getDataSource(), getStatementFactory(), bmd, propertyTypes);
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setMethodArguments(args);
        queryObject.setDaoClass(daoClass);
        int rows = handler.execute(queryObject);

        return new Integer(rows);
    }

}
