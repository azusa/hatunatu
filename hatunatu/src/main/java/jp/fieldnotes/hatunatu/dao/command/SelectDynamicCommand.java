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
import jp.fieldnotes.hatunatu.dao.ResultSetFactory;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.BasicSelectHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.pager.PagingSqlRewriter;

import javax.sql.DataSource;

public class SelectDynamicCommand extends AbstractDynamicCommand {

    private ResultSetHandler resultSetHandler;

    private ResultSetFactory resultSetFactory;

    private PagingSqlRewriter pagingSqlRewriter;

    public SelectDynamicCommand(DataSource dataSource,
            StatementFactory statementFactory,
            ResultSetHandler resultSetHandler,
            ResultSetFactory resultSetFactory,
            PagingSqlRewriter pagingSqlRewriter) {

        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
        this.resultSetFactory = resultSetFactory;
        this.pagingSqlRewriter = pagingSqlRewriter;
    }

    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    @Override
    protected Object doExecute(Object[] args) throws Exception {
        CommandContext ctx = apply(args);
        Object[] bindVariables = ctx.getBindVariables();
        Class[] bindVariableTypes = ctx.getBindVariableTypes();
        String sql = ctx.getSql();
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(sql);
        queryObject.setBindArguments(bindVariables);
        queryObject.setBindTypes(bindVariableTypes);
        queryObject.setMethodArguments(args);
        queryObject.setDaoClass(daoClass);

        pagingSqlRewriter.rewrite(queryObject);
        BasicSelectHandler selectHandler = new BasicSelectHandler(
                getDataSource(), resultSetHandler,
                getStatementFactory(), resultSetFactory);
        /*
         * Statement#setFetchSizeをサポートしていないDBMSがあるため、
         * S2DaoからはsetFetchSizeを行わないようにする。
         * https://www.seasar.org/issues/browse/DAO-2
         */
        selectHandler.setFetchSize(-1);

        Object ret = selectHandler.execute(queryObject);
        pagingSqlRewriter.setCount(queryObject);

        return ret;
    }
}
