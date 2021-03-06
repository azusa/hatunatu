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
package jp.fieldnotes.hatunatu.dao.dataset.states;


import jp.fieldnotes.hatunatu.dao.dataset.RowState;

/**
 * {@link RowState}を管理するインターフェースです。
 */
public interface RowStates {

    /**
     * 変更がないとき用の {@link RowState}です。
     *
     * @see UnchangedState
     */
    RowState UNCHANGED = new UnchangedState();

    /**
     * 新規作成用の {@link RowState}です。
     *
     * @see CreatedState
     */
    RowState CREATED = new CreatedState();

    /**
     * 更新用の {@link RowState}です。
     *
     * @see ModifiedState
     */
    RowState MODIFIED = new ModifiedState();

    /**
     * 削除用の {@link RowState}です。
     *
     * @see RemovedState
     */
    RowState REMOVED = new RemovedState();
}
