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

import static org.junit.Assert.assertEquals;

public class PagerContextTest {

    @Test
    public void testIsPagerCondition() {
        PagerCondition pagerConderion = new DefaultPagerCondition();
        pagerConderion.setLimit(10);
        assertEquals(true, PagerContext
                .isPagerCondition(new Object[] { pagerConderion }));
        assertEquals(true, PagerContext.isPagerCondition(new Object[] {
                pagerConderion, "dummy" }));
        assertEquals(false, PagerContext
                .isPagerCondition(new Object[] { "dummy" }));
        pagerConderion.setLimit(PagerCondition.NONE_LIMIT);
        pagerConderion.setOffset(0);
        assertEquals(false, PagerContext
                .isPagerCondition(new Object[] { pagerConderion }));
        pagerConderion.setLimit(PagerCondition.NONE_LIMIT);
        pagerConderion.setOffset(10);
        assertEquals(true, PagerContext
                .isPagerCondition(new Object[] { pagerConderion }));

    }

    @Test
    public void testGetPagerCondition() {
        PagerCondition condition = new DefaultPagerCondition();
        PagerCondition condition2 = PagerContext
                .getPagerCondition(new Object[] { "dummy", condition, "dummy" });
        assertEquals(true, condition == condition2);
    }

}
