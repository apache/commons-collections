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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections4.MapIterator;

/**
 * Abstract class for testing the MapIterator interface.
 * <p>
 * This class provides a framework for testing an implementation of MapIterator.
 * Concrete subclasses must provide the list iterator to be tested.
 * They must also specify certain details of how the list iterator operates by
 * overriding the supportsXxx() methods if necessary.
 *
 * @since 3.0
 */
public abstract class AbstractMapIteratorTest<K, V> extends AbstractIteratorTest<K> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractMapIteratorTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implement this method to return a map iterator over an empty map.
     *
     * @return an empty iterator
     */
    @Override
    public abstract MapIterator<K, V> makeEmptyIterator();

    /**
     * Implement this method to return a map iterator over a map with elements.
     *
     * @return a full iterator
     */
    @Override
    public abstract MapIterator<K, V> makeObject();

    /**
     * Implement this method to return the map which contains the same data as the
     * iterator.
     *
     * @return a full map which can be updated
     */
    public abstract Map<K, V> getMap();

    /**
     * Implement this method to return the confirmed map which contains the same
     * data as the iterator.
     *
     * @return a full map which can be updated
     */
    public abstract Map<K, V> getConfirmedMap();

    /**
     * Whether or not we are testing an iterator that supports setValue().
     * Default is true.
     *
     * @return true if Iterator supports set
     */
    public boolean supportsSetValue() {
        return true;
    }

    /**
     * Whether the get operation on the map structurally modifies the map,
     * such as with LRUMap. Default is false.
     *
     * @return true if the get method structurally modifies the map
     */
    public boolean isGetStructuralModify() {
        return false;
    }

    /**
     * The values to be used in the add and set tests.
     * Default is two strings.
     */
    @SuppressWarnings("unchecked")
    public V[] addSetValues() {
        return (V[]) new Object[] { "A", "B" };
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the empty list iterator contract is correct.
     */
    public void testEmptyMapIterator() {
        if (!supportsEmptyIterator()) {
            return;
        }

        final MapIterator<K, V> it = makeEmptyIterator();
        assertFalse(it.hasNext());

        // next() should throw a NoSuchElementException
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            it.next();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        }
        // getKey() should throw an IllegalStateException
        exception = assertThrows(IllegalStateException.class, () -> {
            it.getKey();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        // getValue() should throw an IllegalStateException
        exception = assertThrows(IllegalStateException.class, () -> {
            it.getValue();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        if (!supportsSetValue()) {
            // setValue() should throw an UnsupportedOperationException/IllegalStateException
            exception = assertThrows(Exception.class, () -> {
                it.setValue(addSetValues()[0]);
            });
            if (null != exception.getMessage()) {
                assertNotNull(exception.getMessage());
            }
        } else {
            // setValue() should throw an IllegalStateException
            exception = assertThrows(IllegalStateException.class, () -> {
                it.setValue(addSetValues()[0]);
            });
            if (null != exception.getMessage()) {
                assertNotNull(exception.getMessage());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the full list iterator contract is correct.
     */
    public void testFullMapIterator() {
        if (!supportsFullIterator()) {
            return;
        }

        final MapIterator<K, V> it = makeObject();
        final Map<K, V> map = getMap();
        assertTrue(it.hasNext());

        assertTrue(it.hasNext());
        final Set<K> set = new HashSet<>();
        while (it.hasNext()) {
            // getKey
            final K key = it.next();
            assertSame("it.next() should equals getKey()", key, it.getKey());
            assertTrue(map.containsKey(key));
            assertTrue(set.add(key));

            // getValue
            final V value = it.getValue();
            if (!isGetStructuralModify()) {
                assertSame("Value must be mapped to key", map.get(key), value);
            }
            assertTrue(map.containsValue(value));

            verify();
        }
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorSet() {
        if (!supportsFullIterator()) {
            return;
        }

        final V newValue = addSetValues()[0];
        final V newValue2 = addSetValues().length == 1 ? addSetValues()[0] : addSetValues()[1];
        final MapIterator<K, V> it = makeObject();
        final Map<K, V> map = getMap();
        final Map<K, V> confirmed = getConfirmedMap();
        assertTrue(it.hasNext());
        final K key = it.next();
        final V value = it.getValue();

        if (!supportsSetValue()) {
            Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
                it.setValue(newValue);
            });
            if (null != exception.getMessage()) {
                assertTrue(exception.getMessage().contains("not support"));
            }
            return;
        }
        final V old = it.setValue(newValue);
        confirmed.put(key, newValue);
        assertSame("Key must not change after setValue", key, it.getKey());
        assertSame("Value must be changed after setValue", newValue, it.getValue());
        assertSame("setValue must return old value", value, old);
        assertTrue(map.containsKey(key));
        // test against confirmed, as map may contain value twice
        assertEquals(confirmed.containsValue(old), map.containsValue(old));
        assertTrue(map.containsValue(newValue));
        verify();

        it.setValue(newValue);  // same value - should be OK
        confirmed.put(key, newValue);
        assertSame("Key must not change after setValue", key, it.getKey());
        assertSame("Value must be changed after setValue", newValue, it.getValue());
        verify();

        it.setValue(newValue2);  // new value
        confirmed.put(key, newValue2);
        assertSame("Key must not change after setValue", key, it.getKey());
        assertSame("Value must be changed after setValue", newValue2, it.getValue());
        verify();
    }

    //-----------------------------------------------------------------------
    @Override
    public void testRemove() { // override
        final MapIterator<K, V> it = makeObject();
        final Map<K, V> map = getMap();
        final Map<K, V> confirmed = getConfirmedMap();
        assertTrue(it.hasNext());
        final K key = it.next();

        if (!supportsRemove()) {
            Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
                it.remove();
            });
            if (null != exception.getMessage()) {
                assertTrue(exception.getMessage().contains("not support"));
            }
            return;
        }

        it.remove();
        confirmed.remove(key);
        assertFalse(map.containsKey(key));
        verify();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            it.remove();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("can only be called"));
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorSetRemoveSet() {
        if (!supportsSetValue() || !supportsRemove()) {
            return;
        }
        final V newValue = addSetValues()[0];
        final MapIterator<K, V> it = makeObject();
        final Map<K, V> confirmed = getConfirmedMap();

        assertTrue(it.hasNext());
        final K key = it.next();

        it.setValue(newValue);
        it.remove();
        confirmed.remove(key);
        verify();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            it.setValue(newValue);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("can only be called"));
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorRemoveGetKey() {
        if (!supportsRemove()) {
            return;
        }
        final MapIterator<K, V> it = makeObject();
        final Map<K, V> confirmed = getConfirmedMap();

        assertTrue(it.hasNext());
        final K key = it.next();

        it.remove();
        confirmed.remove(key);
        verify();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            it.getKey();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("can only be called"));
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorRemoveGetValue() {
        if (!supportsRemove()) {
            return;
        }
        final MapIterator<K, V> it = makeObject();
        final Map<K, V> confirmed = getConfirmedMap();

        assertTrue(it.hasNext());
        final K key = it.next();

        it.remove();
        confirmed.remove(key);
        verify();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            it.getValue();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("can only be called"));
        }
        verify();
    }

}
