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
import java.util.List;

import org.apache.commons.collections4.Unmodifiable;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link UnmodifiableCollection} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class UnmodifiableCollectionTest<E> extends AbstractCollectionTest<E> {

    public UnmodifiableCollectionTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Collection<E> makeObject() {
        return UnmodifiableCollection.unmodifiableCollection(new ArrayList<E>());
    }

    @Override
    public Collection<E> makeFullCollection() {
        final List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableCollection.unmodifiableCollection(list);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final ArrayList<E> list = new ArrayList<E>();
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

    //-----------------------------------------------------------------------
    
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }
    
    public void testDecorateFactory() {
        final Collection<E> coll = makeFullCollection();
        assertSame(coll, UnmodifiableCollection.unmodifiableCollection(coll));

        try {
            UnmodifiableCollection.unmodifiableCollection(null);
            fail();
        } catch (final IllegalArgumentException ex) {}
    }

    //-----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableCollection.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableCollection.fullCollection.version4.obj");
//    }

}
