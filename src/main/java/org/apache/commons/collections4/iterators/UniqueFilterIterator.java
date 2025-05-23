/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import java.util.Iterator;

import org.apache.commons.collections4.functors.UniquePredicate;

/**
 * A FilterIterator which only returns "unique" Objects.  Internally,
 * the Iterator maintains a Set of objects it has already encountered,
 * and duplicate Objects are skipped.
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 2.1
 */
public class UniqueFilterIterator<E> extends FilterIterator<E> {

    /**
     *  Constructs a new {@code UniqueFilterIterator}.
     *
     *  @param iterator  the iterator to use
     */
    public UniqueFilterIterator(final Iterator<? extends E> iterator) {
        super(iterator, UniquePredicate.uniquePredicate());
    }

}
