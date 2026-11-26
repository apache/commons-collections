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
package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A case-insensitive {@code Map}.
 * <p>
 * Before keys are added to the map or compared to other existing keys, each of their
 * characters (Unicode code points) is converted with
 * {@code Character.toLowerCase(Character.toUpperCase(codePoint))}, that is with the simple,
 * one-to-one case mappings of the Unicode data file, independently of the locale. This is not
 * the same as {@link String#toLowerCase(java.util.Locale)}: for example, U+03D0 (GREEK BETA
 * SYMBOL) and U+03B2 (GREEK SMALL LETTER BETA) give the same key.
 * </p>
 * <p>
 * Null keys are supported.
 * </p>
 * <p>
 * The {@code keySet()} method returns all lowercase keys, or nulls.
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre><code>
 *  Map&lt;String, String&gt; map = new CaseInsensitiveMap&lt;String, String&gt;();
 *  map.put("One", "One");
 *  map.put("Two", "Two");
 *  map.put(null, "Three");
 *  map.put("one", "Four");
 * </code></pre>
 * <p>
 * The example above creates a {@code CaseInsensitiveMap} with three entries.
 * </p>
 * <p>
 * {@code map.get(null)} returns {@code "Three"} and {@code map.get("ONE")}
 * returns {@code "Four".}  The {@code Set} returned by {@code keySet()}
 * equals {@code {"one", "two", null}.}
 * </p>
 * <p>
 * <strong>This map will violate the detail of various Map and map view contracts.</strong>
 * As a general rule, don't compare this map to other maps. In particular, you can't
 * use decorators like {@link ListOrderedMap} on it, which silently assume that these
 * contracts are fulfilled.
 * </p>
 * <p>
 * <strong>Note that CaseInsensitiveMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedMap(Map)}. This class may throw
 * exceptions when accessed by concurrent threads without synchronization.
 * </p>
 *
 * @param <K> The type of the keys in this map
 * @param <V> The type of the values in this map
 * @since 3.0
 */
public class CaseInsensitiveMap<K, V> extends AbstractHashedMap<K, V> implements Serializable, Cloneable {

    /** Serialization version */
    private static final long serialVersionUID = -7074655917369299456L;

    /**
     * Constructs a new empty map with default size and load factor.
     */
    public CaseInsensitiveMap() {
        super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity.
     *
     * @param initialCapacity  The initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public CaseInsensitiveMap(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor.
     *
     * @param initialCapacity  The initial capacity
     * @param loadFactor  The load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     * @throws IllegalArgumentException if the load factor is less than zero
     */
    public CaseInsensitiveMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor copying elements from another map.
     * <p>
     * Keys will be converted to lower case strings, which may cause
     * some entries to be removed (if string representation of keys differ
     * only by character case).
     *
     * @param map  The map to copy
     * @throws NullPointerException if the map is null
     */
    public CaseInsensitiveMap(final Map<? extends K, ? extends V> map) {
        super(map);
    }

    /**
     * Clones the map without cloning the keys or values.
     *
     * @return A shallow clone
     */
    @Override
    public CaseInsensitiveMap<K, V> clone() {
        return (CaseInsensitiveMap<K, V>) super.clone();
    }

    /**
     * Overrides convertKey() from {@link AbstractHashedMap} to convert each code point of
     * keys with {@code Character.toLowerCase(Character.toUpperCase(codePoint))}.
     * <p>
     * Returns {@link AbstractHashedMap#NULL} if key is null.
     *
     * @param key  The key convert
     * @return The converted key
     */
    @Override
    protected Object convertKey(final Object key) {
        if (key != null) {
            final String keyString = key.toString();
            final StringBuilder converted = new StringBuilder(keyString.length());
            for (int i = 0; i < keyString.length();) {
                final int codePoint = keyString.codePointAt(i);
                converted.appendCodePoint(Character.toLowerCase(Character.toUpperCase(codePoint)));
                i += Character.charCount(codePoint);
            }
            return converted.toString();
        }
        return NULL;
    }

    /**
     * Deserializes the map in using a custom routine.
     *
     * @param in The input stream
     * @throws IOException Thrown if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out The target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

}
