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

import jp.fieldnotes.hatunatu.dao.ColumnNaming;
import jp.fieldnotes.hatunatu.dao.util.DaoNamingConventionUtil;

/**
 * プロパティ名のキャメルケースの区切りにアンダースコアを挿入したものをカラム名とする{@link ColumnNaming}の実装クラスです。
 * 
 * @author taedium
 */
public class DecamelizeColumnNaming implements ColumnNaming {

    public String fromPropertyNameToColumnName(String propertyName) {
        return DaoNamingConventionUtil
                .fromPropertyNameToColumnName(propertyName);
    }

}