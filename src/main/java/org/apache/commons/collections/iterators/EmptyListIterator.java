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
package org.apache.commons.collections.iterators;

import java.util.ListIterator;

import org.apache.commons.collections.ResettableListIterator;

/**
 * Provides an implementation of an empty list iterator.
 * <p>
 * This class provides an implementation of an empty list iterator. This class
 * provides for binary compatability between Commons Collections 2.1.1 and 3.1
 * due to issues with <code>IteratorUtils</code>.
 *
 * @since 2.1.1 and 3.1
 * @version $Id$
 */
public class EmptyListIterator<E> extends AbstractEmptyIterator<E> implements
        ResettableListIterator<E> {

    /**
     * Singleton instance of the iterator.
     * @since 3.1
     */
    public static final ResettableListIterator<Object> RESETTABLE_INSTANCE = new EmptyListIterator<Object>();

    /**
     * Singleton instance of the iterator.
     * @since 2.1.1 and 3.1
     */
    public static final ListIterator<Object> INSTANCE = RESETTABLE_INSTANCE;

    /**
     * Get a typed instance of the iterator.
     * @param <E>
     * @return {@link ResettableListIterator}<E>
     */
    @SuppressWarnings("unchecked")
    public static <E> ResettableListIterator<E> resettableEmptyListIterator() {
        return (ResettableListIterator<E>) RESETTABLE_INSTANCE;
    }

    /**
     * Get a typed instance of the iterator.
     * @param <E>
     * @return {@link ListIterator}<E>
     */
    @SuppressWarnings("unchecked")
    public static <E> ListIterator<E> emptyListIterator() {
        return (ListIterator<E>) INSTANCE;
    }

    /**
     * Constructor.
     */
    protected EmptyListIterator() {
        super();
    }

}
