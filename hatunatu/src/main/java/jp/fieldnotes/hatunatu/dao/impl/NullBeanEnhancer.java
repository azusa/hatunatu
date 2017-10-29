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
import jp.fieldnotes.hatunatu.dao.BeanEnhancer;
import jp.fieldnotes.hatunatu.dao.DaoNamingConvention;
import jp.fieldnotes.hatunatu.dao.impl.BeanMetaDataImpl.ModifiedPropertySupport;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import java.util.Collections;
import java.util.Set;

public class NullBeanEnhancer implements BeanEnhancer, ModifiedPropertySupport {

    private DaoNamingConvention daoNamingConvention = new DaoNamingConventionImpl();

    private static final Set EMPTY_SET = Collections.EMPTY_SET;

    /* (non-Javadoc)
     * @see jp.fieldnotes.hatunatu.dao.BeanEnhancer#enhanceBeanClass(java.lang.Class, java.lang.String, java.lang.String)
     */
    public Class enhanceBeanClass(Class beanClass,
            String versionNoPropertyName, String timestampPropertyName) {
        return beanClass;
    }

    /* (non-Javadoc)
     * @see jp.fieldnotes.hatunatu.dao.BeanEnhancer#getOriginalClass(java.lang.Class)
     */
    public Class getOriginalClass(Class beanClass) {
        return beanClass;
    }

    /* (non-Javadoc)
     * @see jp.fieldnotes.hatunatu.dao.BeanEnhancer#isEnhancedClass(java.lang.Class)
     */
    public boolean isEnhancedClass(Class beanClass) {
        return false;
    }

    /* (non-Javadoc)
     * @see jp.fieldnotes.hatunatu.dao.BeanEnhancer#getSupporter()
     */
    public ModifiedPropertySupport getSupporter() {
        return this;
    }

    /* (non-Javadoc)
     * @see jp.fieldnotes.hatunatu.dao.impl.BeanMetaDataImpl.ModifiedPropertySupport#getModifiedPropertyNames(java.lang.Object)
     */
    public Set getModifiedPropertyNames(Object bean) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        final String propertyName = getDaoNamingConvention()
                .getModifiedPropertyNamesPropertyName();
        if (!beanDesc.hasPropertyDesc(propertyName)) {
            return EMPTY_SET;
        }
        final PropertyDesc propertyDesc = beanDesc
                .getPropertyDesc(propertyName);
        final Object value = propertyDesc.getValue(bean);
        final Set names = (Set) value;
        return names;
    }

    public DaoNamingConvention getDaoNamingConvention() {
        return daoNamingConvention;
    }

    public void setDaoNamingConvention(
            final DaoNamingConvention daoNamingConvention) {
        this.daoNamingConvention = daoNamingConvention;
    }
}
