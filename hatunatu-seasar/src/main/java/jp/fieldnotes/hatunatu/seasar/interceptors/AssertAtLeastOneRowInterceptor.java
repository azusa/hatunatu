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
package jp.fieldnotes.hatunatu.seasar.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import jp.fieldnotes.hatunatu.seasar.exception.NoRowsUpdatedRuntimeException;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

/**
 * @author manhole
 */
public class AssertAtLeastOneRowInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object result = invocation.proceed();
        if (result instanceof Number) {
            final int rows = ((Number) result).intValue();
            if (rows < 1) {
                throw new NoRowsUpdatedRuntimeException();
            }
        }
        return result;
    }

}
