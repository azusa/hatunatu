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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.DaoMetaDataFactory;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.api.pager.PagerContext;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.NumberConversionUtil;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Interceptor for seasar2.
 */
public class S2DaoInterceptor extends AbstractInterceptor implements MethodInterceptor, Serializable  {

    private static final long serialVersionUID = 1L;

    private DaoMetaDataFactory daoMetaDataFactory;

    public S2DaoInterceptor(DaoMetaDataFactory daoMetaDataFactory) {
        this.daoMetaDataFactory = daoMetaDataFactory;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean started = false;
        PagerContext pagerContext = PagerContext.getContext();
        if (pagerContext == null) {
            PagerContext.start();
            started = true;
            pagerContext = PagerContext.getContext();
        }
        pagerContext.pushArgs(invocation.getArguments());
        try {
            return doInvoke(invocation);
        } finally {
            pagerContext.popArgs();
            if (started) {
                PagerContext.end();
            }
        }
    }

    private Object doInvoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (!MethodUtil.isAbstract(method)) {
            return invocation.proceed();
        }
        Class targetClass = getTargetClass(invocation);
        DaoMetaData dmd = daoMetaDataFactory.getDaoMetaData(targetClass);
        SqlCommand cmd = dmd.getSqlCommand(method);
        Object ret = cmd.execute(invocation.getArguments());
        Class retType = method.getReturnType();
        if (retType.isPrimitive()) {
            return NumberConversionUtil.convertPrimitiveWrapper(retType, ret);
        } else if (Number.class.isAssignableFrom(retType)) {
            return NumberConversionUtil.convertNumber(retType, ret);
        }
        return ret;
    }
}
