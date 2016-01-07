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
package jp.fieldnotes.hatunatu.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationRowCache {

    private List rowMapList;

    public RelationRowCache(int size) {
        rowMapList = new ArrayList();
        for (int i = 0; i < size; ++i) {
            rowMapList.add(new HashMap());
        }
    }

    public Object getRelationRow(int relno, RelationKey key) {
        return getRowMap(relno).get(key);
    }

    public void addRelationRow(int relno, RelationKey key, Object row) {
        getRowMap(relno).put(key, row);
    }

    protected Map getRowMap(int relno) {
        return (Map) rowMapList.get(relno);
    }
}
