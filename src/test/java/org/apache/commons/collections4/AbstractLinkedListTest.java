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
package org.apache.commons.collections4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.list.AbstractListTest;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests base {@link java.util.LinkedList} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject()} method.
 * <p>
 * If your {@link LinkedList} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link List} fails.
 *
 */
public abstract class AbstractLinkedListTest<T> extends AbstractListTest<T> {

    public AbstractLinkedListTest(final String testName) {
        super(testName);
    }

    @Override
    public abstract LinkedList<T> makeObject();

    /**
     *  Returns the {@link #collection} field cast to a {@link LinkedList}.
     *
     *  @return the collection field as a List
     */
    @Override
    public LinkedList<T> getCollection() {
        return (LinkedList<T>) super.getCollection();
    }

    /**
     *  Returns the {@link #confirmed} field cast to a {@link LinkedList}.
     *
     *  @return the confirmed field as a List
     */
    protected LinkedList<T> getConfirmedLinkedList() {
        return (LinkedList<T>) getConfirmed();
    }

    /**
     *  Tests {@link LinkedList#addFirst(Object)}.
     */
    @SuppressWarnings("unchecked")
    public void testLinkedListAddFirst() {
        if (!isAddSupported()) {
            return;
        }
        final T o = (T) "hello";

        resetEmpty();
        getCollection().addFirst(o);
        getConfirmedLinkedList().addFirst(o);
        verify();

        resetFull();
        getCollection().addFirst(o);
        getConfirmedLinkedList().addFirst(o);
        verify();
    }

    /**
     *  Tests {@link LinkedList#addLast(Object)}.
     */
    @SuppressWarnings("unchecked")
    public void testLinkedListAddLast() {
        if (!isAddSupported()) {
            return;
        }
        final T o = (T) "hello";

        resetEmpty();
        getCollection().addLast(o);
        getConfirmedLinkedList().addLast(o);
        verify();

        resetFull();
        getCollection().addLast(o);
        getConfirmedLinkedList().addLast(o);
        verify();
    }

    /**
     * Tests {@link LinkedList#getFirst()}.
     */
    public void testLinkedListGetFirst() {
        resetEmpty();
        final Executable testMethod = () -> getCollection()
                .getFirst();
        final NoSuchElementException thrown = assertThrows(NoSuchElementException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("FIXME")));
        verify();

        resetFull();
        final Object first = getCollection().getFirst();
        final Object confirmedFirst = getConfirmedLinkedList().getFirst();
        assertEquals("Result returned by getFirst() was wrong.",
                confirmedFirst, first);
        verify();
    }

    /**
     * Tests {@link LinkedList#getLast()}.
     */
    public void testLinkedListGetLast() {
        resetEmpty();
        final Executable testMethod = () -> getCollection()
                .getLast();
        final NoSuchElementException thrown = assertThrows(NoSuchElementException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("FIXME")));
        verify();

        resetFull();
        final Object last = getCollection().getLast();
        final Object confirmedLast = getConfirmedLinkedList().getLast();
        assertEquals("Result returned by getLast() was wrong.",
                confirmedLast, last);
        verify();
    }

    /**
     * Tests {@link LinkedList#removeFirst()}.
     */
    public void testLinkedListRemoveFirst() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        final Executable testMethod = () -> getCollection()
                .removeFirst();
        final NoSuchElementException thrown = assertThrows(NoSuchElementException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("FIXME")));
        verify();

        resetFull();
        final Object first = getCollection().removeFirst();
        final Object confirmedFirst = getConfirmedLinkedList().removeFirst();
        assertEquals("Result returned by removeFirst() was wrong.",
                confirmedFirst, first);
        verify();
    }

    /**
     * Tests {@link LinkedList#removeLast()}.
     */
    public void testLinkedListRemoveLast() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        final Executable testMethod = () -> getCollection()
                .removeLast();
        final NoSuchElementException thrown = assertThrows(NoSuchElementException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("FIXME")));
        verify();

        resetFull();
        final Object last = getCollection().removeLast();
        final Object confirmedLast = getConfirmedLinkedList().removeLast();
        assertEquals("Result returned by removeLast() was wrong.",
                confirmedLast, last);
        verify();
    }

    /**
     *  Returns an empty {@link LinkedList}.
     */
    @Override
    public Collection<T> makeConfirmedCollection() {
        return new LinkedList<>();
    }

    /**
     *  Returns a full {@link LinkedList}.
     */
    @Override
    public Collection<T> makeConfirmedFullCollection() {
        final List<T> list = new LinkedList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }
}
