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
package org.apache.commons.collections4.multimap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LinkedMap;

/**
 * Decorates another <code>MultiValuedMap</code> to transform objects that are added.
 * <p>
 * This class affects the MultiValuedMap put methods. Thus objects must be
 * removed or searched for using their transformed form. For example, if the
 * transformation converts Strings to Integers, you must use the Integer form to
 * remove objects.
 * <p>
 * <strong>Note that TransformedMultiValuedMap is not synchronized and is not thread-safe.</strong>
 *
 * @since 4.1
 * @version $Id$
 */
public class TransformedMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V> {

    /** Serialization Version */
    private static final long serialVersionUID = -1254147899086470720L;

    private final Transformer<? super K, ? extends K> keyTransformer;

    private final Transformer<? super V, ? extends V> valueTransformer;

    /**
     * Factory method to create a transforming MultiValuedMap.
     * <p>
     * If there are any elements already in the map being decorated, they are
     * NOT transformed. Contrast this with
     * {@link #transformedMap(MultiValuedMap, Transformer, Transformer)}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the MultiValuedMap to decorate, must not be null
     * @param keyTransformer the transformer to use for key conversion, null
     *        means no transformation
     * @param valueTransformer the transformer to use for value conversion, null
     *        means no transformation
     * @return a new transformed MultiValuedMap
     * @throws IllegalArgumentException if map is null
     */
    public static <K, V> TransformedMultiValuedMap<K, V> transformingMap(final MultiValuedMap<K, V> map,
            final Transformer<? super K, ? extends K> keyTransformer,
            final Transformer<? super V, ? extends V> valueTransformer) {
        return new TransformedMultiValuedMap<K, V>(map, keyTransformer, valueTransformer);
    }

    /**
     * Factory method to create a transforming MultiValuedMap that will
     * transform existing contents of the specified map.
     * <p>
     * If there are any elements already in the map being decorated, they will
     * be transformed by this method. Contrast this with
     * {@link #transformingMap(MultiValuedMap, Transformer, Transformer)}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the MultiValuedMap to decorate, must not be null
     * @param keyTransformer the transformer to use for key conversion, null
     *        means no transformation
     * @param valueTransformer the transformer to use for value conversion, null
     *        means no transformation
     * @return a new transformed MultiValuedMap
     * @throws IllegalArgumentException if map is null
     */
    public static <K, V> TransformedMultiValuedMap<K, V> transformedMap(final MultiValuedMap<K, V> map,
            final Transformer<? super K, ? extends K> keyTransformer,
            final Transformer<? super V, ? extends V> valueTransformer) {
        final TransformedMultiValuedMap<K, V> decorated =
                new TransformedMultiValuedMap<K, V>(map, keyTransformer, valueTransformer);
        if (map.size() > 0) {
            MultiValuedMap<K, V> transformed = decorated.transformMultiValuedMap(map);
            decorated.clear();
            // to avoid double transform
            decorated.decorated().putAll(transformed);
        }
        return decorated;
    }

    // -----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are NOT transformed.
     *
     * @param map the MultiValuedMap to decorate, must not be null
     * @param keyTransformer the transformer to use for key conversion, null
     *        means no conversion
     * @param valueTransformer the transformer to use for value conversion, null
     *        means no conversion
     * @throws IllegalArgumentException if map is null
     */
    protected TransformedMultiValuedMap(MultiValuedMap<K, V> map,
            Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        super(map);
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }

    /**
     * Transforms a key.
     * <p>
     * The transformer itself may throw an exception if necessary.
     *
     * @param object the object to transform
     * @return the transformed object
     */
    protected K transformKey(final K object) {
        if (keyTransformer == null) {
            return object;
        }
        return keyTransformer.transform(object);
    }

    /**
     * Transforms a value.
     * <p>
     * The transformer itself may throw an exception if necessary.
     *
     * @param object the object to transform
     * @return the transformed object
     */
    protected V transformValue(final V object) {
        if (valueTransformer == null) {
            return object;
        }
        return valueTransformer.transform(object);
    }

    /**
     * Transforms a map.
     * <p>
     * The transformer itself may throw an exception if necessary.
     *
     * @param map the map to transform
     * @return the transformed object
     */
    @SuppressWarnings("unchecked")
    protected Map<K, V> transformMap(final Map<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            return (Map<K, V>) map;
        }
        final Map<K, V> result = new LinkedMap<K, V>(map.size());

        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            result.put(transformKey(entry.getKey()), transformValue(entry.getValue()));
        }
        return result;
    }

    /**
     * Transforms a MultiValuedMap.
     * <p>
     * The transformer itself may throw an exception if necessary.
     *
     * @param map the MultiValuedMap to transform
     * @return the transformed object
     */
    @SuppressWarnings("unchecked")
    protected MultiValuedMap<K, V> transformMultiValuedMap(final MultiValuedMap<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            return (MultiValuedMap<K, V>) map;
        }
        final MultiValuedMap<K, V> result = new MultiValuedHashMap<K, V>();

        for (final Map.Entry<? extends K, ? extends V> entry : map.entries()) {
            result.put(transformKey(entry.getKey()), transformValue(entry.getValue()));
        }
        return result;
    }

    @Override
    public V put(K key, V value) {
        K transformedKey = transformKey(key);
        V transformedValue = transformValue(value);
        return decorated().put(transformedKey, transformedValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean putAll(K key, Iterable<? extends V> values) {
        if (values == null || values.iterator() == null || !values.iterator().hasNext()) {
            return false;
        }
        K transformedKey = transformKey(key);
        List<V> transformedValues = new LinkedList<V>();
        Iterator<V> it = (Iterator<V>) values.iterator();
        while (it.hasNext()) {
            transformedValues.add(transformValue(it.next()));
        }
        return decorated().putAll(transformedKey, transformedValues);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m == null) {
            return;
        }
        decorated().putAll(transformMap(m));
    }

    @Override
    public void putAll(MultiValuedMap<? extends K, ? extends V> m) {
        if (m == null) {
            return;
        }
        decorated().putAll(transformMultiValuedMap(m));
    }

}
