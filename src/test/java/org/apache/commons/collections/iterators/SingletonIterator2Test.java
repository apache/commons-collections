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
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.ResettableIterator;

/**
 * Tests the SingletonIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 *
 * @version $Id$
 */
public class SingletonIterator2Test<E> extends AbstractIteratorTest<E> {

    private static final Object testValue = "foo";

    public SingletonIterator2Test(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public SingletonIterator<E> makeEmptyIterator() {
        SingletonIterator<E> iter = new SingletonIterator<E>((E) testValue);
        iter.next();
        iter.remove();
        iter.reset();
        return iter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SingletonIterator<E> makeObject() {
        return new SingletonIterator<E>((E) testValue, false);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Override
    public boolean supportsEmptyIterator() {
        return false;
    }

    //-----------------------------------------------------------------------
    public void testIterator() {
        Iterator<E> iter = makeObject();
        assertTrue("Iterator has a first item", iter.hasNext());

        E iterValue = iter.next();
        assertEquals("Iteration value is correct", testValue, iterValue);

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            iter.next();
        } catch (Exception e) {
            assertTrue(
                "NoSuchElementException must be thrown",
                e.getClass().equals(new NoSuchElementException().getClass()));
        }
    }

    public void testReset() {
        ResettableIterator<E> it = makeObject();

        assertEquals(true, it.hasNext());
        assertEquals(testValue, it.next());
        assertEquals(false, it.hasNext());

        it.reset();

        assertEquals(true, it.hasNext());
        assertEquals(testValue, it.next());
        assertEquals(false, it.hasNext());

        it.reset();
        it.reset();

        assertEquals(true, it.hasNext());
    }

}
