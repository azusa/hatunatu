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

import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.api.ValueType;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.*;

import java.util.ArrayList;
import java.util.List;

public class FastPropertyTypeFactory extends AbstractPropertyTypeFactory {

    private DaoNamingConvention daoNamingConvention = DaoNamingConvention.INSTASNCE;

    /**
     * インスタンスを構築します。
     * 
     * @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     */
    public FastPropertyTypeFactory(Class beanClass,
            BeanAnnotationReader beanAnnotationReader,
            ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming) {
        super(beanClass, beanAnnotationReader, valueTypeFactory, columnNaming);
    }

    /**
     * インスタンスを構築します。
     *  @param beanClass Beanのクラス
     * @param beanAnnotationReader Beanのアノテーションリーダ
     * @param valueTypeFactory {@link ValueType}のファクトリ
     * @param columnNaming カラムのネーミング
     * @param dbms DBMS
     */
    public FastPropertyTypeFactory(Class beanClass,
                                   BeanAnnotationReader beanAnnotationReader,
                                   ValueTypeFactory valueTypeFactory, ColumnNaming columnNaming,
                                   Dbms dbms) {
        super(beanClass, beanAnnotationReader, valueTypeFactory, columnNaming,
                dbms);
    }

    public PropertyType[] createBeanPropertyTypes(String tableName) {
        List list = new ArrayList();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            if (isRelation(pd)) {
                continue;
            }
            PropertyType pt = createPropertyType(pd);
            pt.setPrimaryKey(isPrimaryKey(pd));
            pt.setPersistent(isPersistent(pt));
            list.add(pt);
        }
        return (PropertyType[]) list.toArray(new PropertyType[list.size()]);
    }

    protected boolean isPersistent(PropertyType propertyType) {
        DaoNamingConvention convention = this.daoNamingConvention;
        String propertyName = propertyType.getPropertyName();
        if (propertyName.equals(convention
                .getModifiedPropertyNamesPropertyName())) {
            return false;
        }
        return super.isPersistent(propertyType);
    }


}
