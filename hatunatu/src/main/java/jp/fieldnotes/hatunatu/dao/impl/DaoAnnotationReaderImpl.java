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

import jp.fieldnotes.hatunatu.api.DaoAnnotationReader;
import jp.fieldnotes.hatunatu.api.beans.BeanDesc;
import jp.fieldnotes.hatunatu.dao.annotation.tiger.*;
import jp.fieldnotes.hatunatu.dao.util.AnnotationUtil;
import jp.fieldnotes.hatunatu.dao.util.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DaoAnnotationReaderImpl implements DaoAnnotationReader {

    private Class<?> daoClass_;

    public DaoAnnotationReaderImpl(BeanDesc daoBeanDesc) {
        daoClass_ = daoBeanDesc.getBeanClass();
    }

    @Override
    public String getQuery(Method method) {
        Query query = method.getAnnotation(Query.class);
        return (query != null) ? query.value() : null;
    }


    @Override
    public String getProcedureCallName(Method method) {
        ProcedureCall value =  method.getAnnotation(ProcedureCall.class);
        return value != null ? value.value() : null;
    }

    @Override
    public List<String> getArgNames(Method method) {
        Annotation[][] arg = method.getParameterAnnotations();
        List<String> arguments = new ArrayList<>();
        for (Annotation[] a1 : arg){
            for (Annotation a2 : a1){
                if (a2 instanceof Argument){
                    arguments.add(((Argument) a2).value());
                } else {
                    arguments.add("");
                }
            }
        }
        return arguments;
    }


    @Override
    public Class<?> getBeanClass(Method method) {
        Type type = method.getGenericReturnType();
        if (isTypeOf(type, List.class)) {
            Type ret = getElementTypeOfList(type);
            if (ret != null) {
                return getRawClass(ret);
            }
        }
        if (List.class.isAssignableFrom(method.getReturnType())) {
            return null;
        }
        if (TypeUtil.isSimpleType(method.getReturnType())) {
            return method.getReturnType();
        }
        if (method.getReturnType().isArray()) {
            return method.getReturnType().getComponentType();
        }
        return method.getReturnType();
    }

    protected static Type getElementTypeOfList(final Type type) {
        if (!isTypeOf(type, List.class)) {
            return null;
        }
        return getGenericParameter(type, 0);
    }

    protected static boolean isTypeOf(final Type type, final Class<?> clazz) {
        if (Class.class.isInstance(type)) {
            return clazz.isAssignableFrom(Class.class.cast(type));
        }
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType parameterizedType = ParameterizedType.class
                    .cast(type);
            return isTypeOf(parameterizedType.getRawType(), clazz);
        }
        return false;
    }

    protected static Type[] getGenericParameter(final Type type) {
        if (!ParameterizedType.class.isInstance(type)) {
            return null;
        }
        return ParameterizedType.class.cast(type).getActualTypeArguments();
    }

    protected static Type getGenericParameter(final Type type, final int index) {
        if (!ParameterizedType.class.isInstance(type)) {
            return null;
        }
        final Type[] genericParameter = getGenericParameter(type);
        if (genericParameter == null) {
            return null;
        }
        return genericParameter[index];
    }

    /**
     * <code>type</code>の原型を返します。
     * <p>
     * <code>type</code>が原型でもパラメータ化された型でもない場合は<code>null</code>を返します。
     * </p>
     * 
     * @param type
     *            タイプ
     * @return <code>type</code>の原型
     */
    protected static Class<?> getRawClass(final Type type) {
        if (Class.class.isInstance(type)) {
            return Class.class.cast(type);
        }
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType parameterizedType = ParameterizedType.class
                    .cast(type);
            return getRawClass(parameterizedType.getRawType());
        }
        return null;
    }



    @Override
    public String[] getNoPersistentProps(Method method) {
        NoPersistentProperty npp = method
                .getAnnotation(NoPersistentProperty.class);
        return (npp != null) ? npp.value() : null;
    }

    @Override
    public String[] getPersistentProps(Method method) {
        PersistentProperty pp = (PersistentProperty) method
                .getAnnotation(PersistentProperty.class);
        return (pp != null) ? pp.value() : null;
    }

    @Override
    public String getSQL(Method method, String suffix) {
        Sql sql = getSqls(method, suffix);
        if (sql == null) {
            sql = method.getAnnotation(Sql.class);
            if (sql == null) {
                return null;
            } else if (("_" + sql.dbms()).equals(suffix)
                    || sql.dbms().equals("")) {
                return (sql != null) ? sql.value() : null;
            } else {
                return null;
            }
        }
        return (sql != null) ? sql.value() : null;
    }

    private Sql getSqls(Method method, String dbmsSuffix) {
        Sqls sqls = method.getAnnotation(Sqls.class);
        if (sqls == null || sqls.value().length == 0) {
            return null;
        }
        Sql defaultSql = null;
        for (int i = 0; i < sqls.value().length; i++) {
            Sql sql = sqls.value()[i];
            if (dbmsSuffix.equals("_" + sql.dbms())) {
                return sql;
            }
            if ("".equals(sql.dbms())) {
                defaultSql = sql;
            }
        }

        return defaultSql;
    }

    @Override
    public boolean isSqlFile(final Method method) {
        final SqlFile sqlFile = method.getAnnotation(SqlFile.class);
        if (sqlFile != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getSqlFilePath(Method method) {
        final SqlFile sqlFile = method.getAnnotation(SqlFile.class);
        if (sqlFile != null) {
            String path = sqlFile.value();
            if (path == null) {
                path = "";
            }
            return path;
        }
        return "";
    }

    @Override
    public boolean isCheckSingleRowUpdate() {
        final CheckSingleRowUpdate checkSingleRowUpdate = AnnotationUtil
                .getAnnotation(daoClass_, CheckSingleRowUpdate.class);
        if (checkSingleRowUpdate != null) {
            return checkSingleRowUpdate.value();
        }
        return true;
    }

    @Override
    public boolean isCheckSingleRowUpdate(Method method) {
        final CheckSingleRowUpdate checkSingleRowUpdate = method
                .getAnnotation(CheckSingleRowUpdate.class);
        if (checkSingleRowUpdate != null) {
            return checkSingleRowUpdate.value();
        }
        return true;
    }



}
