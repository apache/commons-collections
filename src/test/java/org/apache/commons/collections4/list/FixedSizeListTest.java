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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extension of {@link AbstractListTest} for exercising the {@link FixedSizeList}
 * implementation.
 *
 * @since 3.0
 */
public class FixedSizeListTest<E> extends AbstractListTest<E> {

    public FixedSizeListTest(final String testName) {
        super(testName);
    }

    @Override
    public List<E> makeObject() {
        return FixedSizeList.fixedSizeList(new ArrayList<E>());
    }

    @Override
    public List<E> makeFullCollection() {
        final List<E> list = new ArrayList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return FixedSizeList.fixedSizeList(list);
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/FixedSizeList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/FixedSizeList.fullCollection.version4.obj");
//    }

    public void testListAllowsMutationOfUnderlyingCollection() {

        final List<String> decoratedList = new ArrayList<>();
        decoratedList.add("item 1");
        decoratedList.add("item 2");
        //
        final FixedSizeList<String> fixedSizeList = FixedSizeList.fixedSizeList(decoratedList);
        final int sizeBefore = fixedSizeList.size();
        //
        final boolean changed = decoratedList.add("New Value");
        assertTrue(changed);
        //
        assertEquals("Modifying an the underlying list is allowed",
                sizeBefore + 1, fixedSizeList.size());
    }

    private FixedSizeList<String> initFixedSizeList() {
        final List<String> decoratedList = new ArrayList<>();
        decoratedList.add("item 1");
        decoratedList.add("item 2");
        //
        return FixedSizeList.fixedSizeList(decoratedList);
    }

    public void testAdd() {
        final FixedSizeList<String> fixedSizeList = initFixedSizeList();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            fixedSizeList.add(2, "New Value");
        });
        assertTrue(exception.getMessage().contains("List is fixed size"));
    }


    public void testAddAll() {
        final FixedSizeList<String> fixedSizeList = initFixedSizeList();

        final List<String> addList = new ArrayList<>();
        addList.add("item 3");
        addList.add("item 4");

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            fixedSizeList.addAll(2, addList);
        });
        assertTrue(exception.getMessage().contains("List is fixed size"));
    }

    public void testRemove() {
        final FixedSizeList<String> fixedSizeList = initFixedSizeList();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            fixedSizeList.remove(1);
        });
        assertTrue(exception.getMessage().contains("List is fixed size"));
    }

    public void testSubList() {
        final FixedSizeList<String> fixedSizeList = initFixedSizeList();

        final List<String> subFixedSizeList = fixedSizeList.subList(1, 1);
        assertNotNull(subFixedSizeList);
        assertEquals(0, subFixedSizeList.size());
    }

    public void testIsFull() {
        final FixedSizeList<String> fixedSizeList = initFixedSizeList();

        assertTrue(fixedSizeList.isFull());
    }

    public void testMaxSize() {
        final FixedSizeList<String> fixedSizeList = initFixedSizeList();

        assertEquals(2, fixedSizeList.maxSize());
    }
}
