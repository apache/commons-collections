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
package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

/**
 * A {@code MultiKey} allows multiple map keys to be merged together.
 * <p>
 * The purpose of this class is to avoid the need to write code to handle
 * maps of maps. An example might be the need to look up a file name by
 * key and locale. The typical solution might be nested maps. This class
 * can be used instead by creating an instance passing in the key and locale.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * // populate map with data mapping key+locale to localizedText
 * Map map = new HashMap();
 * MultiKey multiKey = new MultiKey(key, locale);
 * map.put(multiKey, localizedText);
 *
 * // later retrieve the localized text
 * MultiKey multiKey = new MultiKey(key, locale);
 * String localizedText = (String) map.get(multiKey);
 * </pre>
 *
 * @param <K> the type of keys
 * @since 3.0
 */
public class MultiKey<K> implements Serializable {
    // This class could implement List, but that would confuse it's purpose

    /** Serialisation version */
    private static final long serialVersionUID = 4465448607415788805L;

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> getClass(final T value) {
        return (Class<? extends T>) (value == null ? Object.class : value.getClass());
    }

    private static <T> Class<? extends T> getComponentType(final T... values) {
        @SuppressWarnings("unchecked")
        final Class<? extends T> rootClass = (Class<? extends T>) Object.class;
        if (values == null) {
            return rootClass;
        }
        Class<? extends T> prevClass = values.length > 0 ? getClass(values[0]) : rootClass;
        for (int i = 1; i < values.length; i++) {
            final Class<? extends T> classI = getClass(values[i]);
            if (prevClass != classI) {
                return rootClass;
            }
            prevClass = classI;
        }
        return prevClass;
    }

    private static <T> T[] newArray(final T key1, final T key2) {
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(getComponentType(key1, key2), 2);
        array[0] = key1;
        array[1] = key2;
        return array;
    }

    private static <T> T[] newArray(final T key1, final T key2, final T key3) {
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(getComponentType(key1, key2, key3), 3);
        array[0] = key1;
        array[1] = key2;
        array[2] = key3;
        return array;
    }

    private static <T> T[] newArray(final T key1, final T key2, final T key3, final T key4) {
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(getComponentType(key1, key2, key3, key4), 4);
        array[0] = key1;
        array[1] = key2;
        array[2] = key3;
        array[3] = key4;
        return array;
    }

    private static <T> T[] newArray(final T key1, final T key2, final T key3, final T key4, final T key5) {
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) Array.newInstance(getComponentType(key1, key2, key3, key4, key5), 5);
        array[0] = key1;
        array[1] = key2;
        array[2] = key3;
        array[3] = key4;
        array[4] = key5;
        return array;
    }

    /** The individual keys */
    private final K[] keys;

    /** The cached hashCode */
    private transient int hashCode;

    /**
     * Constructor taking two keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param key1  the first key
     * @param key2  the second key
     */
    public MultiKey(final K key1, final K key2) {
        this(newArray(key1, key2), false);
    }


    /**
     * Constructor taking three keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     */
    public MultiKey(final K key1, final K key2, final K key3) {
        this(newArray(key1, key2, key3), false);
    }

    /**
     * Constructor taking four keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     */
    public MultiKey(final K key1, final K key2, final K key3, final K key4) {
        this(newArray(key1, key2, key3, key4), false);
    }

    /**
     * Constructor taking five keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     */
    public MultiKey(final K key1, final K key2, final K key3, final K key4, final K key5) {
        this(newArray(key1, key2, key3, key4, key5), false);
    }

    /**
     * Constructor taking an array of keys which is cloned.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     * <p>
     * This is equivalent to {@code new MultiKey(keys, true)}.
     *
     * @param keys  the array of keys, not null
     * @throws NullPointerException if the key array is null
     */
    public MultiKey(final K[] keys) {
        this(keys, true);
    }

    /**
     * Constructor taking an array of keys, optionally choosing whether to clone.
     * <p>
     * <b>If the array is not cloned, then it must not be modified.</b>
     * <p>
     * This method is public for performance reasons only, to avoid a clone.
     * The hashcode is calculated once here in this method.
     * Therefore, changing the array passed in would not change the hashcode but
     * would change the equals method, which is a bug.
     * <p>
     * This is the only fully safe usage of this constructor, as the object array
     * is never made available in a variable:
     * <pre>
     * new MultiKey(new Object[] {...}, false);
     * </pre>
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param keys  the array of keys, not null
     * @param makeClone  true to clone the array, false to assign it
     * @throws NullPointerException if the key array is null
     * @since 3.1
     */
    public MultiKey(final K[] keys, final boolean makeClone) {
        Objects.requireNonNull(keys, "keys");
        this.keys = makeClone ? keys.clone() : keys;
        calculateHashCode(keys);
    }

    /**
     * Calculate the hash code of the instance using the provided keys.
     * @param keys the keys to calculate the hash code for
     */
    private void calculateHashCode(final Object[] keys) {
        int total = 0;
        for (final Object key : keys) {
            if (key != null) {
                total ^= key.hashCode();
            }
        }
        hashCode = total;
    }

    /**
     * Compares this object to another.
     * <p>
     * To be equal, the other object must be a {@code MultiKey} with the
     * same number of keys which are also equal.
     *
     * @param other  the other object to compare to
     * @return true if equal
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            final MultiKey<?> otherMulti = (MultiKey<?>) other;
            return Arrays.equals(keys, otherMulti.keys);
        }
        return false;
    }

    /**
     * Gets the key at the specified index.
     * <p>
     * The key should be immutable.
     * If it is not then it must not be changed.
     *
     * @param index  the index to retrieve
     * @return the key at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @since 3.1
     */
    public K getKey(final int index) {
        return keys[index];
    }

    /**
     * Gets a clone of the array of keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed.
     *
     * @return the individual keys
     */
    public K[] getKeys() {
        return keys.clone();
    }

    /**
     * Gets the combined hash code that is computed from all the keys.
     * <p>
     * This value is computed once and then cached, so elements should not
     * change their hash codes once created (note that this is the same
     * constraint that would be used if the individual keys elements were
     * themselves {@link java.util.Map Map} keys.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Recalculate the hash code after deserialization. The hash code of some
     * keys might have change (hash codes based on the system hash code are
     * only stable for the same process).
     * @return the instance with recalculated hash code
     */
    protected Object readResolve() {
        calculateHashCode(keys);
        return this;
    }

    /**
     * Gets the size of the list of keys.
     *
     * @return the size of the list of keys
     * @since 3.1
     */
    public int size() {
        return keys.length;
    }

    /**
     * Gets a debugging string version of the key.
     *
     * @return a debugging string
     */
    @Override
    public String toString() {
        return "MultiKey" + Arrays.toString(keys);
    }
}
