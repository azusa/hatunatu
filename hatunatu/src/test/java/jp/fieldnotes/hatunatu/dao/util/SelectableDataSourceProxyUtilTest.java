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
package jp.fieldnotes.hatunatu.dao.util;

import org.junit.Test;
import org.seasar.extension.datasource.impl.DataSourceFactoryImpl;
import org.seasar.extension.datasource.impl.SelectableDataSourceProxy;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SelectableDataSourceProxyUtilTest {

    @Test
    public void testDataSource() throws Exception {
        DataSource dataSource = new MyDataSource();
        assertNull(SelectableDataSourceProxyUtil
                .getSelectableDataSourceName(dataSource));
    }

    @Test
    public void testSelectableDataSourceProxy() throws Exception {
        DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();
        SelectableDataSourceProxy proxy = new SelectableDataSourceProxy();
        proxy.setDataSourceFactory(dataSourceFactory);

        dataSourceFactory.setSelectableDataSourceName("hoge");
        assertEquals("hoge", SelectableDataSourceProxyUtil
                .getSelectableDataSourceName(proxy));

        dataSourceFactory.setSelectableDataSourceName("foo");
        assertEquals("foo", SelectableDataSourceProxyUtil
                .getSelectableDataSourceName(proxy));
    }

    public static class MyDataSource implements DataSource {

        public Connection getConnection() throws SQLException {
            return null;
        }

        public Connection getConnection(String username, String password)
                throws SQLException {
            return null;
        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLoginTimeout(int seconds) throws SQLException {
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }
    }
}
