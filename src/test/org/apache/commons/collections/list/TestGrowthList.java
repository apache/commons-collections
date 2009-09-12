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
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link TestList} for exercising the {@link GrowthList}.
 *
 * @since Commons Collections 3.2
 * @version $Revision: 155406 $ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestGrowthList<E> extends AbstractTestList<E> {

    public TestGrowthList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestGrowthList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestGrowthList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public List<E> makeObject() {
        return new GrowthList<E>();
    }

    public List<E> makeFullCollection() {
        List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return GrowthList.decorate(list);
    }

    //-----------------------------------------------------------------------
    public void testGrowthAdd() {
        Integer one = new Integer(1);
        GrowthList<Integer> grower = new GrowthList<Integer>();
        assertEquals(0, grower.size());
        grower.add(1, one);
        assertEquals(2, grower.size());
        assertEquals(null, grower.get(0));
        assertEquals(one, grower.get(1));
    }

    public void testGrowthAddAll() {
        Integer one = new Integer(1);
        Integer two = new Integer(2);
        Collection<Integer> coll = new ArrayList<Integer>();
        coll.add(one);
        coll.add(two);
        GrowthList<Integer> grower = new GrowthList<Integer>();
        assertEquals(0, grower.size());
        grower.addAll(1, coll);
        assertEquals(3, grower.size());
        assertEquals(null, grower.get(0));
        assertEquals(one, grower.get(1));
        assertEquals(two, grower.get(2));
    }

    public void testGrowthSet1() {
        Integer one = new Integer(1);
        GrowthList<Integer> grower = new GrowthList<Integer>();
        assertEquals(0, grower.size());
        grower.set(1, one);
        assertEquals(2, grower.size());
        assertEquals(null, grower.get(0));
        assertEquals(one, grower.get(1));
    }

    public void testGrowthSet2() {
        Integer one = new Integer(1);
        GrowthList<Integer> grower = new GrowthList<Integer>();
        assertEquals(0, grower.size());
        grower.set(0, one);
        assertEquals(1, grower.size());
        assertEquals(one, grower.get(0));
    }

    //-----------------------------------------------------------------------
    /**
     * Override.
     */
    public void testListAddByIndexBoundsChecking() {
        List<E> list;
        E element = getOtherElements()[0];
        try {
            list = makeObject();
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * Override.
     */
    public void testListAddByIndexBoundsChecking2() {
        List<E> list;
        E element = getOtherElements()[0];
        try {
            list = makeFullCollection();
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * Override.
     */
    public void testListSetByIndexBoundsChecking() {
        List<E> list = makeObject();
        E element = getOtherElements()[0];
        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * Override.
     */
    public void testListSetByIndexBoundsChecking2() {
        List<E> list = makeFullCollection();
        E element = getOtherElements()[0];
        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 
    }

    //-----------------------------------------------------------------------
    public String getCompatibilityVersion() {
        return "3.2";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "C:/commons/collections/data/test/GrowthList.emptyCollection.version3.2.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "C:/commons/collections/data/test/GrowthList.fullCollection.version3.2.obj");
//    }

}
