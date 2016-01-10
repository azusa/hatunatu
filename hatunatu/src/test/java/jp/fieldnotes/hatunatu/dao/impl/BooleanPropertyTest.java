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

import jp.fieldnotes.hatunatu.api.DaoMetaData;
import jp.fieldnotes.hatunatu.api.SqlCommand;
import jp.fieldnotes.hatunatu.dao.command.SelectDynamicCommand;
import jp.fieldnotes.hatunatu.dao.impl.bean.Department2;
import jp.fieldnotes.hatunatu.dao.impl.dao.Department2AutoDao;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import jp.fieldnotes.hatunatu.dao.unit.S2DaoTestCase;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanPropertyTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this);

    @Test
    public void testInsertAndSelectTx() throws Exception {
        DaoMetaData dmd = test.createDaoMetaData(Department2AutoDao.class);
        SqlCommand cmd = dmd.getSqlCommand(test.getSingleDaoMethod(Department2AutoDao.class,"insert"));
        Department2 dept = new Department2();
        dept.setDeptno(99);
        dept.setDname("hoge");
        dept.setActive(true);
        cmd.execute(new Object[] { dept });
        SelectDynamicCommand cmd2 = (SelectDynamicCommand) dmd
                .getSqlCommand(test.getSingleDaoMethod(Department2AutoDao.class,"getDepartment"));
        Department2 dept2 = (Department2) cmd2
                .execute(new Object[] { new Integer(99) });
        assertEquals("1", true, dept2.isActive());
    }

}
