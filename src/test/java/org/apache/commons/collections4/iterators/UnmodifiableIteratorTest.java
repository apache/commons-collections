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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.Unmodifiable;

/**
 * Tests the UnmodifiableIterator.
 *
 * @version $Id$
 */
public class UnmodifiableIteratorTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three" };
    protected List<E> testList;

    public UnmodifiableIteratorTest(final String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testList = new ArrayList<E>(Arrays.asList((E[]) testArray));
    }

    @Override
    public Iterator<E> makeEmptyIterator() {
        return UnmodifiableIterator.unmodifiableIterator(Collections.<E>emptyList().iterator());
    }

    @Override
    public Iterator<E> makeObject() {
        return UnmodifiableIterator.unmodifiableIterator(testList.iterator());
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    //-----------------------------------------------------------------------
    public void testIterator() {
        assertTrue(makeEmptyIterator() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        Iterator<E> it = makeObject();
        assertSame(it, UnmodifiableIterator.unmodifiableIterator(it));

        it = testList.iterator();
        assertTrue(it != UnmodifiableIterator.unmodifiableIterator(it));

        try {
            UnmodifiableIterator.unmodifiableIterator(null);
            fail();
        } catch (final IllegalArgumentException ex) {}
    }

}
