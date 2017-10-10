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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.functors.NOPTransformer;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.splitmap.TransformedSplitMap;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link TransformedSplitMap}
 *
 * @since 4.0
 */
@SuppressWarnings("boxing")
public class SplitMapUtilsTest {
    private Map<String, Integer> backingMap;
    private TransformedSplitMap<String, String, String, Integer> transformedMap;

    private final Transformer<String, Integer> stringToInt = new Transformer<String, Integer>() {
        @Override
        public Integer transform(final String input) {
            return Integer.valueOf(input);
        }
    };

    @Before
    public void setUp() throws Exception {
        backingMap = new HashMap<>();
        transformedMap = TransformedSplitMap.transformingMap(backingMap, NOPTransformer.<String> nopTransformer(),
                stringToInt);
        for (int i = 0; i < 10; i++) {
            transformedMap.put(String.valueOf(i), String.valueOf(i));
        }
    }

    // -----------------------------------------------------------------------

    @Test
    public void testReadableMap() {
        final IterableMap<String, Integer> map = SplitMapUtils.readableMap(transformedMap);

        // basic
        for (int i = 0; i < 10; i++) {
            assertFalse(map.containsValue(String.valueOf(i)));
            assertEquals(i, map.get(String.valueOf(i)).intValue());
        }

        // mapIterator
        final MapIterator<String, Integer> it = map.mapIterator();
        while (it.hasNext()) {
            final String k = it.next();
            assertEquals(k, it.getKey());
            assertEquals(Integer.valueOf(k), it.getValue());
        }

        // unmodifiable
        assertTrue(map instanceof Unmodifiable);

        // check individual operations
        int sz = map.size();

        attemptPutOperation(new Runnable() {
            @Override
            public void run() {
                map.clear();
            }
        });

        assertEquals(sz, map.size());

        attemptPutOperation(new Runnable() {
            @Override
            public void run() {
                map.put("foo", 100);
            }
        });

        final HashMap<String, Integer> m = new HashMap<>();
        m.put("foo", 100);
        m.put("bar", 200);
        m.put("baz", 300);
        attemptPutOperation(new Runnable() {
            @Override
            public void run() {
                map.putAll(m);
            }
        });

        // equals, hashcode
        final IterableMap<String, Integer> other = SplitMapUtils.readableMap(transformedMap);
        assertEquals(other, map);
        assertEquals(other.hashCode(), map.hashCode());

        // remove
        for (int i = 0; i < 10; i++) {
            assertEquals(i, map.remove(String.valueOf(i)).intValue());
            assertEquals(--sz, map.size());
        }
        assertTrue(map.isEmpty());
        assertSame(map, SplitMapUtils.readableMap(map));
    }

    @Test
    public void testAlreadyReadableMap() {
        final HashedMap<String, Integer> hashedMap = new HashedMap<>();
        assertSame(hashedMap, SplitMapUtils.readableMap(hashedMap));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWritableMap() {
        final Map<String, String> map = SplitMapUtils.writableMap(transformedMap);
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.get(null);
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.entrySet();
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.keySet();
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.values();
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.size();
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.isEmpty();
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.containsKey(null);
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.containsValue(null);
            }
        });
        attemptGetOperation(new Runnable() {
            @Override
            public void run() {
                map.remove(null);
            }
        });

        // equals, hashcode
        final Map<String, String> other = SplitMapUtils.writableMap(transformedMap);
        assertEquals(other, map);
        assertEquals(other.hashCode(), map.hashCode());

        // put
        int sz = backingMap.size();
        assertFalse(backingMap.containsKey("foo"));
        map.put("new", "66");
        assertEquals(++sz, backingMap.size());

        // putall
        final Map<String, String> more = new HashMap<>();
        more.put("foo", "77");
        more.put("bar", "88");
        more.put("baz", "99");
        map.putAll(more);
        assertEquals(sz + more.size(), backingMap.size());

        // clear
        map.clear();
        assertTrue(backingMap.isEmpty());
        assertSame(map, SplitMapUtils.writableMap((Put<String, String>) map));
    }

    @Test
    public void testAlreadyWritableMap() {
        final HashedMap<String, String> hashedMap = new HashedMap<>();
        assertSame(hashedMap, SplitMapUtils.writableMap(hashedMap));
    }

    private void attemptGetOperation(final Runnable r) {
        attemptMapOperation("Put exposed as writable Map must not allow Get operations", r);
    }

    private void attemptPutOperation(final Runnable r) {
        attemptMapOperation("Get exposed as writable Map must not allow Put operations", r);
    }

    private void attemptMapOperation(final String s, final Runnable r) {
        try {
            r.run();
            fail(s);
        } catch (final UnsupportedOperationException e) {
        }
    }

}
