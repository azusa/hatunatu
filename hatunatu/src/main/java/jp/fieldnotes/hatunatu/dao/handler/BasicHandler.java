/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
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
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.exception.EmptyRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.BasicStatementFactory;
import jp.fieldnotes.hatunatu.dao.impl.SqlLogImpl;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.dao.util.BindVariableUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;
import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.log.Logger;
import org.seasar.extension.jdbc.SqlLog;
import org.seasar.extension.jdbc.SqlLogRegistry;
import org.seasar.extension.jdbc.SqlLogRegistryLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * SQL文を実行するための基本的なクラスです。
 * 
 * @author higa
 * 
 */
public abstract class BasicHandler {

    private DataSource dataSource;

    private StatementFactory statementFactory = BasicStatementFactory.INSTANCE;

    /**
     * ログで使われるクラスです。
     */
    protected Class loggerClass = BasicHandler.class;

    /**
     * {@link BasicHandler}を作成します。
     */
    public BasicHandler() {
    }

    /**
     * {@link BasicHandler}を作成します。
     * 
     * @param ds
     *            データソース
     * @param statementFactory
     *            ステートメントファクトリ
     */
    public BasicHandler(DataSource ds,
                        StatementFactory statementFactory) {

        setDataSource(ds);
        setStatementFactory(statementFactory);
    }

    /**
     * データソースを返します。
     * 
     * @return データソース
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * データソースを設定します。
     * 
     * @param dataSource
     *            データソース
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }



    /**
     * ステートメントファクトリを返します。
     * 
     * @return ステートメントファクトリ
     */
    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    /**
     * ステートメントファクトリを設定します。
     * 
     * @param statementFactory
     *            ステートメントファクトリ
     */
    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    /**
     * コネクションを返します。
     * 
     * @return コネクション
     */
    protected Connection getConnection() {
        if (dataSource == null) {
            throw new EmptyRuntimeException("dataSource");
        }
        return DataSourceUtil.getConnection(dataSource);
    }

    /**
     * 準備されたステートメントを返します。
     * 
     * @param connection
     *            コネクション
     * @param queryObject
     * @return 準備されたステートメント
     */
    protected PreparedStatement prepareStatement(Connection connection, QueryObject queryObject) {
        return statementFactory.createPreparedStatement(connection, queryObject);
    }

    /**
     * 引数をバインドします。
     * 
     * @param ps
     *            準備されたステートメント
     * @param args
     *            引数
     * @param argTypes
     *            引数のタイプ
     */
    protected void bindArgs(PreparedStatement ps, Object[] args,
            Class[] argTypes) {

        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            ValueType valueType = getValueType(argTypes[i]);
            try {
                valueType.bindValue(ps, i + 1, args[i]);
            } catch (SQLException ex) {
                throw new SQLRuntimeException(ex);
            }
        }
    }

    /**
     * 引数の型を返します。
     * 
     * @param args
     *            引数
     * @return 引数の型
     */
    protected Class[] getArgTypes(Object[] args) {
        if (args == null) {
            return null;
        }
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            if (arg != null) {
                argTypes[i] = arg.getClass();
            }
        }
        return argTypes;
    }

    /**
     * Returns Complate SQL.
     *
     * @param queryObject
     *            An object of SQL query.
     * @return Complate SQL
     */
    protected String getCompleteSql(QueryObject queryObject) {
        return BindVariableUtil.getCompleteSql(queryObject.getSql(), queryObject.getBindArguments());
    }

    /**
     * バインド変数を文字列として返します。
     * 
     * @param bindVariable
     *            バインド変数
     * @return バインド変数の文字列表現
     */
    protected String getBindVariableText(Object bindVariable) {
        return BindVariableUtil.getBindVariableText(bindVariable);
    }

    /**
     * S2JDBC用の値の型を返します。
     * 
     * @param clazz
     *            クラス
     * @return S2JDBC用の値の型
     */
    protected ValueType getValueType(Class clazz) {
        return ValueTypes.getValueType(clazz);
    }

    /**
     * SQLをログ出力します。
     *
     * @param queryObject
     */
    protected void logSql(QueryObject queryObject) {
        Logger logger = Logger.getLogger(loggerClass);
        SqlLogRegistry sqlLogRegistry = SqlLogRegistryLocator.getInstance();
        if (logger.isDebugEnabled() || sqlLogRegistry != null) {
            String completeSql = getCompleteSql(queryObject);
            if (logger.isDebugEnabled()) {
                logger.debug(completeSql);
            }
            if (sqlLogRegistry != null) {
                SqlLog sqlLog = new SqlLogImpl(queryObject.getSql(), completeSql, queryObject.getBindArguments(),
                        queryObject.getBindTypes());
                sqlLogRegistry.add(sqlLog);
            }
        }
    }

    /**
     * ログ用のクラスを返します。
     * 
     * @return ログ用のクラス
     */
    public Class getLoggerClass() {
        return loggerClass;
    }

    /**
     * ログ用のクラスを設定します。
     * 
     * @param loggerClass
     *            ログ用のクラス
     */
    public void setLoggerClass(Class loggerClass) {
        this.loggerClass = loggerClass;
    }
}