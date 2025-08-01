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

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the UnmodifiableIterator.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class UnmodifiableIteratorTest<E> extends AbstractIteratorTest<E> {

    private final String[] testArray = { "One", "Two", "Three" };
    private List<E> testList;

    @Override
    public Iterator<E> makeEmptyIterator() {
        return UnmodifiableIterator.unmodifiableIterator(Collections.<E>emptyList().iterator());
    }

    @Override
    public Iterator<E> makeObject() {
        return UnmodifiableIterator.unmodifiableIterator(testList.iterator());
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    protected void setUp() throws Exception {
        testList = new ArrayList<>(Arrays.asList((E[]) testArray));
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Test
    void testDecorateFactory() {
        Iterator<E> it = makeObject();
        assertSame(it, UnmodifiableIterator.unmodifiableIterator(it));
        it = testList.iterator();
        assertNotSame(it, UnmodifiableIterator.unmodifiableIterator(it));
        assertThrows(NullPointerException.class, () -> UnmodifiableIterator.unmodifiableIterator(null));
    }

    @Test
    void testIterator() {
        assertTrue(makeEmptyIterator() instanceof Unmodifiable);
    }

    @Test
    void testUnwrap() {
        final Iterator<E> iterator = testList.iterator();
        @SuppressWarnings("unchecked")
        final UnmodifiableIterator<E, Iterator<E>> unmodifiableIterator = (UnmodifiableIterator<E, Iterator<E>>) UnmodifiableIterator
                .unmodifiableIterator(iterator);
        assertSame(iterator, unmodifiableIterator.unwrap());
    }

    @Test
    void testWrapUnwrap() {
        final Iterator<E> iterator = testList.iterator();
        final UnmodifiableIterator<E, Iterator<E>> unmodifiableIterator = UnmodifiableIterator.wrap(iterator);
        assertSame(iterator, unmodifiableIterator.unwrap());
    }
}
