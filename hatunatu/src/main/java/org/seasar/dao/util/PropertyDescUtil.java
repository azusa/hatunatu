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
package org.seasar.dao.util;

import java.lang.reflect.Method;

import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.MethodUtil;

/**
 * Seasar2.3とSeasar2.4における{@link PropertyDesc}の仕様の違いを吸収するユーティリティです。
 * 
 * @author taedium
 */
public class PropertyDescUtil {

    private static Method isWritableMethod = getIsWritableMethod();

    private static Method isReadableMethod = getIsReadableMethod();

    private PropertyDescUtil() {
    }

    /**
     * プロパティが書き込み可能な場合<code>true</code>を返します。
     * 
     * @param propertyDesc プロパティ記述
     * @return プロパティが書き込み可能な場合<code>true</code>、そうでない場合<code>false</code>
     */
    public static boolean isWritable(PropertyDesc propertyDesc) {
        if (isWritableMethod != null) {
            Boolean b = (Boolean) MethodUtil.invoke(isWritableMethod,
                    propertyDesc, null);
            return b.booleanValue();
        }
        return propertyDesc.hasWriteMethod();
    }

    /**
     * プロパティが読み取り可能な場合<code>true</code>を返します。
     * 
     * @param propertyDesc プロパティ記述
     * @return プロパティが読み取り可能な場合<code>true</code>、そうでない場合<code>false</code>
     */
    public static boolean isReadable(PropertyDesc propertyDesc) {
        if (isReadableMethod != null) {
            Boolean b = (Boolean) MethodUtil.invoke(isReadableMethod,
                    propertyDesc, null);
            return b.booleanValue();
        }
        return propertyDesc.hasReadMethod();
    }

    private static Method getIsWritableMethod() {
        try {
            return PropertyDesc.class.getMethod("isWritable", null);
        } catch (NoSuchMethodException ignore) {
            return null;
        }
    }

    private static Method getIsReadableMethod() {
        try {
            return PropertyDesc.class.getMethod("isReadable", null);
        } catch (NoSuchMethodException ignore) {
            return null;
        }
    }
}
