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

import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.impl.Identifier;

public interface BeanAnnotationReader {

    String getColumnAnnotation(PropertyDesc pd);

    /**
     * Get the value of {@link jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean}
     * @return the null value when Bean does not have {@link jp.fieldnotes.hatunatu.dao.annotation.tiger.Bean} annotation.
     */
    String getTableAnnotation();

    String getVersionNoPropertyName();

    String getTimestampPropertyName();

    Identifier getId(PropertyDesc pd, Dbms dbms);

    String[] getNoPersisteneProps();

    boolean hasRelationNo(PropertyDesc pd);

    int getRelationNo(PropertyDesc pd);

    String getRelationKey(PropertyDesc pd);

    String getValueType(PropertyDesc pd);

}
