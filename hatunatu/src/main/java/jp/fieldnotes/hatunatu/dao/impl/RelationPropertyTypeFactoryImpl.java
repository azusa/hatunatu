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
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.dao.BeanMetaDataFactory;
import jp.fieldnotes.hatunatu.dao.RelationPropertyTypeFactory;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RelationPropertyTypeFactoryImpl implements
        RelationPropertyTypeFactory {

    protected Class beanClass;

    protected BeanAnnotationReader beanAnnotationReader;

    protected BeanMetaDataFactory beanMetaDataFactory;

    protected DatabaseMetaData databaseMetaData;

    protected int relationNestLevel;

    protected boolean isStopRelationCreation;

    public RelationPropertyTypeFactoryImpl(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            BeanMetaDataFactory beanMetaDataFactory,
            DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
        this.beanMetaDataFactory = beanMetaDataFactory;
        this.databaseMetaData = databaseMetaData;
        this.relationNestLevel = relationNestLevel;
        this.isStopRelationCreation = isStopRelationCreation;
    }

    public RelationPropertyType[] createRelationPropertyTypes() {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (isStopRelationCreation || !isRelationProperty(pd)) {
                continue;
            }
            RelationPropertyType rpt = createRelationPropertyType(pd);
            list.add(rpt);
        }
        return (RelationPropertyType[]) list
                .toArray(new RelationPropertyType[list.size()]);
    }

    protected RelationPropertyType createRelationPropertyType(
            PropertyDesc propertyDesc) {

        String[] myKeys = new String[0];
        String[] yourKeys = new String[0];
        int relno = beanAnnotationReader.getRelationNo(propertyDesc);
        String relkeys = beanAnnotationReader.getRelationKey(propertyDesc);
        if (relkeys != null) {
            StringTokenizer st = new StringTokenizer(relkeys, " \t\n\r\f,");
            List myKeyList = new ArrayList();
            List yourKeyList = new ArrayList();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int index = token.indexOf(':');
                if (index > 0) {
                    myKeyList.add(token.substring(0, index));
                    yourKeyList.add(token.substring(index + 1));
                } else {
                    myKeyList.add(token);
                    yourKeyList.add(token);
                }
            }
            myKeys = (String[]) myKeyList.toArray(new String[myKeyList.size()]);
            yourKeys = (String[]) yourKeyList.toArray(new String[yourKeyList
                    .size()]);
        }
        final BeanMetaData beanMetaData = createRelationBeanMetaData(propertyDesc
                .getPropertyType());
        final RelationPropertyType rpt = new RelationPropertyTypeImpl(
                propertyDesc, relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected BeanMetaData createRelationBeanMetaData(
            final Class relationBeanClass) {
        return beanMetaDataFactory.createBeanMetaData(databaseMetaData,
                relationBeanClass, relationNestLevel + 1);
    }

    protected boolean isRelationProperty(PropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    protected BeanDesc getBeanDesc() {
        return BeanDescFactory.getBeanDesc(beanClass);
    }

}
