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
package org.apache.commons.collections4;

/**
 * Defines an iterator that operates over an ordered {@code Map}.
 * <p>
 * This iterator allows both forward and reverse iteration through the map.
 * </p>
 *
 * @param <K> the type of the keys in the map
 * @param <V> the type of the values in the map
 * @since 3.0
 */
public interface OrderedMapIterator<K, V> extends MapIterator<K, V>, OrderedIterator<K> {

    /**
     * Checks to see if there is a previous entry that can be iterated to.
     *
     * @return {@code true} if the iterator has a previous element
     */
    @Override
    boolean hasPrevious();

    /**
     * Gets the previous <em>key</em> from the {@code Map}.
     *
     * @return the previous key in the iteration
     * @throws java.util.NoSuchElementException if the iteration is finished
     */
    @Override
    K previous();

}
