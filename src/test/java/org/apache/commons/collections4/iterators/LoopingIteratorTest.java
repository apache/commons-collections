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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

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
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new LoopingIterator<>(null);
        });
        assertTrue(exception.getMessage().contains("collection"));
    }

    /**
     * Tests whether an empty looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testLooping0() throws Exception {
        final List<Object> list = new ArrayList<>();
        final LoopingIterator<Object> loop = new LoopingIterator<>(list);
        assertTrue(!loop.hasNext());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            loop.next();
        });
        assertTrue(exception.getMessage().contains("There are no elements for this iterator to loop on"));
    }

    /**
     * Tests whether a populated looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    @Test
    public void testLooping1() throws Exception {
        final List<String> list = Arrays.asList("a");
        final LoopingIterator<String> loop = new LoopingIterator<>(list);

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext());
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

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext());
        assertEquals("b", loop.next());

        assertTrue(loop.hasNext());
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

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue(loop.hasNext());
        assertEquals("b", loop.next());

        assertTrue(loop.hasNext());
        assertEquals("c", loop.next());

        assertTrue(loop.hasNext());
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
        assertEquals(3, list.size());

        assertTrue(loop.hasNext());
        assertEquals("a", loop.next());
        loop.remove();  // removes a
        assertEquals(2, list.size());

        assertTrue(loop.hasNext());
        assertEquals("b", loop.next());
        loop.remove();  // removes b
        assertEquals(1, list.size());

        assertTrue(loop.hasNext());
        assertEquals("c", loop.next());
        loop.remove();  // removes c
        assertEquals(0, list.size());

        assertFalse(loop.hasNext());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            loop.next();
        });
        assertTrue(exception.getMessage().contains("There are no elements for this iterator to loop on"));
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
