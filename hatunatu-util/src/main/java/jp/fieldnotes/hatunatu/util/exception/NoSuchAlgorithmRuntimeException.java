/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.util.exception;

import jp.fieldnotes.hatunatu.util.collection.ArrayUtil;

import java.security.NoSuchAlgorithmException;

/**
 * {@link NoSuchAlgorithmException}をラップする例外です。
 * 
 * @author higa
 */
public class NoSuchAlgorithmRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = -3176447530746274091L;

    /**
     * {@link NoSuchAlgorithmRuntimeException}を作成します。
     * 
     * @param cause
     *            原因となった例外
     */
    public NoSuchAlgorithmRuntimeException(final NoSuchAlgorithmException cause) {
        super("EUTL0067", ArrayUtil.asArray(cause), cause);
    }

}
