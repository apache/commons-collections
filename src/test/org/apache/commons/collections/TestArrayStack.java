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

import java.util.EmptyStackException;

import junit.framework.Test;

/**
 * Tests ArrayStack.
 *
 * @version $Revision$ $Date$
 *
 * @author Craig McClanahan
 */
public class TestArrayStack<E> extends TestArrayList<E> {
    
    public TestArrayStack(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestArrayStack.class);
    }

    @Override
    public ArrayStack<E> makeObject() {
        return new ArrayStack<E>();
    }

    //-----------------------------------------------------------------------
    public void testNewStack() {
        ArrayStack<E> stack = makeObject();
        assertTrue("New stack is empty", stack.empty());
        assertEquals("New stack has size zero", 0, stack.size());

        try {
            stack.peek();
            fail("peek() should have thrown EmptyStackException");
        } catch (EmptyStackException e) {
            // Expected result
        }

        try {
            stack.pop();
            fail("pop() should have thrown EmptyStackException");
        } catch (EmptyStackException e) {
            // Expected result
        }

    }

    @SuppressWarnings("unchecked")
    public void testPushPeekPop() {
        ArrayStack<E> stack = makeObject();

        stack.push((E) "First Item");
        assertTrue("Stack is not empty", !stack.empty());
        assertEquals("Stack size is one", 1, stack.size());
        assertEquals("Top item is 'First Item'",
                     "First Item", (String) stack.peek());
        assertEquals("Stack size is one", 1, stack.size());

        stack.push((E) "Second Item");
        assertEquals("Stack size is two", 2, stack.size());
        assertEquals("Top item is 'Second Item'",
                     "Second Item", (String) stack.peek());
        assertEquals("Stack size is two", 2, stack.size());

        assertEquals("Popped item is 'Second Item'",
                     "Second Item", (String) stack.pop());
        assertEquals("Top item is 'First Item'",
                     "First Item", (String) stack.peek());
        assertEquals("Stack size is one", 1, stack.size());

        assertEquals("Popped item is 'First Item'",
                     "First Item", (String) stack.pop());
        assertEquals("Stack size is zero", 0, stack.size());

    }

    @Override
    @SuppressWarnings("unchecked")
    public void testSearch() {
        ArrayStack<E> stack = makeObject();

        stack.push((E) "First Item");
        stack.push((E) "Second Item");
        assertEquals("Top item is 'Second Item'",
                     1, stack.search("Second Item"));
        assertEquals("Next Item is 'First Item'",
                     2, stack.search("First Item"));
        assertEquals("Cannot find 'Missing Item'",
                     -1, stack.search("Missing Item"));

    }

}
