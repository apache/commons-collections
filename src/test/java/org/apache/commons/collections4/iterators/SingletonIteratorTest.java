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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableIterator;
import org.junit.jupiter.api.Test;

/**
 * Tests the SingletonIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 *
 */
public class SingletonIteratorTest<E> extends AbstractIteratorTest<E> {

    private static final Object testValue = "foo";

    public SingletonIteratorTest() {
        super(SingletonIteratorTest.class.getSimpleName());
    }

    /**
     * Returns a SingletonIterator from which
     * the element has already been removed.
     */
    @Override
    public SingletonIterator<E> makeEmptyIterator() {
        final SingletonIterator<E> iter = makeObject();
        iter.next();
        iter.remove();
        iter.reset();
        return iter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SingletonIterator<E> makeObject() {
        return new SingletonIterator<>((E) testValue);
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    @Override
    public boolean supportsEmptyIterator() {
        return true;
    }

    @Test
    public void testIterator() {
        final Iterator<E> iter = makeObject();
        assertTrue("Iterator has a first item", iter.hasNext());

        final E iterValue = iter.next();
        assertEquals("Iteration value is correct", testValue, iterValue);

        assertFalse("Iterator should now be empty", iter.hasNext());

        try {
            iter.next();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), new NoSuchElementException().getClass());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingletonIteratorRemove() {
        final ResettableIterator<E> iter = new SingletonIterator<>((E) "xyzzy");
        assertTrue(iter.hasNext());
        assertEquals("xyzzy", iter.next());
        iter.remove();
        iter.reset();
        assertFalse(iter.hasNext());
    }

    @Test
    public void testReset() {
        final ResettableIterator<E> it = makeObject();

        assertTrue(it.hasNext());
        assertEquals(testValue, it.next());
        assertFalse(it.hasNext());

        it.reset();

        assertTrue(it.hasNext());
        assertEquals(testValue, it.next());
        assertFalse(it.hasNext());

        it.reset();
        it.reset();

        assertTrue(it.hasNext());
    }

}
