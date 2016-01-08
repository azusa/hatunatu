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

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.fieldnotes.hatunatu.dao.types.CharacterType;
import junit.framework.TestCase;

import jp.fieldnotes.hatunatu.dao.impl.ResultSetWrapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CharacterTypeTest  {

    @Test
    public void testGetValue() throws Exception {
        // ## Arrange ##
        CharacterType type = new CharacterType();
        final ResultSet resultSet = new MockResultSet() {
            public String getString(int columnIndex) throws SQLException {
                return "v";
            }
        };

        // ## Act ##
        final Object value = type.getValue(resultSet, 0);

        // ## Assert ##
        assertEquals(new Character('v'), value);
    }

    @Test
    public void testGetValue_TooLongString() throws Exception {
        // ## Arrange ##
        CharacterType type = new CharacterType();
        final ResultSet resultSet = new MockResultSet() {
            public String getString(int columnIndex) throws SQLException {
                return "12";
            }
        };

        // ## Act ##
        // ## Assert ##
        try {
            type.getValue(resultSet, 0);
            fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testGetValue_TooShortString() throws Exception {
        // ## Arrange ##
        CharacterType type = new CharacterType();
        final ResultSet resultSet = new MockResultSet() {
            public String getString(int columnIndex) throws SQLException {
                return "";
            }
        };

        // ## Act ##
        final Object value = type.getValue(resultSet, 0);

        // ## Assert ##
        assertEquals(null, value);
    }

    @Test
    public void testGetValue_NullString() throws Exception {
        // ## Arrange ##
        CharacterType type = new CharacterType();
        final ResultSet resultSet = new MockResultSet() {
            public String getString(int columnIndex) throws SQLException {
                return null;
            }
        };

        // ## Act ##
        final Object value = type.getValue(resultSet, 0);

        // ## Assert ##
        assertEquals(null, value);
    }

    private static class MockResultSet extends ResultSetWrapper {
        MockResultSet() {
            super(null);
        }
    }

}
