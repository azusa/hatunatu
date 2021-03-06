/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package jp.fieldnotes.hatunatu.util.collection;

import jp.fieldnotes.hatunatu.util.exception.SUnsupportedOperationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author wyukawa
 * 
 */
public class EmptyIteratorTest {

    /**
     * @see org.junit.rules.ExpectedException
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Test method for
     * {@link EmptyIterator#EmptyIterator()}.
     */
    @Test
    public void testEmptyIterator() {
        EmptyIterator<String> emptyIterator = new EmptyIterator<String>();
        assertThat(emptyIterator, is(notNullValue()));
    }

    /**
     * Test method for {@link EmptyIterator#remove()}
     * .
     */
    @Test
    public void testRemove() {
        exception.expect(SUnsupportedOperationException.class);
        exception.expectMessage(is("remove"));
        EmptyIterator<String> emptyIterator = new EmptyIterator<String>();
        emptyIterator.remove();
    }

    /**
     * Test method for
     * {@link EmptyIterator#hasNext()}.
     */
    @Test
    public void testHasNext() {
        EmptyIterator<String> emptyIterator = new EmptyIterator<String>();
        assertThat(emptyIterator.hasNext(), is(false));
    }

    /**
     * Test method for {@link EmptyIterator#next()}.
     */
    @Test
    public void testNext() {
        exception.expect(SUnsupportedOperationException.class);
        exception.expectMessage(is("next"));
        EmptyIterator<String> emptyIterator = new EmptyIterator<String>();
        emptyIterator.next();
    }

}
