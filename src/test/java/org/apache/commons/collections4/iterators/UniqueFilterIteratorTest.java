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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the UniqueFilterIterator class.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class UniqueFilterIteratorTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<E> list1;

    @Override
    public UniqueFilterIterator<E> makeEmptyIterator() {
        final ArrayList<E> list = new ArrayList<>();
        return new UniqueFilterIterator<>(list.iterator());
    }

    @Override
    public UniqueFilterIterator<E> makeObject() {
        final Iterator<E> i = list1.iterator();
        return new UniqueFilterIterator<>(i);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        list1 = new ArrayList<>();
        list1.add((E) "One");
        list1.add((E) "Two");
        list1.add((E) "Three");
        list1.add((E) "Two");
        list1.add((E) "One");
        list1.add((E) "Four");
        list1.add((E) "Five");
        list1.add((E) "Five");
        list1.add((E) "Six");
        list1.add((E) "Five");
    }

    @Test
    public void testIterator() {
        final Iterator<E> iter = makeObject();
        for (final String testValue : testArray) {
            final E iterValue = iter.next();
            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
        assertFalse(iter.hasNext(), "Iterator should now be empty");
        assertThrows(NoSuchElementException.class, iter::next);
    }

}

