/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * Tests the IteratorEnumeration.
 *
 */
public class IteratorEnumerationTest extends TestCase {

    public void testEnumeration() {
        final Iterator<String> iterator = Arrays.asList("a", "b", "c").iterator();
        final IteratorEnumeration<String> enumeration = new IteratorEnumeration<>(iterator);

        assertEquals(iterator, enumeration.getIterator());

        assertTrue(enumeration.hasMoreElements());
        assertEquals("a", enumeration.nextElement());
        assertEquals("b", enumeration.nextElement());
        assertEquals("c", enumeration.nextElement());
        assertFalse(enumeration.hasMoreElements());

        try {
            enumeration.nextElement();
            fail("NoSuchElementException expected");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }
}
