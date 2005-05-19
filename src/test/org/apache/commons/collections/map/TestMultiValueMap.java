/*
 *  Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.map;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;

import org.apache.commons.collections.IteratorUtils;

/**
 * TestMultiValueMap.
 *
 * @author <a href="mailto:jcarman@apache.org">James Carman</a>
 * @since Commons Collections 3.2
 */
public class TestMultiValueMap extends TestCase {
    public void testNoMappingReturnsNull() {
        final MultiValueMap map = createTestMap();
        assertNull(map.get("whatever"));
    }

    public void testValueCollectionType() {
        final MultiValueMap map = createTestMap(LinkedList.class);
        assertTrue(map.get("one") instanceof LinkedList);
    }

    public void testMultipleValues() {
        final MultiValueMap map = createTestMap(HashSet.class);
        final HashSet expected = new HashSet();
        expected.add("uno");
        expected.add("un");
        assertEquals(expected, map.get("one"));
    }

    public void testContainsValue() {
        final MultiValueMap map = createTestMap(HashSet.class);
        assertTrue(map.containsValue("uno"));
        assertTrue(map.containsValue("un"));
        assertTrue(map.containsValue("dos"));
        assertTrue(map.containsValue("deux"));
        assertTrue(map.containsValue("tres"));
        assertTrue(map.containsValue("trois"));
        assertFalse(map.containsValue("quatro"));
    }

    public void testKeyContainsValue() {
        final MultiValueMap map = createTestMap(HashSet.class);
        assertTrue(map.containsValue("one", "uno"));
        assertTrue(map.containsValue("one", "un"));
        assertTrue(map.containsValue("two", "dos"));
        assertTrue(map.containsValue("two", "deux"));
        assertTrue(map.containsValue("three", "tres"));
        assertTrue(map.containsValue("three", "trois"));
        assertFalse(map.containsValue("four", "quatro"));
    }

    public void testValues() {
        final MultiValueMap map = createTestMap(HashSet.class);
        final HashSet expected = new HashSet();
        expected.add("uno");
        expected.add("dos");
        expected.add("tres");
        expected.add("un");
        expected.add("deux");
        expected.add("trois");
        final Collection c = map.values();
        assertEquals(6, c.size());
        assertEquals(expected, new HashSet(c));
    }

    private MultiValueMap createTestMap() {
        return createTestMap(ArrayList.class);
    }

    private MultiValueMap createTestMap(Class collectionClass) {
        final MultiValueMap map = MultiValueMap.decorate(new HashMap(), collectionClass);
        map.put("one", "uno");
        map.put("one", "un");
        map.put("two", "dos");
        map.put("two", "deux");
        map.put("three", "tres");
        map.put("three", "trois");
        return map;
    }

    public void testKeyedIterator() {
        final MultiValueMap map = createTestMap();
        final ArrayList actual = new ArrayList(IteratorUtils.toList(map.iterator("one")));
        final ArrayList expected = new ArrayList(Arrays.asList(new String[]{"uno", "un"}));
        assertEquals(expected, actual);
    }

    public void testRemoveAllViaIterator() {
        final MultiValueMap map = createTestMap();
        for(Iterator i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertTrue(map.isEmpty());
    }

    public void testRemoveAllViaKeyedIterator() {
        final MultiValueMap map = createTestMap();
        for(Iterator i = map.iterator("one"); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertEquals(4, map.totalSize());
    }

    public void testTotalSize() {
        assertEquals(6, createTestMap().totalSize());
    }
}
