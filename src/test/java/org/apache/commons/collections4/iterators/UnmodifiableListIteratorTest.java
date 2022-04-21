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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the UnmodifiableListIterator.
 */
public class UnmodifiableListIteratorTest<E> extends AbstractListIteratorTest<E> {

    protected String[] testArray = {"One", "Two", "Three"};
    protected List<E> testList;

    public UnmodifiableListIteratorTest() {
        super(UnmodifiableListIteratorTest.class.getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        testList = new ArrayList<>(Arrays.asList((E[]) testArray));
    }

    @Override
    public ListIterator<E> makeEmptyIterator() {
        return UnmodifiableListIterator.unmodifiableListIterator(Collections.<E>emptyList().listIterator());
    }

    @Override
    public ListIterator<E> makeObject() {
        return UnmodifiableListIterator.unmodifiableListIterator(testList.listIterator());
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

    @Test
    public void testListIterator() {
        assertTrue(makeEmptyIterator() instanceof Unmodifiable);
    }

    @Test
    public void testDecorateFactory() {
        ListIterator<E> it = makeObject();
        assertSame(it, UnmodifiableListIterator.unmodifiableListIterator(it));

        it = testList.listIterator();
        assertNotSame(it, UnmodifiableListIterator.unmodifiableListIterator(it));

        assertThrows(NullPointerException.class, () -> UnmodifiableListIterator.unmodifiableListIterator(null));
    }

}
