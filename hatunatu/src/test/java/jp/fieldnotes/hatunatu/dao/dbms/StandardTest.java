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

import jp.fieldnotes.hatunatu.api.BeanMetaData;
import jp.fieldnotes.hatunatu.dao.Dbms;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import jp.fieldnotes.hatunatu.util.exception.SRuntimeException;
import jp.fieldnotes.hatunatu.util.misc.DisposableUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class StandardTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Before
    public void setUp() throws Exception {
        Dbms dbms = new Standard();
        test.setDbms(dbms);
    }

    @Test
    public void testCreateAutoSelectList() throws Exception {
        BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        String sql = test.getDbms().getAutoSelectSql(bmd);
        System.out.println(sql);
    }

    @Test
    public void testDispose() throws Exception {
        final Standard standard = new Standard();
        test.setDbms(standard);
        assertEquals(0, standard.autoSelectFromClauseCache.size());

        final BeanMetaData bmd = test.createBeanMetaData(Employee.class);
        {
            final String sql = standard.getAutoSelectSql(bmd);
            assertNotNull(sql);
        }

        assertEquals(1, standard.autoSelectFromClauseCache.size());
        DisposableUtil.dispose();
        assertEquals(0, standard.autoSelectFromClauseCache.size());
        {
            final String sql = standard.getAutoSelectSql(bmd);
            assertNotNull(sql);
        }
        assertEquals(1, standard.autoSelectFromClauseCache.size());
        DisposableUtil.dispose();
        assertEquals(0, standard.autoSelectFromClauseCache.size());
    }

    @Test
    public void testGetIdentitySelectString() throws Exception {
        try {
            test.getDbms().getIdentitySelectString();
            fail();
        } catch (SRuntimeException e) {
            assertEquals("EDAO0022", e.getMessageCode());
            System.out.println(e);
        }
    }

    @Test
    public void testGetSequenceNextValString() throws Exception {
        try {
            test.getDbms().getSequenceNextValString(null);
            fail();
        } catch (SRuntimeException e) {
            assertEquals("EDAO0022", e.getMessageCode());
            System.out.println(e);
        }
    }
}