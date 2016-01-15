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
package jp.fieldnotes.hatunatu.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Factory of {@link ResultSet}
 */
public interface ResultSetFactory {

    /**
     * Create {@link ResultSet}。
     *
     * @param statement SQL statement.
     * @return ResultSet
     */
    ResultSet getResultSet(Statement statement);

    /**
     * Create {@link ResultSet}
     * @param ps Prepared Statement.
     * @param methodArgument Argument of DAO.
     * @return ResultSet
     */
    ResultSet createResultSet(PreparedStatement ps, Object[] methodArgument);

}
