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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.Test;

/**
 * Tests for MultiMapUtils
 *
 * @since 4.1
 */
public class MultiMapUtilsTest {

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testEmptyUnmodifiableMultiValuedMap() {
        final MultiValuedMap map = MultiMapUtils.EMPTY_MULTI_VALUED_MAP;
        assertTrue(map.isEmpty());
        try {
            map.put("key", "value");
            fail("Should throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException e) {
        }
    }

    @Test
    public void testTypeSafeEmptyMultiValuedMap() {
        final MultiValuedMap<String, String> map = MultiMapUtils.<String, String>emptyMultiValuedMap();
        assertTrue(map.isEmpty());
        try {
            map.put("key", "value");
            fail("Should throw UnsupportedOperationException");
        } catch (final UnsupportedOperationException e) {
        }
    }

    @Test
    public void testEmptyIfNull() {
        assertTrue(MultiMapUtils.emptyIfNull(null).isEmpty());

        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        map.put("item", "value");
        assertFalse(MultiMapUtils.emptyIfNull(map).isEmpty());
    }

    @Test
    public void testIsEmptyWithEmptyMap() {
        final MultiValuedMap<Object, Object> map = new ArrayListValuedHashMap<>();
        assertEquals(true, MultiMapUtils.isEmpty(map));
    }

    @Test
    public void testIsEmptyWithNonEmptyMap() {
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        map.put("item", "value");
        assertEquals(false, MultiMapUtils.isEmpty(map));
    }

    @Test
    public void testIsEmptyWithNull() {
        final MultiValuedMap<Object, Object> map = null;
        assertEquals(true, MultiMapUtils.isEmpty(map));
    }

    @Test
    public void testGetCollection() {
        assertNull(MultiMapUtils.getCollection(null, "key1"));

        final String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
        }

        final Collection<String> col = MultiMapUtils.getCollection(map, "key1");
        for (final String val : values) {
            assertTrue(col.contains(val));
        }
    }

    @Test
    public void testGetValuesAsList() {
        assertNull(MultiMapUtils.getValuesAsList(null, "key1"));

        final String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
        }

        final List<String> list = MultiMapUtils.getValuesAsList(map, "key1");
        int i = 0;
        for (final String val : list) {
            assertTrue(val.equals(values[i++]));
        }
    }

    @Test
    public void testGetValuesAsSet() {
        assertNull(MultiMapUtils.getValuesAsList(null, "key1"));

        final String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new ArrayListValuedHashMap<>();
        for (final String val : values) {
            map.put("key1", val);
            map.put("key1", val);
        }

        final Set<String> set = MultiMapUtils.getValuesAsSet(map, "key1");
        assertEquals(3, set.size());
        for (final String val : values) {
            assertTrue(set.contains(val));
        }
    }

    @Test
    public void testGetValuesAsBag() {
        assertNull(MultiMapUtils.getValuesAsBag(null, "key1"));

        final String values[] = { "v1", "v2", "v3" };
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

}
