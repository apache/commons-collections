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
package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.ResettableListIterator;

/**
 * Tests the SingletonListIterator.
 *
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestSingletonListIterator<E> extends AbstractTestListIterator<E> {

    private static final Object testValue = "foo";
    
    public static Test suite() {
        return new TestSuite(TestSingletonListIterator.class);
    }
    
    public TestSingletonListIterator(String testName) {
        super(testName);
    }
    
    /**
     * Returns a SingletonListIterator from which 
     * the element has already been removed.
     */
    public SingletonListIterator<E> makeEmptyIterator() {
        SingletonListIterator<E> iter = makeObject();
        iter.next();
        iter.remove();
        iter.reset();        
        return iter;
    }

    @SuppressWarnings("unchecked")
    public SingletonListIterator<E> makeObject() {
        return new SingletonListIterator<E>((E) testValue);
    }

    public boolean supportsAdd() {
        return false;
    }

    public boolean supportsRemove() {
        return true;
    }

    public boolean supportsEmptyIterator() {
        return true;
    }

    public void testIterator() {
        ListIterator<E> iter = makeObject();
        assertTrue( "Iterator should have next item", iter.hasNext() );
        assertTrue( "Iterator should have no previous item", !iter.hasPrevious() );
        assertEquals( "Iteration next index", 0, iter.nextIndex() );
        assertEquals( "Iteration previous index", -1, iter.previousIndex() );
        
        Object iterValue = iter.next();
        assertEquals( "Iteration value is correct", testValue, iterValue );
        
        assertTrue( "Iterator should have no next item", !iter.hasNext() );
        assertTrue( "Iterator should have previous item", iter.hasPrevious() );
        assertEquals( "Iteration next index", 1, iter.nextIndex() );
        assertEquals( "Iteration previous index", 0, iter.previousIndex() );

        iterValue = iter.previous();
        assertEquals( "Iteration value is correct", testValue, iterValue );
        
        assertTrue( "Iterator should have next item", iter.hasNext() );
        assertTrue( "Iterator should have no previous item", !iter.hasPrevious() );
        assertEquals( "Iteration next index", 0, iter.nextIndex() );
        assertEquals( "Iteration previous index", -1, iter.previousIndex() );

        iterValue = iter.next();
        assertEquals( "Iteration value is correct", testValue, iterValue );
        
        assertTrue( "Iterator should have no next item", !iter.hasNext() );
        assertTrue( "Iterator should have previous item", iter.hasPrevious() );
        assertEquals( "Iteration next index", 1, iter.nextIndex() );
        assertEquals( "Iteration previous index", 0, iter.previousIndex() );

        try {
            iter.next();
        } catch (Exception e) {
          assertTrue("NoSuchElementException must be thrown", 
             e.getClass().equals((new NoSuchElementException()).getClass()));
        }
        iter.previous();
        try {
            iter.previous();
        } catch (Exception e) {
          assertTrue("NoSuchElementException must be thrown", 
             e.getClass().equals((new NoSuchElementException()).getClass()));
        }
    }
    
    public void testReset() {
        ResettableListIterator<E> it = makeObject();
        
        assertEquals(true, it.hasNext());
        assertEquals(false, it.hasPrevious());
        assertEquals(testValue, it.next());
        assertEquals(false, it.hasNext());
        assertEquals(true, it.hasPrevious());

        it.reset();
        
        assertEquals(true, it.hasNext());
        assertEquals(false, it.hasPrevious());
        assertEquals(testValue, it.next());
        assertEquals(false, it.hasNext());
        assertEquals(true, it.hasPrevious());
        
        it.reset();
        it.reset();
        
        assertEquals(true, it.hasNext());
    }
    
}

