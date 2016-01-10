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

import org.junit.Ignore;
import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;

public class TimestampTypeTest {

    private TimestampType timestampType = new TimestampType();

    @Test
    @Ignore
    public void testToText() throws Exception {
        Timestamp timestamp = Timestamp.valueOf("2007-11-29 13:14:15");
        assertEquals(timestamp, timestampType
                .toTimestamp("2007/11/29 13:14:15"));
        
        timestamp = Timestamp.valueOf("2010-03-19 00:00:00");
        assertEquals(timestamp, timestampType.toTimestamp("2010/03/19"));
    }

}
