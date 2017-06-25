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

import org.apache.commons.collections4.OrderedMapIterator;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;


/**
 * Unit tests for class {@link AbstractOrderedMapIteratorDecorator}.
 *
 * @date 25.06.2017
 * @see AbstractOrderedMapIteratorDecorator
 **/
public class AbstractOrderedMapIteratorDecoratorTest {


    @Test
    public void testFailsToCreateAbstractOrderedMapIteratorDecoratorThrowsNullPointerException() {

        AbstractOrderedMapIteratorDecorator<Object, Object> abstractOrderedMapIteratorDecorator = null;

        try {
            abstractOrderedMapIteratorDecorator = new AbstractOrderedMapIteratorDecorator<Object, Object>(null);
            fail("Expecting exception: NullPointerException");
        } catch(NullPointerException e) {
            assertEquals("OrderedMapIterator must not be null",e.getMessage());
            assertEquals(AbstractOrderedMapIteratorDecorator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testNextThrowsNoSuchElementException() {

        EmptyOrderedMapIterator<Object, Integer> emptyOrderedMapIterator =
                new EmptyOrderedMapIterator<Object, Integer>();
        AbstractOrderedMapIteratorDecorator<Object, Integer> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<Object, Integer>(emptyOrderedMapIterator);

        try {
            abstractOrderedMapIteratorDecorator.next();
            fail("Expecting exception: NoSuchElementException");
        } catch(NoSuchElementException e) {
            assertEquals("Iterator contains no elements",e.getMessage());
            assertEquals(AbstractEmptyIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testGetOrderedMapIterator() {

        EmptyOrderedMapIterator<Integer, Integer> emptyOrderedMapIterator = new EmptyOrderedMapIterator<Integer, Integer>();
        AbstractOrderedMapIteratorDecorator<Integer, Integer> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<Integer, Integer>(emptyOrderedMapIterator);
        OrderedMapIterator<Integer, Integer> orderedMapIterator = abstractOrderedMapIteratorDecorator.getOrderedMapIterator();

        assertFalse(orderedMapIterator.hasPrevious());

    }


    @Test
    public void testSetValueThrowsIllegalStateException() {

        OrderedMapIterator<String, Object> orderedMapIterator = EmptyOrderedMapIterator.emptyOrderedMapIterator();
        AbstractOrderedMapIteratorDecorator<String, Object> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<String, Object>(orderedMapIterator);

        try {
            abstractOrderedMapIteratorDecorator.setValue(new Object());
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator contains no elements",e.getMessage());
            assertEquals(AbstractEmptyMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testGetKeyThrowsIllegalStateException() {

        OrderedMapIterator<String, String> orderedMapIterator = EmptyOrderedMapIterator.emptyOrderedMapIterator();
        AbstractOrderedMapIteratorDecorator<String, String> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<String, String>(orderedMapIterator);

        try {
            abstractOrderedMapIteratorDecorator.getKey();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator contains no elements",e.getMessage());
            assertEquals(AbstractEmptyMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testPreviousThrowsNoSuchElementException() {

        OrderedMapIterator<Object, Object> orderedMapIterator = EmptyOrderedMapIterator.emptyOrderedMapIterator();
        AbstractOrderedMapIteratorDecorator<Object, Object> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<Object, Object>(orderedMapIterator);

        try {
            abstractOrderedMapIteratorDecorator.previous();
            fail("Expecting exception: NoSuchElementException");
        } catch(NoSuchElementException e) {
            assertEquals("Iterator contains no elements",e.getMessage());
            assertEquals(AbstractEmptyIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testHasNext() {

        OrderedMapIterator<String, String> orderedMapIterator = EmptyOrderedMapIterator.emptyOrderedMapIterator();
        AbstractOrderedMapIteratorDecorator<String, String> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<String, String>(orderedMapIterator);

        assertFalse(abstractOrderedMapIteratorDecorator.hasNext());

    }


    @Test
    public void testHasPrevious() {

        EmptyOrderedMapIterator<String, Integer> emptyOrderedMapIterator = new EmptyOrderedMapIterator<String, Integer>();
        AbstractOrderedMapIteratorDecorator<String, Integer> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<String, Integer>(emptyOrderedMapIterator);

        assertFalse(abstractOrderedMapIteratorDecorator.hasPrevious());

    }


    @Test
    public void testRemoveThrowsIllegalStateException() {

        OrderedMapIterator<Object, Integer> orderedMapIterator = EmptyOrderedMapIterator.emptyOrderedMapIterator();
        AbstractOrderedMapIteratorDecorator<Object, Integer> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<Object, Integer>(orderedMapIterator);

        try {
            abstractOrderedMapIteratorDecorator.remove();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator contains no elements",e.getMessage());
            assertEquals(AbstractEmptyIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testGetValueThrowsIllegalStateException() {

        EmptyOrderedMapIterator<Object, String> emptyOrderedMapIterator = new EmptyOrderedMapIterator<Object, String>();
        AbstractOrderedMapIteratorDecorator<Object, String> abstractOrderedMapIteratorDecorator =
                new AbstractOrderedMapIteratorDecorator<Object, String>(emptyOrderedMapIterator);

        try {
            abstractOrderedMapIteratorDecorator.getValue();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Iterator contains no elements",e.getMessage());
            assertEquals(AbstractEmptyMapIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


}