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

import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.ResettableIterator;

/**
 * Provides an implementation of an empty ordered iterator.
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 3.1
 */
public class EmptyOrderedIterator<E> extends AbstractEmptyIterator<E>
        implements OrderedIterator<E>, ResettableIterator<E> {

    /**
     * Singleton instance of the iterator.
     * @since 3.1
     */
    @SuppressWarnings("rawtypes")
    public static final OrderedIterator INSTANCE = new EmptyOrderedIterator<>();

    /**
     * Typed instance of the iterator.
     * @param <E> the element type
     * @return OrderedIterator&lt;E&gt;
     */
    public static <E> OrderedIterator<E> emptyOrderedIterator() {
        return INSTANCE;
    }

    /**
     * Constructor.
     */
    protected EmptyOrderedIterator() {
        super();
    }

}
