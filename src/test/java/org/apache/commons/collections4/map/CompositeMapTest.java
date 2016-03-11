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

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Extension of {@link AbstractMapTest} for exercising the
 * {@link CompositeMap} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class CompositeMapTest<K, V> extends AbstractIterableMapTest<K, V> {
    /** used as a flag in MapMutator tests */
    private boolean pass = false;

    public CompositeMapTest(final String testName) {
        super(testName);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.pass = false;
    }

    @Override
    public CompositeMap<K, V> makeObject() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>();
        map.addComposited(new HashMap<K, V>());
        map.setMutator( new EmptyMapMutator<K, V>() );
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<K, V> buildOne() {
        final HashMap<K, V> map = new HashMap<K, V>();
        map.put((K) "1", (V) "one");
        map.put((K) "2", (V) "two");
        return map;
    }

    @SuppressWarnings("unchecked")
    public Map<K, V> buildTwo() {
        final HashMap<K, V> map = new HashMap<K, V>();
        map.put((K) "3", (V) "three");
        map.put((K) "4", (V) "four");
        return map;
    }

    public void testGet() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        assertEquals("one", map.get("1"));
        assertEquals("four", map.get("4"));
    }

    @SuppressWarnings("unchecked")
    public void testAddComposited() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        try {
            map.addComposited(three);
            fail("Expecting IllegalArgumentException.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testRemoveComposited() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        map.removeComposited(three);
        assertFalse(map.containsKey("5"));

        map.removeComposited(buildOne());
        assertFalse(map.containsKey("2"));

    }

    @SuppressWarnings("unchecked")
    public void testRemoveFromUnderlying() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        //Now remove "5"
        three.remove("5");
        assertFalse(map.containsKey("5"));
    }

    @SuppressWarnings("unchecked")
    public void testRemoveFromComposited() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        final HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));

        //Now remove "5"
        map.remove("5");
        assertFalse(three.containsKey("5"));
    }

    public void testResolveCollision() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo(),
            new CompositeMap.MapMutator<K, V>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void resolveCollision(final CompositeMap<K, V> composite,
            final Map<K, V> existing,
            final Map<K, V> added,
            final Collection<K> intersect) {
                pass = true;
            }

            @Override
            public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key,
                final V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException();
            }
        });

        map.addComposited(buildOne());
        assertTrue(pass);
    }

    @SuppressWarnings("unchecked")
    public void testPut() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo(),
            new CompositeMap.MapMutator<K, V>() {
            private static final long serialVersionUID = 1L;
            @Override
            public void resolveCollision(final CompositeMap<K, V> composite,
            final Map<K, V> existing,
            final Map<K, V> added,
            final Collection<K> intersect) {
                throw new UnsupportedOperationException();
            }

            @Override
            public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key,
                final V value) {
                pass = true;
                return (V) "foo";
            }

            @Override
            public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException();
            }
        });

        map.put((K) "willy", (V) "wonka");
        assertTrue(pass);
    }

    public void testPutAll() {
        final CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo(),
            new CompositeMap.MapMutator<K, V>() {
            private static final long serialVersionUID = 1L;
            @Override
            public void resolveCollision(final CompositeMap<K, V> composite,
            final Map<K, V> existing,
            final Map<K, V> added,
            final Collection<K> intersect) {
                throw new UnsupportedOperationException();
            }

            @Override
            public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key,
                final V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
                pass = true;
            }
        });

        map.putAll(null);
        assertTrue(pass);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/CompositeMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/CompositeMap.fullCollection.version4.obj");
//    }

}
