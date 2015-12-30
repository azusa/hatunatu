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
package jp.fieldnotes.hatunatu.api;



import java.util.Set;

/**
 * Meta data of Bean (related to table)
 */
public interface BeanMetaData extends DtoMetaData {

    public String getTableName();

    public PropertyType getVersionNoPropertyType();

    public String getVersionNoPropertyName();

    public boolean hasVersionNoPropertyType();

    public PropertyType getTimestampPropertyType();

    public String getTimestampPropertyName();

    public boolean hasTimestampPropertyType();

    public String convertFullColumnName(String alias);

    public PropertyType getPropertyTypeByAliasName(String aliasName);

    public PropertyType getPropertyTypeByColumnName(String columnName);

    public boolean hasPropertyTypeByColumnName(String columnName);

    public boolean hasPropertyTypeByAliasName(String aliasName);

    public int getRelationPropertyTypeSize();

    public RelationPropertyType getRelationPropertyType(int index);

    public RelationPropertyType getRelationPropertyType(String propertyName);

    public int getPrimaryKeySize();

    public String getPrimaryKey(int index);

    public int getIdentifierGeneratorSize();

    public IdentifierGenerator getIdentifierGenerator(int index);

    public IdentifierGenerator getIdentifierGenerator(String propertyName);

    public String getAutoSelectList();

    public Set getModifiedPropertyNames(Object bean);

    public boolean hasRelationToTable();

}
