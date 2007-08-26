/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dao.handler;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dao.ProcedureMetaData;
import org.seasar.dao.ProcedureParameterType;
import org.seasar.extension.jdbc.ResultSetFactory;
import org.seasar.extension.jdbc.ResultSetHandler;
import org.seasar.extension.jdbc.StatementFactory;
import org.seasar.framework.exception.SRuntimeException;

/**
 * @author manhole
 * @author taedium
 */
public class ProcedureHandlerImpl extends AbstractProcedureHandler {

    private Method daoMethod;

    public Method getDaoMethod() {
        return daoMethod;
    }

    public void setDaoMethod(final Method daoMethod) {
        this.daoMethod = daoMethod;
    }

    public ProcedureHandlerImpl(final DataSource dataSource, final String sql,
            final ResultSetHandler resultSetHandler,
            final StatementFactory statementFactory,
            final ResultSetFactory resultSetFactory,
            final ProcedureMetaData procedureMetaData, final Method daoMethod) {

        super(dataSource, sql, resultSetHandler, statementFactory,
                resultSetFactory, procedureMetaData);
        setDaoMethod(daoMethod);
    }

    protected void bindArgs(final CallableStatement cs, final Object[] args)
            throws SQLException {
        if (args == null) {
            return;
        }
        final ProcedureMetaData procedureMetaData = getProcedureMetaData();
        final int size = procedureMetaData.getParameterTypeSize();
        for (int i = 0, argIndex = 0; i < size; i++) {
            final ProcedureParameterType ppt = procedureMetaData.getParameterType(i);
            if (isReturnOrOutType(ppt)) {
                registerOutParameter(cs, ppt);
            }
            if (ppt.isInType()) {
                bindValue(cs, ppt, args[argIndex]);
                argIndex++;
            }
        }
    }

    protected Object handleNoResultSet(final CallableStatement cs,
            final Object[] args) throws SQLException {
        final Class returnType = getDaoMethod().getReturnType();
        final ProcedureMetaData procedureMetaData = getProcedureMetaData();
        if (Map.class.isAssignableFrom(returnType)) {
            final Map result = new HashMap();
            for (int i = 0; i < procedureMetaData.getParameterTypeSize(); i++) {
                final ProcedureParameterType ppt = procedureMetaData
                        .getParameterType(i);
                if (isReturnOrOutType(ppt)) {
                    result.put(ppt.getParameterName(), getValue(cs, ppt));
                }
            }
            return result;
        } else {
            Object result = null;
            for (int i = 0; i < procedureMetaData.getParameterTypeSize(); i++) {
                if (result != null) {
                    throw new SRuntimeException("EDAO0010");
                }
                final ProcedureParameterType ppt = procedureMetaData
                        .getParameterType(i);
                if (isReturnOrOutType(ppt)) {
                    result = getValue(cs, ppt);
                }
            }
            return result;
        }
    }

}
