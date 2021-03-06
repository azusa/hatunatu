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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.dao.types.ValueTypes;
import jp.fieldnotes.hatunatu.util.lang.StringUtil;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class PropertyTypeUtil {

    /** カラム名に含まれるアンダースコアを維持してプロパティ名とする場合は<code>true</code>です。 */
    protected static boolean preserveUnderscore = false;

    /**
     * インスタンスを構築します。
     */
    protected PropertyTypeUtil() {
    }

    /**
     * カラム名に含まれるアンダースコアを維持してプロパティ名とする場合は<code>true</code>を設定します。
     * 
     * @param preserve
     *            カラム名に含まれるアンダースコアを維持してプロパティ名とする場合は<code>true</code>
     */
    public static void setPreserveUnderscore(final boolean preserve) {
        preserveUnderscore = preserve;
    }

    /**
     * {@link PropertyType}の配列を作成します。
     * 
     * @param rsmd
     *            結果セットメタデータ
     * @return {@link PropertyType}の配列
     * @throws SQLException
     *             SQL例外が発生した場合
     */
    public static PropertyType[] createPropertyTypes(ResultSetMetaData rsmd)
            throws SQLException {

        int count = rsmd.getColumnCount();
        PropertyType[] propertyTypes = new PropertyType[count];
        for (int i = 0; i < count; ++i) {
            String columnName = rsmd.getColumnLabel(i + 1);
            String propertyName = preserveUnderscore ? columnName : StringUtil
                    .replace(columnName, "_", "");
            ValueType valueType = ValueTypes.getValueType(rsmd
                    .getColumnType(i + 1));
            propertyTypes[i] = new PropertyTypeImpl(propertyName, valueType,
                    columnName);
        }
        return propertyTypes;
    }
}