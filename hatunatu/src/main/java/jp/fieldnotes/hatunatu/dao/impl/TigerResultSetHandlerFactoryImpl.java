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
import jp.fieldnotes.hatunatu.api.DaoAnnotationReader;
import jp.fieldnotes.hatunatu.dao.FetchHandler;
import jp.fieldnotes.hatunatu.dao.ResultSetHandler;
import jp.fieldnotes.hatunatu.dao.resultset.FetchResultSetHandler;

import java.lang.reflect.Method;

/**
 * @author jundu
 * 
 */
@SuppressWarnings("unchecked")
public class TigerResultSetHandlerFactoryImpl extends
        ResultSetHandlerFactoryImpl {

    @Override
    public ResultSetHandler getResultSetHandler(
            DaoAnnotationReader daoAnnotationReader, BeanMetaData beanMetaData,
            Method method) {
        if (hasFetchHandler(method)) {
            final Class beanClass = daoAnnotationReader.getBeanClass();
            return createFetchResultSetHandler(beanClass, beanMetaData);
        }
        return super.getResultSetHandler(daoAnnotationReader, beanMetaData,
                method);
    }

    protected boolean hasFetchHandler(Method method) {
        Class[] argClasses = method.getParameterTypes();
        if (argClasses.length > 0) {
            Class lastArgClass = argClasses[argClasses.length - 1];
            if (FetchHandler.class.isAssignableFrom(lastArgClass)) {
                // 最後の引数がFetchHandlerであればFetch動作
                return true;
            }
        }
        return false;
    }

    protected ResultSetHandler createFetchResultSetHandler(
            final Class beanClass, final BeanMetaData beanMetaData) {
        return new FetchResultSetHandler(beanClass, beanMetaData,
                dtoMetaDataFactory);
    }

}
