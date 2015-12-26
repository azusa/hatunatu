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

/**
 * @author jundu
 * 
 */
public class LimitOffsetPagingSqlRewriter extends AbstractPagingSqlRewriter {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.dao.pager.AbstractSqlRewriteStatementFactory#makeCountSql(
     * java.lang.String)
     */
    protected String makeCountSql(String baseSQL) {
        StringBuilder sqlBuf = new StringBuilder("SELECT count(*) FROM (");
        if (isChopOrderBy()) {
            sqlBuf.append(chopOrderBy(baseSQL));
        } else {
            sqlBuf.append(baseSQL);
        }
        sqlBuf.append(") AS total");
        return sqlBuf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.dao.pager.AbstractSqlRewriteStatementFactory#makeLimitOffsetSql
     * (java.lang.String, int, int)
     */
    protected String makeLimitOffsetSql(String baseSQL, int limit, int offset) {
        StringBuilder sqlBuf = new StringBuilder(baseSQL);
        sqlBuf.append(" LIMIT ");
        sqlBuf.append(limit);
        sqlBuf.append(" OFFSET ");
        sqlBuf.append(offset);
        return sqlBuf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.seasar.dao.pager.AbstractPagingSqlRewriter#
     * isOriginalArgsRequiredForCounting()
     */
    protected boolean isOriginalArgsRequiredForCounting() {
        return true;
    }

}
