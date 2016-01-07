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
package jp.fieldnotes.hatunatu.dao;

import java.lang.reflect.Method;

public interface DaoAnnotationReader {

    /**
     * @param name
     * @return
     */
    String getQuery(Method method);

    /**
     * 
     * @param method
     * @return
     */
    String getStoredProcedureName(Method method);

    /**
     * 
     * @param method
     * @return
     */
    String getProcedureCallName(Method method);

    /**
     * @param method
     * @return
     */
    String[] getArgNames(Method method);

    /**
     * @return
     */
    Class getBeanClass();

    /**
     * @return
     */
    Class getBeanClass(Method method);

    /**
     * @param methodName
     * @return
     */
    String[] getNoPersistentProps(Method method);

    /**
     * @param methodName
     * @return
     */
    String[] getPersistentProps(Method method);

    /**
     * @param name
     * @param suffix
     * @return
     */
    String getSQL(Method method, String suffix);

    /**
     * 
     * @param method
     * @return
     */
    boolean isSqlFile(Method method);

    /**
     * SQLファイルのパスを返します。
     * 
     * <p>
     * 取得できなかった場合は、空文字列を返します。
     * </p>
     * @param method
     * @return
     */
    String getSqlFilePath(Method method);

    /**
     * 
     * @return
     */
    boolean isCheckSingleRowUpdate();

    /**
     * 
     * @param method
     * @return
     */
    boolean isCheckSingleRowUpdate(Method method);
}
