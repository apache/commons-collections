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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Extension of {@link AbstractListTest} for exercising the {@link GrowthList}.
 *
 * @since 3.2
 */
public class GrowthListTest<E> extends AbstractListTest<E> {

    public GrowthListTest(final String testName) {
        super(testName);
    }

    @Override
    public List<E> makeObject() {
        return new GrowthList<>();
    }

    @Override
    public List<E> makeFullCollection() {
        final List<E> list = new ArrayList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return GrowthList.growthList(list);
    }

    //-----------------------------------------------------------------------
    public void testGrowthAdd() {
        final Integer one = Integer.valueOf(1);
        final GrowthList<Integer> grower = new GrowthList<>();
        assertEquals(0, grower.size());
        grower.add(1, one);
        assertEquals(2, grower.size());
        assertEquals(null, grower.get(0));
        assertEquals(one, grower.get(1));
    }

    public void testGrowthAddAll() {
        final Integer one = Integer.valueOf(1);
        final Integer two = Integer.valueOf(2);
        final Collection<Integer> coll = new ArrayList<>();
        coll.add(one);
        coll.add(two);
        final GrowthList<Integer> grower = new GrowthList<>();
        assertEquals(0, grower.size());
        grower.addAll(1, coll);
        assertEquals(3, grower.size());
        assertEquals(null, grower.get(0));
        assertEquals(one, grower.get(1));
        assertEquals(two, grower.get(2));
    }

    public void testGrowthSet1() {
        final Integer one = Integer.valueOf(1);
        final GrowthList<Integer> grower = new GrowthList<>();
        assertEquals(0, grower.size());
        grower.set(1, one);
        assertEquals(2, grower.size());
        assertEquals(null, grower.get(0));
        assertEquals(one, grower.get(1));
    }

    public void testGrowthSet2() {
        final Integer one = Integer.valueOf(1);
        final GrowthList<Integer> grower = new GrowthList<>();
        assertEquals(0, grower.size());
        grower.set(0, one);
        assertEquals(1, grower.size());
        assertEquals(one, grower.get(0));
    }

    //-----------------------------------------------------------------------
    /**
     * Override.
     */
    @Override
    public void testListAddByIndexBoundsChecking() {
        List<E> list;
        final E element = getOtherElements()[0];
        try {
            list = makeObject();
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch (final IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * Override.
     */
    @Override
    public void testListAddByIndexBoundsChecking2() {
        List<E> list;
        final E element = getOtherElements()[0];
        try {
            list = makeFullCollection();
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch (final IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * Override.
     */
    @Override
    public void testListSetByIndexBoundsChecking() {
        final List<E> list = makeObject();
        final E element = getOtherElements()[0];
        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch (final IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * Override.
     */
    @Override
    public void testListSetByIndexBoundsChecking2() {
        final List<E> list = makeFullCollection();
        final E element = getOtherElements()[0];
        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(final IndexOutOfBoundsException e) {
            // expected
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/GrowthList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/GrowthList.fullCollection.version4.obj");
//    }

}
