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
package jp.fieldnotes.hatunatu.dao.resultset;

import jp.fieldnotes.hatunatu.api.DtoMetaData;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.FetchHandler;
import jp.fieldnotes.hatunatu.dao.RowCreator;
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class FetchDtoMetaDataResultSetHandler extends
        AbstractDtoMetaDataResultSetHandler {

    @SuppressWarnings("unchecked")
    protected FetchHandler fetchHandler;

    public FetchDtoMetaDataResultSetHandler(DtoMetaData dtoMetaData,
                                            RowCreator rowCreator, FetchHandler<?> fetchHandler) {
        super(dtoMetaData, rowCreator);
        this.fetchHandler = fetchHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.dao.ResultSetHandler#handle(java.sql.ResultSet)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object handle(ResultSet rs, QueryObject queryObject) throws SQLException {
        // Map<String(columnName), PropertyType>
        Map<String, PropertyType> propertyCache = null;

        final Set<String> columnNames = createColumnNames(rs.getMetaData());
        int count = 0;
        while (rs.next()) {
            // Lazy initialization because if the result is zero, the cache is
            // unused.
            if (propertyCache == null) {
                propertyCache = createPropertyCache(columnNames);
            }
            final Object row = createRow(rs, propertyCache);
            count++;
            if (!fetchHandler.execute(row)) {
                break;
            }
        }
        return Integer.valueOf(count);
    }

}