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

import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;

/** 
 * Provides an implementation of an empty ordered map iterator.
 *
 * @since Commons Collections 3.1
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class EmptyOrderedMapIterator<K, V> extends AbstractEmptyMapIterator<K, V> implements
        OrderedMapIterator<K, V>, ResettableIterator<K> {

    /**
     * Singleton instance of the iterator.
     * @since Commons Collections 3.1
     */
    public static final OrderedMapIterator<Object, Object> INSTANCE = new EmptyOrderedMapIterator<Object, Object>();

    /**
     * Get a typed instance of the iterator.
     * @param <K>
     * @param <V>
     * @return {@link OrderedMapIterator}<K, V>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> OrderedMapIterator<K, V> getInstance() {
        return (OrderedMapIterator<K, V>) INSTANCE;
    }

    /**
     * Constructor.
     */
    protected EmptyOrderedMapIterator() {
        super();
    }

}
