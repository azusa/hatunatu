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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.exception.NotSingleResultRuntimeException;
import jp.fieldnotes.hatunatu.dao.parser.SqlTokenizerImpl;
import jp.fieldnotes.hatunatu.dao.resultset.AbstractMapResultSetHandler;
import jp.fieldnotes.hatunatu.util.log.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapResultSetHandler extends AbstractMapResultSetHandler {

    private static final Logger logger = Logger
            .getLogger(MapResultSetHandler.class);

    public MapResultSetHandler() {
    }

    /**
     * @see ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            PropertyType[] propertyTypes = createPropertyTypes(resultSet
                    .getMetaData());
            Object row = createRow(resultSet, propertyTypes);
            if (resultSet.next()){
                handleNotSingleResult();
            }
            return row;
        }
        return null;
    }

    /**
     * 結果が1件でない場合の処理を行います。 デフォルトでは警告ログを出力します。
     */
    protected void handleNotSingleResult() {
        logger.log("WDAO0003", null);
    }

    /**
     * 結果が2件以上のときに例外をスローする{@link SqlTokenizerImpl.ObjectResultSetHandler}です。
     * 
     * @author azusa
     * 
     */
    public static class RestrictMapResultSetHandler extends MapResultSetHandler {

        public RestrictMapResultSetHandler() {
            super();
        }

        protected void handleNotSingleResult() {
            throw new NotSingleResultRuntimeException();
        }

    }
}