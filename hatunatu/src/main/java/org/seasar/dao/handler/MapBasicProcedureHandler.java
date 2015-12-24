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
package org.seasar.dao.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dao.StatementFactory;
import org.seasar.dao.impl.BasicStatementFactory;
import org.seasar.dao.exception.SQLRuntimeException;
import jp.fieldnotes.hatunatu.util.sql.StatementUtil;

/**
 * @deprecated
 */
public class MapBasicProcedureHandler extends AbstractBasicProcedureHandler {

    public MapBasicProcedureHandler(DataSource ds, String procedureName) {
        this(ds, procedureName, BasicStatementFactory.INSTANCE);
    }

    public MapBasicProcedureHandler(DataSource ds, String procedureName,
            StatementFactory statementFactory) {
        setDataSource(ds);
        setProcedureName(procedureName);
        setStatementFactory(statementFactory);
    }

    public void initialize() {
        initTypes();
    }

    protected Object execute(Connection connection, Object[] args) {
        CallableStatement cs = null;
        try {
            cs = prepareCallableStatement(connection);
            bindArgs(cs, args);
            cs.execute();
            Map result = new HashMap();
            for (int i = 0; i < columnInOutTypes.length; i++) {
                if (isOutputColum(columnInOutTypes[i].intValue())) {
                    result.put(columnNames[i], cs.getObject(i + 1));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            StatementUtil.close(cs);
        }
    }
}
