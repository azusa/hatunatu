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

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import jp.fieldnotes.hatunatu.dao.types.OracleResultSetType;
import jp.fieldnotes.hatunatu.dao.types.PostgreResultSetType;
import junit.framework.TestCase;

import org.junit.Test;
import org.seasar.framework.mock.sql.MockCallableStatement;
import org.seasar.framework.mock.sql.MockResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostgreResultSetTypeTest {

    @Test
    public void testGetValueCallableStatementInt() throws Exception {
        CallableStatement cs = mock(CallableStatement.class);
        when(cs.getObject(1)).thenReturn(new Object());
        PostgreResultSetType rsType = new PostgreResultSetType();
        assertNotNull(rsType.getValue(cs, 1));
        verify(cs).getObject(1);
    }

    @Test
    public void testSqlType() {
        PostgreResultSetType rsType = new PostgreResultSetType();
        assertEquals(Types.OTHER, rsType.getSqlType());
    }

    @Test
    public void testToText() {
        OracleResultSetType rsType = new OracleResultSetType();
        assertEquals("null", rsType.toText(null));
    }
}
