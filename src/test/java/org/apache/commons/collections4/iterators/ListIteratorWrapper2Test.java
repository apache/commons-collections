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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableListIterator;

/**
 * Tests the ListIteratorWrapper to insure that it behaves as expected when wrapping a ListIterator.
 *
 */
public class ListIteratorWrapper2Test<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<E> list1 = null;

    public ListIteratorWrapper2Test(final String testName) {
        super(testName);
    }

    @Override
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

    @Override
    public ResettableListIterator<E> makeEmptyIterator() {
        final ArrayList<E> list = new ArrayList<>();
        return new ListIteratorWrapper<>(list.listIterator());
    }

    @Override
    public ResettableListIterator<E> makeObject() {
        return new ListIteratorWrapper<>(list1.listIterator());
    }

    public void testIterator() {
        final ListIterator<E> iter = makeObject();
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertTrue(!iter.hasNext());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.next();
        });
        assertNull(exception.getMessage());

        // now, read it backwards
        for (int i = testArray.length - 1; i > -1; --i) {
            final Object testValue = testArray[i];
            final E iterValue = iter.previous();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        exception = assertThrows(NoSuchElementException.class, () -> {
            iter.previous();
        });
        assertNull(exception.getMessage());

        // now, read it forwards again
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

    }

    @Override
    public void testRemove() {
        final ListIterator<E> iter = makeObject();

        //initial state:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            iter.remove();
        });
        assertNull(exception.getMessage());

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

        exception = assertThrows(IllegalStateException.class, () -> {
            iter.remove();
        });
        assertNull(exception.getMessage());

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

        //this would dig into cache on a plain Iterator, but forwards directly to wrapped ListIterator:
        assertEquals(list1.get(0), iter.previous());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //here's the proof; remove() still works:
        iter.remove();
        assertEquals(--sz, list1.size());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //further testing would be fairly meaningless:
    }

    public void testReset() {
        final ResettableListIterator<E> iter = makeObject();
        final E first = iter.next();
        final E second = iter.next();

        iter.reset();

        // after reset, there shouldn't be any previous elements
        assertFalse("No previous elements after reset()", iter.hasPrevious());

        // after reset, the results should be the same as before
        assertEquals("First element should be the same", first, iter.next());
        assertEquals("Second elment should be the same", second, iter.next());

        // after passing the point, where we resetted, continuation should work as expected
        for (int i = 2; i < testArray.length; i++) {
            final Object testValue = testArray[i];
            final E iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }
    }

}
