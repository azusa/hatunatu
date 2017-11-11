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

import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.api.pager.PagerCondition;
import jp.fieldnotes.hatunatu.dao.CommandContext;
import jp.fieldnotes.hatunatu.dao.ResultSetFactory;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.dbms.DbmsManager;
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.dao.handler.BasicSelectHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.pager.PagingSqlRewriter;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.util.convert.IntegerConversionUtil;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SelectDynamicCommand extends AbstractDynamicCommand {

    private ResultSetHandler resultSetHandler;

    private ResultSetFactory resultSetFactory;

    private PagingSqlRewriter pagingSqlRewriter;

    public SelectDynamicCommand(DataSource dataSource,
                                StatementFactory statementFactory,
                                ResultSetHandler resultSetHandler,
                                ResultSetFactory resultSetFactory) {

        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
        this.resultSetFactory = resultSetFactory;
        this.pagingSqlRewriter = pagingSqlRewriter;
        try {
            this.pagingSqlRewriter = DbmsManager.getDbms(this.dataSource);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
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
        if (PagerContext.isPagerCondition(queryObject.getMethodArguments())) {
            PagerCondition condition = PagerContext.getPagerCondition(queryObject.getMethodArguments());
            QueryObject pagingQuery = pagingSqlRewriter.getCountSql(queryObject);

            BasicSelectHandler handler = new BasicSelectHandler(dataSource,
                    new ObjectResultSetHandler(), statementFactory,
                    this.resultSetFactory);
            // [DAO-139]
            handler.setFetchSize(-1);
            Object count = handler.execute(pagingQuery);
            if (ret != null) {
                condition.setCount(IntegerConversionUtil.toPrimitiveInt(count));
            } else {
                throw new SQLException("[S2Pager]Result not found.");
            }
        }
        return ret;
    }

    private static class ObjectResultSetHandler implements ResultSetHandler {

        /**
         * Construcor.
         */
        public ObjectResultSetHandler() {
        }

        @Override
        public Object handle(ResultSet rs, QueryObject queryObject) throws SQLException {
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                ValueType valueType = ValueTypes
                        .getValueType(rsmd.getColumnType(1));
                return valueType.getValue(rs, 1);
            }
            return null;
        }
    }
}
