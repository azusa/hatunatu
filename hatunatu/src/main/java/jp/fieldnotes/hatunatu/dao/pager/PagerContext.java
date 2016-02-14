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

/**
 * ページャの情報をスレッドローカルに保持します。
 * 
 * @author Toshitaka Agata(Nulab,inc.)
 * @author azusa
 */
public class PagerContext {

    private static final Object[] EMPTY_ARGS = new Object[0];

    /**
     * コンストラクタ
     */
    private PagerContext() {
    };

    /**
     * メソッドの引数にPagerConditionが含まれているかどうかを判定します。
     * <p>ただし、PagerConditon#getLimitがPagerConditon#NONE_LIMITの場合はfalseを返します。</p>
     * 
     * @param args
     *            引数
     * @return true/false
     */
    public static boolean isPagerCondition(Object[] args) {
        final PagerCondition condition = getPagerCondition(args);
        if (condition == null) {
            return false;
        }
        if (condition.getLimit() == PagerCondition.NONE_LIMIT
                && condition.getOffset() == 0) {
            return false;
        }
        return true;
    }

    /**
     * メソッドの引数からPagerConditionを取得します。
     * 
     * @param args
     *            引数
     * @return PagerCondition
     */
    public static PagerCondition getPagerCondition(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof PagerCondition) {
                return (PagerCondition) arg;
            }
        }
        return null;
    }

}
