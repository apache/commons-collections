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
package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A case-insensitive {@code Map}.
 * <p>
 * Before keys are added to the map or compared to other existing keys, they are converted
 * to all lowercase in a locale-independent fashion by using information from the Unicode
 * data file.
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
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 * @since 3.0
 */
public class CaseInsensitiveMap<K, V> extends AbstractHashedMap<K, V> implements Serializable, Cloneable {

    /** Serialisation version */
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
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public CaseInsensitiveMap(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor.
     *
     * @param initialCapacity  the initial capacity
     * @param loadFactor  the load factor
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
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    public CaseInsensitiveMap(final Map<? extends K, ? extends V> map) {
        super(map);
    }

    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    @Override
    public CaseInsensitiveMap<K, V> clone() {
        return (CaseInsensitiveMap<K, V>) super.clone();
    }

    /**
     * Overrides convertKey() from {@link AbstractHashedMap} to convert keys to
     * lower case.
     * <p>
     * Returns {@link AbstractHashedMap#NULL} if key is null.
     *
     * @param key  the key convert
     * @return the converted key
     */
    @Override
    protected Object convertKey(final Object key) {
        if (key != null) {
            final char[] chars = key.toString().toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                chars[i] = Character.toLowerCase(Character.toUpperCase(chars[i]));
            }
            return new String(chars);
        }
        return AbstractHashedMap.NULL;
    }

    @Override
    public V put(final K key, final V value) {
        final Object convertedKey = convertKey(key);
        final int hashCode = hash(convertedKey);
        final int index = hashIndex(hashCode, data.length);
        HashEntry<K, V> entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(convertedKey, entry.key)) {
                final V oldValue = entry.getValue();
                updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }

        addMappingWithoutKeyConversion(index, hashCode, convertedKey, value);
        return null;
    }

    /**
     * Adds a new key-value mapping into this map.
     * <p>
     * This implementation calls {@code createEntryWithoutKeyConversion()}, {@code addEntry()}
     * and {@code checkCapacity()}.
     * It also handles changes to {@code modCount} and {@code size}.
     * Subclasses could override to fully control adds to the map.
     *
     * @param hashIndex  the index into the data array to store at
     * @param hashCode  the hash code of the key to add
     * @param convertedKey  the key to add, already converted to lower case by {@code convertKey()}
     * @param value  the value to add
     */
    protected void addMappingWithoutKeyConversion(final int hashIndex, final int hashCode, final Object convertedKey, final V value) {
        modCount++;
        final HashEntry<K, V> entry = createEntryWithoutKeyConversion(data[hashIndex], hashCode, convertedKey, value);
        addEntry(entry, hashIndex);
        size++;
        checkCapacity();
    }

    /**
     * Creates an entry to store the key-value data.
     * <p>
     * This implementation creates a new HashEntry instance.
     * Subclasses can override this to return a different storage class,
     * or implement caching.
     *
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param convertedKey  the key to add, already converted to lower case by {@code convertKey()}
     * @param value  the value to store
     * @return the newly created entry
     */
    protected HashEntry<K, V> createEntryWithoutKeyConversion(final HashEntry<K, V> next, final int hashCode, final Object convertedKey, final V value) {
        return new HashEntry<>(next, hashCode, convertedKey, value);
    }

    /**
     * Read the map in using a custom routine.
     *
     * @param in the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream can not be loaded
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

    /**
     * Write the map out using a custom routine.
     *
     * @param out  the output stream
     * @throws IOException if an error occurs while writing to the stream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

}
