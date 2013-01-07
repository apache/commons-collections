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

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.collection.AbstractCollectionTest;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link UnmodifiableBuffer} implementation.
 *
 * @since 3.1
 * @version $Id$
 */
public class UnmodifiableBufferTest<E> extends AbstractCollectionTest<E> {

    public UnmodifiableBufferTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Collection<E> makeObject() {
        return UnmodifiableBuffer.unmodifiableBuffer(new UnboundedFifoBuffer<E>());
    }

    @Override
    public Collection<E> makeFullCollection() {
        final Buffer<E> buffer = new UnboundedFifoBuffer<E>();
        buffer.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableBuffer.unmodifiableBuffer(buffer);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayStack<E>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final ArrayStack<E> list = new ArrayStack<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    public void testBufferRemove() {
        resetEmpty();
        try {
            getCollection().remove();
            fail();
        } catch (final UnsupportedOperationException ex) {}
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnmodifiableBuffer.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnmodifiableBuffer.fullCollection.version3.1.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer<E> getCollection() {
        return (Buffer<E>) super.getCollection();
    }
}
