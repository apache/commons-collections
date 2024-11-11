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
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMapTest} for exercising the {@link CompositeMap} implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class CompositeMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    /** Used as a flag in MapMutator tests */
    private boolean pass;

    @SuppressWarnings("unchecked")
    private Map<K, V> buildOne() {
        final HashMap<K, V> map = new HashMap<>();
        map.put((K) "1", (V) "one");
        map.put((K) "2", (V) "two");
        return map;
    }

    @SuppressWarnings("unchecked")
    public Map<K, V> buildTwo() {
        final HashMap<K, V> map = new HashMap<>();
        map.put((K) "3", (V) "three");
        map.put((K) "4", (V) "four");
        return map;
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public CompositeMap<K, V> makeObject() {
        final CompositeMap<K, V> map = new CompositeMap<>();
        map.addComposited(new HashMap<>());
        map.setMutator(new EmptyMapMutator<>());
        return map;
    }

    @BeforeEach
    public void setUp() throws Exception {
        pass = false;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddComposited() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<>();
        three.put((K) "5", (V) "five");
        map.addComposited(null);
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        assertThrows(IllegalArgumentException.class, () -> map.addComposited(three));
    }

    @Test
    public void testGet() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo());
        assertEquals("one", map.get("1"));
        assertEquals("four", map.get("4"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPut() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo(), new CompositeMap.MapMutator<K, V>() {
            private static final long serialVersionUID = 1L;

            @Override
            public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key, final V value) {
                pass = true;
                return (V) "foo";
            }

            @Override
            public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void resolveCollision(final CompositeMap<K, V> composite, final Map<K, V> existing, final Map<K, V> added, final Collection<K> intersect) {
                throw new UnsupportedOperationException();
            }
        });

        map.put((K) "willy", (V) "wonka");
        assertTrue(pass);
    }

    @Test
    public void testPutAll() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo(), new CompositeMap.MapMutator<K, V>() {
            private static final long serialVersionUID = 1L;

            @Override
            public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key, final V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
                pass = true;
            }

            @Override
            public void resolveCollision(final CompositeMap<K, V> composite, final Map<K, V> existing, final Map<K, V> added, final Collection<K> intersect) {
                throw new UnsupportedOperationException();
            }
        });

        map.putAll(null);
        assertTrue(pass);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveComposited() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<>();
        three.put((K) "5", (V) "five");
        map.addComposited(null);
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        map.removeComposited(three);
        assertFalse(map.containsKey("5"));

        map.removeComposited(buildOne());
        assertFalse(map.containsKey("2"));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveFromComposited() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<>();
        three.put((K) "5", (V) "five");
        map.addComposited(null);
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        // Now remove "5"
        map.remove("5");
        assertFalse(three.containsKey("5"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveFromUnderlying() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<>();
        three.put((K) "5", (V) "five");
        map.addComposited(null);
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        // Now remove "5"
        three.remove("5");
        assertFalse(map.containsKey("5"));
    }

    @Test
    public void testResolveCollision() {
        final CompositeMap<K, V> map = new CompositeMap<>(buildOne(), buildTwo(), new CompositeMap.MapMutator<K, V>() {
            private static final long serialVersionUID = 1L;

            @Override
            public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key, final V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void resolveCollision(final CompositeMap<K, V> composite, final Map<K, V> existing, final Map<K, V> added, final Collection<K> intersect) {
                pass = true;
            }
        });

        map.addComposited(buildOne());
        assertTrue(pass);
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/CompositeMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/CompositeMap.fullCollection.version4.obj");
//    }

}
