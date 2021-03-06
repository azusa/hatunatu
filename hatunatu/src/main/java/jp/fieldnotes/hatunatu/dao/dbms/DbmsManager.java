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
package jp.fieldnotes.hatunatu.dao.dbms;

import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.exception.ReflectiveOperationRuntimeException;
import jp.fieldnotes.hatunatu.dao.util.ConnectionUtil;
import jp.fieldnotes.hatunatu.dao.util.DataSourceUtil;
import jp.fieldnotes.hatunatu.dao.util.DatabaseMetaDataUtil;
import jp.fieldnotes.hatunatu.util.io.ResourceUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public final class DbmsManager {

    private static Properties dbmsClassNames;

    private static Map dbmsInstances = new HashMap();

    static {
        dbmsClassNames = ResourceUtil.getProperties("dbms.properties");
    }

    private DbmsManager() {
    }

    public static Dbms getDbms(DataSource dataSource) throws SQLException {
        Dbms dbms = null;
        try (Connection con = DataSourceUtil.getConnection(dataSource)){
            DatabaseMetaData dmd = ConnectionUtil.getMetaData(con);
            dbms = getDbms(dmd);
        }
        return dbms;
    }

    public static Dbms getDbms(DatabaseMetaData dmd) {
        return getDbms(DatabaseMetaDataUtil.getDatabaseProductName(dmd));
    }

    public static Dbms getDbms(String productName) {
        Dbms dbms = (Dbms) dbmsInstances.get(productName);
        if (dbms == null) {
            String className = dbmsClassNames.getProperty("");
            for (Iterator i = dbmsClassNames.keySet().iterator(); i.hasNext();) {
                String productPrefix = (String) i.next();
                if (productName.startsWith(productPrefix)) {
                    className = dbmsClassNames.getProperty(productPrefix);
                    break;
                }
            }
            try {
                dbms = (Dbms) Class.forName(className).newInstance();
                dbmsInstances.put(productName, dbms);
            } catch (ReflectiveOperationException e) {
                throw new ReflectiveOperationRuntimeException(e);
            }

        }
        return dbms;

    }
}