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

import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.handler.ArgumentDtoProcedureHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;
import java.sql.ResultSet;

public class ArgumentDtoProcedureCommand extends AbstractSqlCommand implements SqlCommand,
        InjectDaoClassSupport {

    protected ResultSetHandler resultSetHandler;

    protected ResultSetFactory resultSetFactory;

    protected ProcedureMetaData procedureMetaData;

    protected Class daoClass;

    /**
     * インスタンスを構築します。
     * 
     * @param resultSetHandler　{@link ResultSet}のハンドラ
     * @param resultSetFactory　{@link ResultSet}のファクトリ
     * @param procedureMetaData　プロシージャのメタ情報
     */
    public ArgumentDtoProcedureCommand(final DataSource dataSource,
            final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData) {
        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
        this.resultSetFactory = resultSetFactory;
        this.procedureMetaData = procedureMetaData;
    }

    @Override
    protected Object doExecute(final Object[] args) throws Exception {
        final ArgumentDtoProcedureHandler handler = new ArgumentDtoProcedureHandler(
                dataSource, createSql(procedureMetaData), resultSetHandler,
                statementFactory, resultSetFactory, procedureMetaData);
        if (daoClass != null) {
            handler.setLoggerClass(daoClass);
        }
        handler.setFetchSize(-1);
        QueryObject queryObject = new QueryObject();
        queryObject.setSql(createSql(procedureMetaData));
        queryObject.setMethodArguments(args);
        queryObject.setDaoClass(daoClass);
        return handler.execute(queryObject);
    }

    public void setDaoClass(Class clazz) {
        daoClass = clazz;
    }

    /**
     * SQLを作成します。
     * 
     * @param procedureMetaData プロシージャのメタ情報
     * @return SQL
     */
    protected String createSql(final ProcedureMetaData procedureMetaData) {
        final StringBuilder buf = new StringBuilder();
        buf.append("{");
        int size = procedureMetaData.getParameterTypeSize();
        if (procedureMetaData.hasReturnParameterType()) {
            buf.append("? = ");
            size--;
        }
        buf.append("call ");
        buf.append(procedureMetaData.getProcedureName());
        buf.append(" (");
        for (int i = 0; i < size; i++) {
            buf.append("?, ");
        }
        if (size > 0) {
            buf.setLength(buf.length() - 2);
        }
        buf.append(")}");
        return buf.toString();
    }

}