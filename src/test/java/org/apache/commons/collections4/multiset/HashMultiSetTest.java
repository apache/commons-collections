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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InvalidObjectException;
import java.util.Iterator;

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
    void testViewIteratorRemoveKeepsSizeConsistent() {
        final HashMultiSet<String> multiset = new HashMultiSet<>();
        multiset.add("a", 3);
        final Iterator<String> unique = multiset.uniqueSet().iterator();
        unique.next();
        unique.remove();
        assertEquals(0, multiset.size());
        assertTrue(multiset.isEmpty());
        assertEquals(0, multiset.toArray().length);

        multiset.add("b", 4);
        final Iterator<MultiSet.Entry<String>> entries = multiset.entrySet().iterator();
        entries.next();
        entries.remove();
        assertEquals(0, multiset.size());
        assertTrue(multiset.isEmpty());
        assertEquals(0, multiset.toArray().length);
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
