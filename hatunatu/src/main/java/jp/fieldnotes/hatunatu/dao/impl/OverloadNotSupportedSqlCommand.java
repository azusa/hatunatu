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

import jp.fieldnotes.hatunatu.dao.exception.OverloadNotSupportedRuntimeException;
import jp.fieldnotes.hatunatu.api.SqlCommand;

/**
 * {@link OverloadNotSupportedRuntimeException}をスローする{@link SqlCommand}です。
 * 
 * @author taedium
 */
public class OverloadNotSupportedSqlCommand implements SqlCommand {

    private String className;

    private String methodName;

    /**
     * {@link OverloadNotSupportedSqlCommand}を生成します。
     * 
     * @param className
     *            クラス名
     * @param methodName
     *            メソッド名
     */
    public OverloadNotSupportedSqlCommand(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public Object execute(Object[] args) {
        throw new OverloadNotSupportedRuntimeException(className, methodName);
    }
}
