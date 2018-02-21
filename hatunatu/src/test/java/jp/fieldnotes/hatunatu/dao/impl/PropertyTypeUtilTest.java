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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.api.PropertyType;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

public class PropertyTypeUtilTest {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @After
    public void tearDown() throws Exception {
        PropertyTypeUtil.setPreserveUnderscore(false);
    }


    @Test
    public void testCreatePropertyTypes() throws Exception {

        try (PreparedStatement ps = test.getConnection().prepareStatement("select d_name, active from dept3"); ResultSet rs = ps.executeQuery()) {
            PropertyType[] propertyTypes = PropertyTypeUtil
                    .createPropertyTypes(rs.getMetaData());
            assertEquals(2, propertyTypes.length);
            PropertyType p = propertyTypes[0];
            assertEquals("dname", p.getPropertyName().toLowerCase());
            assertEquals("d_name", p.getColumnName().toLowerCase());
            p = propertyTypes[1];
            assertEquals("active", p.getPropertyName().toLowerCase());
            assertEquals("active", p.getColumnName().toLowerCase());
        }

    }

    @Test
    public void testCreatePropertyTypes_preserveUnderscore() throws Exception {
        PropertyTypeUtil.setPreserveUnderscore(true);

        try (PreparedStatement ps = test.getConnection().prepareStatement("select d_name, active from dept3"); ResultSet rs = ps.executeQuery()) {
                PropertyType[] propertyTypes = PropertyTypeUtil
                        .createPropertyTypes(rs.getMetaData());
                assertEquals(2, propertyTypes.length);
                PropertyType p = propertyTypes[0];
                assertEquals("d_name", p.getPropertyName().toLowerCase());
                assertEquals("d_name", p.getColumnName().toLowerCase());
                p = propertyTypes[1];
                assertEquals("active", p.getPropertyName().toLowerCase());
                assertEquals("active", p.getColumnName().toLowerCase());
        }
    }

}
