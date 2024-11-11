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
package org.apache.commons.collections4.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections4.map.LinkedMap;
import org.junit.jupiter.api.Test;

/**
 * JUnit test.
 */
public class MapBackedSet2Test<E> extends AbstractSetTest<E> {

    @Override
    public Set<E> makeObject() {
        return MapBackedSet.mapBackedSet(new LinkedMap<>());
    }

    @SuppressWarnings("unchecked")
    protected Set<E> setupSet() {
        final Set<E> set = makeObject();

        for (int i = 0; i < 10; i++) {
            set.add((E) Integer.toString(i));
        }
        return set;
    }

    @Test
    @Override
    public void testCanonicalEmptyCollectionExists() {
    }

    @Test
    @Override
    public void testCanonicalFullCollectionExists() {
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOrdering() {
        final Set<E> set = setupSet();
        Iterator<E> it = set.iterator();

        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.toString(i), it.next(), "Sequence is wrong");
        }

        for (int i = 0; i < 10; i += 2) {
            assertTrue(set.remove(Integer.toString(i)), "Must be able to remove int");
        }

        it = set.iterator();
        for (int i = 1; i < 10; i += 2) {
            assertEquals(Integer.toString(i), it.next(), "Sequence is wrong after remove ");
        }

        for (int i = 0; i < 10; i++) {
            set.add((E) Integer.toString(i));
        }

        assertEquals(10, set.size(), "Size of set is wrong!");

        it = set.iterator();
        for (int i = 1; i < 10; i += 2) {
            assertEquals(Integer.toString(i), it.next(), "Sequence is wrong");
        }
        for (int i = 0; i < 10; i += 2) {
            assertEquals(Integer.toString(i), it.next(), "Sequence is wrong");
        }
    }

}
