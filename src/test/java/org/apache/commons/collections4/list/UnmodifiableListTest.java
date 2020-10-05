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
package org.apache.commons.collections4.list;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Extension of {@link AbstractListTest} for exercising the
 * {@link UnmodifiableList} implementation.
 *
 * @since 3.0
 */
public class UnmodifiableListTest<E> extends AbstractListTest<E> {

    public UnmodifiableListTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public UnmodifiableList<E> makeObject() {
        return new UnmodifiableList<>(new ArrayList<E>());
    }

    @Override
    public UnmodifiableList<E> makeFullCollection() {
        final ArrayList<E> list = new ArrayList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return new UnmodifiableList<>(list);
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
        array = new ArrayList<>();
        array.add((E) Integer.valueOf(1));
    }

    /**
     * Verify that base list and sublists are not modifiable
     */
    public void testUnmodifiable() {
        setupList();
        verifyUnmodifiable(list);
        verifyUnmodifiable(list.subList(0, 2));
    }

    public void testDecorateFactory() {
        final List<E> list = makeObject();
        assertSame(list, UnmodifiableList.unmodifiableList(list));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            UnmodifiableList.unmodifiableList(null);
        });
        assertTrue(exception.getMessage().contains("collection"));
    }

    @SuppressWarnings("unchecked")
    protected void verifyUnmodifiable(final List<E> list) {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.add(0, (E) Integer.valueOf(0));
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.add((E) Integer.valueOf(0));
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.addAll(0, array);
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.addAll(array);
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.clear();
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.remove(0);
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.remove(Integer.valueOf(0));
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.removeAll(array);
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.retainAll(array);
        });
        assertNull(exception.getMessage());
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            list.set(0, (E) Integer.valueOf(0));
        });
        assertNull(exception.getMessage());
    }

    /**
     * Verify that iterator is not modifiable
     */
    public void testUnmodifiableIterator() {
        setupList();
        final Iterator<E> iterator = list.iterator();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            iterator.next();
            iterator.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));
    }

    //-----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableList.fullCollection.version4.obj");
//    }

}
