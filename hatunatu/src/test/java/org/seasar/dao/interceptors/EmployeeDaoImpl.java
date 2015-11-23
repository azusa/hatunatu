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
package org.seasar.dao.interceptors;

import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.impl.AbstractDao;

/**
 * @author higa
 * 
 */
public abstract class EmployeeDaoImpl extends AbstractDao implements
        EmployeeDao {

    /**
     * @param daoMetaDataFactory
     */
    public EmployeeDaoImpl(DaoMetaDataFactory daoMetaDataFactory) {
        super(daoMetaDataFactory);
    }

    public Employee[] getEmployeesByDeptno(int deptno) {
        return (Employee[]) getEntityManager().findArray("EMP.deptno = ?",
                new Integer(deptno));
    }
}
