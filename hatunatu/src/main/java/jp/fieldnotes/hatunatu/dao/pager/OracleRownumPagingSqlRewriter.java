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

public class OracleRownumPagingSqlRewriter extends AbstractPagingSqlRewriter {

    @Override
    protected String makeCountSql(String baseSQL) {
        StringBuilder sqlBuf = new StringBuilder("SELECT count(*) FROM (");
        if (isChopOrderBy()) {
            sqlBuf.append(chopOrderBy(baseSQL));
        } else {
            sqlBuf.append(baseSQL);
        }
        sqlBuf.append(")");
        return sqlBuf.toString();
    }

    @Override
    protected String makeLimitOffsetSql(String baseSQL, int limit, int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException(
                    "The offset must be greater than or equal to zero.("
                            + offset + ")");
        }
        StringBuilder sqlBuf = new StringBuilder(baseSQL);
        sqlBuf
                .insert(0,
                        "SELECT * FROM (SELECT S2DAO_ORIGINAL_DATA.*, ROWNUM AS S2DAO_ROWNUMBER FROM (");
        sqlBuf.append(") S2DAO_ORIGINAL_DATA) WHERE S2DAO_ROWNUMBER BETWEEN ");
        sqlBuf.append(offset + 1);
        sqlBuf.append(" AND ");
        sqlBuf.append(offset + limit);
        sqlBuf.append(" AND ROWNUM <= ");
        sqlBuf.append(limit);
        sqlBuf.append(" ORDER BY S2DAO_ROWNUMBER");
        return sqlBuf.toString();
    }

}
