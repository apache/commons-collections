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
 *
 */

package org.apache.commons.collections4;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * An object that maps String prefixes to values.
 * A PrefixMap cannot contain duplicate prefixes; each prefix can map to at most one value.
 * </p>
 * <p>
 * The PrefixMap is a key-value type collection where the key is assumed to be the prefix of
 * the String for which later an answer is requested.
 * </p>
 * <p>
 * So the retrieval of the value is based on a startsWith() check of the requested <tt>input</tt> against each of
 * the stored <tt>prefixes</tt>. The value associated with the selected prefix is then returned.
 * </p>
 * <p>
 * An example use case is where mobile phone device models of a certain brand all start with
 * the same substring. This data structure can be used to efficiently retrieve the best
 * fitting prefix to find the associated value (i.e. the brand of the device).
 * </p>
 * <p>Note that implementations may be constructed to match either case sensitive or case insensitive.</p>
 * @param <V> The type of the value that is to be stored.
 */
public interface PrefixMap<V extends Serializable> extends Serializable {
    /**
     * @return the number of prefix-value mappings in this prefixmap
     */
    int size();

    /**
     * @return <tt>true</tt> if empty.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * <p>Returns <tt>true</tt> if this map contains an exact mapping
     * for the specified prefix.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     * @param prefix  prefix whose presence in this prefixmap is to be checked
     * @return <tt>true</tt> if this map contains an the exact mapping
     *         for the specified prefix.
     */
    boolean containsPrefix(String prefix);

    /**
     * Copies all of the mappings from the specified map to this prefixmap.
     * @param prefixesAndValues  mappings to be stored in this map
     * @throws NullPointerException  if one or more of the prefixes or values are null.
     *         If this happens the PrefixMap is in an undefined state.
     */
    default void putAll(Map<String, V> prefixesAndValues) {
        prefixesAndValues.forEach(this::put);
    }

    /**
     * <p>Stored the specified value as the result for the specified prefix.</p>
     * <p>If for the specified prefix previously a value was present then the
     * old value is replaced with the new value and the old value is returned.</p>
     * <p>Note that implementations may be constructed to store this value for either
     * case sensitive or case insensitive matching during retrieval.</p>
     * @param prefix  prefix with which the specified value is to be associated
     * @param value  value to be associated with the specified prefix
     * @return the previous value of the specified <tt>prefix</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>prefix</tt>.
     * @throws NullPointerException  if either the prefix or value are null.
     *         If this happens the PrefixMap is unchanged.
     */
    V put(String prefix, V value);

    /**
     * <p>Removes the mapping for a prefix if present.</p>
     * <p>Note that implementations may be constructed to remove either
     * case sensitive (only this exact value) or case insensitive (all case variations).</p>
     * @param prefix  prefix whose mapping is to be removed from the prefixmap
     * @return the previous value associated with <tt>prefix</tt> (may be null).
     * @throws UnsupportedOperationException  if not implemented.
     */
    default V remove(String prefix) {
        throw new UnsupportedOperationException("The 'remove(String prefix)' method has not been implemented in " +
                this.getClass().getCanonicalName());
    }

    /**
     * The prefixmap will be empty after this call returns.
     * @throws UnsupportedOperationException  if the <tt>clear</tt> operation
     *         is not supported by this map
     */
    default void clear() {
        throw new UnsupportedOperationException("The 'clear()' method has not been implemented in " +
                this.getClass().getCanonicalName());
    }

    /**
     * <p>Return the value of the <tt>shortest</tt> matching prefix. </p>
     * <p>The value returned is the shortest stored prefix for which is true:
     * <tt>input.startsWith(prefix)</tt>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     * @param input  The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V getShortestMatch(String input);

    /**
     * <p>Return the value of the longest matching prefix.</p>
     * <p>The value returned is the longest stored prefix for which is true:
     * <tt>input.startsWith(prefix)</tt>.</p>
     * <p>Note that implementations may be constructed to match either
     * case sensitive or case insensitive.</p>
     * @param input  The string for which we need value of the stored prefix
     * @return The value, null if not found.
     */
    V getLongestMatch(String input);

}
