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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Tests the ArrayIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 * The code of this test was supplied by Mauricio S. Moura.
 */
public class ArrayIteratorTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three" };

    public ArrayIteratorTest() {
        super(ArrayIteratorTest.class.getSimpleName());
    }

    @Override
    public ArrayIterator<E> makeEmptyIterator() {
        return new ArrayIterator<>(new Object[0]);
    }

    @Override
    public ArrayIterator<E> makeObject() {
        return new ArrayIterator<>(testArray);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Test
    public void testIterator() {
        final Iterator<E> iter = makeObject();
        for (final String testValue : testArray) {
            final E iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertFalse("Iterator should now be empty", iter.hasNext());

        assertThrows(NoSuchElementException.class, iter::next, "NoSuchElementException must be thrown");
    }

    @Test
    public void testNullArray() {
        assertThrows(NullPointerException.class, () -> new ArrayIterator<>(null));
    }

    @Test
    public void testReset() {
        final ArrayIterator<E> it = makeObject();
        it.next();
        it.reset();
        assertEquals("One", it.next());
    }

}
