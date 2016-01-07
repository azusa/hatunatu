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
package jp.fieldnotes.hatunatu.dao.dbms;

import jp.fieldnotes.hatunatu.dao.Dbms;

public class DB2 extends Standard {

    /**
     * @see Dbms#getSuffix()
     */
    public String getSuffix() {
        return "_db2";
    }

    public String getIdentitySelectString() {
        return "values IDENTITY_VAL_LOCAL()";
    }

    public String getSequenceNextValString(String sequenceName) {
        return "values nextval for " + sequenceName;
    }
}
