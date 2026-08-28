/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.Test;

/**
 * Tests the EnumerationIterator.
 */
public class EnumerationIteratorTest {

    @Test
    void testRemoveBeforeNext() {
        final List<String> list = new ArrayList<>(Arrays.asList("a", "b"));
        final Vector<String> vector = new Vector<>(list);
        final EnumerationIterator<String> it = new EnumerationIterator<>(vector.elements(), list);
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    void testRemoveTwiceThrows() {
        final List<String> list = new ArrayList<>(Arrays.asList("a", "a", "b"));
        final Vector<String> vector = new Vector<>(list);
        final EnumerationIterator<String> it = new EnumerationIterator<>(vector.elements(), list);
        it.next();
        it.remove();
        assertEquals(Arrays.asList("a", "b"), list);
        // remove() may only run once per next(); a repeat must not delete a second element
        assertThrows(IllegalStateException.class, it::remove);
        assertEquals(Arrays.asList("a", "b"), list);
    }

    @Test
    void testRemoveWithoutCollection() {
        final Vector<String> vector = new Vector<>(Arrays.asList("a"));
        final EnumerationIterator<String> it = new EnumerationIterator<>(vector.elements());
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

}
