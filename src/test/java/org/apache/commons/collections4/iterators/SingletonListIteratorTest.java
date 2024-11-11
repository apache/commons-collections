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

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableListIterator;
import org.junit.jupiter.api.Test;

/**
 * Tests the SingletonListIterator.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class SingletonListIteratorTest<E> extends AbstractListIteratorTest<E> {

    private static final Object testValue = "foo";

    /**
     * Returns a SingletonListIterator from which
     * the element has already been removed.
     */
    @Override
    public SingletonListIterator<E> makeEmptyIterator() {
        final SingletonListIterator<E> iter = makeObject();
        iter.next();
        iter.remove();
        iter.reset();
        return iter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SingletonListIterator<E> makeObject() {
        return new SingletonListIterator<>((E) testValue);
    }

    @Override
    public boolean supportsAdd() {
        return false;
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
        final ListIterator<E> iter = makeObject();
        assertTrue(iter.hasNext(), "Iterator should have next item");
        assertFalse(iter.hasPrevious(), "Iterator should have no previous item");
        assertEquals(0, iter.nextIndex(), "Iteration next index");
        assertEquals(-1, iter.previousIndex(), "Iteration previous index");

        Object iterValue = iter.next();
        assertEquals(testValue, iterValue, "Iteration value is correct");

        assertFalse(iter.hasNext(), "Iterator should have no next item");
        assertTrue(iter.hasPrevious(), "Iterator should have previous item");
        assertEquals(1, iter.nextIndex(), "Iteration next index");
        assertEquals(0, iter.previousIndex(), "Iteration previous index");

        iterValue = iter.previous();
        assertEquals(testValue, iterValue, "Iteration value is correct");

        assertTrue(iter.hasNext(), "Iterator should have next item");
        assertFalse(iter.hasPrevious(), "Iterator should have no previous item");
        assertEquals(0, iter.nextIndex(), "Iteration next index");
        assertEquals(-1, iter.previousIndex(), "Iteration previous index");

        iterValue = iter.next();
        assertEquals(testValue, iterValue, "Iteration value is correct");

        assertFalse(iter.hasNext(), "Iterator should have no next item");
        assertTrue(iter.hasPrevious(), "Iterator should have previous item");
        assertEquals(1, iter.nextIndex(), "Iteration next index");
        assertEquals(0, iter.previousIndex(), "Iteration previous index");

        assertThrows(NoSuchElementException.class, iter::next);
        iter.previous();
        assertThrows(NoSuchElementException.class, iter::previous);
    }

    @Test
    public void testReset() {
        final ResettableListIterator<E> it = makeObject();

        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(testValue, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());

        it.reset();

        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(testValue, it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());

        it.reset();
        it.reset();

        assertTrue(it.hasNext());
    }

}

