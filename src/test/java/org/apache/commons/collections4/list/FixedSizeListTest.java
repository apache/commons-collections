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

import org.junit.Assert;

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

        List<String> decoratedList = new ArrayList<>();
        decoratedList.add("item 1");
        decoratedList.add("item 2");
        //
        FixedSizeList<String> fixedSizeList = FixedSizeList.fixedSizeList(decoratedList);
        int sizeBefore = fixedSizeList.size();
        //
        boolean changed = decoratedList.add("New Value");
        Assert.assertTrue(changed);
        //
        Assert.assertEquals("Modifying an the underlying list is allowed",
                sizeBefore + 1, fixedSizeList.size());
    }
}
