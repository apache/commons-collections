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

import static org.junit.Assert.*;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.PredicatedBag;
import org.apache.commons.collections4.bag.PredicatedSortedBag;
import org.apache.commons.collections4.bag.SynchronizedBag;
import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.apache.commons.collections4.bag.TransformedBag;
import org.apache.commons.collections4.bag.TransformedSortedBag;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.bag.UnmodifiableBag;
import org.apache.commons.collections4.bag.UnmodifiableSortedBag;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.Test;

/**
 * Tests for BagUtils factory methods.
 *
 */
public class BagUtilsTest {

    protected Predicate<Object> truePredicate = TruePredicate.truePredicate();
    protected Transformer<Object, Object> nopTransformer = TransformerUtils.nopTransformer();

    //----------------------------------------------------------------------

    @Test
    public void testSynchronizedBag() {
        final Bag<Object> bag = BagUtils.synchronizedBag(new HashBag<>());
        assertTrue("Returned object should be a SynchronizedBag.",
            bag instanceof SynchronizedBag);
        try {
            BagUtils.synchronizedBag(null);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testUnmodifiableBag() {
        final Bag<Object> bag = BagUtils.unmodifiableBag(new HashBag<>());
        assertTrue("Returned object should be an UnmodifiableBag.",
            bag instanceof UnmodifiableBag);
        try {
            BagUtils.unmodifiableBag(null);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }

        assertSame("UnmodifiableBag shall not be decorated", bag, BagUtils.unmodifiableBag(bag));
    }

    @Test
    public void testPredicatedBag() {
        final Bag<Object> bag = BagUtils.predicatedBag(new HashBag<>(), truePredicate);
        assertTrue("Returned object should be a PredicatedBag.",
            bag instanceof PredicatedBag);
        try {
            BagUtils.predicatedBag(null,truePredicate);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            BagUtils.predicatedBag(new HashBag<>(), null);
            fail("Expecting NullPointerException for null predicate.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testTransformedBag() {
        final Bag<Object> bag = BagUtils.transformingBag(new HashBag<>(), nopTransformer);
        assertTrue("Returned object should be an TransformedBag.",
            bag instanceof TransformedBag);
        try {
            BagUtils.transformingBag(null, nopTransformer);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            BagUtils.transformingBag(new HashBag<>(), null);
            fail("Expecting NullPointerException for null transformer.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testSynchronizedSortedBag() {
        final Bag<Object> bag = BagUtils.synchronizedSortedBag(new TreeBag<>());
        assertTrue("Returned object should be a SynchronizedSortedBag.",
            bag instanceof SynchronizedSortedBag);
        try {
            BagUtils.synchronizedSortedBag(null);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testUnmodifiableSortedBag() {
        final SortedBag<Object> bag = BagUtils.unmodifiableSortedBag(new TreeBag<>());
        assertTrue("Returned object should be an UnmodifiableSortedBag.",
            bag instanceof UnmodifiableSortedBag);
        try {
            BagUtils.unmodifiableSortedBag(null);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }

        assertSame("UnmodifiableSortedBag shall not be decorated", bag, BagUtils.unmodifiableSortedBag(bag));
    }

    @Test
    public void testPredicatedSortedBag() {
        final Bag<Object> bag = BagUtils.predicatedSortedBag(new TreeBag<>(), truePredicate);
        assertTrue("Returned object should be a PredicatedSortedBag.",
            bag instanceof PredicatedSortedBag);
        try {
            BagUtils.predicatedSortedBag(null, truePredicate);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            BagUtils.predicatedSortedBag(new TreeBag<>(), null);
            fail("Expecting NullPointerException for null predicate.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testTransformedSortedBag() {
        final Bag<Object> bag = BagUtils.transformingSortedBag(new TreeBag<>(), nopTransformer);
        assertTrue("Returned object should be an TransformedSortedBag",
            bag instanceof TransformedSortedBag);
        try {
            BagUtils.transformingSortedBag(null, nopTransformer);
            fail("Expecting NullPointerException for null bag.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            BagUtils.transformingSortedBag(new TreeBag<>(), null);
            fail("Expecting NullPointerException for null transformer.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }
}


