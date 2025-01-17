package org.apache.commons.collections4.set;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

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
 * @since 5.0
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
        if (o instanceof String)
            return trie.containsKey(o);

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