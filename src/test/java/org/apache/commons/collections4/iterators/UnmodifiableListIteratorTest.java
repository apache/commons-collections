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
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections4.Unmodifiable;

/**
 * Tests the UnmodifiableListIterator.
 *
 * @version $Id$
 */
public class UnmodifiableListIteratorTest<E> extends AbstractListIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three" };
    protected List<E> testList;

    public UnmodifiableListIteratorTest(final String testName) {
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
    public ListIterator<E> makeEmptyIterator() {
        return UnmodifiableListIterator.umodifiableListIterator(Collections.<E>emptyList().listIterator());
    }

    @Override
    public ListIterator<E> makeObject() {
        return UnmodifiableListIterator.umodifiableListIterator(testList.listIterator());
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Override
    public boolean supportsAdd() {
        return false;
    }

    @Override
    public boolean supportsSet() {
        return false;
    }

    //-----------------------------------------------------------------------
    public void testListIterator() {
        assertTrue(makeEmptyIterator() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        ListIterator<E> it = makeObject();
        assertSame(it, UnmodifiableListIterator.umodifiableListIterator(it));

        it = testList.listIterator();
        assertTrue(it != UnmodifiableListIterator.umodifiableListIterator(it));

        try {
            UnmodifiableListIterator.umodifiableListIterator(null);
            fail();
        } catch (final IllegalArgumentException ex) {}
    }

}
