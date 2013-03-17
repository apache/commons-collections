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
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

/**
 * Provides utilities for making mock-based tests.  Most notable is the generic "type-safe"
 * {@link #createMock(Class)} method, and {@link #replay()} and {@link #verify()} methods
 * that call the respective methods on all created mock objects.
 */
public abstract class MockTestCase {
    private final List<Object> mockObjects = new ArrayList<Object>();

    @SuppressWarnings("unchecked")
    protected <T> T createMock(final Class<?> name) {
        final T mock = (T) EasyMock.createMock(name);
        return registerMock(mock);
    }

    private <T> T registerMock(final T mock) {
        mockObjects.add(mock);
        return mock;
    }

    protected <T> IExpectationSetters<T> expect(final T t) {
        return EasyMock.expect(t);
    }

    protected final void replay() {
        for (final Object o : mockObjects) {
            EasyMock.replay(o);
        }
    }

    protected final void verify() {
        for (final ListIterator<Object> i = mockObjects.listIterator(); i.hasNext();) {
            try {
                EasyMock.verify(i.next());
            } catch (final AssertionError e) {
                throw new AssertionError(i.previousIndex() + 1 + ""
                        + e.getMessage());
            }
        }
    }
}
