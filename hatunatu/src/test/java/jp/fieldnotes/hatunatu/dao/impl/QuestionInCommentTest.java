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

import java.util.List;

import jp.fieldnotes.hatunatu.dao.impl.bean.Employee;
import jp.fieldnotes.hatunatu.dao.unit.HatunatuTest;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.extension.unit.S2TestCase;

import static org.junit.Assert.assertTrue;

public class QuestionInCommentTest  {

    @Rule
    public HatunatuTest test = new HatunatuTest(this, "QuestionInCommentTest.dicon");

    private QuestionInCommentDao dao;

    //[DAO-72]
    @Test
    public void testInsertByManualSql2Tx() throws Exception {
        Employee emp = new Employee();
        emp.setEmpno(2222);
        emp.setEname("aaaaaaaaaa");
        dao.insertBySql(emp);
        assertTrue(true);
    }

    @Test
    public void testQuestionInCommentTx(){
        dao.questionInQuote("'te?st'");
        assertTrue(true);
    }

    public static interface QuestionInCommentDao {

        public void insertBySql(Employee emp);

        List<Employee> questionInQuote(String arg);
    }

}