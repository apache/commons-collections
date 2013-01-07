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
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Extension of {@link AbstractListTest} for exercising the
 * {@link UnmodifiableList} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Phil Steitz
 */
public class UnmodifiableListTest<E> extends AbstractListTest<E> {

    public UnmodifiableListTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public UnmodifiableList<E> makeObject() {
        return new UnmodifiableList<E>(new ArrayList<E>());
    }

    @Override
    public UnmodifiableList<E> makeFullCollection() {
        final ArrayList<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return new UnmodifiableList<E>(list);
    }

    @Override
    public boolean isSetSupported() {
        return false;
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    protected UnmodifiableList<E> list;
    protected ArrayList<E> array;

    @SuppressWarnings("unchecked")
    protected void setupList() {
        list = makeFullCollection();
        array = new ArrayList<E>();
        array.add((E) new Integer(1));
    }

    /**
     * Verify that base list and sublists are not modifiable
     */
    public void testUnmodifiable() {
        setupList();
        verifyUnmodifiable(list);
        verifyUnmodifiable(list.subList(0, 2));
    }

    @SuppressWarnings("unchecked")
    protected void verifyUnmodifiable(final List<E> list) {
        try {
            list.add(0, (E) new Integer(0));
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.add((E) new Integer(0));
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.addAll(0, array);
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.addAll(array);
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.clear();
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.remove(0);
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.remove(new Integer(0));
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.removeAll(array);
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.retainAll(array);
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            list.set(0, (E) new Integer(0));
             fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     * Verify that iterator is not modifiable
     */
    public void testUnmodifiableIterator() {
        setupList();
        final Iterator<E> iterator = list.iterator();
        try {
            iterator.next();
            iterator.remove();
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnmodifiableList.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnmodifiableList.fullCollection.version3.1.obj");
//    }

}
