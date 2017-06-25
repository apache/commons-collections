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

import org.junit.Test;
import java.util.HashMap;

import static org.junit.Assert.*;


/**
 * Unit tests for class {@link EntrySetMapIterator}.
 *
 * @date 25.06.2017
 * @see EntrySetMapIterator
 **/
public class EntrySetMapIteratorTest {


    @Test
    public void testToString() {

        EntrySetMapIterator<String, Object> entrySetMapIterator = new EntrySetMapIterator<String, Object>(new HashMap<String, Object>());
        String string = entrySetMapIterator.toString();

        assertEquals("MapIterator[]", string);

    }


    @Test
    public void testSetValueThrowsIllegalStateException() {

        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        EntrySetMapIterator<Integer, String> entrySetMapIterator = new EntrySetMapIterator<Integer, String>(hashMap);
        HashMap<String, Object> hashMapTwo = new HashMap<String, Object>();
        EntrySetMapIterator<String, Object> entrySetMapIteratorTwo = new EntrySetMapIterator<String, Object>(hashMapTwo);

        try {
            entrySetMapIteratorTwo.setValue(entrySetMapIterator);
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator setValue() can only be called after next() and before remove()",e.getMessage());
            assertEquals(EntrySetMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testSetValue() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put(null,  null);
        EntrySetMapIterator<String, Object> entrySetMapIterator = new EntrySetMapIterator<String, Object>(hashMap);

        assertNull(entrySetMapIterator.next());
        assertEquals(1, hashMap.size());

        assertFalse(hashMap.isEmpty());

        Object object = entrySetMapIterator.setValue(hashMap);

        assertEquals(1, hashMap.size());

        assertFalse(hashMap.isEmpty());

    }


    @Test
    public void testGetValueThrowsIllegalStateException() {

        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        EntrySetMapIterator<Integer, Integer> entrySetMapIterator = new EntrySetMapIterator<Integer, Integer>(hashMap);

        try {
            entrySetMapIterator.getValue();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator getValue() can only be called after next() and before remove()",e.getMessage());
            assertEquals(EntrySetMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testNext() {

        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        hashMap.put(-1, "");
        EntrySetMapIterator<Integer, String> entrySetMapIterator = new EntrySetMapIterator<Integer, String>(hashMap);
        Integer integerTwo = entrySetMapIterator.next();

        assertEquals(-1, (int)integerTwo);
        assertEquals(1, hashMap.size());

        assertFalse(hashMap.isEmpty());

    }


    @Test
    public void testGetKeyThrowsIllegalStateException() {

        HashMap<Object, String> hashMap = new HashMap<Object, String>(3957);
        EntrySetMapIterator<Object, String> entrySetMapIterator = new EntrySetMapIterator<Object, String>(hashMap);

        try {
            entrySetMapIterator.getKey();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator getKey() can only be called after next() and before remove()",e.getMessage());
            assertEquals(EntrySetMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testRemoveThrowsIllegalStateException() {

        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        EntrySetMapIterator<Object, Object> entrySetMapIterator = new EntrySetMapIterator<Object, Object>(hashMap);

        try {
            entrySetMapIterator.remove();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator remove() can only be called once after next()",e.getMessage());
            assertEquals(EntrySetMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testRemove() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        Object object = new Object();
        hashMap.put("asdf", object);
        EntrySetMapIterator<String, Object> entrySetMapIterator = new EntrySetMapIterator<String, Object>(hashMap);

        assertEquals("asdf", entrySetMapIterator.next());
        assertFalse(hashMap.isEmpty());

        assertEquals(1, hashMap.size());

        entrySetMapIterator.remove();

        assertEquals(0, hashMap.size());
        assertTrue(hashMap.isEmpty());

    }


    @Test
    public void testReset() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        hashMap.put("a1","b");
        hashMap.put("a2","c");

        EntrySetMapIterator<String, Object> entrySetMapIterator = new EntrySetMapIterator<String, Object>(hashMap);

        assertTrue(entrySetMapIterator.hasNext());

        entrySetMapIterator.next();

        assertTrue(entrySetMapIterator.hasNext());

        entrySetMapIterator.next();

        assertFalse(entrySetMapIterator.hasNext());

        entrySetMapIterator.reset();

        assertTrue( entrySetMapIterator.hasNext() );

    }


    @Test
    public void testCreatesEntrySetMapIterator() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        EntrySetMapIterator<String, Object> entrySetMapIterator = new EntrySetMapIterator<String, Object>(hashMap);

    }


}