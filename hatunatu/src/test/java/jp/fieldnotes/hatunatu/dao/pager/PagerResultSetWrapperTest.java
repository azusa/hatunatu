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
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PagerResultSetWrapperTest {

    @Test
    public void testNext() throws Exception {

        assertPaging(50, 20, 10, 51, 50);
        assertPaging(50, 45, 10, 51, 50);
        assertPaging(5, 0, 10, 6, 5);
        assertPaging(1, 0, 10, 2, 1);
    }

    private void assertPaging(int total, int offset, int limit,
                              int expectedNextCount, int expectedCount) throws Exception {
        MockResultSet original = new MockResultSet(total);
        PagerCondition condition = new DefaultPagerCondition();
        condition.setOffset(offset);
        condition.setLimit(limit);
        PagerResultSetWrapper wrapper = new PagerResultSetWrapper(original,
                condition, false);
        while (wrapper.next()) {
        }
        assertThat(original.getCallNextCount(), is(expectedNextCount));
        assertEquals(expectedCount, condition.getCount());
    }

}