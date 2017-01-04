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

import jp.fieldnotes.hatunatu.api.pager.PagerCondition;
import jp.fieldnotes.hatunatu.dao.ResultSetFactory;
import jp.fieldnotes.hatunatu.dao.impl.BasicResultSetFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PagerResultSetFactoryWrapper implements ResultSetFactory {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private ResultSetFactory resultSetFactory;

    private boolean useScrollCursor = true;

    /**
     * Constructor.
     *
     * @param resultSetFactory original {@link ResultSetFactory},
     *
     */
    public PagerResultSetFactoryWrapper(ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
    }

    public PagerResultSetFactoryWrapper() {
        this.resultSetFactory = BasicResultSetFactory.INSTANCE;
    }

    public boolean isUseScrollCursor() {
        return useScrollCursor;
    }

    /**
     * @param useScrollCursor
     */
    public void setUseScrollCursor(boolean useScrollCursor) {
        this.useScrollCursor = useScrollCursor;
    }

    @Override
    public ResultSet getResultSet(Statement statement) {
        ResultSet resultSet = resultSetFactory.getResultSet(statement);
        return wrapResultSet(resultSet, EMPTY_ARGS);
    }


    /**
     * {@inheritDoc}
     *
     * Wraps {@link ResultSet} by {@link PagerResultSetWrapper} if arguments contain {@link PagerCondition}.
     *
     * @param ps Prepared Statement.
     * @param methodArgument Argument of DAO.
     * @return
     */
    @Override
    public ResultSet createResultSet(PreparedStatement ps, Object[] methodArgument) {
        ResultSet resultSet = resultSetFactory.createResultSet(ps, methodArgument);
        return wrapResultSet(resultSet, methodArgument);
    }

    protected ResultSet wrapResultSet(ResultSet resultSet, Object[] methodArgument) {
        if (PagerContext.isPagerCondition(methodArgument)) {
            PagerCondition condition = PagerContext.getPagerCondition(methodArgument);
            return new PagerResultSetWrapper(resultSet, condition,
                    useScrollCursor);
        } else {
            return resultSet;
        }
    }

}
