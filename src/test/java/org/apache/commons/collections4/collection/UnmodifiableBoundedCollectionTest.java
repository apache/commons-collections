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
package org.apache.commons.collections4.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.list.FixedSizeList;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link UnmodifiableBoundedCollection} implementation.
 *
 */
public class UnmodifiableBoundedCollectionTest<E> extends AbstractCollectionTest<E> {

    public UnmodifiableBoundedCollectionTest(final String testName) {
        super(testName);
    }

    @Override
    public Collection<E> makeObject() {
        final BoundedCollection<E> coll = FixedSizeList.<E>fixedSizeList(new ArrayList<E>());
        return UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
    }

    @Override
    public BoundedCollection<E> makeFullCollection() {
        final E[] allElements = getFullElements();
        final BoundedCollection<E> coll = FixedSizeList.<E>fixedSizeList(new ArrayList<>(Arrays.asList(allElements)));
        return UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        return new ArrayList<>(Arrays.asList(getFullElements()));
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
        return "4";
    }


    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        final BoundedCollection<E> coll = makeFullCollection();
        assertSame(coll, UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll));

        try {
            UnmodifiableBoundedCollection.unmodifiableBoundedCollection(null);
            fail();
        } catch (final NullPointerException ex) {}
    }

}
