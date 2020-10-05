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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Tests the LoopingListIterator class.
 *
 */
public class LoopingListIteratorTest {

    /**
     * Tests constructor exception.
     */
    @Test
    public void testConstructorEx() throws Exception {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new LoopingListIterator<>(null);
        });
        assertTrue(exception.getMessage().contains("collection"));
    }

    /**
     * Tests whether an empty looping list iterator works.
     */
    @Test
    public void testLooping0() throws Exception {
        final List<Object> list = new ArrayList<>();
        final LoopingListIterator<Object> loop = new LoopingListIterator<>(list);
        assertFalse(loop.hasNext());
        assertFalse(loop.hasPrevious());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            loop.next();
        });
        assertTrue(exception.getMessage().contains("There are no elements for this iterator to loop on"));

        exception = assertThrows(NoSuchElementException.class, () -> {
            loop.previous();
        });
        assertTrue(exception.getMessage().contains("There are no elements for this iterator to loop on"));
    }

    /**
     * Tests whether a looping list iterator works on a list with only
     * one element.
     */
    @Test
    public void testLooping1() throws Exception {
        final List<String> list = Arrays.asList("a");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a>

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());     // <a>

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());     // <a>

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());     // <a>

        assertTrue(loop.hasPrevious());
        assertEquals("a", loop.previous()); // <a>

        assertTrue(loop.hasPrevious());
        assertEquals("a", loop.previous()); // <a>

        assertTrue(loop.hasPrevious());
        assertEquals("a", loop.previous()); // <a>
    }

    /**
     * Tests whether a looping list iterator works on a list with two
     * elements.
     */
    @Test
    public void testLooping2() throws Exception {
        final List<String> list = Arrays.asList("a", "b");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());     // a <b>

        assertTrue(loop.hasNext());
        assertEquals("b", loop.next());     // <a> b

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());     // a <b>

        // Reset the iterator and try using previous.
        loop.reset();                       // <a> b

        assertTrue(loop.hasPrevious());
        assertEquals("b", loop.previous()); // a <b>

        assertTrue(loop.hasPrevious());
        assertEquals("a", loop.previous()); // <a> b

        assertTrue(loop.hasPrevious());
        assertEquals("b", loop.previous()); // a <b>
    }

    /**
     * Tests jogging back and forth between two elements, but not over
     * the begin/end boundary of the list.
     */
    @Test
    public void testJoggingNotOverBoundary() {
        final List<String> list = Arrays.asList("a", "b");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b

        // Try jogging back and forth between the elements, but not
        // over the begin/end boundary.
        loop.reset();
        assertEquals("a", loop.next());     // a <b>
        assertEquals("a", loop.previous()); // <a> b
        assertEquals("a", loop.next());     // a <b>

        assertEquals("b", loop.next());     // <a> b
        assertEquals("b", loop.previous()); // a <b>
        assertEquals("b", loop.next());     // <a> b
    }

    /**
     * Tests jogging back and forth between two elements over the
     * begin/end boundary of the list.
     */
    @Test
    public void testJoggingOverBoundary() {
        final List<String> list = Arrays.asList("a", "b");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b

        // Try jogging back and forth between the elements, but not
        // over the begin/end boundary.
        assertEquals("b", loop.previous()); // a <b>
        assertEquals("b", loop.next());     // <a> b
        assertEquals("b", loop.previous()); // a <b>

        assertEquals("a", loop.previous()); // <a> b
        assertEquals("a", loop.next());     // a <b>
        assertEquals("a", loop.previous()); // <a> b
    }

    /**
     * Tests removing an element from a wrapped ArrayList.
     */
    @Test
    public void testRemovingElementsAndIteratingForward() {
        final List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b c

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next()); // a <b> c
        loop.remove();                  // <b> c
        assertEquals(2, list.size());

        assertTrue(loop.hasNext());
        assertEquals("b", loop.next()); // b <c>
        loop.remove();                  // <c>
        assertEquals(1, list.size());

        assertTrue(loop.hasNext());
        assertEquals("c", loop.next()); // <c>
        loop.remove();                  // ---
        assertEquals(0, list.size());

        assertFalse(loop.hasNext());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            loop.next();
        });
        assertTrue(exception.getMessage().contains("There are no elements for this iterator to loop on"));
    }

    /**
     * Tests removing an element from a wrapped ArrayList.
     */
    @Test
    public void testRemovingElementsAndIteratingBackwards() {
        final List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b c

        assertTrue(loop.hasPrevious());
        assertEquals("c", loop.previous()); // a b <c>
        loop.remove();                      // <a> b
        assertEquals(2, list.size());

        assertTrue(loop.hasPrevious());
        assertEquals("b", loop.previous()); // a <b>
        loop.remove();                      // <a>
        assertEquals(1, list.size());

        assertTrue(loop.hasPrevious());
        assertEquals("a", loop.previous()); // <a>
        loop.remove();                      // ---
        assertEquals(0, list.size());

        assertFalse(loop.hasPrevious());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            loop.previous();
        });
        assertTrue(exception.getMessage().contains("There are no elements for this iterator to loop on"));
    }

    /**
     * Tests the reset method.
     */
    @Test
    public void testReset() {
        final List<String> list = Arrays.asList("a", "b", "c");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b c

        assertEquals("a", loop.next()); // a <b> c
        assertEquals("b", loop.next()); // a b <c>
        loop.reset();                   // <a> b c
        assertEquals("a", loop.next()); // a <b> c
        loop.reset();                   // <a> b c
        assertEquals("a", loop.next()); // a <b> c
        assertEquals("b", loop.next()); // a b <c>
        assertEquals("c", loop.next()); // <a> b c
        loop.reset();                   // <a> b c

        assertEquals("c", loop.previous()); // a b <c>
        assertEquals("b", loop.previous()); // a <b> c
        loop.reset();                       // <a> b c
        assertEquals("c", loop.previous()); // a b <c>
        loop.reset();                       // <a> b c
        assertEquals("c", loop.previous()); // a b <c>
        assertEquals("b", loop.previous()); // a <b> c
        assertEquals("a", loop.previous()); // <a> b c
    }

    /**
     * Tests the add method.
     */
    @Test
    public void testAdd() {
        List<String> list = new ArrayList<>(Arrays.asList("b", "e", "f"));
        LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <b> e f

        loop.add("a");                      // <a> b e f
        assertEquals("b", loop.next());     // a <b> e f
        loop.reset();                       // <a> b e f
        assertEquals("a", loop.next());     // a <b> e f
        assertEquals("b", loop.next());     // a b <e> f

        loop.add("c");                      // a b c <e> f
        assertEquals("e", loop.next());     // a b c e <f>
        assertEquals("e", loop.previous()); // a b c <e> f
        assertEquals("c", loop.previous()); // a b <c> e f
        assertEquals("c", loop.next());     // a b c <e> f

        loop.add("d");                      // a b c d <e> f
        loop.reset();                       // <a> b c d e f
        assertEquals("a", loop.next());     // a <b> c d e f
        assertEquals("b", loop.next());     // a b <c> d e f
        assertEquals("c", loop.next());     // a b c <d> e f
        assertEquals("d", loop.next());     // a b c d <e> f
        assertEquals("e", loop.next());     // a b c d e <f>
        assertEquals("f", loop.next());     // <a> b c d e f
        assertEquals("a", loop.next());     // a <b> c d e f

        list = new ArrayList<>(Arrays.asList("b", "e", "f"));
        loop = new LoopingListIterator<>(list); // <b> e f

        loop.add("a");                      // a <b> e f
        assertEquals("a", loop.previous()); // a b e <f>
        loop.reset();                       // <a> b e f
        assertEquals("f", loop.previous()); // a b e <f>
        assertEquals("e", loop.previous()); // a b <e> f

        loop.add("d");                      // a b d <e> f
        assertEquals("d", loop.previous()); // a b <d> e f

        loop.add("c");                      // a b c <d> e f
        assertEquals("c", loop.previous()); // a b <c> d e f

        loop.reset();
        assertEquals("a", loop.next());     // a <b> c d e f
        assertEquals("b", loop.next());     // a b <c> d e f
        assertEquals("c", loop.next());     // a b c <d> e f
        assertEquals("d", loop.next());     // a b c d <e> f
        assertEquals("e", loop.next());     // a b c d e <f>
        assertEquals("f", loop.next());     // <a> b c d e f
        assertEquals("a", loop.next());     // a <b> c d e f
    }

    /**
     * Tests nextIndex and previousIndex.
     */
    @Test
    public void testNextAndPreviousIndex() {
        final List<String> list = Arrays.asList("a", "b", "c");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <a> b c

        assertEquals(0, loop.nextIndex());
        assertEquals(2, loop.previousIndex());

        assertEquals("a", loop.next());        // a <b> c
        assertEquals(1, loop.nextIndex());
        assertEquals(0, loop.previousIndex());

        assertEquals("a", loop.previous());    // <a> b c
        assertEquals(0, loop.nextIndex());
        assertEquals(2, loop.previousIndex());

        assertEquals("c", loop.previous());    // a b <c>
        assertEquals(2, loop.nextIndex());
        assertEquals(1, loop.previousIndex());

        assertEquals("b", loop.previous());    // a <b> c
        assertEquals(1, loop.nextIndex());
        assertEquals(0, loop.previousIndex());

        assertEquals("a", loop.previous());    // <a> b c
        assertEquals(0, loop.nextIndex());
        assertEquals(2, loop.previousIndex());
    }

    /**
     * Tests using the set method to change elements.
     */
    @Test
    public void testSet() {
        final List<String> list = Arrays.asList("q", "r", "z");
        final LoopingListIterator<String> loop = new LoopingListIterator<>(list); // <q> r z

        assertEquals("z", loop.previous()); // q r <z>
        loop.set("c");                      // q r <c>

        loop.reset();                       // <q> r c
        assertEquals("q", loop.next());     // q <r> c
        loop.set("a");                      // a <r> c

        assertEquals("r", loop.next());     // a r <c>
        loop.set("b");                      // a b <c>

        loop.reset();                       // <a> b c
        assertEquals("a", loop.next());     // a <b> c
        assertEquals("b", loop.next());     // a b <c>
        assertEquals("c", loop.next());     // <a> b c
    }

}
