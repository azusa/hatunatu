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
package jp.fieldnotes.hatunatu.dao.util;

import jp.fieldnotes.hatunatu.util.exception.SQLRuntimeException;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 * {@link DataSource}用のユーティリティクラスです。
 * 
 * @author higa
 * 
 */
public class DataSourceUtil {

    /**
     * インスタンスを構築します。
     */
    protected DataSourceUtil() {
    }

    /**
     * コネクションを返します。
     * 
     * @param dataSource
     *            データソース
     * @return コネクション
     * @throws SQLRuntimeException
     *             SQL例外が発生した場合
     */
    public static Connection getConnection(DataSource dataSource)
            throws SQLRuntimeException {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }
}
