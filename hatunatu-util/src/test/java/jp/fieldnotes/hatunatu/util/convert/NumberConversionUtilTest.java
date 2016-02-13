/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.util.convert;

import org.junit.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * @author shot
 * 
 */
public class NumberConversionUtilTest {

    @Test
    public void testConvertNumber_byte() throws Exception {
        assertEquals(
            new Byte("1"),
            NumberConversionUtil.convertNumber(Byte.class, "1"));
    }

    @Test
    public void testConvertNumber_primitiveWrapper() throws Exception {
        assertEquals(
            new Byte("1"),
            NumberConversionUtil.convertPrimitiveWrapper(byte.class, "1"));
        assertEquals(
            new Byte("0"),
            NumberConversionUtil.convertPrimitiveWrapper(byte.class, null));
    }

    @Test
    public void testFindFractionDelimeter() throws Exception {
        String delim = NumberConversionUtil.findDecimalSeparator(Locale.JAPAN);
        assertEquals(".", delim);
    }

    @Test
    public void testFindFractionDelimeter2() throws Exception {
        String delim = NumberConversionUtil.findDecimalSeparator(Locale.FRANCE);
        assertEquals(",", delim);
    }

    @Test
    public void testFindFractionDelimeter3() throws Exception {
        String delim = NumberConversionUtil.findDecimalSeparator(null);
        char c =
            new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
        assertEquals(Character.toString(c), delim);
    }

    @Test
    public void testFindIntegerDelimeter() throws Exception {
        String delim = NumberConversionUtil.findGroupingSeparator(Locale.JAPAN);
        assertEquals(",", delim);
    }

    @Test
    public void testFindIntegerDelimeter2() throws Exception {
        String delim =
            NumberConversionUtil.findGroupingSeparator(Locale.GERMANY);
        assertEquals(".", delim);
    }

    @Test
    public void testFindIntegerDelimeter3() throws Exception {
        String delim = NumberConversionUtil.findGroupingSeparator(null);
        char c =
            new DecimalFormatSymbols(Locale.getDefault())
                .getGroupingSeparator();
        assertEquals(Character.toString(c), delim);
    }

    @Test
    public void testRemoveDelimeter() throws Exception {
        assertEquals(
            "1000000.234",
            NumberConversionUtil.removeDelimeter("1,000,000.234", Locale.JAPAN));
    }
}
