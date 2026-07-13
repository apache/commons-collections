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
package org.apache.commons.collections4.multiset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InvalidObjectException;
import java.util.Arrays;

import org.apache.commons.collections4.MultiSet;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMultiSetTest} for exercising the
 * {@link HashMultiSet} implementation.
 */
public class HashMultiSetTest<T> extends AbstractMultiSetTest<T> {

    @Override
    public String getCompatibilityVersion() {
        return "4.1";
    }

    @Override
    protected int getIterationBehaviour() {
        return UNORDERED;
    }

    @Override
    public MultiSet<T> makeObject() {
        return new HashMultiSet<>();
    }

    @Test
    void testAddClampsCountAndSizeToIntegerMaxValue() {
        final HashMultiSet<String> set = new HashMultiSet<>();
        set.add("X", Integer.MAX_VALUE);
        set.add("X", 1);
        assertEquals(Integer.MAX_VALUE, set.getCount("X"));
        assertEquals(Integer.MAX_VALUE, set.size());
        set.add("Y", Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, set.size());
        // true size is 2 * Integer.MAX_VALUE - 2 after this, so size() must stay saturated
        set.remove("X", 2);
        assertEquals(Integer.MAX_VALUE - 2, set.getCount("X"));
        assertEquals(Integer.MAX_VALUE, set.size());
        // size() only drops below Integer.MAX_VALUE once the true size does
        set.remove("Y", Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE - 2, set.size());
    }

    @Test
    void testConstructorFromIterable() {
        final Iterable<String> iterable = () -> Arrays.asList("a", "b", "a").iterator();
        final MultiSet<String> multiset = new HashMultiSet<>(iterable);
        assertEquals(3, multiset.size());
        assertEquals(2, multiset.getCount("a"));
        assertEquals(1, multiset.getCount("b"));
    }

    @Test
    void testDeserializeRejectsNonPositiveCount() throws Exception {
        final int marker = 0x11223344;
        final HashMultiSet<String> set = new HashMultiSet<>();
        set.add("Y", marker);
        final byte[] byteArray = serialize(set);
        for (final int count : new int[] {0, -7}) {
            final byte[] bytes = byteArray.clone();
            replaceInt(bytes, marker, count);
            assertThrows(InvalidObjectException.class, () -> deserialize(bytes));
        }
    }

//    void testCreate() throws Exception {
//        MultiSet<T> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/HashMultiSet.emptyCollection.version4.1.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/HashMultiSet.fullCollection.version4.1.obj");
//    }

}
