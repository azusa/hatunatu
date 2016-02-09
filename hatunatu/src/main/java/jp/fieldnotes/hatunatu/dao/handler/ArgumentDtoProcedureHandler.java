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

import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.dao.*;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.util.exception.SIllegalArgumentException;
import jp.fieldnotes.hatunatu.util.sql.StatementUtil;

import javax.sql.DataSource;
import java.sql.*;

public class ArgumentDtoProcedureHandler extends BasicSelectHandler implements
        ProcedureHandler {

    private ProcedureMetaData procedureMetaData;

    /**
     * プロシージャのメタ情報を返します。
     * 
     * @return プロシージャのメタ情報
     */
    public ProcedureMetaData getProcedureMetaData() {
        return procedureMetaData;
    }

    /**
     * プロシージャのメタ情報を設定します。
     * 
     * @param procedureMetaData プロシージャのメタ情報
     */
    public void setProcedureMetaData(final ProcedureMetaData procedureMetaData) {
        this.procedureMetaData = procedureMetaData;
    }

    /**
     * インスタンスを構築します。
     * 
     * @param dataSource データソース
     * @param resultSetHandler　{@link ResultSet}のハンドラ
     * @param statementFactory　{@link Statement}のファクトリ
     * @param resultSetFactory　{@link ResultSet}のファクトリ
     * @param procedureMetaData　プロシージャのメタ情報
     */
    public ArgumentDtoProcedureHandler(final DataSource dataSource,
                                       final ResultSetHandler resultSetHandler,
                                       final StatementFactory statementFactory,
                                       final ResultSetFactory resultSetFactory,
                                       final ProcedureMetaData procedureMetaData) {

        super(dataSource, resultSetHandler, statementFactory,
                resultSetFactory);
        setProcedureMetaData(procedureMetaData);
    }

    @Override
    public Object execute(final Connection connection, QueryObject queryObject) throws SQLException {
        final Object dto = getArgumentDto(queryObject.getMethodArguments());
        logSql(queryObject);
        try (CallableStatement cs = prepareCallableStatement(connection, queryObject)) {
            bindArgs(cs, dto);
            if (cs.execute()) {
                return handleResultSet(cs, queryObject);
            } else {
                return handleOutParameters(cs, dto);
            }
        }
    }

    @Override
    protected String getCompleteSql(final QueryObject queryObject) {
        String sql = queryObject.getSql();
        Object dto = getArgumentDto(queryObject.getMethodArguments());
        if (queryObject.getMethodArguments() == null || dto == null) {
            return sql;
        }
        StringBuilder buf = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            if ((pos2 = sql.indexOf('?', pos)) < 0) {
                break;
            }
            buf.append(sql.substring(pos, pos2));
            pos = pos2 + 1;
            if (ppt.isInType()) {
                buf.append(getBindVariableText(ppt.getValue(dto)));
            } else {
                buf.append(sql.substring(pos2, pos));
            }
        }
        buf.append(sql.substring(pos));
        return buf.toString();
    }

    protected CallableStatement prepareCallableStatement(
            final Connection connection, QueryObject queryObject) {
        if (queryObject.getSql() == null) {
            throw new EmptyRuntimeException("sql");
        }
        final CallableStatement cs = getStatementFactory()
                .createCallableStatement(connection, queryObject.getSql());
        if (getFetchSize() > -1) {
            StatementUtil.setFetchSize(cs, getFetchSize());
        }
        if (getMaxRows() > -1) {
            StatementUtil.setMaxRows(cs, getMaxRows());
        }
        return cs;
    }

    /**
     * 引数をバインドします。
     * 
     * @param cs　ストアドプロシージャを表す文
     * @param dto 引数のDTO
     * @throws SQLException SQL例外が発生した場合
     */
    protected void bindArgs(final CallableStatement cs, final Object dto)
            throws SQLException {
        if (dto == null) {
            return;
        }
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData
                    .getParameterType(i);
            final ValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                valueType.registerOutParameter(cs, i + 1);
            }
            if (ppt.isInType()) {
                final Object value = ppt.getValue(dto);
                valueType.bindValue(cs, i + 1, value);
            }
        }
    }

    /**
     * <code>ResultSet</code>を処理します。
     * 
     * @param cs ストアドプロシージャを表す文
     * @return <code>ResultSet</code>から変換された値
     * @throws SQLException SQL例外が発生した場合
     */
    protected Object handleResultSet(final CallableStatement cs, QueryObject queryObject)
            throws SQLException {
        try (ResultSet rs = getResultSetFactory().getResultSet(cs)) {
            return getResultSetHandler().handle(rs, queryObject);
        }
    }

    /**
     * <code>OUT</code>パラメータを処理します。
     * 
     * @param cs　ストアドプロシージャを表す文
     * @param dto 引数のDTO
     * @return 引数のDTO
     * @throws SQLException
     */
    protected Object handleOutParameters(final CallableStatement cs,
            final Object dto) throws SQLException {
        if (dto == null) {
            return null;
        }
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData
                    .getParameterType(i);
            final ValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                final Object value = valueType.getValue(cs, i + 1);
                ppt.setValue(dto, value);
            }
        }
        return dto;
    }

    /**
     * 引数のDTOを返します。
     * 
     * @param args 引数のDTO
     * @return 引数のDTO
     */
    protected Object getArgumentDto(Object[] args) {
        if (args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            if (args[0] == null) {
                throw new SIllegalArgumentException("", "EDAO0029", new Object[] {});
            }
            return args[0];
        }
        throw new IllegalArgumentException("args");
    }

}
