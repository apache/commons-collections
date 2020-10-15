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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.function.Executable;

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

        final Executable testMethod = () -> BagUtils.synchronizedBag(null);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("collection")));
    }

    @Test
    public void testUnmodifiableBag() {
        final Bag<Object> bag = BagUtils.unmodifiableBag(new HashBag<>());
        assertTrue("Returned object should be an UnmodifiableBag.",
                bag instanceof UnmodifiableBag);

        final Executable testMethod = () -> BagUtils.unmodifiableBag(null);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("collection")));

        assertSame("UnmodifiableBag shall not be decorated", bag, BagUtils.unmodifiableBag(bag));
    }

    @Test
    public void testPredicatedBag() {
        final Bag<Object> bag = BagUtils.predicatedBag(new HashBag<>(), truePredicate);
        assertTrue("Returned object should be a PredicatedBag.",
                bag instanceof PredicatedBag);

        final Executable testMethod0 = () -> BagUtils.predicatedBag(null, truePredicate);
        final NullPointerException thrown0 = assertThrows(NullPointerException.class, testMethod0);
        assertThat(thrown0.getMessage(), is(equalTo("collection")));

        final Executable testMethod1 = () -> BagUtils.predicatedBag(new HashBag<>(), null);
        final NullPointerException thrown1 = assertThrows(NullPointerException.class, testMethod1);
        assertThat(thrown1.getMessage(), is(equalTo("predicate")));
    }

    @Test
    public void testTransformedBag() {
        final Bag<Object> bag = BagUtils.transformingBag(new HashBag<>(), nopTransformer);
        assertTrue("Returned object should be an TransformedBag.",
                bag instanceof TransformedBag);

        final Executable testMethod0 = () -> BagUtils.transformingBag(null, nopTransformer);
        final NullPointerException thrown0 = assertThrows(NullPointerException.class, testMethod0);
        assertThat(thrown0.getMessage(), is(equalTo("collection")));

        final Executable testMethod1 = () -> BagUtils.transformingBag(new HashBag<>(), null);
        final NullPointerException thrown1 = assertThrows(NullPointerException.class, testMethod1);
        assertThat(thrown1.getMessage(), is(equalTo("transformer")));
    }

    @Test
    public void testSynchronizedSortedBag() {
        final Bag<Object> bag = BagUtils.synchronizedSortedBag(new TreeBag<>());
        assertTrue("Returned object should be a SynchronizedSortedBag.",
                bag instanceof SynchronizedSortedBag);

        final Executable testMethod = () -> BagUtils.synchronizedSortedBag(null);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("collection")));
    }

    @Test
    public void testUnmodifiableSortedBag() {
        final SortedBag<Object> bag = BagUtils.unmodifiableSortedBag(new TreeBag<>());
        assertTrue("Returned object should be an UnmodifiableSortedBag.",
                bag instanceof UnmodifiableSortedBag);

        final Executable testMethod = () -> BagUtils.unmodifiableSortedBag(null);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("collection")));

        assertSame("UnmodifiableSortedBag shall not be decorated", bag, BagUtils.unmodifiableSortedBag(bag));
    }

    @Test
    public void testPredicatedSortedBag() {
        final Bag<Object> bag = BagUtils.predicatedSortedBag(new TreeBag<>(), truePredicate);
        assertTrue("Returned object should be a PredicatedSortedBag.",
                bag instanceof PredicatedSortedBag);

        final Executable testMethod0 = () -> BagUtils.predicatedSortedBag(null, truePredicate);
        final NullPointerException thrown0 = assertThrows(NullPointerException.class, testMethod0);
        assertThat(thrown0.getMessage(), is(equalTo("collection")));

        final Executable testMethod1 = () -> BagUtils.predicatedSortedBag(new TreeBag<>(), null);
        final NullPointerException thrown1 = assertThrows(NullPointerException.class, testMethod1);
        assertThat(thrown1.getMessage(), is(equalTo("predicate")));
    }

    @Test
    public void testTransformedSortedBag() {
        final Bag<Object> bag = BagUtils.transformingSortedBag(new TreeBag<>(), nopTransformer);
        assertTrue("Returned object should be an TransformedSortedBag",
                bag instanceof TransformedSortedBag);

        final Executable testMethod0 = () -> BagUtils.transformingSortedBag(null, nopTransformer);
        final NullPointerException thrown0 = assertThrows(NullPointerException.class, testMethod0);
        assertThat(thrown0.getMessage(), is(equalTo("collection")));

        final Executable testMethod1 = () -> BagUtils.transformingSortedBag(new TreeBag<>(), null);
        final NullPointerException thrown1 = assertThrows(NullPointerException.class, testMethod1);
        assertThat(thrown1.getMessage(), is(equalTo("transformer")));
    }

}
