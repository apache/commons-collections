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
package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Base class for tests of composite predicates.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Edwin Tellman
 */
public abstract class AbstractTestCompositePredicate<T> extends MockPredicateTestBase<T> {

    /**
     * Creates a new <code>TestCompositePredicate</code>.
     *
     * @param testValue the value which the mock predicates should expect to see (may be null).
     */
    protected AbstractTestCompositePredicate(final T testValue) {
        super(testValue);
    }

    /**
     * Creates an instance of the predicate to test.
     *
     * @param predicates the arguments to <code>getInstance</code>.
     *
     * @return a predicate to test.
     */
    protected abstract Predicate<T> getPredicateInstance(final Predicate<? super T> ... predicates);

    /**
     * Creates an instance of the predicate to test.
     *
     * @param predicates the argument to <code>getInstance</code>.
     *
     * @return a predicate to test.
     */
    protected abstract Predicate<T> getPredicateInstance(final Collection<Predicate<T>> predicates);

    /**
     * Creates an instance of the predicate to test.
     *
     * @param mockReturnValues the return values for the mock predicates, or null if that mock is not expected
     *                         to be called
     *
     * @return a predicate to test.
     */
    protected final Predicate<T> getPredicateInstance(final Boolean... mockReturnValues) {
        final List<Predicate<T>> predicates = new ArrayList<Predicate<T>>();
        for (Boolean returnValue : mockReturnValues) {
            predicates.add(createMockPredicate(returnValue));
        }
        return getPredicateInstance(predicates);
    }

    /**
     * Tests whether <code>getInstance</code> with a one element array returns the first element in the array.
     */
    @SuppressWarnings("unchecked")
    public void singleElementArrayToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(predicate);
        Assert.assertSame("expected argument to be returned by getInstance()", predicate, allPredicate);
    }

    /**
     * Tests that passing a singleton collection to <code>getInstance</code> returns the single element in the
     * collection.
     */
    public void singletonCollectionToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(
                Collections.<Predicate<T>>singleton(predicate));
        Assert.assertSame("expected argument to be returned by getInstance()", predicate, allPredicate);
    }

    /**
     * Tests <code>getInstance</code> with a null predicate array.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullArrayToGetInstance() {
        getPredicateInstance((Predicate<T>[]) null);
    }

    /**
     * Tests <code>getInstance</code> with a single null element in the predicate array.
     */
    @SuppressWarnings({"unchecked"})
    @Test(expected = IllegalArgumentException.class)
    public final void nullElementInArrayToGetInstance() {
        getPredicateInstance(new Predicate[] { null });
    }

    /**
     * Tests <code>getInstance</code> with two null elements in the predicate array.
     */
    @SuppressWarnings({"unchecked"})
    @Test(expected = IllegalArgumentException.class)
    public final void nullElementsInArrayToGetInstance() {
        getPredicateInstance(new Predicate[] { null, null });
    }


    /**
     * Tests <code>getInstance</code> with a null predicate collection
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullCollectionToGetInstance() {
        getPredicateInstance((Collection<Predicate<T>>) null);
    }

    /**
     * Tests <code>getInstance</code> with a predicate collection that contains null elements
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullElementsInCollectionToGetInstance() {
        final Collection<Predicate<T>> coll = new ArrayList<Predicate<T>>();
        coll.add(null);
        coll.add(null);
        getPredicateInstance(coll);
    }
}
