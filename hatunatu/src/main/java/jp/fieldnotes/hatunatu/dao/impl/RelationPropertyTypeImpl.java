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
import jp.fieldnotes.hatunatu.api.RelationPropertyType;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;

public class RelationPropertyTypeImpl extends PropertyTypeImpl implements
        RelationPropertyType {

    protected int relationNo;

    protected String[] myKeys;

    protected String[] yourKeys;

    protected BeanMetaData beanMetaData;

    public RelationPropertyTypeImpl(PropertyDesc propertyDesc) {
        super(propertyDesc);
    }

    public RelationPropertyTypeImpl(PropertyDesc propertyDesc, int relationNo,
            String[] myKeys, String[] yourKeys, BeanMetaData beanMetaData) {

        super(propertyDesc);
        this.relationNo = relationNo;
        this.myKeys = myKeys;
        this.yourKeys = yourKeys;
        this.beanMetaData = beanMetaData;
    }

    @Override
    public int getRelationNo() {
        return relationNo;
    }

    @Override
    public int getKeySize() {
        if (myKeys.length > 0) {
            return myKeys.length;
        } else {
            return beanMetaData.getPrimaryKeySize();
        }

    }

    @Override
    public String getMyKey(int index) {
        if (myKeys.length > 0) {
            return myKeys[index];
        } else {
            return beanMetaData.getPrimaryKey(index);
        }
    }

    @Override
    public String getYourKey(int index) {
        if (yourKeys.length > 0) {
            return yourKeys[index];
        } else {
            return beanMetaData.getPrimaryKey(index);
        }
    }

    @Override
    public boolean isYourKey(String columnName) {
        for (int i = 0; i < getKeySize(); ++i) {
            if (columnName.equalsIgnoreCase(getYourKey(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BeanMetaData getBeanMetaData() {
        return beanMetaData;
    }
}