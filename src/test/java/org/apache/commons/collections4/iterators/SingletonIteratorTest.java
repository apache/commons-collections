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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableIterator;
import org.junit.jupiter.api.Test;

/**
 * Tests the SingletonIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class SingletonIteratorTest<E> extends AbstractIteratorTest<E> {

    private static final Object testValue = "foo";

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
    public boolean supportsEmptyIterator() {
        return true;
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    @Test
    public void testIterator() {
        final Iterator<E> iter = makeObject();
        assertTrue(iter.hasNext(), "Iterator has a first item");
        final E iterValue = iter.next();
        assertEquals(testValue, iterValue, "Iteration value is correct");
        assertFalse(iter.hasNext(), "Iterator should now be empty");
        assertThrows(NoSuchElementException.class, iter::next);
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

}
