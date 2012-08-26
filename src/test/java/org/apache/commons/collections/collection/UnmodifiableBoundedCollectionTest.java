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
package org.apache.commons.collections.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.BoundedBuffer;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link UnmodifiableBoundedCollection} implementation.
 * 
 * @version $Id$
 */
public class UnmodifiableBoundedCollectionTest<E> extends AbstractCollectionTest<E> {

    public UnmodifiableBoundedCollectionTest(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Collection<E> makeObject() {
        BoundedBuffer<E> buffer = BoundedBuffer.<E>boundedBuffer(new ArrayStack<E>(), 10);
        return UnmodifiableBoundedCollection.unmodifiableBoundedCollection(buffer);
    }

    @Override
    public Collection<E> makeFullCollection() {
        E[] allElements = getFullElements();
        Buffer<E> buffer = BufferUtils.boundedBuffer(new ArrayStack<E>(), allElements.length);
        buffer.addAll(Arrays.asList(allElements));
        return UnmodifiableBoundedCollection.unmodifiableBoundedCollection(buffer);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        ArrayList<E> list = new ArrayList<E>();
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
    protected boolean skipSerializedCanonicalTests() {
        return true;
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }
}
