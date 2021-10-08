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

package org.apache.commons.collections4.properties;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.iterators.IteratorEnumeration;

/**
 * A drop-in replacement for {@link Properties} for sorting keys.
 * <p>
 * Overrides {@link Properties#keys()} to sort keys. Allows other methods on the superclass to work with sorted keys.
 * </p>
 *
 * @since 4.2
 */
public class SortedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        final Stream<SimpleEntry<Object, Object>> stream = sortedKeys().map(k -> new AbstractMap.SimpleEntry<>(k, getProperty(k)));
        return stream.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return new IteratorEnumeration<>(sortedKeys().collect(Collectors.toList()).iterator());
    }

    private Stream<String> sortedKeys() {
        return keySet().stream().map(Object::toString).sorted();
    }
}
