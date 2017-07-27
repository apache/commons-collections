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
package org.apache.commons.collections4.multiset;

import java.util.Arrays;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Unmodifiable;

/**
 * Extension of {@link AbstractMultiSetTest} for exercising the
 * {@link UnmodifiableMultiSet} implementation.
 *
 * @since 4.1
 * @version $Id$
 */
public class UnmodifiableMultiSetTest<E> extends AbstractMultiSetTest<E> {

    public UnmodifiableMultiSetTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(UnmodifiableMultiSetTest.class);
    }

    //-----------------------------------------------------------------------
    @Override
    public MultiSet<E> makeObject() {
        return UnmodifiableMultiSet.unmodifiableMultiSet(new HashMultiSet<E>());
    }

    @Override
    public MultiSet<E> makeFullCollection() {
        final MultiSet<E> multiset = new HashMultiSet<>();
        multiset.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
    }

    @Override
    public MultiSet<E> getCollection() {
        return super.getCollection();
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

    //-----------------------------------------------------------------------

    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }
    
    public void testDecorateFactory() {
        final MultiSet<E> multiset = makeFullCollection();
        assertSame(multiset, UnmodifiableMultiSet.unmodifiableMultiSet(multiset));

        try {
            UnmodifiableMultiSet.unmodifiableMultiSet(null);
            fail();
        } catch (final NullPointerException ex) {}
    }

    //-----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4.1";
    }

//    public void testCreate() throws Exception {
//        MultiSet<E> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/UnmodifiableMultiSet.emptyCollection.version4.1.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/UnmodifiableMultiSet.fullCollection.version4.1.obj");
//    }

}
