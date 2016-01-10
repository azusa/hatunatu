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
import jp.fieldnotes.hatunatu.dao.InjectDaoClassSupport;
import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.handler.BasicHandler;

import javax.sql.DataSource;

public abstract class AbstractSqlCommand implements SqlCommand,
        InjectDaoClassSupport {

    private DataSource dataSource;

    private StatementFactory statementFactory;

    private String sql;

    private Class notSingleRowUpdatedExceptionClass;

    private Class daoClass;

    public AbstractSqlCommand(DataSource dataSource,
            StatementFactory statementFactory) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class getNotSingleRowUpdatedExceptionClass() {
        return notSingleRowUpdatedExceptionClass;
    }

    public void setNotSingleRowUpdatedExceptionClass(
            Class notSingleRowUpdatedExceptionClass) {
        this.notSingleRowUpdatedExceptionClass = notSingleRowUpdatedExceptionClass;
    }

    public void setDaoClass(Class daoClass) {
        this.daoClass = daoClass;
    }

    protected void injectDaoClass(BasicHandler handler) {
        if (daoClass != null) {
            handler.setLoggerClass(daoClass);
        }
    }
}