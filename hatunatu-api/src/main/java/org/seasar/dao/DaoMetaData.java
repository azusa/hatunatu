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
package org.seasar.dao;



/**
 * @author higa
 * @author manhole
 * 
 */
public interface DaoMetaData {

    Class getBeanClass();

    BeanMetaData getBeanMetaData();

    DaoAnnotationReader getDaoAnnotationReader();

    boolean hasSqlCommand(String methodName);

    SqlCommand getSqlCommand(String methodName);

    SqlCommand createFindCommand(String query);

    SqlCommand createFindCommand(Class dtoClass, String query);

    SqlCommand createFindArrayCommand(String query);

    SqlCommand createFindArrayCommand(Class dtoClass, String query);

    SqlCommand createFindBeanCommand(String query);

    SqlCommand createFindBeanCommand(Class dtoClass, String query);

    SqlCommand createFindMapCommand(String query);

    SqlCommand createFindMapListCommand(String query);

    SqlCommand createFindMapArrayCommand(String query);


}