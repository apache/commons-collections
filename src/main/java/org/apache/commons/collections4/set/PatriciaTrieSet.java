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

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * Add  {@code Set} to work with PatriciaTree with unique key ignore corresponding values
 * <p>
 * This set exists to provide interface to work with Set + PatriciaTree
 * </p>
 * <p>
 * One usage would be to ensure that no null entries are added to the set.
 * </p>
 * <pre>PatriciaTrieSet = new PatriciaTrieSet();</pre>
 * <p>
 * This class is Serializable and Cloneable.
 * </p>
 *
 */
public class PatriciaTrieSet extends AbstractSet<String> implements TrieSet<String>, Serializable {

    private static final long serialVersionUID = -2365733183789787136L;

    // Stub for all values in PatriciaTrie
    static final Object PRESENT = new Object();
    transient PatriciaTrie<Object> trie;

    public PatriciaTrieSet() {
        trie = new PatriciaTrie<>();
    }

    @Override
    public Iterator<String> iterator() {
        return trie.keySet().iterator();
    }

    @Override
    public int size() {
        return trie.size();
    }

    @Override
    public boolean isEmpty() {
        return trie.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            return trie.containsKey(o);
        }

        return false;
    }

    @Override
    public boolean add(String e) {
        return trie.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return trie.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        trie.clear();
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        out.writeInt(trie.size());

        for (String entry : this.trie.keySet()) {
            out.writeObject(entry);
        }
    }

    /**
     * Deserializes the set in using a custom routine.
     *
     * @param s the input stream
     * @throws IOException            if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    private void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.readFields();
        int size = s.readInt();
        this.trie = new PatriciaTrie<>();

        for (int i = 0; i < size; i++) {
            this.add((String) s.readObject());
        }
    }

    @Override
    public SortedSet<String> prefixSet(String key) {
        return new TreeSet<>(trie.prefixMap(key).keySet());
    }

    @Override
    public Comparator<? super String> comparator() {
        return trie.comparator();
    }

    @Override
    public SortedSet<String> subSet(String fromElement, String toElement) {
        return new TreeSet<>(trie.subMap(fromElement, toElement).keySet());
    }

    @Override
    public SortedSet<String> headSet(String toElement) {
        return new TreeSet<>(trie.headMap(toElement).keySet());
    }

    @Override
    public SortedSet<String> tailSet(String fromElement) {
        return new TreeSet<>(trie.tailMap(fromElement).keySet());
    }

    @Override
    public String first() {
        return trie.firstKey();
    }

    @Override
    public String last() {
        return trie.lastKey();
    }

}
