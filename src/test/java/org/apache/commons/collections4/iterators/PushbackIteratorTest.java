/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * Tests the PushbackIterator.
 *
 */
public class PushbackIteratorTest<E> extends AbstractIteratorTest<E> {

    private final String[] testArray = { "a", "b", "c" };

    private List<E> testList;

    public PushbackIteratorTest(final String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testList = new ArrayList<>(Arrays.asList((E[]) testArray));
    }

    @Override
    public Iterator<E> makeEmptyIterator() {
        return PushbackIterator.pushbackIterator(Collections.<E>emptyList().iterator());
    }

    @Override
    public PushbackIterator<E> makeObject() {
        return PushbackIterator.pushbackIterator(testList.iterator());
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    // -----------------------------------------------------------------------

    @Test
    public void testNormalIteration() {
        final PushbackIterator<E> iter = makeObject();
        assertEquals("a", iter.next());
        assertEquals("b", iter.next());
        assertEquals("c", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testImmediatePushback() {
        final PushbackIterator<E> iter = makeObject();
        iter.pushback((E) "x");
        assertEquals("x", iter.next());
        assertEquals("a", iter.next());
        validate(iter, "b", "c");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDelayedPushback() {
        final PushbackIterator<E> iter = makeObject();
        assertEquals("a", iter.next());
        iter.pushback((E) "x");
        assertEquals("x", iter.next());
        assertEquals("b", iter.next());
        validate(iter, "c");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiplePushback() {
        final PushbackIterator<E> iter = makeObject();
        assertEquals("a", iter.next());
        iter.pushback((E) "x");
        iter.pushback((E) "y");
        assertEquals("y", iter.next());
        assertEquals("x", iter.next());
        assertEquals("b", iter.next());
        validate(iter, "c");
    }

    private void validate(final Iterator<E> iter, final Object... items) {
        for (final Object x : items) {
            assertTrue(iter.hasNext());
            assertEquals(x, iter.next());
        }
        assertFalse(iter.hasNext());
    }

}
