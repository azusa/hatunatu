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
package jp.fieldnotes.hatunatu.lastadi;


import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.DaoMetaDataFactory;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.util.convert.NumberConversionUtil;
import jp.fieldnotes.hatunatu.util.lang.MethodUtil;
import org.lastaflute.di.core.aop.frame.MethodInterceptor;
import org.lastaflute.di.core.aop.frame.MethodInvocation;
import org.lastaflute.di.core.aop.interceptors.AbstractInterceptor;


import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Interceptor for LastaDi.
 */
public class LastaDiInterceptor extends AbstractInterceptor implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;

    private DaoMetaDataFactory daoMetaDataFactory;

    public LastaDiInterceptor(DaoMetaDataFactory daoMetaDataFactory) {
        this.daoMetaDataFactory = daoMetaDataFactory;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return doInvoke(invocation);
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
