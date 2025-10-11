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

package org.apache.commons.collections4.properties;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.iterators.IteratorEnumeration;

/**
 * A drop-in replacement for {@link Properties} for sorting keys.
 * <p>
 * Overrides {@link Properties#keys()} to sort keys. Allows other methods on the superclass to work with sorted keys.
 * </p>
 *
 * @see SortedPropertiesFactory#INSTANCE
 * @since 4.2
 */
public class SortedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new instance.
     */
    public SortedProperties() {
        // empty
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        final Stream<SimpleEntry<Object, Object>> stream = sortedKeys().map(k -> new AbstractMap.SimpleEntry<>(k, getProperty(k)));
        return stream.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Enumerates all key/value pairs in the specified TreeSet and omits the property if the key or value is not a string.
     *
     * @param result The result set to populate.
     * @return The given set.
     */
    private synchronized TreeSet<String> enumerateStringProperties(final TreeSet<String> result) {
        if (defaults != null) {
            result.addAll(defaults.stringPropertyNames());
        }
        for (final Enumeration<?> e = keys(); e.hasMoreElements();) {
            final Object k = e.nextElement();
            final Object v = get(k);
            if (k instanceof String && v instanceof String) {
                result.add((String) k);
            }
        }
        return result;
    }

    @Override
    public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
        keySet().stream().forEach(k -> action.accept(k, get(k)));
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return new IteratorEnumeration<>(sortedKeys().collect(Collectors.toList()).iterator());
    }

    @Override
    public Set<Object> keySet() {
        return new TreeSet<>(super.keySet());
    }

    @Override
    public Enumeration<?> propertyNames() {
        return Collections.enumeration(keySet());
    }

    private Stream<String> sortedKeys() {
        return keySet().stream().map(Object::toString).sorted();
    }

    @Override
    public Set<String> stringPropertyNames() {
        return enumerateStringProperties(new TreeSet<>());
    }
}
