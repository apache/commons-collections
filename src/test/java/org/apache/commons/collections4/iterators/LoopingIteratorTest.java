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
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the LoopingIterator class.
 */
public class LoopingIteratorTest {

    /**
     * Tests constructor exception.
     */
    @Test
    public void testConstructorEx() {
        assertThrows(NullPointerException.class, () -> new LoopingIterator<>(null));
    }

    /**
     * Tests whether an empty looping iterator works as designed.
     */
    @Test
    public void testLooping0() {
        final List<Object> list = new ArrayList<>();
        final LoopingIterator<Object> loop = new LoopingIterator<>(list);
        assertFalse(loop.hasNext(), "hasNext should return false");

        assertThrows(NoSuchElementException.class, () -> loop.next(),
                "NoSuchElementException was not thrown during next() call.");
    }

    /**
     * Tests whether a populated looping iterator works as designed.
     */
    @Test
    public void testLooping1() {
        final List<String> list = Arrays.asList("a");
        final LoopingIterator<String> loop = new LoopingIterator<>(list);

        assertTrue(loop.hasNext(), "1st hasNext should return true");
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext(), "2nd hasNext should return true");
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext(), "3rd hasNext should return true");
        assertEquals("a", loop.next());
    }

    /**
     * Tests whether a populated looping iterator works as designed.
     */
    @Test
    public void testLooping2() {
        final List<String> list = Arrays.asList("a", "b");
        final LoopingIterator<String> loop = new LoopingIterator<>(list);

        assertTrue(loop.hasNext(), "1st hasNext should return true");
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext(), "2nd hasNext should return true");
        assertEquals("b", loop.next());

        assertTrue(loop.hasNext(), "3rd hasNext should return true");
        assertEquals("a", loop.next());
    }

    /**
     * Tests whether a populated looping iterator works as designed.
     */
    @Test
    public void testLooping3() {
        final List<String> list = Arrays.asList("a", "b", "c");
        final LoopingIterator<String> loop = new LoopingIterator<>(list);

        assertTrue(loop.hasNext(), "1st hasNext should return true");
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext(), "2nd hasNext should return true");
        assertEquals("b", loop.next());

        assertTrue(loop.hasNext(), "3rd hasNext should return true");
        assertEquals("c", loop.next());

        assertTrue(loop.hasNext(), "4th hasNext should return true");
        assertEquals("a", loop.next());
    }

    /**
     * Tests the remove() method on a LoopingIterator wrapped ArrayList.
     */
    @Test
    public void testRemoving1() {
        final List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        final LoopingIterator<String> loop = new LoopingIterator<>(list);
        assertEquals(3, list.size(), "list should have 3 elements.");

        assertTrue(loop.hasNext(), "1st hasNext should return true");
        assertEquals("a", loop.next());
        loop.remove();  // removes a
        assertEquals(2, list.size(), "list should have 2 elements.");

        assertTrue(loop.hasNext(), "2nd hasNext should return true");
        assertEquals("b", loop.next());
        loop.remove();  // removes b
        assertEquals(1, list.size(), "list should have 1 elements.");

        assertTrue(loop.hasNext(), "3rd hasNext should return true");
        assertEquals("c", loop.next());
        loop.remove();  // removes c
        assertEquals(0, list.size(), "list should have 0 elements.");

        assertFalse(loop.hasNext(), "4th hasNext should return false");

        assertThrows(NoSuchElementException.class, () -> loop.next(),
                "Expected NoSuchElementException to be thrown.");
    }

    /**
     * Tests the reset() method on a LoopingIterator wrapped ArrayList.
     */
    @Test
    public void testReset() {
        final List<String> list = Arrays.asList("a", "b", "c");
        final LoopingIterator<String> loop = new LoopingIterator<>(list);

        assertEquals("a", loop.next());
        assertEquals("b", loop.next());
        loop.reset();
        assertEquals("a", loop.next());
        loop.reset();
        assertEquals("a", loop.next());
        assertEquals("b", loop.next());
        assertEquals("c", loop.next());
        loop.reset();
        assertEquals("a", loop.next());
        assertEquals("b", loop.next());
        assertEquals("c", loop.next());
    }

    /**
     * Tests the size() method on a LoopingIterator wrapped ArrayList.
     */
    @Test
    public void testSize() {
        final List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        final LoopingIterator<String> loop = new LoopingIterator<>(list);

        assertEquals(3, loop.size());
        loop.next();
        loop.next();
        assertEquals(3, loop.size());
        loop.reset();
        assertEquals(3, loop.size());
        loop.next();
        loop.remove();
        assertEquals(2, loop.size());
    }

}
