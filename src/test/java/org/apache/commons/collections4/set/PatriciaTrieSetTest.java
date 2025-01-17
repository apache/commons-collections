package org.apache.commons.collections4.set;

import org.apache.commons.collections4.trie.analyzer.StringKeyAnalyzer;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.SortedSet;

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
}