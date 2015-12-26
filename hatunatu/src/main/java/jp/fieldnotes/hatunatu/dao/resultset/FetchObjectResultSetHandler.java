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

import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.dao.FetchHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author jundu
 * 
 */
public class FetchObjectResultSetHandler extends AbstractObjectResultSetHandler {

    @SuppressWarnings("unchecked")
    protected FetchHandler fetchHandler;

    /**
     * @param clazz
     */
    public FetchObjectResultSetHandler(Class<?> clazz,
            FetchHandler<?> fetchHandler) {
        super(clazz);
        this.fetchHandler = fetchHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.dao.ResultSetHandler#handle(java.sql.ResultSet)
     */
    @SuppressWarnings("unchecked")
    public Object handle(ResultSet rs) throws SQLException {
        ValueType valueType = getValueType(rs);
        int count = 0;
        while (rs.next()) {
            count++;
            if (!fetchHandler.execute(valueType.getValue(rs, 1))) {
                break;
            }
        }
        return Integer.valueOf(count);
    }

}
