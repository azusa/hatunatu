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
package jp.fieldnotes.hatunatu.util.misc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author higa
 * 
 */
public class Base64UtilTest {

    private static final String ORIGINAL = "how now brown cow\r\n";

    private static final byte[] BINARY_DATA = ORIGINAL.getBytes();

    private static final String ENCODED_DATA = "aG93IG5vdyBicm93biBjb3cNCg==";

    @Test
    public void testEncode() throws Exception {
        assertEquals("1", ENCODED_DATA, Base64Util.encode(BINARY_DATA));
        System.out.println(Base64Util.encode(new byte[] { 'a', 'b', 'c' }));
    }

    @Test
    public void testDecode() throws Exception {
        byte[] decodedData = Base64Util.decode(ENCODED_DATA);
        assertEquals("1", BINARY_DATA.length, decodedData.length);
        for (int i = 0; i < decodedData.length; i++) {
            assertEquals("2", BINARY_DATA[i], decodedData[i]);
        }
    }
}
