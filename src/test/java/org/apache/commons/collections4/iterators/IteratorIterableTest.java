/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;

/**
 * Tests for IteratorIterable.
 *
 */
public class IteratorIterableTest extends BulkTest {

    public static Test suite() {
        return BulkTest.makeSuite(IteratorIterableTest.class);
    }

    public IteratorIterableTest(final String name) {
        super(name);
    }

    private Iterator<Integer> createIterator() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iter = list.iterator();
        return iter;
    }

    @SuppressWarnings("unused")
    public void testIterator() {
        final Iterator<Integer> iter = createIterator();
        final Iterable<Number> iterable = new IteratorIterable<Number>(iter);

        // first use
        verifyIteration(iterable);

        // second use
        for (final Number actual : iterable) {
            fail("should not be able to iterate twice");
        }
    }

    public void testMultipleUserIterator() {
        final Iterator<Integer> iter = createIterator();

        final Iterable<Number> iterable = new IteratorIterable<Number>(iter, true);

        // first use
        verifyIteration(iterable);

        // second use
        verifyIteration(iterable);
    }

    private void verifyIteration(final Iterable<Number> iterable) {
        int expected = 0;
        for (final Number actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        assertTrue(expected > 0);
    }
}

