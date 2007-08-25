/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * @author jflute
 */
public interface RowCreator {

    /**
     * @param rs Result set. (NotNull)
     * @param columnPropertyTypeMap The map of row property cache. The key is String(columnName) and the value is PropertyType. (NotNull)
     * @param beanClass Bean class. (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    Object createRow(ResultSet rs, Map columnPropertyTypeMap,
            Class beanClass) throws SQLException;

    /**
     * @param columnNames The set of column name. (NotNull)
     * @param beanMetaData Bean meta data. (NotNull)
     * @return The map of row property cache. The key is String(columnName) and the value is PropertyType. (NotNull)
     * @throws SQLException
     */
    Map createRowPropertyCache(Set columnNames, BeanMetaData beanMetaData)
            throws SQLException;
}
