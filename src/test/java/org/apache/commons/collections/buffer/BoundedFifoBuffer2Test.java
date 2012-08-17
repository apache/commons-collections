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
package org.apache.commons.collections.buffer;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;

import org.apache.commons.collections.BufferOverflowException;
import org.apache.commons.collections.BulkTest;

/**
 * Runs tests against a full BoundedFifoBuffer, since many of the algorithms
 * differ depending on whether the fifo is full or not.
 *
 * @version $Id$
 */
public class BoundedFifoBuffer2Test<E> extends BoundedFifoBufferTest<E> {

    public BoundedFifoBuffer2Test(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(BoundedFifoBuffer2Test.class);
    }

    /**
     *  Returns a BoundedFifoBuffer that's filled to capacity.
     *  Any attempt to add to the returned buffer will result in a 
     *  BufferOverflowException.
     *
     *  @return a full BoundedFifoBuffer
     */
    @Override
    public Collection<E> makeFullCollection() {
        return new BoundedFifoBuffer<E>(Arrays.asList(getFullElements()));
    }

    /**
     *  Overridden to skip the add tests.  All of them would fail with a 
     *  BufferOverflowException.
     *
     *  @return false
     */
    @Override
    public boolean isAddSupported() {
        return false;
    }

    /**
     *  Overridden because the add operations raise BufferOverflowException
     *  instead of UnsupportedOperationException.
     */
    @Override
    public void testUnsupportedAdd() {
    }

    /**
     *  Tests to make sure the add operations raise BufferOverflowException.
     */
    public void testBufferOverflow() {
        resetFull();
        try {
            getCollection().add(getOtherElements()[0]);
            fail("add should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();

        try {
            getCollection().addAll(Arrays.asList(getOtherElements()));
            fail("addAll should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();
    }

    /**
     * Tests is full
     */
    @SuppressWarnings("unchecked")
    public void testIsFull() {
        resetFull();
        assertEquals(true, getCollection().isFull());
        getCollection().remove();
        assertEquals(false, getCollection().isFull());
        getCollection().add((E) "jj");
        assertEquals(true, getCollection().isFull());
    }

    /**
     * Tests max size
     */
    @SuppressWarnings("unchecked")
    public void testMaxSize() {
        resetFull();
        assertEquals(getFullElements().length, getCollection().maxSize());
        getCollection().remove();
        assertEquals(getFullElements().length, getCollection().maxSize());
        getCollection().add((E) "jj");
        assertEquals(getFullElements().length, getCollection().maxSize());
    }

}

