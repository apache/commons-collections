/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;

import org.apache.commons.collections.collection.AbstractTestCollection;

/**
 * Test cases for UnboundedFifoBuffer.
 * 
 * @version $Revision$ $Date$
 * 
 * @author Unknown
 */
public class TestUnboundedFifoBuffer extends AbstractTestCollection {

    public TestUnboundedFifoBuffer(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestUnboundedFifoBuffer.class);
    }

    //-----------------------------------------------------------------------
    /**
     *  Verifies that the ArrayList has the same elements in the same 
     *  sequence as the UnboundedFifoBuffer.
     */
    public void verify() {
        super.verify();
        Iterator iterator1 = collection.iterator();
        Iterator iterator2 = confirmed.iterator();
        while (iterator2.hasNext()) {
            assertTrue(iterator1.hasNext());
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            assertEquals(o1, o2);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Overridden because UnboundedFifoBuffer doesn't allow null elements.
     * @return false
     */
    public boolean isNullSupported() {
        return false;
    }

    /**
     * Overridden because UnboundedFifoBuffer isn't fail fast.
     * @return false
     */
    public boolean isFailFastSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns an empty ArrayList.
     *
     *  @return an empty ArrayList
     */
    public Collection makeConfirmedCollection() {
        return new ArrayList();
    }

    /**
     *  Returns a full ArrayList.
     *
     *  @return a full ArrayList
     */
    public Collection makeConfirmedFullCollection() {
        Collection c = makeConfirmedCollection();
        c.addAll(java.util.Arrays.asList(getFullElements()));
        return c;
    }

    /**
     *  Returns an empty UnboundedFifoBuffer with a small capacity.
     *
     *  @return an empty UnboundedFifoBuffer
     */
    public Collection makeCollection() {
        return new UnboundedFifoBuffer(5);
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests that UnboundedFifoBuffer removes elements in the right order.
     */
    public void testUnboundedFifoBufferRemove() {
        resetFull();
        int size = confirmed.size();
        for (int i = 0; i < size; i++) {
            Object o1 = ((UnboundedFifoBuffer)collection).remove();
            Object o2 = ((ArrayList)confirmed).remove(0);
            assertEquals("Removed objects should be equal", o1, o2);
            verify();
        }
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException1() {
        try {
            new UnboundedFifoBuffer(0);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException2() {
        try {
            new UnboundedFifoBuffer(-20);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
}

