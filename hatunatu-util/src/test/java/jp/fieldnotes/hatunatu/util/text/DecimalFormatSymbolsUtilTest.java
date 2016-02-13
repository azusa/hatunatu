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
package jp.fieldnotes.hatunatu.util.text;

import org.junit.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author higa
 * 
 */
public class DecimalFormatSymbolsUtilTest {

    @Test
    public void testGetDecimalFormatSymbols() throws Exception {
        DecimalFormatSymbols symbols =
            DecimalFormatSymbolsUtil.getDecimalFormatSymbols(Locale.GERMAN);
        System.out.println("DecimalSeparator:" + symbols.getDecimalSeparator());
        System.out.println("GroupingSeparator:"
            + symbols.getGroupingSeparator());
    }
}
