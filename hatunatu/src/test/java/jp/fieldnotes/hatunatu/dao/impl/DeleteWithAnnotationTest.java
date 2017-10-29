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
package jp.fieldnotes.hatunatu.dao.impl;

import jp.fieldnotes.hatunatu.dao.impl.dao.Employee12Dao;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.dataset.DataTable;

import static org.junit.Assert.assertEquals;

public class DeleteWithAnnotationTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "DeleteWithAnnotationTest.xml");

    private Employee12Dao dao;

    @Test
    public void testDeleteTx() throws Exception {
        DataTable before = test.readDbByTable("EMP");
        dao.delete(7369);
        assertEquals(before.getRowSize() - 1, test.readDbByTable("EMP").getRowSize());
        dao.deleteNoWhere(7499);
        assertEquals(before.getRowSize() - 2, test.readDbByTable("EMP").getRowSize());
    }
}
