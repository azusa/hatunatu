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
package jp.fieldnotes.hatunatu.dao.exception;

import jp.fieldnotes.hatunatu.util.exception.SRuntimeException;

public class UpdateFailureRuntimeException extends SRuntimeException {

    private Object bean;

    private int rows;

    public UpdateFailureRuntimeException(Object bean, int rows) {
        super("EDAO0005",
                new Object[] { bean.toString(), String.valueOf(rows) });
        this.bean = bean;
        this.rows = rows;
    }

    public Object getBean() {
        return bean;
    }

    public int getRows() {
        return rows;
    }
}