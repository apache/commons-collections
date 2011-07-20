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

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.Unmodifiable;

/** 
 * Decorates a map iterator such that it cannot be modified.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException. 
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public final class UnmodifiableMapIterator<K, V> implements MapIterator<K, V>, Unmodifiable {

    /** The iterator being decorated */
    private final MapIterator<K, V> iterator;

    //-----------------------------------------------------------------------
    /**
     * Decorates the specified iterator such that it cannot be modified.
     *
     * @param iterator  the iterator to decorate
     * @throws IllegalArgumentException if the iterator is null
     */
    public static <K, V> MapIterator<K, V> unmodifiableMapIterator(MapIterator<K, V> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("MapIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableMapIterator<K, V>(iterator);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param iterator  the iterator to decorate
     */
    private UnmodifiableMapIterator(MapIterator<K, V> iterator) {
        super();
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public K next() {
        return iterator.next();
    }

    public K getKey() {
        return iterator.getKey();
    }

    public V getValue() {
        return iterator.getValue();
    }

    public V setValue(V value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }

}
