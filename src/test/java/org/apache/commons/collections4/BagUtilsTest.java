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

import junit.framework.Test;

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

/**
 * Tests for BagUtils factory methods.
 *
 * @version $Id$
 */
public class BagUtilsTest extends BulkTest {

    public BagUtilsTest(final String name) {
        super(name);
    }


    public static Test suite() {
        return BulkTest.makeSuite(BagUtilsTest.class);
    }

    //----------------------------------------------------------------------

    protected Class<?> stringClass = this.getName().getClass();
    protected Predicate<Object> truePredicate = TruePredicate.truePredicate();
    protected Transformer<Object, Object> nopTransformer = TransformerUtils.nopTransformer();

    //----------------------------------------------------------------------

    public void testSynchronizedBag() {
        Bag<Object> bag = BagUtils.synchronizedBag(new HashBag<Object>());
        assertTrue("Returned object should be a SynchronizedBag.",
            bag instanceof SynchronizedBag);
        try {
            BagUtils.synchronizedBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testUnmodifiableBag() {
        Bag<Object> bag = BagUtils.unmodifiableBag(new HashBag<Object>());
        assertTrue("Returned object should be an UnmodifiableBag.",
            bag instanceof UnmodifiableBag);
        try {
            BagUtils.unmodifiableBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testPredicatedBag() {
        Bag<Object> bag = BagUtils.predicatedBag(new HashBag<Object>(), truePredicate);
        assertTrue("Returned object should be a PredicatedBag.",
            bag instanceof PredicatedBag);
        try {
            BagUtils.predicatedBag(null,truePredicate);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            BagUtils.predicatedBag(new HashBag<Object>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

     public void testTransformedBag() {
        Bag<Object> bag = BagUtils.transformingBag(new HashBag<Object>(), nopTransformer);
        assertTrue("Returned object should be an TransformedBag.",
            bag instanceof TransformedBag);
        try {
            BagUtils.transformingBag(null, nopTransformer);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            BagUtils.transformingBag(new HashBag<Object>(), null);
            fail("Expecting IllegalArgumentException for null transformer.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testSynchronizedSortedBag() {
        Bag<Object> bag = BagUtils.synchronizedSortedBag(new TreeBag<Object>());
        assertTrue("Returned object should be a SynchronizedSortedBag.",
            bag instanceof SynchronizedSortedBag);
        try {
            BagUtils.synchronizedSortedBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testUnmodifiableSortedBag() {
        Bag<Object> bag = BagUtils.unmodifiableSortedBag(new TreeBag<Object>());
        assertTrue("Returned object should be an UnmodifiableSortedBag.",
            bag instanceof UnmodifiableSortedBag);
        try {
            BagUtils.unmodifiableSortedBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testPredicatedSortedBag() {
        Bag<Object> bag = BagUtils.predicatedSortedBag(new TreeBag<Object>(), truePredicate);
        assertTrue("Returned object should be a PredicatedSortedBag.",
            bag instanceof PredicatedSortedBag);
        try {
            BagUtils.predicatedSortedBag(null, truePredicate);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            BagUtils.predicatedSortedBag(new TreeBag<Object>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testTransformedSortedBag() {
        Bag<Object> bag = BagUtils.transformingSortedBag(new TreeBag<Object>(), nopTransformer);
        assertTrue("Returned object should be an TransformedSortedBag",
            bag instanceof TransformedSortedBag);
        try {
            BagUtils.transformingSortedBag(null, nopTransformer);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            BagUtils.transformingSortedBag(new TreeBag<Object>(), null);
            fail("Expecting IllegalArgumentException for null transformer.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }
}


