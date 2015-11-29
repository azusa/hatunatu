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
package org.seasar.dao.impl;

import java.lang.reflect.Method;

import org.seasar.dao.BeanMetaData;
import org.seasar.dao.DaoAnnotationReader;
import org.seasar.dao.DtoMetaDataFactory;
import org.seasar.dao.ResultSetHandlerFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.framework.exception.ClassNotFoundRuntimeException;
import org.seasar.framework.util.ClassUtil;

/**
 * @author jundu
 * 
 */
public class ResultSetHandlerFactorySelector implements ResultSetHandlerFactory {

    public static final String dtoMetaDataFactory_BINDING = "bindingType=must";

    public static final String INIT_METHOD = "init";

    protected ResultSetHandlerFactory resultSetHandlerFactory;

    protected DtoMetaDataFactory dtoMetaDataFactory;

    /**
     * プロパティrestrictNotSingleResultに対するBINDINGアノテーションです。
     */
    public static final String restrictNotSingleResult_BINDING = "bindingType=may";

    /**
     * 返り値がBean、DTOやMapのメソッドで、結果が2件以上の時に例外を投げるかを設定します。
     */
    protected boolean restrictNotSingleResult = false;

    public void init() {
        ResultSetHandlerFactoryImpl factory = new TigerResultSetHandlerFactoryImpl();
        factory.setDtoMetaDataFactory(dtoMetaDataFactory);
        factory.setRestrictNotSingleResult(restrictNotSingleResult);
        resultSetHandlerFactory = factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.dao.ResultSetHandlerFactory#getResultSetHandler(org.seasar
     * .dao.DaoAnnotationReader, org.seasar.dao.BeanMetaData,
     * java.lang.reflect.Method)
     */
    public ResultSetHandler getResultSetHandler(
            DaoAnnotationReader daoAnnotationReader, BeanMetaData beanMetaData,
            Method method) {
        return resultSetHandlerFactory.getResultSetHandler(daoAnnotationReader,
                beanMetaData, method);
    }

    public void setDtoMetaDataFactory(DtoMetaDataFactory dtoMetaDataFactory) {
        this.dtoMetaDataFactory = dtoMetaDataFactory;
    }

    /**
     * 返り値がBean、DTOやMapのメソッドで、結果が2件以上の時に例外を投げるかを設定します。
     * 
     * @param restrictNotSingleResult
     *            例外を投げる時に<code>true</code>
     */
    public void setRestrictNotSingleResult(boolean restrictNotSingleResult) {
        this.restrictNotSingleResult = restrictNotSingleResult;
    }

}