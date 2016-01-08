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
package jp.fieldnotes.hatunatu.dao.types;

import java.sql.SQLException;

import jp.fieldnotes.hatunatu.dao.types.OracleResultSetType;
import junit.framework.TestCase;

import org.junit.Test;
import org.seasar.framework.mock.sql.MockCallableStatement;
import org.seasar.framework.mock.sql.MockResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OracleResultSetTypeTest {

    private boolean gotObject;

    @Test
    public void testGetValueCallableStatementInt() throws Exception {
        MockCallableStatement cs = new MockCallableStatement(null, null) {
            public Object getObject(int parameterIndex) throws SQLException {
                gotObject = true;
                return new MockResultSet();
            }
        };
        OracleResultSetType rsType = new OracleResultSetType();
        assertNotNull(rsType.getValue(cs, 1));
        assertTrue(gotObject);
    }

    @Test
    public void testGetValueCallableStatementString() throws Exception {
        MockCallableStatement cs = new MockCallableStatement(null, null) {
            public Object getObject(String parameterName) throws SQLException {
                gotObject = true;
                return new MockResultSet();
            }
        };
        OracleResultSetType rsType = new OracleResultSetType();
        assertNotNull(rsType.getValue(cs, "hoge"));
        assertTrue(gotObject);
    }

    @Test
    public void testSqlType() {
        OracleResultSetType rsType = new OracleResultSetType();
        assertEquals(OracleResultSetType.CURSOR, rsType.getSqlType());
    }

    /**
     * 
     */
    public void testToText() {
        OracleResultSetType rsType = new OracleResultSetType();
        assertEquals("null", rsType.toText(null));
    }
}
