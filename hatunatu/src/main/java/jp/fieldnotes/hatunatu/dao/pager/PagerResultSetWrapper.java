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
import jp.fieldnotes.hatunatu.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.dao.impl.ResultSetWrapper;
import jp.fieldnotes.hatunatu.util.log.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

class PagerResultSetWrapper extends ResultSetWrapper {

    /** ログ */
    private static final Logger LOGGER = Logger
            .getLogger(PagerResultSetWrapper.class);

    /** カウント */
    private int counter = 0;

    /** オリジナルのResultSet */
    private ResultSet original;

    /** 検索条件オブジェクト */
    private PagerCondition condition;

    /** absoluteメソッドを使用するかどうかのフラグ */
    private boolean useAbsolute = true;

    public void setUseAbsolute(boolean useAbsolute) {
        this.useAbsolute = useAbsolute;
    }

    /**
     * コンストラクタ
     * 
     * @param original
     *            オリジナルのResultSet
     * @param condition
     *            検索条件オブジェクト
     * @param useAbsolute
     * @throws SQLException
     */
    public PagerResultSetWrapper(ResultSet original, PagerCondition condition,
            boolean useAbsolute) {
        super(original);
        this.original = original;
        this.condition = condition;
        this.useAbsolute = useAbsolute;
        moveOffset();
    }

    /**
     * 開始位置までカーソルを進めます。
     * 
     * @throws SQLException
     */
    private void moveOffset() {
        if (isUseCursor()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("S2Pager use scroll cursor.");
            }
            try {
                if (0 == condition.getOffset()) {
                    original.beforeFirst();
                } else {
                    original.absolute(condition.getOffset());
                }
                counter = original.getRow();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("S2Pager not use scroll cursor.");
            }
            try {
                while (original.getRow() < condition.getOffset()
                        && original.next()) {
                    counter++;
                }
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }
    }

    public boolean next() throws SQLException {
        boolean next = super.next();
        if ((condition.getLimit() == PagerCondition.NONE_LIMIT || counter < condition
                .getOffset()
                + condition.getLimit())
                && next) {
            counter += 1;
            return true;
        } else {
            if (isUseCursor()) {
                original.last();
                int count = original.getRow();
                condition.setCount(count);
            } else {
                if (next) {
                    counter++; // 調整
                    while (original.next()) {
                        counter++;
                    }
                }
                condition.setCount(counter);
            }
            return false;
        }
    }

    private boolean isUseCursor() {
        return useAbsolute && ResultSetUtil.isCursorSupport(original);
    }

}
