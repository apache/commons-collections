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
package org.apache.commons.collections;

import java.util.ArrayList;

import org.apache.commons.collections.list.AbstractTestList;

/**
 * Abstract test class for ArrayList.
 *
 * @version $Revision$ $Date$
 *
 * @author Jason van Zyl
 */
public abstract class TestArrayList<E> extends AbstractTestList<E> {

    public TestArrayList(String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract ArrayList<E> makeObject();

    //-----------------------------------------------------------------------
    public void testNewArrayList() {
        ArrayList<E> list = makeObject();
        assertTrue("New list is empty", list.isEmpty());
        assertEquals("New list has size zero", 0, list.size());

        try {
            list.get(1);
            fail("get(int i) should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected result
        }
    }

    @SuppressWarnings("unchecked")
    public void testSearch() {
        ArrayList<E> list = makeObject();
        list.add((E) "First Item");
        list.add((E) "Last Item");
        assertEquals("First item is 'First Item'", "First Item", list.get(0));
        assertEquals("Last Item is 'Last Item'", "Last Item", list.get(1));
    }

}
