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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.api.IdentifierGenerator;
import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.RelationPropertyType;
import jp.fieldnotes.hatunatu.dao.NullBean;
import jp.fieldnotes.hatunatu.dao.exception.BeanNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.dao.exception.ColumnNotFoundRuntimeException;
import jp.fieldnotes.hatunatu.util.exception.PropertyNotFoundRuntimeException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NullBeanMetaData implements BeanMetaData {

    private Class daoInterface;

    public NullBeanMetaData(Class daoInterface) {
        this.daoInterface = daoInterface;
    }

    @Override
    public String convertFullColumnName(String alias) {
        throwException();
        return null;
    }

    @Override
    public String getAutoSelectList() {
        throwException();
        return null;
    }

    @Override
    public Set getModifiedPropertyNames(Object bean) {
        throwException();
        return null;
    }

    @Override
    public boolean hasRelationToTable() {
        return false;
    }

    @Override
    public String getPrimaryKey(int index) {
        throwException();
        return null;
    }


    @Override
    public PropertyType getPropertyTypeByAliasName(String aliasName)
            throws ColumnNotFoundRuntimeException {
        throwException();
        return null;
    }

    @Override
    public PropertyType getPropertyTypeByColumnName(String columnName)
            throws ColumnNotFoundRuntimeException {
        throwException();
        return null;
    }

    @Override
    public RelationPropertyType getRelationPropertyType(int index) {
        throwException();
        return null;
    }

    @Override
    public RelationPropertyType getRelationPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    @Override
    public List<String> getPrimaryKeys() {
        throwException();
        return null;
    }

    @Override
    public int getRelationPropertyTypeSize() {
        throwException();
        return 0;
    }

    @Override
    public String getTableName() {
        throwException();
        return null;
    }

    @Override
    public String getTimestampPropertyName() {
        throwException();
        return null;
    }

    @Override
    public PropertyType getTimestampPropertyType()
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    @Override
    public String getVersionNoPropertyName() {
        throwException();
        return null;
    }

    @Override
    public PropertyType getVersionNoPropertyType()
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }

    @Override
    public boolean hasPropertyTypeByAliasName(String aliasName) {
        throwException();
        return false;
    }

    @Override
    public boolean hasPropertyTypeByColumnName(String columnName) {
        throwException();
        return false;
    }

    @Override
    public boolean hasTimestampPropertyType() {
        throwException();
        return false;
    }

    @Override
    public boolean hasVersionNoPropertyType() {
        throwException();
        return false;
    }

    @Override
    public Class getBeanClass() {
        return NullBean.class;
    }

    @Override
    public PropertyType getPropertyType(int index) {
        throwException();
        return null;
    }

    @Override
    public PropertyType getPropertyType(String propertyName)
            throws PropertyNotFoundRuntimeException {
        throwException();
        return null;
    }


    @Override
    public Collection<PropertyType> getPropertyTypes() {
        throwException();
        return null;
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(int index) {
        throwException();
        return null;
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(String propertyName) {
        throwException();
        return null;
    }

    @Override
    public int getIdentifierGeneratorSize() {
        throwException();
        return 0;
    }

    @Override
    public boolean hasPropertyType(String propertyName) {
        throwException();
        return false;
    }

    protected void throwException() {

        throw new BeanNotFoundRuntimeException(daoInterface);
    }

}
