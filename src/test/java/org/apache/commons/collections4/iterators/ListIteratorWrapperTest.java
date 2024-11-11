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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableListIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ListIteratorWrapper to ensure that it simulates
 * a ListIterator correctly.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class ListIteratorWrapperTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<E> list1;

    @Override
    public ResettableListIterator<E> makeEmptyIterator() {
        final ArrayList<E> list = new ArrayList<>();
        return new ListIteratorWrapper<>(list.iterator());
    }

    @Override
    public ResettableListIterator<E> makeObject() {
        return new ListIteratorWrapper<>(list1.iterator());
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        list1 = new ArrayList<>();
        list1.add((E) "One");
        list1.add((E) "Two");
        list1.add((E) "Three");
        list1.add((E) "Four");
        list1.add((E) "Five");
        list1.add((E) "Six");
    }

    @Test
    public void testIterator() {
        final ListIterator<E> iter = makeObject();
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();
            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
        assertFalse(iter.hasNext(), "Iterator should now be empty");
        assertThrows(NoSuchElementException.class, iter::next);
        // now, read it backwards
        for (int i = testArray.length - 1; i > -1; --i) {
            final Object testValue = testArray[i];
            final E iterValue = iter.previous();
            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
        assertThrows(NoSuchElementException.class, iter::previous);
        // now, read it forwards again
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();
            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
    }

    @Test
    @Override
    public void testRemove() {
        final ListIterator<E> iter = makeObject();

        //initial state:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        assertThrows(IllegalStateException.class, () -> iter.remove(),
                "ListIteratorWrapper#remove() should fail; must be initially positioned first");

        //no change from invalid op:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //establish size:
        int sz = list1.size();

        //verify initial next() call:
        assertEquals(list1.get(0), iter.next());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        //verify remove():
        iter.remove();
        assertEquals(--sz, list1.size());
        //like we never started iterating:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        assertThrows(IllegalStateException.class, () -> iter.remove(),
                "ListIteratorWrapper#remove() should fail; must be repositioned first");

        //no change from invalid op:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //two consecutive next() calls:
        assertEquals(list1.get(0), iter.next());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        assertEquals(list1.get(1), iter.next());
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.nextIndex());

        //call previous():
        assertEquals(list1.get(1), iter.previous());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        //should support remove() after calling previous() once from tip because we haven't changed the underlying iterator's position:
        iter.remove();
        assertEquals(--sz, list1.size());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        //dig into cache
        assertEquals(list1.get(0), iter.previous());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        assertThrows(IllegalStateException.class, () -> iter.remove(),
                "ListIteratorWrapper does not support the remove() method while dug into the cache via previous()");

        //no change from invalid op:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //dig out of cache, first next() maintains current position:
        assertEquals(list1.get(0), iter.next());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());
        //continue traversing underlying iterator with this next() call, and we're out of the hole, so to speak:
        assertEquals(list1.get(1), iter.next());
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.nextIndex());

        //verify remove() works again:
        iter.remove();
        assertEquals(--sz, list1.size());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        assertEquals(list1.get(1), iter.next());
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.nextIndex());

    }

    @Test
    public void testReset() {
        final ResettableListIterator<E> iter = makeObject();
        final E first = iter.next();
        final E second = iter.next();

        iter.reset();

        // after reset, there shouldn't be any previous elements
        assertFalse(iter.hasPrevious(), "No previous elements after reset()");

        // after reset, the results should be the same as before
        assertEquals(first, iter.next(), "First element should be the same");
        assertEquals(second, iter.next(), "Second element should be the same");

        // after passing the point, where we reset, continuation should work as expected
        for (int i = 2; i < testArray.length; i++) {
            final Object testValue = testArray[i];
            final E iterValue = iter.next();

            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
    }

}
