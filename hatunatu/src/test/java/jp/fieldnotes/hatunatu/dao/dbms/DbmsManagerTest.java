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
package jp.fieldnotes.hatunatu.dao.dbms;

import junit.framework.TestCase;

/**
 * @author higa
 * @author manhole
 */
public class DbmsManagerTest extends TestCase {

    public void testCreateAutoSelectList() throws Exception {
        assertTrue("1", DbmsManager.getDbms("") instanceof Standard);
        assertTrue("2",
                DbmsManager.getDbms("HSQL Database Engine") instanceof HSQL);
        assertTrue("3", DbmsManager.getDbms("AAAAAAAAAAA") instanceof Standard);
    }

    public void testGetDbmsByProductName() throws Exception {
        // https://www.seasar.org/issues/browse/DAO-68
        assertEquals(true, DbmsManager.getDbms("DB2/AIX64") instanceof DB2);
        assertEquals(true,
                DbmsManager.getDbms("DB2 UDB for AS/400") instanceof DB2);
    }

}
