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

import java.util.TreeMap;

import org.apache.commons.collections4.map.AbstractMapTest;

/**
 * Tests TreeMap.
 *
 * @version $Id$
 */
public abstract class AbstractTreeMapTest<K, V> extends AbstractMapTest<K, V> {

    public AbstractTreeMapTest(final String testName) {
        super(testName);
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract TreeMap<K, V> makeObject();

    public void testNewMap() {
        final TreeMap<K, V> map = makeObject();
        assertTrue("New map is empty", map.isEmpty());
        assertEquals("New map has size zero", 0, map.size());
    }

    @SuppressWarnings("unchecked")
    public void testSearch() {
        final TreeMap<K, V> map = makeObject();
        map.put((K) "first", (V) "First Item");
        map.put((K) "second", (V) "Second Item");
        assertEquals("Top item is 'Second Item'",
            "First Item", map.get("first"));
        assertEquals("Next Item is 'First Item'",
            "Second Item", map.get("second"));
    }

}
