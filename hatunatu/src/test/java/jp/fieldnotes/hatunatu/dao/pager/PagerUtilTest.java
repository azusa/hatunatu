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
package jp.fieldnotes.hatunatu.dao.pager;

import jp.fieldnotes.hatunatu.api.pager.PagerCondition;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PagerUtilTest {

    private List list;

    DefaultPagerCondition condition;

    @Before
    public void setUp() throws Exception {
        list = new ArrayList();
        for (int i = 0; i < 21; i++) {
            list.add(String.valueOf(i));
        }
        condition = new DefaultPagerCondition();
    }

    @Test
    public void testFilter1() {
        condition.setLimit(10);
        condition.setOffset(0);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(10, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i), result.get(i));
        }
    }

    @Test
    public void testFilter2() {
        condition.setLimit(10);
        condition.setOffset(10);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(10, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i + 10), result.get(i));
        }
    }

    @Test
    public void testFilter3() {
        condition.setLimit(10);
        condition.setOffset(20);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(1, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i + 20), result.get(i));
        }
    }

    @Test
    public void testFilter4() {
        condition.setLimit(PagerCondition.NONE_LIMIT);
        condition.setOffset(20);
        List result = PagerUtil.filter(list, condition);
        assertEquals(21, condition.getCount());
        assertEquals(21, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i), result.get(i));
        }
    }

    @Test
    public void testGetCurrentLastOffset() {
        condition.setLimit(10);
        condition.setOffset(0);
        condition.setCount(11);
        assertEquals(9, PagerUtil.getCurrentLastOffset(condition));

        condition.setOffset(1);
        assertEquals(10, PagerUtil.getCurrentLastOffset(condition));

        condition.setOffset(2);
        assertEquals(10, PagerUtil.getCurrentLastOffset(condition));
    }

}
