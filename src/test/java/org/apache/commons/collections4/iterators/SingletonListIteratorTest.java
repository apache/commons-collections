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

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableListIterator;
import org.junit.jupiter.api.Test;

/**
 * Tests the SingletonListIterator.
 *
 */
public class SingletonListIteratorTest<E> extends AbstractListIteratorTest<E> {

    private static final Object testValue = "foo";

    public SingletonListIteratorTest() {
        super(SingletonListIteratorTest.class.getSimpleName());
    }

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
    public boolean supportsRemove() {
        return true;
    }

    @Override
    public boolean supportsEmptyIterator() {
        return true;
    }

    @Test
    public void testIterator() {
        final ListIterator<E> iter = makeObject();
        assertTrue( "Iterator should have next item", iter.hasNext() );
        assertFalse("Iterator should have no previous item", iter.hasPrevious());
        assertEquals( "Iteration next index", 0, iter.nextIndex() );
        assertEquals( "Iteration previous index", -1, iter.previousIndex() );

        Object iterValue = iter.next();
        assertEquals( "Iteration value is correct", testValue, iterValue );

        assertFalse("Iterator should have no next item", iter.hasNext());
        assertTrue( "Iterator should have previous item", iter.hasPrevious() );
        assertEquals( "Iteration next index", 1, iter.nextIndex() );
        assertEquals( "Iteration previous index", 0, iter.previousIndex() );

        iterValue = iter.previous();
        assertEquals( "Iteration value is correct", testValue, iterValue );

        assertTrue( "Iterator should have next item", iter.hasNext() );
        assertFalse("Iterator should have no previous item", iter.hasPrevious());
        assertEquals( "Iteration next index", 0, iter.nextIndex() );
        assertEquals( "Iteration previous index", -1, iter.previousIndex() );

        iterValue = iter.next();
        assertEquals( "Iteration value is correct", testValue, iterValue );

        assertFalse("Iterator should have no next item", iter.hasNext());
        assertTrue( "Iterator should have previous item", iter.hasPrevious() );
        assertEquals( "Iteration next index", 1, iter.nextIndex() );
        assertEquals( "Iteration previous index", 0, iter.previousIndex() );

        try {
            iter.next();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), new NoSuchElementException().getClass());
        }
        iter.previous();
        try {
            iter.previous();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), new NoSuchElementException().getClass());
        }
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

