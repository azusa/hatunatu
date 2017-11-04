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
import jp.fieldnotes.hatunatu.dao.DaoNamingConvention;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;

import java.util.Collections;
import java.util.Set;

public class ModifiedPropertySupport {

    private DaoNamingConvention daoNamingConvention = new DaoNamingConventionImpl();

    /* (non-Javadoc)
     * @see jp.fieldnotes.hatunatu.dao.impl.BeanMetaDataImpl.ModifiedPropertySupport#getModifiedPropertyNames(java.lang.Object)
     */
    public Set getModifiedPropertyNames(Object bean) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
        final String propertyName = this.daoNamingConvention
                .getModifiedPropertyNamesPropertyName();
        if (!beanDesc.hasPropertyDesc(propertyName)) {
            return Collections.EMPTY_SET;
        }
        final PropertyDesc propertyDesc = beanDesc
                .getPropertyDesc(propertyName);
        final Object value = propertyDesc.getValue(bean);
        final Set names = (Set) value;
        return names;
    }
}
