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

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.collections4.map.AbstractMapDecorator;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;
import org.apache.commons.collections4.map.FixedSizeMap;
import org.apache.commons.collections4.map.FixedSizeSortedMap;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.collections4.map.LazySortedMap;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.collections4.map.PredicatedMap;
import org.apache.commons.collections4.map.PredicatedSortedMap;
import org.apache.commons.collections4.map.TransformedMap;
import org.apache.commons.collections4.map.TransformedSortedMap;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;

/**
 * Provides utility methods and decorators for {@link Map} and {@link SortedMap} instances.
 * <p>
 * It contains various type safe methods as well as other useful features like deep copying.
 * </p>
 * <p>
 * It also provides the following decorators:
 * </p>
 *
 * <ul>
 * <li>{@link #fixedSizeMap(Map)}
 * <li>{@link #fixedSizeSortedMap(SortedMap)}
 * <li>{@link #lazyMap(Map,Factory)}
 * <li>{@link #lazyMap(Map,Transformer)}
 * <li>{@link #lazySortedMap(SortedMap,Factory)}
 * <li>{@link #lazySortedMap(SortedMap,Transformer)}
 * <li>{@link #predicatedMap(Map,Predicate,Predicate)}
 * <li>{@link #predicatedSortedMap(SortedMap,Predicate,Predicate)}
 * <li>{@link #transformedMap(Map, Transformer, Transformer)}
 * <li>{@link #transformedSortedMap(SortedMap, Transformer, Transformer)}
 * <li>{@link #multiValueMap( Map )}
 * <li>{@link #multiValueMap( Map, Class )}
 * <li>{@link #multiValueMap( Map, Factory )}
 * </ul>
 *
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class MapUtils {

    /**
     * An empty unmodifiable sorted map. This is not provided in the JDK.
     */
    @SuppressWarnings("rawtypes")
    public static final SortedMap EMPTY_SORTED_MAP = UnmodifiableSortedMap.unmodifiableSortedMap(new TreeMap<>());

    /**
     * String used to indent the verbose and debug Map prints.
     */
    private static final String INDENT_STRING = "    ";

    /**
     * Applies the {@code getFunction} and returns its result if non-null, if null returns the result of applying the
     * default function.
     *
     * @param <K> The key type.
     * @param <R> The result type.
     * @param map The map to query.
     * @param key The key into the map.
     * @param getFunction The get function.
     * @param defaultFunction The function to provide a default value.
     * @return The result of applying a function.
     */
    private static <K, R> R applyDefaultFunction(final Map<? super K, ?> map, final K key,
            final BiFunction<Map<? super K, ?>, K, R> getFunction, final Function<K, R> defaultFunction) {
        return applyDefaultFunction(map, key, getFunction, defaultFunction, null);
    }

    /**
     * Applies the {@code getFunction} and returns its result if non-null, if null returns the result of applying the
     * default function.
     *
     * @param <K> The key type.
     * @param <R> The result type.
     * @param map The map to query.
     * @param key The key into the map.
     * @param getFunction The get function.
     * @param defaultFunction The function to provide a default value.
     * @param defaultValue The default value.
     * @return The result of applying a function.
     */
    private static <K, R> R applyDefaultFunction(final Map<? super K, ?> map, final K key,
            final BiFunction<Map<? super K, ?>, K, R> getFunction, final Function<K, R> defaultFunction,
            final R defaultValue) {
        R value = map != null && getFunction != null ? getFunction.apply(map, key) : null;
        if (value == null) {
            value = defaultFunction != null ? defaultFunction.apply(key) : null;
        }
        return value != null ? value : defaultValue;
    }

    /**
     * Applies the {@code getFunction} and returns its result if non-null, if null returns the {@code defaultValue}.
     *
     * @param <K> The key type.
     * @param <R> The result type.
     * @param map The map to query.
     * @param key The key into the map.
     * @param getFunction The get function.
     * @param defaultValue The default value.
     * @return The result of applying a function.
     */
    private static <K, R> R applyDefaultValue(final Map<? super K, ?> map, final K key,
            final BiFunction<Map<? super K, ?>, K, R> getFunction, final R defaultValue) {
        final R value = map != null && getFunction != null ? getFunction.apply(map, key) : null;
        return value == null ? defaultValue : value;
    }

    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map. Each map entry will be printed with key, value
     * and value classname. When the value is a Map, recursive behavior occurs.
     * </p>
     * <p>
     * This method is NOT thread-safe in any special way. You must manually synchronize on either this class or the
     * stream as required.
     * </p>
     *
     * @param out the stream to print to, must not be null
     * @param label The label to be used, may be {@code null}. If {@code null}, the label is not output. It
     *        typically represents the name of the property in a bean or similar.
     * @param map The map to print, may be {@code null}. If {@code null}, the text 'null' is output.
     * @throws NullPointerException if the stream is {@code null}
     */
    public static void debugPrint(final PrintStream out, final Object label, final Map<?, ?> map) {
        verbosePrintInternal(out, label, map, new ArrayDeque<>(), true);
    }

    /**
     * Returns an immutable empty map if the argument is {@code null}, or the argument itself otherwise.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map, possibly {@code null}
     * @return an empty map if the argument is {@code null}
     */
    public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> map) {
        return map == null ? Collections.<K, V>emptyMap() : map;
    }

    /**
     * Returns a fixed-sized map backed by the given map. Elements may not be added or removed from the returned map,
     * but existing elements can be changed (for instance, via the {@link Map#put(Object,Object)} method).
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map whose size to fix, must not be null
     * @return a fixed-size map backed by that map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> IterableMap<K, V> fixedSizeMap(final Map<K, V> map) {
        return FixedSizeMap.fixedSizeMap(map);
    }

    /**
     * Returns a fixed-sized sorted map backed by the given sorted map. Elements may not be added or removed from the
     * returned map, but existing elements can be changed (for instance, via the {@link Map#put(Object,Object)} method).
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map whose size to fix, must not be null
     * @return a fixed-size map backed by that map
     * @throws NullPointerException if the SortedMap is null
     */
    public static <K, V> SortedMap<K, V> fixedSizeSortedMap(final SortedMap<K, V> map) {
        return FixedSizeSortedMap.fixedSizeSortedMap(map);
    }

    /**
     * Gets a Boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a {@code Boolean} it is returned directly. If the value is a {@code String} and it
     * equals 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, {@code null} is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, {@code null} if null map input
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;
                }
                if (answer instanceof String) {
                    return Boolean.valueOf((String) answer);
                }
                if (answer instanceof Number) {
                    final Number n = (Number) answer;
                    return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a boolean, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a boolean, or defaultValue if the original value is null, the map is null or the
     *         boolean conversion fails
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key, final Boolean defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getBoolean, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a boolean, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a boolean, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the boolean conversion fails
     * @since 4.5
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key,
            final Function<K, Boolean> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getBoolean, defaultFunction);
    }

    /**
     * Gets a boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a {@code Boolean} its value is returned. If the value is a {@code String} and it equals
     * 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, {@code false} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, {@code false} if null map input
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key) {
        return Boolean.TRUE.equals(getBoolean(map, key));
    }

    /**
     * Gets a boolean from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * If the value is a {@code Boolean} its value is returned. If the value is a {@code String} and it equals
     * 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, {@code defaultValue} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a Boolean, {@code defaultValue} if null map input
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key, final boolean defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getBoolean, defaultValue).booleanValue();
    }

    /**
     * Gets a boolean from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * If the value is a {@code Boolean} its value is returned. If the value is a {@code String} and it equals
     * 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, defaultValue produced by the {@code defaultFunction} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a Boolean, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key,
            final Function<K, Boolean> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getBoolean, defaultFunction, false).booleanValue();
    }

    /**
     * Gets a Byte from a Map in a null-safe manner.
     * <p>
     * The Byte is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Byte, {@code null} if null map input
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Byte) {
            return (Byte) answer;
        }
        return Byte.valueOf(answer.byteValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a byte, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key, final Byte defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getByte, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a byte, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key, final Function<K, Byte> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getByte, defaultFunction);
    }

    /**
     * Gets a byte from a Map in a null-safe manner.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a byte, {@code 0} if null map input
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getByte, 0).byteValue();
    }

    /**
     * Gets a byte from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a byte, {@code defaultValue} if null map input
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key, final byte defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getByte, defaultValue).byteValue();
    }

    /**
     * Gets a byte from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a byte, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key,
            final Function<K, Byte> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getByte, defaultFunction, (byte) 0).byteValue();
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Double, {@code null} if null map input
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Double) {
            return (Double) answer;
        }
        return Double.valueOf(answer.doubleValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a double, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key, final Double defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getDouble, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a double, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key,
            final Function<K, Double> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getDouble, defaultFunction);
    }

    /**
     * Gets a double from a Map in a null-safe manner.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a double, {@code 0.0} if null map input
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getDouble, 0d).doubleValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a double, {@code defaultValue} if null map input
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key, final double defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getDouble, defaultValue).doubleValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a double, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key,
            final Function<K, Double> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getDouble, defaultFunction, 0d).doubleValue();
    }

    /**
     * Gets a Float from a Map in a null-safe manner.
     * <p>
     * The Float is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Float, {@code null} if null map input
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Float) {
            return (Float) answer;
        }
        return Float.valueOf(answer.floatValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a float, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key, final Float defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getFloat, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a float, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key,
            final Function<K, Float> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getFloat, defaultFunction);
    }

    /**
     * Gets a float from a Map in a null-safe manner.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a float, {@code 0.0F} if null map input
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getFloat, 0f).floatValue();
    }

    /**
     * Gets a float from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a float, {@code defaultValue} if null map input
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key, final float defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getFloat, defaultValue).floatValue();
    }

    /**
     * Gets a float from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a float, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key,
            final Function<K, Float> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getFloat, defaultFunction, 0f).floatValue();
    }

    /**
     * Gets an Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as an Integer, {@code null} if null map input
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return Integer.valueOf(answer.intValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into an integer, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key,
            final Function<K, Integer> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getInteger, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into an integer, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key, final Integer defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getInteger, defaultValue);
    }

    /**
     * Gets an int from a Map in a null-safe manner.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as an int, {@code 0} if null map input
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getInteger, 0).intValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as an int, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key,
            final Function<K, Integer> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getInteger, defaultFunction, 0).byteValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as an int, {@code defaultValue} if null map input
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key, final int defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getInteger, defaultValue).intValue();
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Long, {@code null} if null map input
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Long) {
            return (Long) answer;
        }
        return Long.valueOf(answer.longValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a Long, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key, final Function<K, Long> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getLong, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a long, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key, final Long defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getLong, defaultValue);
    }

    /**
     * Gets a long from a Map in a null-safe manner.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a long, {@code 0L} if null map input
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getLong, 0L).longValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a long, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key,
            final Function<K, Long> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getLong, defaultFunction, 0L).byteValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a long, {@code defaultValue} if null map input
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key, final long defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getLong, defaultValue).longValue();
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then {@code null} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Map, {@code null} if null map input
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer instanceof Map) {
                return (Map<?, ?>) answer;
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a map, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the map conversion fails
     * @since 4.5
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key,
            final Function<K, Map<?, ?>> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getMap, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a map, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         map conversion fails
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key, final Map<?, ?> defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getMap, defaultValue);
    }

    /**
     * Gets a Number from a Map in a null-safe manner.
     * <p>
     * If the value is a {@code Number} it is returned directly. If the value is a {@code String} it is
     * converted using {@link NumberFormat#parse(String)} on the system default formatter returning {@code null} if
     * the conversion fails. Otherwise, {@code null} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Number, {@code null} if null map input
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;
                }
                if (answer instanceof String) {
                    try {
                        final String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);
                    } catch (final ParseException e) { // NOPMD
                        // failure means null is returned
                    }
                }
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a number, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key,
            final Function<K, Number> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getNumber, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a number, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key, final Number defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getNumber, defaultValue);
    }

    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map, {@code null} if null map input
     */
    public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting null into the given default value.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null
     * @return the value in the map, or defaultValue if the original value is null or the map is null
     */
    public static <K, V> V getObject(final Map<K, V> map, final K key, final V defaultValue) {
        if (map != null) {
            final V answer = map.get(key);
            if (answer != null) {
                return answer;
            }
        }
        return defaultValue;
    }

    /**
     * Gets a Short from a Map in a null-safe manner.
     * <p>
     * The Short is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Short, {@code null} if null map input
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Short) {
            return (Short) answer;
        }
        return Short.valueOf(answer.shortValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a short, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key,
            final Function<K, Short> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getShort, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a short, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     *         number conversion fails
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key, final Short defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getShort, defaultValue);
    }

    /**
     * Gets a short from a Map in a null-safe manner.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a short, {@code 0} if null map input
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getShort, 0).shortValue();
    }

    /**
     * Gets a short from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a short, default value produced by the {@code defaultFunction} if null map
     *         input
     * @since 4.5
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key,
            final Function<K, Short> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getShort, defaultFunction, (short) 0).shortValue();
    }

    /**
     * Gets a short from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map,Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a short, {@code defaultValue} if null map input
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key, final short defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getShort, defaultValue).shortValue();
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via {@code toString}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a String, {@code null} if null map input
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a string, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a string, or defaultValue produced by the defaultFunction if the original value
     *         is null, the map is null or the string conversion fails
     * @since 4.5
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key,
            final Function<K, String> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getString, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a string, using the default value if the
     * conversion fails.
     *
     * @param <K> the key type
     * @param map the map whose value to look up
     * @param key the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a string, or defaultValue if the original value is null, the map is null or the
     *         string conversion fails
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key, final String defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getString, defaultValue);
    }

    /**
     * Inverts the supplied map returning a new HashMap such that the keys of the input are swapped with the values.
     * <p>
     * This operation assumes that the inverse mapping is well defined. If the input map had multiple entries with the
     * same value mapped to different keys, the returned map will map one of those keys to the value, but the exact key
     * which will be mapped is undefined.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to invert, must not be null
     * @return a new HashMap containing the inverted data
     * @throws NullPointerException if the map is null
     */
    public static <K, V> Map<V, K> invertMap(final Map<K, V> map) {
        Objects.requireNonNull(map, "map");
        final Map<V, K> out = new HashMap<>(map.size());
        for (final Entry<K, V> entry : map.entrySet()) {
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }

    /**
     * Null-safe check if the specified map is empty.
     * <p>
     * Null returns true.
     * </p>
     *
     * @param map the map to check, may be null
     * @return true if empty or null
     * @since 3.2
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Null-safe check if the specified map is not empty.
     * <p>
     * Null returns false.
     * </p>
     *
     * @param map the map to check, may be null
     * @return true if non-null and non-empty
     * @since 3.2
     */
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !MapUtils.isEmpty(map);
    }

    /**
     * Get the specified {@link Map} as an {@link IterableMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map to wrap if necessary.
     * @return IterableMap&lt;K, V&gt;
     * @throws NullPointerException if map is null
     * @since 4.0
     */
    public static <K, V> IterableMap<K, V> iterableMap(final Map<K, V> map) {
        Objects.requireNonNull(map, "map");
        return map instanceof IterableMap ? (IterableMap<K, V>) map : new AbstractMapDecorator<K, V>(map) {
            // empty
        };
    }

    /**
     * Get the specified {@link SortedMap} as an {@link IterableSortedMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param sortedMap to wrap if necessary
     * @return {@link IterableSortedMap}&lt;K, V&gt;
     * @throws NullPointerException if sortedMap is null
     * @since 4.0
     */
    public static <K, V> IterableSortedMap<K, V> iterableSortedMap(final SortedMap<K, V> sortedMap) {
        Objects.requireNonNull(sortedMap, "sortedMap");
        return sortedMap instanceof IterableSortedMap ? (IterableSortedMap<K, V>) sortedMap
                : new AbstractSortedMapDecorator<K, V>(sortedMap) {
                    // empty
                };
    }

    /**
     * Returns a "lazy" map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)} method is not present in the map, then the
     * factory will be used to create a new object and that object will become the value associated with that key.
     * </p>
     * <p>
     * For instance:
     * </p>
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * Map lazyMap = MapUtils.lazyMap(new HashMap(), factory);
     * Object obj = lazyMap.get("test");
     * </pre>
     *
     * <p>
     * After the above code is executed, {@code obj} will contain a new {@code Date} instance. Furthermore,
     * that {@code Date} instance is the value for the {@code "test"} key in the map.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to make lazy, must not be null
     * @param factory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the Map or Factory is null
     */
    public static <K, V> IterableMap<K, V> lazyMap(final Map<K, V> map, final Factory<? extends V> factory) {
        return LazyMap.lazyMap(map, factory);
    }

    /**
     * Returns a "lazy" map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)} method is not present in the map, then the
     * factory will be used to create a new object and that object will become the value associated with that key. The
     * factory is a {@link Transformer} that will be passed the key which it must transform into the value.
     * </p>
     * <p>
     * For instance:
     * </p>
     * <pre>
     * Transformer factory = new Transformer() {
     *     public Object transform(Object mapKey) {
     *         return new File(mapKey);
     *     }
     * }
     * Map lazyMap = MapUtils.lazyMap(new HashMap(), factory);
     * Object obj = lazyMap.get("C:/dev");
     * </pre>
     *
     * <p>
     * After the above code is executed, {@code obj} will contain a new {@code File} instance for the C drive
     * dev directory. Furthermore, that {@code File} instance is the value for the {@code "C:/dev"} key in the
     * map.
     * </p>
     * <p>
     * If a lazy map is wrapped by a synchronized map, the result is a simple synchronized cache. When an object is not
     * is the cache, the cache itself calls back to the factory Transformer to populate itself, all within the same
     * synchronized block.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to make lazy, must not be null
     * @param transformerFactory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the Map or Transformer is null
     */
    public static <K, V> IterableMap<K, V> lazyMap(final Map<K, V> map,
            final Transformer<? super K, ? extends V> transformerFactory) {
        return LazyMap.lazyMap(map, transformerFactory);
    }

    /**
     * Returns a "lazy" sorted map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)} method is not present in the map, then the
     * factory will be used to create a new object and that object will become the value associated with that key.
     * </p>
     * <p>
     * For instance:
     * </p>
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     * Object obj = lazy.get("test");
     * </pre>
     * <p>
     * After the above code is executed, {@code obj} will contain a new {@code Date} instance. Furthermore,
     * that {@code Date} instance is the value for the {@code "test"} key.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to make lazy, must not be null
     * @param factory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the SortedMap or Factory is null
     */
    public static <K, V> SortedMap<K, V> lazySortedMap(final SortedMap<K, V> map, final Factory<? extends V> factory) {
        return LazySortedMap.lazySortedMap(map, factory);
    }

    /**
     * Returns a "lazy" sorted map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)} method is not present in the map, then the
     * factory will be used to create a new object and that object will become the value associated with that key. The
     * factory is a {@link Transformer} that will be passed the key which it must transform into the value.
     * </p>
     * <p>
     * For instance:
     * </p>
     * <pre>
     * Transformer factory = new Transformer() {
     *     public Object transform(Object mapKey) {
     *         return new File(mapKey);
     *     }
     * }
     * SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     * Object obj = lazy.get("C:/dev");
     * </pre>
     * <p>
     * After the above code is executed, {@code obj} will contain a new {@code File} instance for the C drive
     * dev directory. Furthermore, that {@code File} instance is the value for the {@code "C:/dev"} key in the
     * map.
     * </p>
     * <p>
     * If a lazy map is wrapped by a synchronized map, the result is a simple synchronized cache. When an object is not
     * is the cache, the cache itself calls back to the factory Transformer to populate itself, all within the same
     * synchronized block.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to make lazy, must not be null
     * @param transformerFactory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the Map or Transformer is null
     */
    public static <K, V> SortedMap<K, V> lazySortedMap(final SortedMap<K, V> map,
            final Transformer<? super K, ? extends V> transformerFactory) {
        return LazySortedMap.lazySortedMap(map, transformerFactory);
    }

    /**
     * Creates a multi-value map backed by the given map which returns collections of type ArrayList.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to decorate
     * @return a multi-value map backed by the given map which returns ArrayLists of values.
     * @see MultiValueMap
     * @since 3.2
     * @deprecated since 4.1, use {@link MultiValuedMap} instead
     */
    @Deprecated
    public static <K, V> MultiValueMap<K, V> multiValueMap(final Map<K, ? super Collection<V>> map) {
        return MultiValueMap.<K, V>multiValueMap(map);
    }

    /**
     * Creates a multi-value map backed by the given map which returns collections of the specified type.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <C> the collection class type
     * @param map the map to decorate
     * @param collectionClass the type of collections to return from the map (must contain public no-arg constructor and
     *        extend Collection)
     * @return a multi-value map backed by the given map which returns collections of the specified type
     * @see MultiValueMap
     * @since 3.2
     * @deprecated since 4.1, use {@link MultiValuedMap} instead
     */
    @Deprecated
    public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(final Map<K, C> map,
            final Class<C> collectionClass) {
        return MultiValueMap.multiValueMap(map, collectionClass);
    }

    /**
     * Creates a multi-value map backed by the given map which returns collections created by the specified collection
     * factory.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <C> the collection class type
     * @param map the map to decorate
     * @param collectionFactory a factor which creates collection objects
     * @return a multi-value map backed by the given map which returns collections created by the specified collection
     *         factory
     * @see MultiValueMap
     * @since 3.2
     * @deprecated since 4.1, use {@link MultiValuedMap} instead
     */
    @Deprecated
    public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(final Map<K, C> map,
            final Factory<C> collectionFactory) {
        return MultiValueMap.multiValueMap(map, collectionFactory);
    }

    /**
     * Returns a map that maintains the order of keys that are added backed by the given map.
     * <p>
     * If a key is added twice, the order is determined by the first add. The order is observed through the keySet,
     * values and entrySet.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to order, must not be null
     * @return an ordered map backed by the given map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> OrderedMap<K, V> orderedMap(final Map<K, V> map) {
        return ListOrderedMap.listOrderedMap(map);
    }

    /**
     * Populates a Map using the supplied {@code Transformer}s to transform the elements into keys and values.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <E> the type of object contained in the {@link Iterable}
     * @param map the {@code Map} to populate.
     * @param elements the {@code Iterable} containing the input values for the map.
     * @param keyTransformer the {@code Transformer} used to transform the element into a key value
     * @param valueTransformer the {@code Transformer} used to transform the element into a value
     * @throws NullPointerException if the map, elements or transformers are null
     */
    public static <K, V, E> void populateMap(final Map<K, V> map, final Iterable<? extends E> elements,
            final Transformer<E, K> keyTransformer, final Transformer<E, V> valueTransformer) {
        for (final E temp : elements) {
            map.put(keyTransformer.transform(temp), valueTransformer.transform(temp));
        }
    }

    /**
     * Populates a Map using the supplied {@code Transformer} to transform the elements into keys, using the
     * unaltered element as the value in the {@code Map}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the {@code Map} to populate.
     * @param elements the {@code Iterable} containing the input values for the map.
     * @param keyTransformer the {@code Transformer} used to transform the element into a key value
     * @throws NullPointerException if the map, elements or transformer are null
     */
    public static <K, V> void populateMap(final Map<K, V> map, final Iterable<? extends V> elements,
            final Transformer<V, K> keyTransformer) {
        populateMap(map, elements, keyTransformer, TransformerUtils.<V>nopTransformer());
    }

    /**
     * Populates a MultiMap using the supplied {@code Transformer}s to transform the elements into keys and values.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <E> the type of object contained in the {@link Iterable}
     * @param map the {@code MultiMap} to populate.
     * @param elements the {@code Iterable} containing the input values for the map.
     * @param keyTransformer the {@code Transformer} used to transform the element into a key value
     * @param valueTransformer the {@code Transformer} used to transform the element into a value
     * @throws NullPointerException if the map, collection or transformers are null
     */
    public static <K, V, E> void populateMap(final MultiMap<K, V> map, final Iterable<? extends E> elements,
            final Transformer<E, K> keyTransformer, final Transformer<E, V> valueTransformer) {
        for (final E temp : elements) {
            map.put(keyTransformer.transform(temp), valueTransformer.transform(temp));
        }
    }

    /**
     * Populates a MultiMap using the supplied {@code Transformer} to transform the elements into keys, using the
     * unaltered element as the value in the {@code MultiMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the {@code MultiMap} to populate.
     * @param elements the {@code Iterable} to use as input values for the map.
     * @param keyTransformer the {@code Transformer} used to transform the element into a key value
     * @throws NullPointerException if the map, elements or transformer are null
     */
    public static <K, V> void populateMap(final MultiMap<K, V> map, final Iterable<? extends V> elements,
            final Transformer<V, K> keyTransformer) {
        populateMap(map, elements, keyTransformer, TransformerUtils.<V>nopTransformer());
    }

    /**
     * Returns a predicated (validating) map backed by the given map.
     * <p>
     * Only objects that pass the tests in the given predicates can be added to the map. Trying to add an invalid object
     * results in an IllegalArgumentException. Keys must pass the key predicate, values must pass the value predicate.
     * It is important not to use the original map after invoking this method, as it is a backdoor for adding invalid
     * objects.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to predicate, must not be null
     * @param keyPred the predicate for keys, null means no check
     * @param valuePred the predicate for values, null means no check
     * @return a predicated map backed by the given map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> IterableMap<K, V> predicatedMap(final Map<K, V> map, final Predicate<? super K> keyPred,
            final Predicate<? super V> valuePred) {
        return PredicatedMap.predicatedMap(map, keyPred, valuePred);
    }

    /**
     * Returns a predicated (validating) sorted map backed by the given map.
     * <p>
     * Only objects that pass the tests in the given predicates can be added to the map. Trying to add an invalid object
     * results in an IllegalArgumentException. Keys must pass the key predicate, values must pass the value predicate.
     * It is important not to use the original map after invoking this method, as it is a backdoor for adding invalid
     * objects.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to predicate, must not be null
     * @param keyPred the predicate for keys, null means no check
     * @param valuePred the predicate for values, null means no check
     * @return a predicated map backed by the given map
     * @throws NullPointerException if the SortedMap is null
     */
    public static <K, V> SortedMap<K, V> predicatedSortedMap(final SortedMap<K, V> map,
            final Predicate<? super K> keyPred, final Predicate<? super V> valuePred) {
        return PredicatedSortedMap.predicatedSortedMap(map, keyPred, valuePred);
    }

    /**
     * Writes indentation to the given stream.
     *
     * @param out the stream to indent
     * @param indent the index of the indentation
     */
    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; i++) {
            out.print(INDENT_STRING);
        }
    }

    /**
     * Puts all the keys and values from the specified array into the map.
     * <p>
     * This method is an alternative to the {@link java.util.Map#putAll(java.util.Map)} method and constructors. It
     * allows you to build a map from an object array of various possible styles.
     * </p>
     * <p>
     * If the first entry in the object array implements {@link java.util.Map.Entry} or {@link KeyValue} then the key
     * and value are added from that object. If the first entry in the object array is an object array itself, then it
     * is assumed that index 0 in the sub-array is the key and index 1 is the value. Otherwise, the array is treated as
     * keys and values in alternate indices.
     * </p>
     * <p>
     * For example, to create a color map:
     * </p>
     *
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(),
     *         new String[][] { { "RED", "#FF0000" }, { "GREEN", "#00FF00" }, { "BLUE", "#0000FF" } });
     * </pre>
     *
     * <p>
     * or:
     * </p>
     *
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(),
     *         new String[] { "RED", "#FF0000", "GREEN", "#00FF00", "BLUE", "#0000FF" });
     * </pre>
     *
     * <p>
     * or:
     * </p>
     *
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(), new Map.Entry[] { new DefaultMapEntry("RED", "#FF0000"),
     *         new DefaultMapEntry("GREEN", "#00FF00"), new DefaultMapEntry("BLUE", "#0000FF") });
     * </pre>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to populate, must not be null
     * @param array an array to populate from, null ignored
     * @return the input map
     * @throws NullPointerException if map is null
     * @throws IllegalArgumentException if sub-array or entry matching used and an entry is invalid
     * @throws ClassCastException if the array contents is mixed
     * @since 3.2
     */
    @SuppressWarnings("unchecked") // As per Javadoc throws CCE for invalid array contents
    public static <K, V> Map<K, V> putAll(final Map<K, V> map, final Object[] array) {
        Objects.requireNonNull(map, "map");
        if (array == null || array.length == 0) {
            return map;
        }
        final Object obj = array[0];
        if (obj instanceof Map.Entry) {
            for (final Object element : array) {
                // cast ok here, type is checked above
                final Map.Entry<K, V> entry = (Map.Entry<K, V>) element;
                map.put(entry.getKey(), entry.getValue());
            }
        } else if (obj instanceof KeyValue) {
            for (final Object element : array) {
                // cast ok here, type is checked above
                final KeyValue<K, V> keyval = (KeyValue<K, V>) element;
                map.put(keyval.getKey(), keyval.getValue());
            }
        } else if (obj instanceof Object[]) {
            for (int i = 0; i < array.length; i++) {
                final Object[] sub = (Object[]) array[i];
                if (sub == null || sub.length < 2) {
                    throw new IllegalArgumentException("Invalid array element: " + i);
                }
                // these casts can fail if array has incorrect types
                map.put((K) sub[0], (V) sub[1]);
            }
        } else {
            for (int i = 0; i < array.length - 1;) {
                // these casts can fail if array has incorrect types
                map.put((K) array[i++], (V) array[i++]);
            }
        }
        return map;
    }

    /**
     * Protects against adding null values to a map.
     * <p>
     * This method checks the value being added to the map, and if it is null it is replaced by an empty string.
     * </p>
     * <p>
     * This could be useful if the map does not accept null values, or for receiving data from a source that may provide
     * null or empty string which should be held in the same way in the map.
     * </p>
     * <p>
     * Keys are not validated. Note that this method can be used to circumvent the map's value type at runtime.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to add to, must not be null
     * @param key the key
     * @param value the value, null converted to ""
     * @throws NullPointerException if the map is null
     */
    public static <K> void safeAddToMap(final Map<? super K, Object> map, final K key, final Object value)
            throws NullPointerException {
        Objects.requireNonNull(map, "map");
        map.put(key, value == null ? "" : value);
    }

    /**
     * Gets the given map size or 0 if the map is null
     *
     * @param map a Map or null
     * @return the given map size or 0 if the map is null
     */
    public static int size(final Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * Returns a synchronized map backed by the given map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to avoid non-deterministic behavior:
     * </p>
     *
     * <pre>
     * Map m = MapUtils.synchronizedMap(myMap);
     * Set s = m.keySet(); // outside synchronized block
     * synchronized (m) { // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process(i.next());
     *     }
     * }
     * </pre>
     *
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     */
    public static <K, V> Map<K, V> synchronizedMap(final Map<K, V> map) {
        return Collections.synchronizedMap(map);
    }

    /**
     * Returns a synchronized sorted map backed by the given sorted map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to avoid non-deterministic behavior:
     * </p>
     *
     * <pre>
     * Map m = MapUtils.synchronizedSortedMap(myMap);
     * Set s = m.keySet(); // outside synchronized block
     * synchronized (m) { // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process(i.next());
     *     }
     * }
     * </pre>
     *
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     * @throws NullPointerException if the map is null
     */
    public static <K, V> SortedMap<K, V> synchronizedSortedMap(final SortedMap<K, V> map) {
        return Collections.synchronizedSortedMap(map);
    }

    /**
     * Creates a new HashMap using data copied from a ResourceBundle.
     *
     * @param resourceBundle the resource bundle to convert, must not be null
     * @return the HashMap containing the data
     * @throws NullPointerException if the bundle is null
     */
    public static Map<String, Object> toMap(final ResourceBundle resourceBundle) {
        Objects.requireNonNull(resourceBundle, "resourceBundle");
        final Enumeration<String> enumeration = resourceBundle.getKeys();
        final Map<String, Object> map = new HashMap<>();

        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }

        return map;
    }

    /**
     * Gets a new Properties object initialized with the values from a Map. A null input will return an empty properties
     * object.
     * <p>
     * A Properties object may only store non-null keys and values, thus if the provided map contains either a key or
     * value which is {@code null}, a {@link NullPointerException} will be thrown.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to convert to a Properties object
     * @return the properties object
     * @throws NullPointerException if a key or value in the provided map is {@code null}
     */
    public static <K, V> Properties toProperties(final Map<K, V> map) {
        final Properties answer = new Properties();
        if (map != null) {
            for (final Entry<K, V> entry2 : map.entrySet()) {
                final Map.Entry<?, ?> entry = entry2;
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }

    /**
     * Returns a transformed map backed by the given map.
     * <p>
     * This method returns a new map (decorating the specified map) that will transform any new entries added to it.
     * Existing entries in the specified map will not be transformed. If you want that behavior, see
     * {@link TransformedMap#transformedMap}.
     * </p>
     * <p>
     * Each object is passed through the transformers as it is added to the Map. It is important not to use the original
     * map after invoking this method, as it is a backdoor for adding untransformed objects.
     * </p>
     * <p>
     * If there are any elements already in the map being decorated, they are NOT transformed.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to transform, must not be null, typically empty
     * @param keyTransformer the transformer for the map keys, null means no transformation
     * @param valueTransformer the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> IterableMap<K, V> transformedMap(final Map<K, V> map,
            final Transformer<? super K, ? extends K> keyTransformer,
            final Transformer<? super V, ? extends V> valueTransformer) {
        return TransformedMap.transformingMap(map, keyTransformer, valueTransformer);
    }

    /**
     * Returns a transformed sorted map backed by the given map.
     * <p>
     * This method returns a new sorted map (decorating the specified map) that will transform any new entries added to
     * it. Existing entries in the specified map will not be transformed. If you want that behavior, see
     * {@link TransformedSortedMap#transformedSortedMap}.
     * </p>
     * <p>
     * Each object is passed through the transformers as it is added to the Map. It is important not to use the original
     * map after invoking this method, as it is a backdoor for adding untransformed objects.
     * </p>
     * <p>
     * If there are any elements already in the map being decorated, they are NOT transformed.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to transform, must not be null, typically empty
     * @param keyTransformer the transformer for the map keys, null means no transformation
     * @param valueTransformer the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws NullPointerException if the SortedMap is null
     */
    public static <K, V> SortedMap<K, V> transformedSortedMap(final SortedMap<K, V> map,
            final Transformer<? super K, ? extends K> keyTransformer,
            final Transformer<? super V, ? extends V> valueTransformer) {
        return TransformedSortedMap.transformingSortedMap(map, keyTransformer, valueTransformer);
    }

    /**
     * Returns an unmodifiable map backed by the given map.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to make unmodifiable, must not be null
     * @return an unmodifiable map backed by the given map
     * @throws NullPointerException if the map is null
     */
    public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> map) {
        return UnmodifiableMap.unmodifiableMap(map);
    }

    /**
     * Returns an unmodifiable sorted map backed by the given sorted map.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the sorted map to make unmodifiable, must not be null
     * @return an unmodifiable map backed by the given map
     * @throws NullPointerException if the map is null
     */
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, ? extends V> map) {
        return UnmodifiableSortedMap.unmodifiableSortedMap(map);
    }

    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map. Each map entry will be printed with key and
     * value. When the value is a Map, recursive behavior occurs.
     * </p>
     * <p>
     * This method is NOT thread-safe in any special way. You must manually synchronize on either this class or the
     * stream as required.
     * </p>
     *
     * @param out the stream to print to, must not be null
     * @param label The label to be used, may be {@code null}. If {@code null}, the label is not output. It
     *        typically represents the name of the property in a bean or similar.
     * @param map The map to print, may be {@code null}. If {@code null}, the text 'null' is output.
     * @throws NullPointerException if the stream is {@code null}
     */
    public static void verbosePrint(final PrintStream out, final Object label, final Map<?, ?> map) {
        verbosePrintInternal(out, label, map, new ArrayDeque<>(), false);
    }

    /**
     * Implementation providing functionality for {@link #debugPrint} and for {@link #verbosePrint}. This prints the
     * given map with nice line breaks. If the debug flag is true, it additionally prints the type of the object value.
     * If the contents of a map include the map itself, then the text <em>(this Map)</em> is printed out. If the
     * contents include a parent container of the map, the text <em>(ancestor[i] Map)</em> is printed, where it actually
     * indicates the number of levels which must be traversed in the sequential list of ancestors (e.g. father,
     * grandfather, great-grandfather, etc).
     *
     * @param out the stream to print to
     * @param label the label to be used, may be {@code null}. If {@code null}, the label is not output. It
     *        typically represents the name of the property in a bean or similar.
     * @param map the map to print, may be {@code null}. If {@code null}, the text 'null' is output
     * @param lineage a stack consisting of any maps in which the previous argument is contained. This is checked to
     *        avoid infinite recursion when printing the output
     * @param debug flag indicating whether type names should be output.
     * @throws NullPointerException if the stream is {@code null}
     */
    private static void verbosePrintInternal(final PrintStream out, final Object label, final Map<?, ?> map,
            final Deque<Map<?, ?>> lineage, final boolean debug) {
        printIndent(out, lineage.size());

        if (map == null) {
            if (label != null) {
                out.print(label);
                out.print(" = ");
            }
            out.println("null");
            return;
        }
        if (label != null) {
            out.print(label);
            out.println(" = ");
        }

        printIndent(out, lineage.size());
        out.println("{");

        lineage.addLast(map);

        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            final Object childKey = entry.getKey();
            final Object childValue = entry.getValue();
            if (childValue instanceof Map && !lineage.contains(childValue)) {
                verbosePrintInternal(out, childKey == null ? "null" : childKey, (Map<?, ?>) childValue, lineage, debug);
            } else {
                printIndent(out, lineage.size());
                out.print(childKey);
                out.print(" = ");

                final int lineageIndex = IterableUtils.indexOf(lineage, PredicateUtils.equalPredicate(childValue));
                if (lineageIndex == -1) {
                    out.print(childValue);
                } else if (lineage.size() - 1 == lineageIndex) {
                    out.print("(this Map)");
                } else {
                    out.print("(ancestor[" + (lineage.size() - 1 - lineageIndex - 1) + "] Map)");
                }

                if (debug && childValue != null) {
                    out.print(' ');
                    out.println(childValue.getClass().getName());
                } else {
                    out.println();
                }
            }
        }

        lineage.removeLast();

        printIndent(out, lineage.size());
        out.println(debug ? "} " + map.getClass().getName() : "}");
    }

    /**
     * Don't allow instances.
     */
    private MapUtils() {
    }

}
