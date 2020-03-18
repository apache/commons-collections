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

import java.util.ListIterator;

import org.apache.commons.collections4.ResettableListIterator;

/**
 * Provides an implementation of an empty list iterator.
 * <p>
 * This class provides an implementation of an empty list iterator. This class
 * provides for binary compatibility between Commons Collections 2.1.1 and 3.1
 * due to issues with {@code IteratorUtils}.
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 2.1.1 and 3.1
 */
public class EmptyListIterator<E> extends AbstractEmptyIterator<E> implements
        ResettableListIterator<E> {

    /**
     * Singleton instance of the iterator.
     * @since 3.1
     */
    @SuppressWarnings("rawtypes")
    public static final ResettableListIterator RESETTABLE_INSTANCE = new EmptyListIterator<>();

    /**
     * Singleton instance of the iterator.
     * @since 2.1.1 and 3.1
     */
    @SuppressWarnings("rawtypes")
    public static final ListIterator INSTANCE = RESETTABLE_INSTANCE;

    /**
     * Get a typed instance of the iterator.
     * @param <E> the element type
     * @return {@link ResettableListIterator}&lt;E&gt;
     */
    public static <E> ResettableListIterator<E> resettableEmptyListIterator() {
        return RESETTABLE_INSTANCE;
    }

    /**
     * Get a typed instance of the iterator.
     * @param <E> the element type
     * @return {@link ListIterator}&lt;E&gt;
     */
    public static <E> ListIterator<E> emptyListIterator() {
        return INSTANCE;
    }

    /**
     * Constructor.
     */
    protected EmptyListIterator() {
        super();
    }

}
