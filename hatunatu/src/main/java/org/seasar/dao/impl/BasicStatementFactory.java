/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.seasar.dao.StatementFactory;
import org.seasar.dao.util.ConnectionUtil;

/**
 * {@link StatementFactory}の基本的な実装クラスです。
 * 
 * @author higa
 * 
 */
public class BasicStatementFactory implements StatementFactory {

    /**
     * 自分自身のインスタンスです。
     */
    public static final StatementFactory INSTANCE = new BasicStatementFactory();

    public PreparedStatement createPreparedStatement(Connection con, String sql) {
        return ConnectionUtil.prepareStatement(con, sql);
    }

    public CallableStatement createCallableStatement(Connection con, String sql) {
        return ConnectionUtil.prepareCall(con, sql);
    }
}
