/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * Tests the PeekingIterator.
 *
 * @version $Id$
 */
public class PeekingIteratorTest<E> extends AbstractIteratorTest<E> {

    private String[] testArray = { "a", "b", "c" };

    private List<E> testList;
    
    public PeekingIteratorTest(final String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testList = new ArrayList<E>(Arrays.asList((E[]) testArray));
    }

    @Override
    public Iterator<E> makeEmptyIterator() {
        return PeekingIterator.peekingIterator(Collections.<E>emptyList().iterator());
    }

    @Override
    public PeekingIterator<E> makeObject() {
        return PeekingIterator.peekingIterator(testList.iterator());
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    //-----------------------------------------------------------------------
    
    @Test
    public void testEmpty() {
        Iterator<E> it = makeEmptyIterator();
        assertFalse(it.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSinglePeek() {
        PeekingIterator<E> it = makeObject();
        assertEquals("a", it.peek());
        assertEquals("a", it.element());
        validate(it, (E[]) testArray);
    }

    @Test
    public void testMultiplePeek() {
        PeekingIterator<E> it = makeObject();
        assertEquals("a", it.peek());
        assertEquals("a", it.peek());
        assertEquals("a", it.next());
        assertTrue(it.hasNext());
        assertEquals("b", it.peek());
        assertEquals("b", it.peek());
        assertEquals("b", it.next());
        assertTrue(it.hasNext());
        assertEquals("c", it.peek());
        assertEquals("c", it.peek());
        assertEquals("c", it.next());
        assertFalse(it.hasNext());
    }
    
    @Test
    public void testIteratorExhausted() {
        PeekingIterator<E> it = makeObject();
        it.next();
        it.next();
        it.next();
        assertFalse(it.hasNext());
        assertNull(it.peek());
        
        try {
            it.element();
            fail();
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testIllegalRemove() {
        PeekingIterator<E> it = makeObject();
        it.next();
        it.remove(); // supported
        
        assertTrue(it.hasNext());
        assertEquals("b", it.peek());
        
        try {
            it.remove();
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private void validate(Iterator<E> iter, E... items) {
        for (E x : items) {
            assertTrue(iter.hasNext());
            assertEquals(x, iter.next());
        }
        assertFalse(iter.hasNext());
    }

}
