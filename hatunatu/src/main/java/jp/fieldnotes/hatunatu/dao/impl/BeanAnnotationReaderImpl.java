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

import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.PropertyDesc;
import jp.fieldnotes.hatunatu.dao.BeanAnnotationReader;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.*;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import java.lang.annotation.Annotation;

public class BeanAnnotationReaderImpl implements BeanAnnotationReader {

    private Class<?> beanClass_;

    private Bean bean_;

    @SuppressWarnings("unchecked")
    public BeanAnnotationReaderImpl(Class beanClass) {
        this.beanClass_ = beanClass;
        bean_ = (Bean) beanClass_.getAnnotation(Bean.class);
    }

    private <T extends Annotation> T getPropertyAnnotation(Class<T> clazz,
            PropertyDesc pd) {
        BeanDesc bd = BeanDescFactory.getBeanDesc(beanClass_);
        if (bd.hasFieldDesc(pd.getPropertyName())) {
            T fieldAnnotation = bd.getFieldDesc(pd.getPropertyName()).getField()
                    .getAnnotation(clazz);
            if (fieldAnnotation != null) {
                return fieldAnnotation;
            }
        }

        if (pd.getWriteMethod() != null) {
            T annotation = pd.getWriteMethod().getAnnotation(clazz);
            if (annotation != null) {
                return annotation;
            }
        }
        if (pd.getReadMethod() != null) {
            return pd.getReadMethod().getAnnotation(clazz);
        }
        return null;
    }

    @Override
    public String getColumnAnnotation(PropertyDesc pd) {
        Column ret = getPropertyAnnotation(Column.class, pd);
        return ret == null ? null : ret.value();
    }

    @Override
    public String getTableAnnotation() {
        if (bean_ == null){
            return null;
        }
        return bean_.table();
    }

    @Override
    public String getVersionNoPropertyName() {
        if (bean_ == null){
            return null;
        }
        return bean_.versionNoProperty();
    }

    @Override
    public String getTimestampPropertyName() {
        if (bean_  == null){
            return null;
        }
        return bean_.timeStampProperty();
    }

    @Override
    public String getId(PropertyDesc pd, Dbms dbms) {
        String dbmsSuffix = dbms.getSuffix();
        Id id = getIds(pd, dbmsSuffix);
        if (id == null) {
            id = getPropertyAnnotation(Id.class, pd);
            if (id == null) {
                return null;
            }
            if (("_" + id.dbms()).equals(dbms.getSuffix())
                    || id.dbms().equals("")) {
                return getIdName(id);
            } else {
                return null;
            }
        } else {
            return getIdName(id);
        }
    }

    @Override
    public String[] getNoPersisteneProps() {
        if (bean_ == null){
            return null;
        }
        return  bean_.noPersistentProperty();
    }

    @Override
    public boolean hasRelationNo(PropertyDesc pd) {
        return getPropertyAnnotation(Relation.class, pd) != null;
    }

    @Override
    public int getRelationNo(PropertyDesc pd) {
        Relation rel = getPropertyAnnotation(Relation.class, pd);
        if (rel != null) {
            return rel.relationNo();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getRelationKey(PropertyDesc pd) {
        Relation rel = getPropertyAnnotation(Relation.class, pd);
        return (rel != null) ? rel.relationKey() : null;
    }

    @Override
    public String getValueType(PropertyDesc pd) {
        ValueType valueType = (ValueType) getPropertyAnnotation(
                ValueType.class, pd);
        return (valueType != null) ? valueType.value() : null;
    }

    protected Id getIds(PropertyDesc pd, String dbmsSuffix) {
        Ids ids = getPropertyAnnotation(Ids.class, pd);
        if (ids == null || ids.value().length == 0) {
            return null;
        }
        Id defaultId = null;
        for (int i = 0; i < ids.value().length; i++) {
            Id id = ids.value()[i];
            if (dbmsSuffix.equals("_" + id.dbms())) {
                return id;
            }
            if ("".equals(id.dbms())) {
                defaultId = id;
            }
        }
        return defaultId;
    }

    protected String getIdName(Id id) {
        if (id.value().equals(IdType.SEQUENCE) && id.sequenceName() != null) {
            StringBuilder buf = new StringBuilder(100);
            buf.append(id.value().name().toLowerCase());
            buf.append(", sequenceName=");
            buf.append(id.sequenceName());
            buf.append(", allocationSize=");
            buf.append(id.allocationSize());
            return buf.toString();
        }
        return id.value().name().toLowerCase();
    }
}
