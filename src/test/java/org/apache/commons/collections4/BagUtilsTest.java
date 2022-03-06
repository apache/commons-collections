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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.Test;

/**
 * Tests for BagUtils factory methods.
 */
public class BagUtilsTest {

    protected Predicate<Object> truePredicate = TruePredicate.truePredicate();
    protected Transformer<Object, Object> nopTransformer = TransformerUtils.nopTransformer();

    //----------------------------------------------------------------------

    @Test
    public void testSynchronizedBag() {
        final Bag<Object> bag = BagUtils.synchronizedBag(new HashBag<>());
        assertTrue(bag instanceof SynchronizedBag, "Returned object should be a SynchronizedBag.");
        assertThrows(NullPointerException.class, () -> BagUtils.synchronizedBag(null),
                "Expecting NullPointerException for null bag.");
    }

    @Test
    public void testUnmodifiableBag() {
        final Bag<Object> bag = BagUtils.unmodifiableBag(new HashBag<>());
        assertTrue(bag instanceof UnmodifiableBag, "Returned object should be an UnmodifiableBag.");
        assertThrows(NullPointerException.class, () -> BagUtils.unmodifiableBag(null),
                "Expecting NullPointerException for null bag.");
        assertSame(bag, BagUtils.unmodifiableBag(bag), "UnmodifiableBag shall not be decorated");
    }

    @Test
    public void testPredicatedBag() {
        final Bag<Object> bag = BagUtils.predicatedBag(new HashBag<>(), truePredicate);
        assertTrue(bag instanceof PredicatedBag, "Returned object should be a PredicatedBag.");
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> BagUtils.predicatedBag(null, truePredicate),
                        "Expecting NullPointerException for null bag."),
                () -> assertThrows(NullPointerException.class, () -> BagUtils.predicatedBag(new HashBag<>(), null),
                        "Expecting NullPointerException for null predicate.")
        );
    }

    @Test
    public void testTransformedBag() {
        final Bag<Object> bag = BagUtils.transformingBag(new HashBag<>(), nopTransformer);
        assertTrue(bag instanceof TransformedBag, "Returned object should be an TransformedBag.");
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> BagUtils.transformingBag(null, nopTransformer),
                        "Expecting NullPointerException for null bag."),
                () -> assertThrows(NullPointerException.class, () -> BagUtils.transformingBag(new HashBag<>(), null),
                        "Expecting NullPointerException for null transformer.")
        );
    }

    @Test
    public void testSynchronizedSortedBag() {
        final Bag<Object> bag = BagUtils.synchronizedSortedBag(new TreeBag<>());
        assertTrue(bag instanceof SynchronizedSortedBag, "Returned object should be a SynchronizedSortedBag.");
        assertThrows(NullPointerException.class, () -> BagUtils.synchronizedSortedBag(null),
                "Expecting NullPointerException for null bag.");
    }

    @Test
    public void testUnmodifiableSortedBag() {
        final SortedBag<Object> bag = BagUtils.unmodifiableSortedBag(new TreeBag<>());
        assertTrue(bag instanceof UnmodifiableSortedBag, "Returned object should be an UnmodifiableSortedBag.");
        assertThrows(NullPointerException.class, () -> BagUtils.unmodifiableSortedBag(null),
                "Expecting NullPointerException for null bag.");
        assertSame(bag, BagUtils.unmodifiableSortedBag(bag), "UnmodifiableSortedBag shall not be decorated");
    }

    @Test
    public void testPredicatedSortedBag() {
        final Bag<Object> bag = BagUtils.predicatedSortedBag(new TreeBag<>(), truePredicate);
        assertTrue(bag instanceof PredicatedSortedBag, "Returned object should be a PredicatedSortedBag.");
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> BagUtils.predicatedSortedBag(null, truePredicate),
                        "Expecting NullPointerException for null bag."),
                () -> assertThrows(NullPointerException.class, () -> BagUtils.predicatedSortedBag(new TreeBag<>(), null),
                        "Expecting NullPointerException for null predicate.")
        );
    }

    @Test
    public void testTransformedSortedBag() {
        final Bag<Object> bag = BagUtils.transformingSortedBag(new TreeBag<>(), nopTransformer);
        assertTrue(bag instanceof TransformedSortedBag, "Returned object should be an TransformedSortedBag");
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> BagUtils.transformingSortedBag(null, nopTransformer),
                        "Expecting NullPointerException for null bag."),
                () -> assertThrows(NullPointerException.class, () -> BagUtils.transformingSortedBag(new TreeBag<>(), null),
                        "Expecting NullPointerException for null transformer.")
        );
    }

}
