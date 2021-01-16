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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the LoopingIterator class.
 *
 */
public class LoopingIteratorTest {

    /**
     * Tests constructor exception.
     */
    @Test
    public void testConstructorEx() throws Exception {
        try {
            new LoopingIterator<>(null);
            fail();
        } catch (final NullPointerException ex) {
        }
    }

    /**
     * Tests whether an empty looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testLooping0() throws Exception {
        final List<Object> list = new ArrayList<>();
        final LoopingIterator<Object> loop = new LoopingIterator<>(list);
        assertTrue(!loop.hasNext(), "hasNext should return false");

        try {
            loop.next();
            fail("NoSuchElementException was not thrown during next() call.");
        } catch (final NoSuchElementException ex) {
        }
    }

    /**
     * Tests whether a populated looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testLooping1() throws Exception {
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
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testLooping2() throws Exception {
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
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testLooping3() throws Exception {
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
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testRemoving1() throws Exception {
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
        try {
            loop.next();
            fail("Expected NoSuchElementException to be thrown.");
        } catch (final NoSuchElementException ex) {
        }
    }

    /**
     * Tests the reset() method on a LoopingIterator wrapped ArrayList.
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testReset() throws Exception {
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
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testSize() throws Exception {
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
