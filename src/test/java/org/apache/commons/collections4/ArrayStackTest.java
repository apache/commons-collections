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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EmptyStackException;

import org.junit.jupiter.api.Test;

/**
 * Tests ArrayStack.
 */
@SuppressWarnings("deprecation") // we test a deprecated class
public class ArrayStackTest<E> extends AbstractArrayListTest<E> {

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public ArrayStack<E> makeObject() {
        return new ArrayStack<>();
    }

    @Test
    public void testNewStack() {
        final ArrayStack<E> stack = makeObject();
        assertTrue(stack.empty(), "New stack is empty");
        assertEquals(0, stack.size(), "New stack has size zero");

        assertThrows(EmptyStackException.class, () -> stack.peek());

        assertThrows(EmptyStackException.class, () -> stack.pop());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPushPeekPop() {
        final ArrayStack<E> stack = makeObject();

        stack.push((E) "First Item");
        assertFalse(stack.empty(), "Stack is not empty");
        assertEquals(1, stack.size(), "Stack size is one");
        assertEquals("First Item", (String) stack.peek(),
                "Top item is 'First Item'");
        assertEquals(1, stack.size(), "Stack size is one");

        stack.push((E) "Second Item");
        assertEquals(2, stack.size(), "Stack size is two");
        assertEquals("Second Item", (String) stack.peek(),
                "Top item is 'Second Item'");
        assertEquals(2, stack.size(), "Stack size is two");

        assertEquals("Second Item", (String) stack.pop(),
                "Popped item is 'Second Item'");
        assertEquals("First Item", (String) stack.peek(),
                "Top item is 'First Item'");
        assertEquals(1, stack.size(), "Stack size is one");

        assertEquals("First Item", (String) stack.pop(),
                "Popped item is 'First Item'");
        assertEquals(0, stack.size(), "Stack size is zero");
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testSearch() {
        final ArrayStack<E> stack = makeObject();

        stack.push((E) "First Item");
        stack.push((E) "Second Item");
        assertEquals(1, stack.search("Second Item"),
                "Top item is 'Second Item'");
        assertEquals(2, stack.search("First Item"),
                "Next Item is 'First Item'");
        assertEquals(-1, stack.search("Missing Item"),
                "Cannot find 'Missing Item'");
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/ArrayStack.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/ArrayStack.fullCollection.version4.obj");
//    }

}
