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
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Spliterator;

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
        Assertions.assertTrue(patriciaTrieSet.contains("Alberto"));
        Assertions.assertTrue(patriciaTrieSet.contains("Albe"));
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
        patriciaTrieSet.add("点评");
        patriciaTrieSet.add("书评");
        assertTrue(patriciaTrieSet.prefixSet("点").contains("点评"));
        assertEquals("点评", patriciaTrieSet.prefixSet("点").first());
        assertFalse(patriciaTrieSet.prefixSet("点").isEmpty());
        assertEquals(1, patriciaTrieSet.prefixSet("点").size());
        assertEquals(1, patriciaTrieSet.prefixSet("点评").size());

        patriciaTrieSet.clear();
        patriciaTrieSet.add("点评");
        patriciaTrieSet.add("点版");
        assertEquals(2, patriciaTrieSet.prefixSet("点").size());
        assertEquals(2, patriciaTrieSet.prefixSet("点").size());
    }

    @Test
    public void setShouldCorrectlyWorkWithMixedCharacters() {
        patriciaTrieSet.add("书评");
        patriciaTrieSet.add("书点");
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Йод");
        patriciaTrieSet.add("Йододефицит");


        SortedSet<String> subset = patriciaTrieSet.prefixSet("Al");
        assertEquals(2, subset.size());
        assertEquals("Al", subset.first());
        assertEquals("Alberto", subset.last());

        subset = patriciaTrieSet.prefixSet("Йод");
        assertEquals(2, subset.size());
        assertEquals("Йод", subset.first());
        assertEquals("Йододефицит", subset.last());

        subset = patriciaTrieSet.prefixSet("书");

        assertEquals(2, subset.size());
        assertEquals("书点", subset.first());
        assertEquals("书评", subset.last());
    }

    @Test
    public void containsShouldReturnFalse() {
        patriciaTrieSet.add("Al");
        patriciaTrieSet.add("Alberto");

        assertFalse(patriciaTrieSet.contains("w"));
        assertFalse(patriciaTrieSet.contains(new Object()));
    }

    @Test
    public void rangePatriciaTrieSortedSetIteratorShouldReturnCorrectOrder() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wirz");

        Assertions.assertNotNull(subSet);

        String[] actual = new String[3];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Al", actual[0]);
        Assertions.assertEquals("Albert", actual[1]);
        Assertions.assertEquals("Alberto", actual[2]);
    }

    @Test
    public void rangePatriciaTrieSortedSetSplitIteratorShouldReturnIteratorOverMap() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wirz");

        Spliterator<String> spliterator = subSet.spliterator();
        Assertions.assertEquals(Spliterator.DISTINCT | Spliterator.SIZED | Spliterator.SUBSIZED, spliterator.characteristics());
        Assertions.assertEquals(3, spliterator.getExactSizeIfKnown());
    }

    @Test
    public void rangePatriciaTrieSortedSetSizeShouldReturnCorrectSize() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wirz");
        Assertions.assertEquals(3, subSet.size());
    }

    @Test
    public void rangePatriciaTrieSortedSetContainsShouldReturnTrue() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wirz");
        Assertions.assertTrue(subSet.contains("Albert"));
    }

    @Test
    public void rangePatriciaTrieSortedSetContainsShouldReturnFalse() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wirz");
        Assertions.assertFalse(subSet.contains("Wirz"));
        Assertions.assertFalse(subSet.contains(new Object()));
    }

    @Test
    public void rangePatriciaTrieSortedSetComparatorShouldCorrectValue() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wirz");
        Assertions.assertEquals(StringKeyAnalyzer.INSTANCE, subSet.comparator());
    }

    @Test
    public void rangePatriciaTrieSortedSetSubSetShouldReturnCorrectSubSet() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.subSet("Albert", "Wirz");

        Assertions.assertNotNull(subSet);

        String[] actual = new String[3];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Albert", actual[0]);
        Assertions.assertEquals("Alberto", actual[1]);
        Assertions.assertEquals("W", actual[2]);
    }

    @Test
    public void rangePatriciaTrieSortedSetSubSetShouldReturnCorrectSubSetTwice() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.subSet("Albert", "Wirz");
        subSet = subSet.subSet("Albert", "W");
        Assertions.assertNotNull(subSet);

        Assertions.assertNotNull(subSet);

        String[] actual = new String[2];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Albert", actual[0]);
        Assertions.assertEquals("Alberto", actual[1]);
    }

    @Test
    public void rangePatriciaTrieSortedSetHeadSetShouldReturnCorrectHeadSet() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wl");
        Assertions.assertNotNull(subSet);

        String[] actual = new String[5];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Al", actual[0]);
        Assertions.assertEquals("Albert", actual[1]);
        Assertions.assertEquals("Alberto", actual[2]);
        Assertions.assertEquals("W", actual[3]);
        Assertions.assertEquals("Wirz", actual[4]);
    }

    @Test
    public void rangePatriciaTrieSortedSetHeadSetShouldReturnCorrectHeadSetTwice() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Wl");
        Assertions.assertNotNull(subSet);
        subSet = subSet.headSet("Alberto");
        Assertions.assertNotNull(subSet);

        String[] actual = new String[2];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Al", actual[0]);
        Assertions.assertEquals("Albert", actual[1]);
    }

    @Test
    public void rangePatriciaTrieSortedSetTailSetShouldReturnCorrectTailSet() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.tailSet("Alberto");
        Assertions.assertNotNull(subSet);

        String[] actual = new String[4];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Alberto", actual[0]);
        Assertions.assertEquals("W", actual[1]);
        Assertions.assertEquals("Wirz", actual[2]);
        Assertions.assertEquals("Wl", actual[3]);
    }

    @Test
    public void rangePatriciaTrieSortedSetTailSetShouldReturnCorrectTailSetTwice() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.tailSet("Alberto");
        Assertions.assertNotNull(subSet);
        subSet = subSet.tailSet("Wirz");

        String[] actual = new String[2];
        int idx = 0;

        for (String s : subSet) {
            actual[idx] = s;
            idx++;
        }

        Assertions.assertEquals("Wirz", actual[0]);
        Assertions.assertEquals("Wl", actual[1]);
    }

    @Test
    public void rangePatriciaTrieSortedSetFirstShouldReturnCorrectResult() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.tailSet("Alberto");

        Assertions.assertEquals("Alberto", subSet.first());

        subSet = patriciaTrieSet.tailSet("Wir");

        Assertions.assertEquals("Wirz", subSet.first());
    }

    @Test
    public void rangePatriciaTrieSortedSetLastShouldReturnCorrectResult() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Alberto");
        Assertions.assertEquals("Albert", subSet.last());

        subSet = patriciaTrieSet.headSet("Al");
        Assertions.assertThrows(NoSuchElementException.class, subSet::last);
    }

    @Test
    public void rangePatriciaTrieSortedSetAddShouldReturnCorrectResult() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Alberto");
        subSet.add("Albert");

        Assertions.assertEquals(2, subSet.size());
        Assertions.assertEquals(6, patriciaTrieSet.size());

        subSet.add("Alber");

        Assertions.assertEquals(3, subSet.size());
        Assertions.assertEquals(7, patriciaTrieSet.size());
    }

    @Test
    public void rangePatriciaTrieSortedSetRemoveShouldReturnCorrectResult() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Alberto");
        subSet.remove("Al");

        Assertions.assertEquals(1, subSet.size());
        Assertions.assertEquals(5, patriciaTrieSet.size());

        subSet.remove("A");

        Assertions.assertEquals(1, subSet.size());
        Assertions.assertEquals(5, patriciaTrieSet.size());

    }

    @Test
    public void rangePatriciaTrieSortedSetIsEmptyShouldReturnCorrectResult() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Al");
        assertTrue(subSet.isEmpty());

        subSet = patriciaTrieSet.headSet("Albert");
        assertFalse(subSet.isEmpty());
    }

    @Test
    public void rangePatriciaTrieSortedSetClearShouldClearElements() {
        patriciaTrieSet.add("Alberto");
        patriciaTrieSet.add("Albert");
        patriciaTrieSet.add("Wirz");
        patriciaTrieSet.add("W");
        patriciaTrieSet.add("Wl");
        patriciaTrieSet.add("Al");

        SortedSet<String> subSet = patriciaTrieSet.headSet("Al");
        subSet.clear();

        Assertions.assertEquals(0, subSet.size());
        Assertions.assertEquals(6, patriciaTrieSet.size());

        subSet = patriciaTrieSet.headSet("Alberto");
        subSet.clear();

        Assertions.assertEquals(0, subSet.size());
        Assertions.assertEquals(4, patriciaTrieSet.size());
    }
}
