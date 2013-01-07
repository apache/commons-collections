package org.apache.commons.collections.trie;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.Trie;
import org.apache.commons.collections.Unmodifiable;

/**
 * An unmodifiable {@link Trie}.
 * 
 * @since 4.0
 * @version $Id$
 */
public class UnmodifiableTrie<K, V> implements Trie<K, V>, Serializable, Unmodifiable {
    
    private static final long serialVersionUID = -7156426030315945159L;
    
    private final Trie<K, V> delegate;
    
    /**
     * Factory method to create a unmodifiable trie.
     * 
     * @param <K>  the key type
     * @param <V>  the value type
     * @param trie  the trie to decorate, must not be null
     * @return a new unmodifiable trie
     * @throws IllegalArgumentException if trie is null
     */
    public static <K, V> UnmodifiableTrie<K, V> unmodifiableTrie(Trie<K, V> trie) {
        return new UnmodifiableTrie<K, V>(trie);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param trie  the trie to decorate, must not be null
     * @throws IllegalArgumentException if trie is null
     */
    public UnmodifiableTrie(Trie<K, V> trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Trie must not be null");
        }
        this.delegate = trie;
    }
    
    public Entry<K, V> select(K key, final Cursor<? super K, ? super V> cursor) {
        Cursor<K, V> c = new Cursor<K, V>() {
            public Decision select(Map.Entry<? extends K, ? extends V> entry) {
                Decision decision = cursor.select(entry);
                switch (decision) {
                    case REMOVE:
                    case REMOVE_AND_EXIT:
                        throw new UnsupportedOperationException();
                    default:
                        // other decisions are fine
                        break;
                }
                
                return decision;
            }
        };
        
        return delegate.select(key, c);
    }

    public Entry<K, V> select(K key) {
        return delegate.select(key);
    }

    public K selectKey(K key) {
        return delegate.selectKey(key);
    }

    public V selectValue(K key) {
        return delegate.selectValue(key);
    }

    public Entry<K, V> traverse(final Cursor<? super K, ? super V> cursor) {
        Cursor<K, V> c = new Cursor<K, V>() {
            public Decision select(Map.Entry<? extends K, ? extends V> entry) {
                Decision decision = cursor.select(entry);
                switch (decision) {
                    case REMOVE:
                    case REMOVE_AND_EXIT:
                        throw new UnsupportedOperationException();
                    default:
                        // other decisions are fine
                        break;
                }
                
                return decision;
            }
        };
        
        return delegate.traverse(c);
    }

    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(delegate.entrySet());
    }
    
    public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    public Collection<V> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    public V get(Object key) {
        return delegate.get(key);
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public K firstKey() {
        return delegate.firstKey();
    }

    public SortedMap<K, V> headMap(K toKey) {
        return Collections.unmodifiableSortedMap(delegate.headMap(toKey));
    }

    public K lastKey() {
        return delegate.lastKey();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return Collections.unmodifiableSortedMap(
                delegate.subMap(fromKey, toKey));
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        return Collections.unmodifiableSortedMap(delegate.tailMap(fromKey));
    }
    
    public SortedMap<K, V> getPrefixedBy(K key, int offset, int length) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedBy(key, offset, length));
    }

    public SortedMap<K, V> getPrefixedBy(K key, int length) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedBy(key, length));
    }

    public SortedMap<K, V> getPrefixedBy(K key) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedBy(key));
    }

    public SortedMap<K, V> getPrefixedByBits(K key, int lengthInBits) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedByBits(key, lengthInBits));
    }
    
    public SortedMap<K, V> getPrefixedByBits(K key, int offsetInBits, int lengthInBits) {
        return Collections.unmodifiableSortedMap(delegate.getPrefixedByBits(key, offsetInBits, lengthInBits));
    }

    public Comparator<? super K> comparator() {
        return delegate.comparator();
    }
    
    public int size() {
        return delegate.size();
    }
    
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }
    
    @Override
    public String toString() {
        return delegate.toString();
    }
}
