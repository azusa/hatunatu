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
import jp.fieldnotes.hatunatu.dao.ResultSetFactory;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class PagerResultSetFactoryWapperTest {

    ResultSetFactory original;

    PagerResultSetFactoryWrapper wrapper;

    ResultSet expect;

    @Before
    public void setUp() throws Exception {
        original = mock(ResultSetFactory.class);
        wrapper = new PagerResultSetFactoryWrapper(original);
        expect = mock(ResultSet.class);
        when(original.createResultSet(anyObject(), anyObject())).thenReturn(expect);

    }


    @Test
    public void testCreateResultSetNotPagerCondition() throws Exception {
        ResultSet resultSet = wrapper.createResultSet(null, createNormalArgs());
        verify(original).createResultSet(anyObject(), anyObject());
        assertThat(resultSet, is(sameInstance(expect)));
    }

    @Test
    public void testCreateResultSetPagerCondition() throws Exception {
        ResultSet resultSet = wrapper.createResultSet(null, createPagerConditionArgs());
        verify(original).createResultSet(anyObject(), anyObject());
        assertThat(resultSet, is(instanceOf(PagerResultSetWrapper.class)));
    }

    @Test
    public void testCreateResultSetPagerConditionNoneLimit() throws Exception {
        ResultSet resultSet = wrapper.createResultSet(null, createPagerConditionArgsNoneLimit());
        verify(original).createResultSet(anyObject(), anyObject());
        assertThat(resultSet, is(sameInstance(expect)));
    }


    private Object[] createNormalArgs() {
        return new Object[]{};
    }

    private Object[] createPagerConditionArgs() {
        DefaultPagerCondition pagerConditionBase = new DefaultPagerCondition();
        pagerConditionBase.setLimit(10);
        return new Object[]{pagerConditionBase};
    }

    private Object[] createPagerConditionArgsNoneLimit() {
        DefaultPagerCondition pagerConditionBase = new DefaultPagerCondition();
        pagerConditionBase.setLimit(PagerCondition.NONE_LIMIT);
        return new Object[]{pagerConditionBase};
    }

}
