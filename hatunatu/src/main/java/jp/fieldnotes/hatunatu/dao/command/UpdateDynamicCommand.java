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

import jp.fieldnotes.hatunatu.dao.CommandContext;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.BasicUpdateHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;

/*
 * INSERT, UPDATE, DELETE文用
 */
public class UpdateDynamicCommand extends AbstractDynamicCommand {

    public UpdateDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    @Override
    protected Object doExecute(Object[] args) throws Exception {
        CommandContext ctx = apply(args);
        BasicUpdateHandler updateHandler = new BasicUpdateHandler(
                getDataSource(), getStatementFactory());
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(ctx.getSql());
        queryObject.setBindArguments(ctx.getBindVariables());
        queryObject.setBindTypes(ctx.getBindVariableTypes());
        queryObject.setMethodArguments(args);
        return new Integer(updateHandler.execute(queryObject));
    }

}