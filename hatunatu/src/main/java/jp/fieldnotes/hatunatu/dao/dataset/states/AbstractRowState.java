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
package jp.fieldnotes.hatunatu.dao.dataset.states;

import jp.fieldnotes.hatunatu.dao.StatementFactory;
import jp.fieldnotes.hatunatu.dao.UpdateHandler;
import jp.fieldnotes.hatunatu.dao.dataset.DataRow;
import jp.fieldnotes.hatunatu.dao.dataset.RowState;
import jp.fieldnotes.hatunatu.dao.handler.BasicUpdateHandler;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import javax.sql.DataSource;

/**
 * {@link RowState}の抽象クラスです。
 */
public abstract class AbstractRowState implements RowState {

    AbstractRowState() {
    }

    public void update(DataSource dataSource, DataRow row) throws Exception {
        SqlContext ctx = getSqlContext(row);
        UpdateHandler handler = new BasicUpdateHandler(dataSource, StatementFactory.INSTANCE);
        QueryObject query = new QueryObject();
        query.setSql(ctx.getSql());
        query.setBindArguments(ctx.getArgs());
        query.setBindTypes(ctx.getArgTypes());
        execute(handler, query);
    }

    /**
     * SQLコンテキストを返します。
     *
     * @param row 行
     * @return SQLコンテキスト
     */
    protected abstract SqlContext getSqlContext(DataRow row);

    /**
     * 更新します。
     *
     * @param handler  更新ハンドラ
     * @param args     引数
     * @param argTypes 引数の型
     */
    protected void execute(UpdateHandler handler, QueryObject query) throws Exception {
        handler.execute(query);
    }
}