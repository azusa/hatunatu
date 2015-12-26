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
package jp.fieldnotes.hatunatu.dao.mock;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import junit.framework.AssertionFailedError;

/**
 * @author manhole
 */
public class NullConnection implements Connection {

    public Statement createStatement() throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new AssertionFailedError();
    }

    public String nativeSQL(final String sql) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean getAutoCommit() throws SQLException {
        throw new AssertionFailedError();
    }

    public void commit() throws SQLException {
        throw new AssertionFailedError();
    }

    public void rollback() throws SQLException {
        throw new AssertionFailedError();
    }

    public void close() throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean isClosed() throws SQLException {
        throw new AssertionFailedError();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setReadOnly(final boolean readOnly) throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean isReadOnly() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setCatalog(final String catalog) throws SQLException {
        throw new AssertionFailedError();
    }

    public String getCatalog() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setTransactionIsolation(final int level) throws SQLException {
        throw new AssertionFailedError();
    }

    public int getTransactionIsolation() throws SQLException {
        throw new AssertionFailedError();
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new AssertionFailedError();
    }

    public void clearWarnings() throws SQLException {
        throw new AssertionFailedError();
    }

    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public Map getTypeMap() throws SQLException {
        throw new AssertionFailedError();
    }

    public void setTypeMap(final Map arg0) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setHoldability(final int holdability) throws SQLException {
        throw new AssertionFailedError();
    }

    public int getHoldability() throws SQLException {
        throw new AssertionFailedError();
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new AssertionFailedError();
    }

    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new AssertionFailedError();
    }

    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new AssertionFailedError();
    }

    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        throw new AssertionFailedError();
    }

    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        throw new AssertionFailedError();
    }

    public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int autoGeneratedKeys) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final int[] columnIndexes) throws SQLException {
        throw new AssertionFailedError();
    }

    public PreparedStatement prepareStatement(final String sql,
            final String[] columnNames) throws SQLException {
        throw new AssertionFailedError();
    }

    public Clob createClob() throws SQLException {
        throw new AssertionFailedError();
    }

    public Blob createBlob() throws SQLException {
        throw new AssertionFailedError();
    }

    public NClob createNClob() throws SQLException {
        throw new AssertionFailedError();
    }

    public SQLXML createSQLXML() throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean isValid(int timeout) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new AssertionFailedError();
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new AssertionFailedError();
    }

    public String getClientInfo(String name) throws SQLException {
        throw new AssertionFailedError();
    }

    public Properties getClientInfo() throws SQLException {
        throw new AssertionFailedError();
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new AssertionFailedError();
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setSchema(String schema) throws SQLException {
        throw new AssertionFailedError();
    }

    public String getSchema() throws SQLException {
        throw new AssertionFailedError();
    }

    public void abort(Executor executor) throws SQLException {
        throw new AssertionFailedError();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new AssertionFailedError();
    }

    public int getNetworkTimeout() throws SQLException {
        throw new AssertionFailedError();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new AssertionFailedError();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new AssertionFailedError();
    }
}
