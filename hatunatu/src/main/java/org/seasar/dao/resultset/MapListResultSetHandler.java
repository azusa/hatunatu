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
package org.seasar.dao.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dao.ResultSetHandler;
import org.seasar.dao.PropertyType;

public class MapListResultSetHandler extends AbstractMapResultSetHandler {

    public MapListResultSetHandler() {
    }

    /**
     * @see ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet resultSet) throws SQLException {
        PropertyType[] propertyTypes = createPropertyTypes(resultSet
                .getMetaData());
        List list = new ArrayList();
        while (resultSet.next()) {
            list.add(createRow(resultSet, propertyTypes));
        }
        return list;
    }
}