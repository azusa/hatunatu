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
import jp.fieldnotes.hatunatu.dao.jdbc.QueryObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPagingSqlRewriter implements PagingSqlRewriter {

    private static final Pattern patternOrderBy = Pattern
            .compile(
                    "order\\s+by\\s+([\\w\\p{L}.`\\[\\]]+(\\s+(asc|desc))?\\s*)(,\\s*[\\w\\p{L}.`\\[\\]]+(\\s+(asc|desc))?\\s*)*$",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    /*
     * 全件数取得時のSQLからorder by句を除去するかどうかのフラグです。 trueならorder
     * by句を除去します、falseなら除去しません。
     */
    private boolean chopOrderBy = true;

    @Override
    public void rewrite(QueryObject queryObject) {
        final Object[] pagingArgs = queryObject.getMethodArguments();
        if (PagerContext.isPagerCondition(pagingArgs)) {
            PagerCondition dto = PagerContext.getPagerCondition(pagingArgs);
            if (dto.getLimit() > 0 && dto.getOffset() > -1) {
                String limitOffsetSql = makeLimitOffsetSql(queryObject.getSql(), dto
                        .getLimit(), dto.getOffset());
                queryObject.setOriginalSql(queryObject.getSql());
                queryObject.setSql(limitOffsetSql);
                return;
            }
        }
    }

    /**
     * 全件数取得時のSQLからorder by句を除去するフラグをセットします
     * 
     * @param chopOrderBy
     *            trueならorder by句を除去します、falseなら除去しません
     */
    public void setChopOrderBy(boolean chopOrderBy) {
        this.chopOrderBy = chopOrderBy;
    }

    /**
     * 全件数取得時のSQLからorder by句を除去するかどうかを返します
     * 
     * @return order by句を除去するならtrue、それ以外ではfalse
     */
    public boolean isChopOrderBy() {
        return this.chopOrderBy;
    }

    @Override
    public QueryObject getCountSql(QueryObject queryObject) throws Exception {
        String countSQL = makeCountSql(queryObject.getOriginalSql());
        QueryObject countQuery = new QueryObject();
        countQuery.setBindArguments(queryObject.getBindArguments());
        countQuery.setBindTypes(queryObject.getBindTypes());
        countQuery.setMethodArguments(queryObject.getMethodArguments());
        countQuery.setSql(countSQL);
        return countQuery;
    }


    /**
     * order by句を除去したSQLを作成します。
     * 
     * @param baseSQL
     *            元のSQL
     * @return order by句が除去されたSQL
     */
    protected String chopOrderBy(String baseSQL) {
        Matcher matcher = patternOrderBy.matcher(baseSQL);
        if (matcher.find()) {
            return matcher.replaceAll("");
        } else {
            return baseSQL;
        }
    }

    /**
     * 指定したオフセットと件数で絞り込む条件を付加したSQLを作成します。
     * 
     * @param baseSQL
     *            変更前のSQL
     * @param limit
     *            取得する件数
     * @param offset
     *            何行目以降を取得するか（offset >= 0)
     * @return 条件を付加したSQL
     */
    protected abstract String makeLimitOffsetSql(String baseSQL, int limit,
            int offset);

    /**
     * count(*)で全件数を取得するSQLを生成します。<br/> パフォーマンス向上のためorder by句を除去したSQLを発行します
     * 
     * @param baseSQL
     *            元のSQL
     * @return count(*)が付加されたSQL
     */
    protected abstract String makeCountSql(String baseSQL);




}
