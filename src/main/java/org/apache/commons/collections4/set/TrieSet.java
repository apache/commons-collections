package org.apache.commons.collections4.set;

import java.util.SortedSet;

/**
 * Defines the interface for a prefix set, an ordered tree data structure. For more information, see <a href="https://en.wikipedia.org/wiki/Trie">Tries</a>.
 *
 * @param <K> – the type of elements maintained by this set
 */
public interface TrieSet<K> extends SortedSet<K> {

    /**
     * Returns a view of this {@link TrieSet} of all elements that are prefixed by the given key without duplicates
     * <p>
     * In a {@link TrieSet} with fixed size keys, this is essentially a {@link #contains(Object)} operation.
     * </p>
     * <p>
     * For example, if the {@link TrieSet} contains 'Anna', 'Anael', 'Analu', 'Andreas', 'Andrea', 'Andres', and 'Anatole', then a lookup of 'And' would return
     * 'Andreas', 'Andrea', and 'Andres'.
     * </p>
     *
     * @param key the key used in the search
     * @return a {@link SortedSet} view of this {@link TrieSet} with all elements whose key is prefixed by the search key
     */
    SortedSet<K> prefixSet(K key);
}