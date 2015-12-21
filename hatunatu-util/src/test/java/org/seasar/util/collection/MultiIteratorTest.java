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
package org.seasar.util.collection;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import static java.util.Arrays.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author koichik
 * 
 */
public class MultiIteratorTest {

    /**
     * 
     */
    @Test
    public void test() {
        List<String> list1 = asList("Foo", "Bar");
        List<String> list2 = asList("Baz");

        @SuppressWarnings("unchecked")
        Iterator<String> it =
            new MultiIterator<String>(list1.iterator(), list2.iterator());

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is("Foo"));

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is("Bar"));

        assertThat(it.hasNext(), is(true));
        assertThat(it.next(), is("Baz"));

        assertThat(it.hasNext(), is(not(true)));
    }

}
