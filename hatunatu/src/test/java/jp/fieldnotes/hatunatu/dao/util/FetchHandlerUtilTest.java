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
package jp.fieldnotes.hatunatu.dao.util;

import jp.fieldnotes.hatunatu.dao.impl.dto.EmployeeDto;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class FetchHandlerUtilTest  {



    /**
     * Test method for
     * {@link FetchHandlerUtil#isFetchHandler(java.lang.Class)}.
     */
    @Test
    public void testIsFetchHandler() {
        assertFalse(FetchHandlerUtil.isFetchHandler(null));
        assertFalse(FetchHandlerUtil.isFetchHandler(EmployeeDto.class));
    }

}
