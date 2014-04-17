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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.multimap.MultiValuedHashMap;

import junit.framework.Test;

/**
 * Tests for MultiMapUtils
 *
 * @since 4.1
 * @version $Id$
 */
public class MultiMapUtilsTest extends BulkTest {

    public static Test suite() {
        return BulkTest.makeSuite(MultiMapUtilsTest.class);
    }

    public MultiMapUtilsTest(String name) {
        super(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testEmptyUnmodifiableMultiValuedMap() {
        final MultiValuedMap map = MultiMapUtils.EMPTY_MULTI_VALUED_MAP;
        assertTrue(map.isEmpty());
        try {
            map.put("key", "value");
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testTypeSafeEmptyMultiValuedMap() {
        final MultiValuedMap<String, String> map = MultiMapUtils.<String, String>emptyMultiValuedMap();
        assertTrue(map.isEmpty());
        try {
            map.put("key", "value");
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testEmptyIfNull() {
        assertTrue(MultiMapUtils.emptyIfNull(null).isEmpty());

        final MultiValuedMap<String, String> map = new MultiValuedHashMap<String, String>();
        map.put("item", "value");
        assertFalse(MultiMapUtils.emptyIfNull(map).isEmpty());
    }

    public void testIsEmptyWithEmptyMap() {
        final MultiValuedMap<Object, Object> map = new MultiValuedHashMap<Object, Object>();
        assertEquals(true, MultiMapUtils.isEmpty(map));
    }

    public void testIsEmptyWithNonEmptyMap() {
        final MultiValuedMap<String, String> map = new MultiValuedHashMap<String, String>();
        map.put("item", "value");
        assertEquals(false, MultiMapUtils.isEmpty(map));
    }

    public void testIsEmptyWithNull() {
        final MultiValuedMap<Object, Object> map = null;
        assertEquals(true, MultiMapUtils.isEmpty(map));
    }

    public void testGetCollection() {
        assertNull(MultiMapUtils.getCollection(null, "key1"));

        String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new MultiValuedHashMap<String, String>();
        for (String val : values) {
            map.put("key1", val);
        }

        Collection<String> col = MultiMapUtils.getCollection(map, "key1");
        for (String val : values) {
            assertTrue(col.contains(val));
        }
    }

    public void testGetList() {
        assertNull(MultiMapUtils.getList(null, "key1"));

        String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new MultiValuedHashMap<String, String>();
        for (String val : values) {
            map.put("key1", val);
        }

        List<String> list = MultiMapUtils.getList(map, "key1");
        int i = 0;
        for (String val : list) {
            assertTrue(val.equals(values[i++]));
        }
    }

    public void testGetSet() {
        assertNull(MultiMapUtils.getList(null, "key1"));

        String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new MultiValuedHashMap<String, String>();
        for (String val : values) {
            map.put("key1", val);
            map.put("key1", val);
        }

        Set<String> set = MultiMapUtils.getSet(map, "key1");
        assertEquals(3, set.size());
        for (String val : values) {
            assertTrue(set.contains(val));
        }
    }

    public void testGetBag() {
        assertNull(MultiMapUtils.getBag(null, "key1"));

        String values[] = { "v1", "v2", "v3" };
        final MultiValuedMap<String, String> map = new MultiValuedHashMap<String, String>();
        for (String val : values) {
            map.put("key1", val);
            map.put("key1", val);
        }

        Bag<String> bag = MultiMapUtils.getBag(map, "key1");
        assertEquals(6, bag.size());
        for (String val : values) {
            assertTrue(bag.contains(val));
            assertEquals(2, bag.getCount(val));
        }
    }

}
