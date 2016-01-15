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
package jp.fieldnotes.hatunatu.dao.pager;

import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import java.sql.SQLException;

public interface PagingSqlRewriter {

    /**
     * 指定されたSQL文を書き換え、 ページング処理が含まれたSQLを返します。
     * 
     */
    void rewrite(QueryObject queryObject) throws Exception;

    /**
     * 元のSQLによる結果総件数を設定します
     * 
     * @param baseSQL
     *            元のSQL
     * @param args
     *            メソッド引数
     * @param bindVariables
     *            対象のSQLにバインドされる予定の値
     * @param bindVariableTypes
     *            対象のSQLにバインドされる予定の値の型
     * @throws SQLException
     *             SQLExceptionが発生した場合
     */
    void setCount(QueryObject queryObject) throws Exception;


}
