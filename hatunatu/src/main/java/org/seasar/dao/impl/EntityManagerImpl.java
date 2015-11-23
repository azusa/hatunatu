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
package org.seasar.dao.impl;

import java.util.List;
import java.util.Map;

import org.seasar.dao.DaoMetaData;
import org.seasar.dao.EntityManager;
import org.seasar.dao.SqlCommand;

/**
 * @author higa
 * 
 */
public class EntityManagerImpl implements EntityManager {

    private static Object[] EMPTY_ARGS = new Object[0];

    private DaoMetaData daoMetaData;

    public EntityManagerImpl(DaoMetaData daoMetaData) {
        this.daoMetaData = daoMetaData;
    }

    public DaoMetaData getDaoMetaData() {
        return daoMetaData;
    }

    /**
     * @see org.seasar.dao.EntityManager#find(java.lang.String)
     */
    public List find(String query) {
        return find(query, EMPTY_ARGS);
    }

    /**
     * @see org.seasar.dao.EntityManager#find(java.lang.String,
     *      java.lang.Object)
     */
    public List find(String query, Object arg1) {
        return find(query, new Object[] { arg1 });
    }

    /**
     * @see org.seasar.dao.EntityManager#find(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public List find(String query, Object arg1, Object arg2) {
        return find(query, new Object[] { arg1, arg2 });
    }

    /**
     * @see org.seasar.dao.EntityManager#find(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public List find(String query, Object arg1, Object arg2, Object arg3) {
        return find(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * @see org.seasar.dao.EntityManager#find(java.lang.String,
     *      java.lang.Object[])
     */
    public List find(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindCommand(query);
        return (List) cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#find(java.lang.Class, java.lang.String)
     */
    public List find(Class dtoClass, String query) {
        return find(dtoClass, query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#find(java.lang.Class, java.lang.String,
     *      java.lang.Object)
     */
    public List find(Class dtoClass, String query, Object arg1) {
        return find(dtoClass, query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#find(java.lang.Class, java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public List find(Class dtoClass, String query, Object arg1, Object arg2) {
        return find(dtoClass, query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#find(java.lang.Class, java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public List find(Class dtoClass, String query, Object arg1, Object arg2,
            Object arg3) {
        return find(dtoClass, query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#find(java.lang.Class, java.lang.String,
     *      java.lang.Object[])
     */
    public List find(Class dtoClass, String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindCommand(dtoClass, query);
        return (List) cmd.execute(args);
    }

    /**
     * @see org.seasar.dao.EntityManager#findArray(java.lang.String)
     */
    public Object[] findArray(String query) {
        return findArray(query, EMPTY_ARGS);
    }

    /**
     * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
     *      java.lang.Object)
     */
    public Object[] findArray(String query, Object arg1) {
        return findArray(query, new Object[] { arg1 });
    }

    /**
     * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Object[] findArray(String query, Object arg1, Object arg2) {
        return findArray(query, new Object[] { arg1, arg2 });
    }

    /**
     * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object[] findArray(String query, Object arg1, Object arg2,
            Object arg3) {

        return findArray(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * @see org.seasar.dao.EntityManager#findArray(java.lang.String,
     *      java.lang.Object[])
     */
    public Object[] findArray(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindArrayCommand(query);
        return (Object[]) cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findArray(java.lang.Class,
     *      java.lang.String)
     */
    public Object[] findArray(Class dtoClass, String query) {
        return findArray(dtoClass, query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findArray(java.lang.Class,
     *      java.lang.String, java.lang.Object)
     */
    public Object[] findArray(Class dtoClass, String query, Object arg1) {
        return findArray(dtoClass, query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findArray(java.lang.Class,
     *      java.lang.String, java.lang.Object, java.lang.Object)
     */
    public Object[] findArray(Class dtoClass, String query, Object arg1,
            Object arg2) {
        return findArray(dtoClass, query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findArray(java.lang.Class,
     *      java.lang.String, java.lang.Object, java.lang.Object,
     *      java.lang.Object)
     */
    public Object[] findArray(Class dtoClass, String query, Object arg1,
            Object arg2, Object arg3) {
        return findArray(dtoClass, query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findArray(java.lang.Class,
     *      java.lang.String, java.lang.Object[])
     */
    public Object[] findArray(Class dtoClass, String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindArrayCommand(dtoClass, query);
        return (Object[]) cmd.execute(args);
    }

    /**
     * @see org.seasar.dao.EntityManager#findBean(java.lang.String)
     */
    public Object findBean(String query) {
        return findBean(query, EMPTY_ARGS);
    }

    /**
     * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
     *      java.lang.Object)
     */
    public Object findBean(String query, Object arg1) {
        return findBean(query, new Object[] { arg1 });
    }

    /**
     * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Object findBean(String query, Object arg1, Object arg2) {
        return findBean(query, new Object[] { arg1, arg2 });
    }

    /**
     * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object findBean(String query, Object arg1, Object arg2, Object arg3) {
        return findBean(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * @see org.seasar.dao.EntityManager#findBean(java.lang.String,
     *      java.lang.Object[])
     */
    public Object findBean(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindBeanCommand(query);
        return cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findBean(java.lang.Class,
     *      java.lang.String)
     */
    public Object findBean(Class dtoClass, String query) {
        return findBean(dtoClass, query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findBean(java.lang.Class,
     *      java.lang.String, java.lang.Object)
     */
    public Object findBean(Class dtoClass, String query, Object arg1) {
        return findBean(dtoClass, query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findBean(java.lang.Class,
     *      java.lang.String, java.lang.Object, java.lang.Object)
     */
    public Object findBean(Class dtoClass, String query, Object arg1,
            Object arg2) {
        return findBean(dtoClass, query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findBean(java.lang.Class,
     *      java.lang.String, java.lang.Object, java.lang.Object,
     *      java.lang.Object)
     */
    public Object findBean(Class dtoClass, String query, Object arg1,
            Object arg2, Object arg3) {
        return findBean(dtoClass, query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findBean(java.lang.Class,
     *      java.lang.String, java.lang.Object[])
     */
    public Object findBean(Class dtoClass, String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindBeanCommand(dtoClass, query);
        return cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMap(java.lang.String)
     */
    public Map findMap(String query) {
        return findMap(query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMap(java.lang.String,
     *      java.lang.Object)
     */
    public Map findMap(String query, Object arg1) {
        return findMap(query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMap(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Map findMap(String query, Object arg1, Object arg2) {
        return findMap(query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMap(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Map findMap(String query, Object arg1, Object arg2, Object arg3) {
        return findMap(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMap(java.lang.String,
     *      java.lang.Object[])
     */
    public Map findMap(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindMapCommand(query);
        return (Map) cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapList(java.lang.String)
     */
    public List findMapList(String query) {
        return findMapList(query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapList(java.lang.String,
     *      java.lang.Object)
     */
    public List findMapList(String query, Object arg1) {
        return findMapList(query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapList(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public List findMapList(String query, Object arg1, Object arg2) {
        return findMapList(query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapList(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public List findMapList(String query, Object arg1, Object arg2, Object arg3) {
        return findMapList(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapList(java.lang.String,
     *      java.lang.Object[])
     */
    public List findMapList(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindMapListCommand(query);
        return (List) cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapArray(java.lang.String)
     */
    public Map[] findMapArray(String query) {
        return findMapArray(query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapArray(java.lang.String,
     *      java.lang.Object)
     */
    public Map[] findMapArray(String query, Object arg1) {
        return findMapArray(query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapArray(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Map[] findMapArray(String query, Object arg1, Object arg2) {
        return findMapArray(query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapArray(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Map[] findMapArray(String query, Object arg1, Object arg2,
            Object arg3) {
        return findMapArray(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findMapArray(java.lang.String,
     *      java.lang.Object[])
     */
    public Map[] findMapArray(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindMapArrayCommand(query);
        return (Map[]) cmd.execute(args);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findObject(java.lang.String)
     */
    public Object findObject(String query) {
        return findObject(query, EMPTY_ARGS);
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
     *      java.lang.Object)
     */
    public Object findObject(String query, Object arg1) {
        return findObject(query, new Object[] { arg1 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
     *      java.lang.Object, java.lang.Object)
     */
    public Object findObject(String query, Object arg1, Object arg2) {
        return findObject(query, new Object[] { arg1, arg2 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
     *      java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object findObject(String query, Object arg1, Object arg2, Object arg3) {
        return findObject(query, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * 
     * @see org.seasar.dao.EntityManager#findObject(java.lang.String,
     *      java.lang.Object[])
     */
    public Object findObject(String query, Object[] args) {
        SqlCommand cmd = daoMetaData.createFindObjectCommand(query);
        return cmd.execute(args);
    }
}