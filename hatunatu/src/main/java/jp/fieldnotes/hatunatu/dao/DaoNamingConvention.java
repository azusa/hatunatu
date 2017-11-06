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
package jp.fieldnotes.hatunatu.dao;

public class DaoNamingConvention {

    public static final DaoNamingConvention INSTASNCE = new DaoNamingConvention();

    private String versionNoPropertyName = "versionNo";

    private String timestampPropertyName = "timestamp";

    private String[] insertPrefixes = new String[] { "insert", "create", "add" };

    private String[] updatePrefixes = new String[] { "update", "modify",
            "store" };

    private String[] deletePrefixes = new String[] { "delete", "remove" };

    private String[] modifiedOnlySuffixes = new String[] { "ModifiedOnly" };

    private String modifiedPropertyNamesPropertyName = "modifiedPropertyNames";

    private DaoNamingConvention(){}


    public String getModifiedPropertyNamesPropertyName() {
        return modifiedPropertyNamesPropertyName;
    }

    public String getTimestampPropertyName() {
        return timestampPropertyName;
    }

    public String getVersionNoPropertyName() {
        return versionNoPropertyName;
    }

    public String[] getDeletePrefixes() {
        return deletePrefixes;
    }

    public String[] getInsertPrefixes() {
        return insertPrefixes;
    }

    public String[] getUpdatePrefixes() {
        return updatePrefixes;
    }

    public String[] getModifiedOnlySuffixes() {
        return modifiedOnlySuffixes;
    }
}
