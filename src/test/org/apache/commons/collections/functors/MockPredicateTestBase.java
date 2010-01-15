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

import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.replay;
import org.junit.Before;
import org.junit.After;
import org.apache.commons.collections.Predicate;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for tests of predicates which delegate to other predicates when evaluating an object.  This class
 * provides methods to create and verify mock predicates to which to delegate.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 468603 $ $Date: 2006-10-27 17:52:37 -0700 (Fri, 27 Oct 2006) $
 *
 * @author Edwin Tellman
 */
public abstract class MockPredicateTestBase<T> {
    /**
     * Mock predicates created by a single test case which need to be verified after the test completes.
     */
    private List<Predicate<? super T>> mockPredicatesToVerify;
    
    /**
     * The value to pass to mocks.
     */
    private final T testValue;

    /**
     * Creates a new <code>PredicateTestBase</code>.
     *
     * @param testValue the value to pass to mock predicates.
     */
    protected MockPredicateTestBase(final T testValue) {
        this.testValue = testValue;
    }

    /**
     * Creates the list of predicates to verify.
     */
    @Before
    public final void createVerifyList()
    {
        mockPredicatesToVerify = new ArrayList<Predicate<? super T>>();
    }

    /**
     * Verifies all the mock predicates created for the test.
     */
    @After
    public final void verifyPredicates()
    {
        for (Predicate<? super T> predicate : mockPredicatesToVerify) {
            verify(predicate);
        }
    }

    /**
     * Gets the value which will be passed to the mock predicates.
     *
     * @return the test value.
     */
    protected final T getTestValue() {
        return testValue;
    }

    /**
     * Creates a single mock predicate.
     *
     * @param returnValue the return value for the mock predicate, or null if the mock is not expected to be called.
     *
     * @return a single mock predicate.
     */
    @SuppressWarnings({"unchecked"})
    protected final Predicate<T> createMockPredicate(final Boolean returnValue) {
        final Predicate<T> mockPredicate = EasyMock.createMock(Predicate.class);
        if (returnValue != null) {
            EasyMock.expect(mockPredicate.evaluate(testValue)).andReturn(returnValue);
        }
        replay(mockPredicate);
        mockPredicatesToVerify.add(mockPredicate);

        return mockPredicate;
    }
}
