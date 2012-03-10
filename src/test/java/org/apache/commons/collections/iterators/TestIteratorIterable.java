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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;

/**
 * Tests for IteratorIterable.
 * 
 * @version $Revision: $
 */
public class TestIteratorIterable extends BulkTest {

    public static Test suite() {
        return BulkTest.makeSuite(TestIteratorIterable.class);
    }

    public TestIteratorIterable(String name) {
        super(name);
    }

    private Iterator<Integer> createIterator() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        Iterator<Integer> iter = list.iterator();
        return iter;
    }

    public void testIterator() {
        Iterator<Integer> iter = createIterator();
        Iterable<Number> iterable = new IteratorIterable<Number>(iter);
        
        // first use
        verifyIteration(iterable);
        
        // second use
        for (Number actual : iterable) {
            fail("should not be able to iterate twice");
        }
    }

    public void testMultipleUserIterator() {
        Iterator<Integer> iter = createIterator();

        Iterable<Number> iterable = new IteratorIterable<Number>(iter, true);
        
        // first use
        verifyIteration(iterable);
        
        // second use
        verifyIteration(iterable);
    }

    private void verifyIteration(Iterable<Number> iterable) {
        int expected = 0;
        for (Number actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        assertTrue(expected > 0);
    }
}

