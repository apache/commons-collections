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

import static org.junit.Assert.assertSame;
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
        assertTrue(bag instanceof SynchronizedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.synchronizedBag(null);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));
    }

    @Test
    public void testUnmodifiableBag() {
        final Bag<Object> bag = BagUtils.unmodifiableBag(new HashBag<>());
        assertTrue(bag instanceof UnmodifiableBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.unmodifiableBag(null);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));

        assertSame("UnmodifiableBag shall not be decorated", bag, BagUtils.unmodifiableBag(bag));
    }

    @Test
    public void testPredicatedBag() {
        final Bag<Object> bag = BagUtils.predicatedBag(new HashBag<>(), truePredicate);
        assertTrue(bag instanceof PredicatedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.predicatedBag(null, truePredicate);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));

        exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.predicatedBag(new HashBag<>(), null);
        });
        actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("predicate"));
    }

    @Test
    public void testTransformedBag() {
        final Bag<Object> bag = BagUtils.transformingBag(new HashBag<>(), nopTransformer);
        assertTrue(bag instanceof TransformedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.transformingBag(null, nopTransformer);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));

        exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.transformingBag(new HashBag<>(), null);
        });
        actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("transformer"));
    }

    @Test
    public void testSynchronizedSortedBag() {
        final Bag<Object> bag = BagUtils.synchronizedSortedBag(new TreeBag<>());
        assertTrue(bag instanceof SynchronizedSortedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.synchronizedSortedBag(null);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));
    }

    @Test
    public void testUnmodifiableSortedBag() {
        final SortedBag<Object> bag = BagUtils.unmodifiableSortedBag(new TreeBag<>());
        assertTrue(bag instanceof UnmodifiableSortedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.unmodifiableSortedBag(null);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));

        assertSame("UnmodifiableSortedBag shall not be decorated", bag, BagUtils.unmodifiableSortedBag(bag));
    }

    @Test
    public void testPredicatedSortedBag() {
        final Bag<Object> bag = BagUtils.predicatedSortedBag(new TreeBag<>(), truePredicate);
        assertTrue(bag instanceof PredicatedSortedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.predicatedSortedBag(null, truePredicate);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));

        exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.predicatedSortedBag(new TreeBag<>(), null);
        });
        actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("predicate"));
    }

    @Test
    public void testTransformedSortedBag() {
        final Bag<Object> bag = BagUtils.transformingSortedBag(new TreeBag<>(), nopTransformer);
        assertTrue(bag instanceof TransformedSortedBag);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.transformingSortedBag(null, nopTransformer);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("collection"));

        exception = assertThrows(NullPointerException.class, () -> {
            BagUtils.transformingSortedBag(new TreeBag<>(), null);
        });
        actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("transformer"));
    }
}


