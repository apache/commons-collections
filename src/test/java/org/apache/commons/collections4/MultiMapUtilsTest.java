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
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.collections4.multimap.LinkedHashSetValuedLinkedHashMap;
import org.junit.jupiter.api.Test;

/**
 * Tests for MultiMapUtils
 */
class MultiMapUtilsTest {

    @Test
    void testEmptyIfNull() {
        assertTrue(MultiMapUtils.emptyIfNull(null).isEmpty());

        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        map.put("item", "value");
        assertFalse(MultiMapUtils.emptyIfNull(map).isEmpty());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testEmptyUnmodifiableMultiValuedMap() {
        final MultiValuedMap map = MultiMapUtils.EMPTY_MULTI_VALUED_MAP;
        assertTrue(map.isEmpty());

        assertThrows(UnsupportedOperationException.class, () -> map.put("key", "value"));
    }

    @Test
    void testGetCollection() {
        assertNull(MultiMapUtils.getCollection(null, "key1"));

        final String[] values = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
        }

        final Collection<String> col = MultiMapUtils.getCollection(map, "key1");
        assertEquals(Arrays.asList(values), col);
    }

    @Test
    void testGetValuesAsBag() {
        assertNull(MultiMapUtils.getValuesAsBag(null, "key1"));

        final String[] values = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
            map.put("key1", val);
        }

        final Bag<String> bag = MultiMapUtils.getValuesAsBag(map, "key1");
        assertEquals(6, bag.size());
        for (final String val : values) {
            assertTrue(bag.contains(val));
            assertEquals(2, bag.getCount(val));
        }
    }

    @Test
    void testGetValuesAsList() {
        assertNull(MultiMapUtils.getValuesAsList(null, "key1"));

        final String[] values = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
        }

        final List<String> list = MultiMapUtils.getValuesAsList(map, "key1");
        assertEquals(Arrays.asList(values), list);
    }

    @Test
    void testGetValuesAsSet() {
        assertNull(MultiMapUtils.getValuesAsList(null, "key1"));

        final String[] values = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
            map.put("key1", val);
        }

        final Set<String> set = MultiMapUtils.getValuesAsSet(map, "key1");
        assertEquals(new HashSet<>(Arrays.asList(values)), set);
    }

    @Test
    void testInvertMultiValuedMap() {
        final HashSetValuedHashMap<String, String> usages = new HashSetValuedHashMap<>();

        final LinkedHashSetValuedLinkedHashMap<String, String> deps = new LinkedHashSetValuedLinkedHashMap<>();
        deps.put("commons-configuration2", "commons-logging");
        deps.put("commons-configuration2", "commons-lang3");
        deps.put("commons-configuration2", "commons-text");
        deps.put("commons-beanutils", "commons-collections");
        deps.put("commons-beanutils", "commons-logging");
        MultiMapUtils.invert(deps, usages);
        final Set<String> loggingUsagesCompile = usages.get("commons-logging");
        assertEquals("[commons-configuration2, commons-beanutils]", loggingUsagesCompile.toString());
        final Set<String> codecUsagesCompile = usages.get("commons-codec");
        assertEquals("[]", codecUsagesCompile.toString());

        final LinkedHashSetValuedLinkedHashMap<String, String> optionalDeps = new LinkedHashSetValuedLinkedHashMap<>();
        optionalDeps.put("commons-configuration2", "commons-codec");
        optionalDeps.put("commons-collections", "commons-codec");
        MultiMapUtils.invert(optionalDeps, usages);
        final Set<String> codecUsagesAll = usages.get("commons-codec");
        assertEquals("[commons-collections, commons-configuration2]", codecUsagesAll.toString());
    }

    @Test
    void testInvertMap() {
        final HashSetValuedHashMap<String, String> usages = new HashSetValuedHashMap<>();

        final Map<String, String> deps = new HashMap<>();
        deps.put("commons-configuration1", "commons-logging");
        deps.put("commons-configuration2", "commons-lang3");
        deps.put("commons-configuration3", "commons-text");
        deps.put("commons-beanutils1", "commons-collections");
        deps.put("commons-beanutils2", "commons-logging");
        MultiMapUtils.invert(deps, usages);
        final Set<String> loggingUsagesCompile = usages.get("commons-logging");
        assertEquals("[commons-beanutils2, commons-configuration1]", loggingUsagesCompile.toString());
        final Set<String> codecUsagesCompile = usages.get("commons-codec");
        assertEquals("[]", codecUsagesCompile.toString());

        final Map<String, String> optionalDeps = new HashMap<>();
        optionalDeps.put("commons-configuration2", "commons-codec");
        optionalDeps.put("commons-collections", "commons-codec");
        MultiMapUtils.invert(optionalDeps, usages);
        final Set<String> codecUsagesAll = usages.get("commons-codec");
        assertEquals("[commons-collections, commons-configuration2]", codecUsagesAll.toString());
    }

    @Test
    void testIsEmptyWithEmptyMap() {
        assertTrue(MultiMapUtils.isEmpty(new ArrayListValuedHashMap<>()));
    }

    @Test
    void testIsEmptyWithNonEmptyMap() {
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        map.put("item", "value");
        assertFalse(MultiMapUtils.isEmpty(map));
    }

    @Test
    void testIsEmptyWithNull() {
        assertTrue(MultiMapUtils.isEmpty(null));
    }

    @Test
    void testTypeSafeEmptyMultiValuedMap() {
        final MultiValuedMap<String, String> map = MultiMapUtils.<String, String>emptyMultiValuedMap();
        assertTrue(map.isEmpty());

        assertThrows(UnsupportedOperationException.class, () -> map.put("key", "value"));
    }

}
