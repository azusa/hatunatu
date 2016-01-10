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
import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import jp.fieldnotes.hatunatu.dao.ResultSetFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PagerResultSetFactoryWrapper implements ResultSetFactory {

    /** オリジナルのResultSetFactory */
    private ResultSetFactory resultSetFactory;

    private boolean useScrollCursor = true;

    /**
     * コンストラクタ
     * 
     * @param resultSetFactory
     *            オリジナルのResultSetFactory
     */
    public PagerResultSetFactoryWrapper(ResultSetFactory resultSetFactory) {
        this.resultSetFactory = resultSetFactory;
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

    public ResultSet getResultSet(Statement statement) {
        ResultSet resultSet = resultSetFactory.getResultSet(statement);
        return wrapResultSet(resultSet);
    }

    /**
     * ResultSetを生成します。
     * <p>
     * PagerContextにPagerConditionがセットされている場合、
     * ResultSetをPagerResultSetWrapperでラップして返します。
     * 
     * @param PreparedStatement
     * @return ResultSet
     */
    public ResultSet createResultSet(PreparedStatement ps) {
        ResultSet resultSet = resultSetFactory.createResultSet(ps);
        return wrapResultSet(resultSet);
    }

    protected ResultSet wrapResultSet(ResultSet resultSet) {
        Object[] args = PagerContext.getContext().peekArgs();
        if (PagerContext.isPagerCondition(args)) {
            PagerCondition condition = PagerContext.getPagerCondition(args);
            return new PagerResultSetWrapper(resultSet, condition,
                    useScrollCursor);
        } else {
            return resultSet;
        }
    }

}
