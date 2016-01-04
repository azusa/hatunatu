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
package jp.fieldnotes.hatunatu.util.lang;

import java.lang.annotation.Annotation;
import java.util.Map;

import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.api.beans.MethodDesc;
import jp.fieldnotes.hatunatu.util.beans.factory.BeanDescFactory;
import jp.fieldnotes.hatunatu.util.collection.CollectionsUtil;
import jp.fieldnotes.hatunatu.util.misc.AssertionUtil;

/**
 * アノテーションのためのユーティリティクラスです。
 * 
 * @author higa
 */
public abstract class AnnotationUtil {

    /**
     * アノテーションの要素を名前と値の{@link Map}として返します。
     * 
     * @param annotation
     *            アノテーション。{@literal null}であってはいけません
     * @return アノテーションの要素の名前と値からなる{@link Map}
     */
    public static Map<String, Object> getProperties(final Annotation annotation) {
        AssertionUtil.assertArgumentNotNull("annotation", annotation);

        final Map<String, Object> map = CollectionsUtil.newHashMap();
        final BeanDesc beanDesc =
            BeanDescFactory.getBeanDesc(annotation.annotationType());
        for (final String name : beanDesc.getMethodNames()) {
            final Object v = getProperty(beanDesc, annotation, name);
            if (v != null) {
                map.put(name, v);
            }
        }
        return map;
    }

    /**
     * アノテーションの要素の値を返します。
     * 
     * @param beanDesc
     *            アノテーションを表す{@link BeanDesc}
     * @param annotation
     *            アノテーション
     * @param name
     *            要素の名前
     * @return アノテーションの要素の値
     */
    protected static Object getProperty(final BeanDesc beanDesc,
            final Annotation annotation, final String name) {
        final MethodDesc methodDesc = beanDesc.getMethodDescNoException(name);
        if (methodDesc == null) {
            return null;
        }
        final Object value = methodDesc.invoke(annotation);
        if (value == null || "".equals(value)) {
            return null;
        }
        return value;
    }

}