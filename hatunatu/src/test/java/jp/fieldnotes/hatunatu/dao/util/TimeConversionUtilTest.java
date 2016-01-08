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
package jp.fieldnotes.hatunatu.dao.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import jp.fieldnotes.hatunatu.dao.util.TimeConversionUtil;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TimeConversionUtilTest  {

    @Test
    public void testToTime_emptyString() throws Exception {
        assertNull(TimeConversionUtil.toTime("", null));
    }

    @Test
    public void testToTime_default() throws Exception {
        assertEquals(new SimpleDateFormat("HH:mm:ss").parse("12:34:56"),
                TimeConversionUtil.toTime("12:34:56", null, Locale.JAPANESE));
    }

    @Test
    public void testToTime_short() throws Exception {
        assertEquals(new SimpleDateFormat("HH:mm:ss").parse("12:34:56"),
                TimeConversionUtil.toTime("123456", null, Locale.JAPANESE));
    }

    @Test
    public void testGetPattern() throws Exception {
        assertEquals("HH:mm:ss", TimeConversionUtil.getPattern(Locale.JAPANESE));
        assertEquals("HH:mm:ss", TimeConversionUtil.getPattern(Locale.ENGLISH));
        assertEquals("HH:mm:ss", TimeConversionUtil.getPattern(Locale.CHINESE));
        assertEquals("HH:mm:ss", TimeConversionUtil.getPattern(Locale.FRENCH));
    }
}