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
package jp.fieldnotes.hatunatu.dao.id;

import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.Dbms;

import javax.sql.DataSource;

public class IdentityIdentifierGenerator extends AbstractIdentifierGenerator {

    /**
     * @param propertyType
     * @param dbms
     */
    public IdentityIdentifierGenerator(PropertyType propertyType, Dbms dbms) {
        super(propertyType, dbms);
    }

    /**
     * @see IdentifierGenerator#setIdentifier(java.lang.Object,
     *      javax.sql.DataSource)
     */
    public void setIdentifier(Object bean, DataSource ds) {
        Object value = executeSql(ds, getDbms().getIdentitySelectString(), null);
        setIdentifier(bean, value);
    }

    /**
     * @see IdentifierGenerator#isSelfGenerate()
     */
    public boolean isSelfGenerate() {
        return false;
    }

}
