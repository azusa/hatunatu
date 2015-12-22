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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.seasar.util.exception.NoSuchFieldRuntimeException;
import org.seasar.util.lang.ClassUtil;
import org.seasar.util.lang.FieldUtil;

/**
 * Seasar2.3とSeasar2.4における動的なデータソースの仕様の違いを吸収するユーティリティです。
 * 
 * @author taedium
 */
public class SelectableDataSourceProxyUtil {

    private static Adapter adapter = getAdapter();

    private SelectableDataSourceProxyUtil() {
    }

    /**
     * 動的なデータソースの名前を返します。
     * 
     * @param dataSource
     *            データソース
     * @return 動的なデータソースの名前、動的なデータソースでない場合は<code>null</code>
     */
    public static String getSelectableDataSourceName(DataSource dataSource) {
        return adapter.getDataSourceName(dataSource);
    }

    private static Adapter getAdapter() {
        return new AdapterS24();
    }

    private static interface Adapter {

        String getDataSourceName(DataSource dataSource);
    }


    private static class AdapterS24 implements Adapter {

        private Class selectableDataSourceProxyClass;

        private Field dataSourceFactoryField;

        private Method getSelectableDataSourceNameMethod;

        private AdapterS24() {
            selectableDataSourceProxyClass = ClassUtil
                    .forName("org.seasar.extension.datasource.impl.SelectableDataSourceProxy");
            try {
                dataSourceFactoryField = selectableDataSourceProxyClass
                        .getDeclaredField("dataSourceFactory");
            } catch (NoSuchFieldException ex) {
                throw new NoSuchFieldRuntimeException(
                        selectableDataSourceProxyClass, "dataSourceFactory", ex);
            }
            dataSourceFactoryField.setAccessible(true);
            getSelectableDataSourceNameMethod = ClassUtil.getMethod(
                    dataSourceFactoryField.getType(),
                    "getSelectableDataSourceName", null);
        }

        public String getDataSourceName(DataSource dataSource) {
            if (!selectableDataSourceProxyClass.isInstance(dataSource)) {
                return null;
            }
            Object dataSourceFactory = FieldUtil.get(dataSourceFactoryField,
                    dataSource);
            return (String) MethodUtil.invoke(
                    getSelectableDataSourceNameMethod, dataSourceFactory, null);
        }
    }

}
