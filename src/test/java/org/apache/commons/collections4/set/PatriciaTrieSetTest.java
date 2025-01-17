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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.SortedSet;

import org.apache.commons.collections4.trie.analyzer.StringKeyAnalyzer;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PatriciaTrieSetTest {

    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();

    @BeforeEach
    public void preInitTest() {
        patriciaTrieSet.clear();
    }

    @Test
    public void setShouldContainUniqueElements() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albe");
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albe");

        Assertions.assertEquals(2, patriciaTrieSet.size());
    }

    @Test
    public void setShouldNotContainsNullKey() {
        Assertions.assertThrows(NullPointerException.class, () -> patriciaTrieSet.add(null));
    }

    @Test
    public void clearShouldClearAllElements() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albe");

        patriciaTrieSet.clear();

        Assertions.assertEquals(0, patriciaTrieSet.size());
        Assertions.assertTrue(patriciaTrieSet.isEmpty());
    }

    @Test
    public void isEmptyShouldReturnFalseIfElementsExist() {
        Assertions.assertTrue(patriciaTrieSet.isEmpty());
        patriciaTrieSet.add("Alberto");

        Assertions.assertFalse(patriciaTrieSet.isEmpty());
    }

    @Test
    public void iteratorShouldContainsAllElements() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albe");

        HashSet<String> set = new HashSet<>(2);
        set.add("Alberto");
        set.add("Albe");

        for (String value : patriciaTrieSet) {
            Assertions.assertTrue(set.remove(value), "Value " + value + " not exists!");
        }
    }

    @Test
    public void containsShouldReturnCorrectValue() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albe");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("A");

        Assertions.assertTrue(patriciaTrieSet.contains("A"));
        Assertions.assertTrue(patriciaTrieSet.contains("Al"));
        Assertions.assertTrue(patriciaTrieSet.contains("Albe"));
        Assertions.assertTrue(patriciaTrieSet.contains("Alberto"));
        Assertions.assertFalse(patriciaTrieSet.contains("Albert"));
    }

    @Test
    public void addShouldReturnFalseIfValueExists() {
        patriciaTrieSet.add("Alberto");

        Assertions.assertFalse(patriciaTrieSet.add("Alberto"));
    }

    @Test
    public void addShouldReturnTrueIfValueNotExists() {
        Assertions.assertTrue(patriciaTrieSet.add("Alberto"));
    }

    @Test
    public void removeShouldReturnTrueIfValueExists() {
        patriciaTrieSet.add("Alberto");

        Assertions.assertTrue(patriciaTrieSet.remove("Alberto"));
    }

    @Test
    public void removeShouldReturnFalseIfValueNotExist() {
        Assertions.assertFalse(patriciaTrieSet.remove("Alberto"));
    }

    @Test
    public void serializeShouldSerializeAndDeserializedCorrectly() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albe");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("A");

        byte[] serializedBytes = SerializationUtils.serialize(patriciaTrieSet);
        PatriciaTrieSet expected = SerializationUtils.deserialize(serializedBytes);

        Assertions.assertEquals(4, expected.size());
        Assertions.assertTrue(patriciaTrieSet.contains("A"));
        Assertions.assertTrue(patriciaTrieSet.contains("Al"));
        Assertions.assertTrue(patriciaTrieSet.contains("Albe"));
        Assertions.assertTrue(patriciaTrieSet.contains("Alberto"));
    }

    @Test
    public void serializeShouldSerializeAndDeserializedCorrectlyEmptyObject() {
        byte[] serializedBytes = SerializationUtils.serialize(patriciaTrieSet);
        PatriciaTrieSet expected = SerializationUtils.deserialize(serializedBytes);

        Assertions.assertEquals(0, expected.size());
    }

    @Test
    public void prefixSetShouldReturnCorrectObject() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Bbe");
        patriciaTrieSet.add("Bb");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("A");

        SortedSet<String> set = patriciaTrieSet.prefixSet("Bb");
        Assertions.assertEquals(2, set.size());

        Assertions.assertEquals("Bb", set.first());
        Assertions.assertEquals("Bbe", set.last());
    }

    @Test
    public void subSetShouldReturnCorrectObject() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Bbe");
        patriciaTrieSet.add("Bb");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("A");

        SortedSet<String> set = patriciaTrieSet.subSet("Al", "Bbe");
        Assertions.assertEquals(3, set.size());

        String[] array = new String[3];
        set.toArray(array);

        Assertions.assertEquals("Al", array[0]);
        Assertions.assertEquals("Alberto", array[1]);
        Assertions.assertEquals("Bb", array[2]);
    }

    @Test
    public void headSetShouldReturnCorrectObject() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Bbe");
        patriciaTrieSet.add("Bb");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("A");

        SortedSet<String> set = patriciaTrieSet.headSet("Bbe");
        Assertions.assertEquals(4, set.size());

        String[] array = new String[4];
        set.toArray(array);

        Assertions.assertEquals("A", array[0]);
        Assertions.assertEquals("Al", array[1]);
        Assertions.assertEquals("Alberto", array[2]);
        Assertions.assertEquals("Bb", array[3]);
    }

    @Test
    public void tailSetShouldReturnCorrectObject() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Bbe");
        patriciaTrieSet.add("Bb");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("Z");

        SortedSet<String> set = patriciaTrieSet.tailSet("Bbe");
        Assertions.assertEquals(2, set.size());

        String[] array = new String[2];
        set.toArray(array);

        Assertions.assertEquals("Bbe", array[0]);
        Assertions.assertEquals("Z", array[1]);
    }

    @Test
    public void firstKeyShouldReturnCorrectObject() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Bbe");
        patriciaTrieSet.add("Bb");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("Z");

        Assertions.assertEquals("Al", patriciaTrieSet.first());
    }

    @Test
    public void lastKeyShouldReturnCorrectObject() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Bbe");
        patriciaTrieSet.add("Bb");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("Zz");

        Assertions.assertEquals("Zz", patriciaTrieSet.last());
    }

    @Test
    public void comparatorShouReturnSetComparator() {
        Assertions.assertEquals(StringKeyAnalyzer.INSTANCE, patriciaTrieSet.comparator());
    }

    @Test
    public void prefixMapShouldCorrectlyWorkWihChineseCharacter() {
        // COLLECTIONS-525
        final PatriciaTrieSet set = new PatriciaTrieSet();
        set.add("点评");
        set.add("书评");
        assertTrue(set.prefixSet("点").contains("点评"));
        assertEquals("点评", set.prefixSet("点").first());
        assertFalse(set.prefixSet("点").isEmpty());
        assertEquals(1, set.prefixSet("点").size());
        assertEquals(1, set.prefixSet("点评").size());

        set.clear();
        set.add("点评");
        set.add("点版");
        assertEquals(2, set.prefixSet("点").size());
        assertEquals(2, set.prefixSet("点").size());
    }

    @Test
    public void setShouldCorrectlyWorkWithMixedCharacters() {
        final PatriciaTrieSet set = new PatriciaTrieSet();
        set.add("书评");
        set.add("书点");
        set.add("Al");
        set.add("Alberto");
        set.add("Йод");
        set.add("Йододефицит");


        SortedSet<String> subset = set.prefixSet("Al");
        assertEquals(2, subset.size());
        assertEquals("Al", subset.first());
        assertEquals("Alberto", subset.last());

        subset = set.prefixSet("Йод");
        assertEquals(2, subset.size());
        assertEquals("Йод", subset.first());
        assertEquals("Йододефицит", subset.last());

        subset = set.prefixSet("书");

        assertEquals(2, subset.size());
        assertEquals("书点", subset.first());
        assertEquals("书评", subset.last());
    }
}
