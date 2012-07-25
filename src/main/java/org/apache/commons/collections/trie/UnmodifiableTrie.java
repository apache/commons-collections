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
     * @throws IllegalArgumentException if set is null
     */
    public UnmodifiableTrie(Trie<K, V> trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.delegate = trie;
    }
    
    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public Entry<K, V> select(K key) {
        return delegate.select(key);
    }

    /**
     * {@inheritDoc}
     */
    public K selectKey(K key) {
        return delegate.selectKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public V selectValue(K key) {
        return delegate.selectValue(key);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(delegate.entrySet());
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public V get(Object key) {
        return delegate.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public K firstKey() {
        return delegate.firstKey();
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> headMap(K toKey) {
        return Collections.unmodifiableSortedMap(delegate.headMap(toKey));
    }

    /**
     * {@inheritDoc}
     */
    public K lastKey() {
        return delegate.lastKey();
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return Collections.unmodifiableSortedMap(
                delegate.subMap(fromKey, toKey));
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> tailMap(K fromKey) {
        return Collections.unmodifiableSortedMap(delegate.tailMap(fromKey));
    }
    
    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedBy(K key, int offset, int length) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedBy(key, offset, length));
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedBy(K key, int length) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedBy(key, length));
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedBy(K key) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedBy(key));
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedByBits(K key, int lengthInBits) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedByBits(key, lengthInBits));
    }
    
    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedByBits(K key, int offsetInBits,
            int lengthInBits) {
        return Collections.unmodifiableSortedMap(
                delegate.getPrefixedByBits(key, offsetInBits, lengthInBits));
    }

    /**
     * {@inheritDoc}
     */
    public Comparator<? super K> comparator() {
        return delegate.comparator();
    }
    
    /**
     * {@inheritDoc}
     */
    public int size() {
        return delegate.size();
    }
    
    /**
     * {@inheritDoc}
     */
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
