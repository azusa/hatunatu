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

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.fieldnotes.hatunatu.api.DtoMetaData;
import jp.fieldnotes.hatunatu.dao.RowCreator;

/**
 * @author jflute
 */
public class DtoArrayMetaDataResultSetHandler extends
        DtoListMetaDataResultSetHandler {

    /**
     * @param dtoMetaData Dto meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     */
    public DtoArrayMetaDataResultSetHandler(DtoMetaData dtoMetaData,
            RowCreator rowCreator) {
        super(dtoMetaData, rowCreator);
    }

    /**
     * @see ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {
        List list = (List) super.handle(rs);
        return list.toArray((Object[]) Array.newInstance(getDtoMetaData()
                .getBeanClass(), list.size()));
    }
}