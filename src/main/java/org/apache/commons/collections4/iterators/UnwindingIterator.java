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
package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class to unwind an iterator of iterators.
 * @param <T> The type of the object returned from the iterator.
 * @since 4.5.0
 */
public class UnwindingIterator<T> implements Iterator<T> {
    /** The innermost iterator */
    private final Iterator<Iterator<T>> inner;
    /** The iterator extracted from the inner iterator */
    private Iterator<T> outer;

    /**
     * Constructs an iterator from an iterator of iterators.
     * <p>
     *     Unlike the IteratorChain<T> the inner iterators are not be constructed until needed so that any overhead
     *     involved in constructing an enclosed Iterator<T> is amortized across the entire operation and not front
     *     loaded on the constructor or first call.
     * </p>
     * @param it The iterator of iterators to unwind.
     */
    public UnwindingIterator(final Iterator<Iterator<T>> it) {
        this.inner = it;
    }

    @Override
    public boolean hasNext() {
        if (outer == null) {
            if (!inner.hasNext()) {
                return false;
            }
            outer = inner.next();
        }
        while (!outer.hasNext()) {
            if (!inner.hasNext()) {
                return false;
            }
            outer = inner.next();
        }
        return true;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return outer.next();
    }
}
