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

import org.seasar.framework.exception.SRuntimeException;

/**
 * @author azusa
 *
 */
public class MethodSetupFailureRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1116636886135117023L;

    public MethodSetupFailureRuntimeException(String className,
            String methodName, SRuntimeException cause) {
        super("EDAO0019", new Object[] { className, methodName, cause }, cause);
    }

}
