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
package jp.fieldnotes.hatunatu.dao.dataset.impl;

import jp.fieldnotes.hatunatu.dao.dataset.DataRow;
import jp.fieldnotes.hatunatu.dao.dataset.DataTable;
import jp.fieldnotes.hatunatu.dao.dataset.RowState;
import jp.fieldnotes.hatunatu.dao.dataset.TableWriter;
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * SQL用の {@link TableWriter}です。
 */
public class SqlTableWriter implements TableWriter {

    private DataSource dataSource;

    /**
     * {@link SqlTableWriter}を作成します。
     *
     * @param dataSource データソース
     */
    public SqlTableWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * データソースを返します。
     *
     * @return データソース
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void write(DataTable table) throws Exception {
        if (!table.hasMetaData()) {
            setupMetaData(table);
        }
        doWrite(table);
    }

    /**
     * データを書き込みます。
     *
     * @param table テーブル
     */
    protected void doWrite(DataTable table) throws Exception {
        for (int i = 0; i < table.getRowSize(); ++i) {
            DataRow row = table.getRow(i);
            RowState state = row.getState();
            state.update(dataSource, row);
        }
    }

    private void setupMetaData(DataTable table) {
        try (Connection con = dataSource.getConnection()) {
            table.setupMetaData(con.getMetaData());
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}