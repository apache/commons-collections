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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Tests the UniqueFilterIterator class.
 *
 * @version $Id$
 */
public class UniqueFilterIteratorTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<E> list1 = null;

    public UniqueFilterIteratorTest(final String testName) {
        super(testName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setUp() {
        list1 = new ArrayList<E>();
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

    @Override
    public UniqueFilterIterator<E> makeEmptyIterator() {
        final ArrayList<E> list = new ArrayList<E>();
        return new UniqueFilterIterator<E>(list.iterator());
    }

    @Override
    public UniqueFilterIterator<E> makeObject() {
        final Iterator<E> i = list1.iterator();
        return new UniqueFilterIterator<E>(i);
    }

    public void testIterator() {
        final Iterator<E> iter = makeObject();
        for (final String testValue : testArray) {
            final E iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertTrue("Iterator should now be empty", ! iter.hasNext() );

        try {
            iter.next();
        } catch (final Exception e) {
            assertTrue("NoSuchElementException must be thrown", 
                       e.getClass().equals(new NoSuchElementException().getClass()));
        }
    }

}

