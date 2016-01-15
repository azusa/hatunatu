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

package jp.fieldnotes.hatunatu.dao.jdbc;


import jp.fieldnotes.hatunatu.api.ValueType;

import java.io.Serializable;

public class QueryObject implements Serializable {

    private final Object[] EMPTY_ARGS = new Object[0];

    private final Class[] EMPTY_TYPES = new Class[0];

    private String sql;

    private String originalSql;

    private Object[] bindArguments = EMPTY_ARGS;

    private Class[] bindTypes = EMPTY_TYPES;

    private Object[] methodArguments = EMPTY_ARGS;

    private Class<?> daoClass;

    private ValueType[] bindVariableValueTypes;

    public ValueType[] getBindVariableValueTypes() {
        return bindVariableValueTypes;
    }

    public void setBindVariableValueTypes(ValueType[] bindVariableValueTypes) {
        this.bindVariableValueTypes = bindVariableValueTypes;
    }

    /**
     * Returns SQL string.
     *
     * @return SQL string
     */
    public String getSql() {
        return sql;
    }

    /**
     * Sets SQL string.
     *
     * @param sql SQL string.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Returns bind arguments.
     *
     * @return bind arguments
     */
    public Object[] getBindArguments() {
        return bindArguments;
    }

    /**
     * Sets bind arguments.
     *
     * @param bindArguments bind arguments
     */
    public void setBindArguments(Object[] bindArguments) {
        this.bindArguments = bindArguments;
    }

    /**
     * Returns types of bind arguments.
     * * @return types of bind arguments
     */
    public Class[] getBindTypes() {
        return bindTypes;
    }

    /**
     * Sets types of bind arguments.
     *
     * @param bindTypes
     */
    public void setBindTypes(Class[] bindTypes) {
        this.bindTypes = bindTypes;
    }

    /**
     * Returns arguments of DAO method.
     *
     * @return arguments of DAO method
     */
    public Object[] getMethodArguments() {
        return methodArguments;
    }

    /**
     * Set arguments of DAO method.
     *
     * @param methodArguments arguments of DAO method
     */
    public void setMethodArguments(Object[] methodArguments) {
        this.methodArguments = methodArguments;
    }

    /**
     * Returns a class of DAO.
     *
     * @return class of DAO
     */
    public Class<?> getDaoClass() {
        return daoClass;
    }

    /**
     * Set a class of DAO.
     *
     * @param daoClass class of DAO
     */
    public void setDaoClass(Class<?> daoClass) {
        this.daoClass = daoClass;
    }

    public String getOriginalSql() {
        if (originalSql == null) {
            return originalSql;
        }
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = originalSql;
    }
}
