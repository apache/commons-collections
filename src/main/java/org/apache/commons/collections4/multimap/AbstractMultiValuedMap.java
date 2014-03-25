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
package org.apache.commons.collections4.multimap;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.functors.InstantiateFactory;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.TransformIterator;

/**
 * Abstract implementation of the {@link MultiValuedMap} interface to simplify
 * the creation of subclass implementations.
 * <p>
 * Subclasses specify a Map implementation to use as the internal storage.
 *
 * @since 4.1
 * @version $Id$
 */
public class AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V>, Serializable {

    /** Serialization Version */
    private static final long serialVersionUID = 7994988366330224277L;

    /** The factory for creating value collections. */
    private final Factory<? extends Collection<V>> collectionFactory;

    /** The values view */
    private transient Collection<V> valuesView;

    /** The EntryValues view */
    private transient EntryValues entryValuesView;

    /** The KeyBag view */
    private transient KeysBag keysBagView;

    /** The map used to store the data */
    private final Map<K, Collection<V>> map;

    /**
     * Constructor that wraps (not copies).
     *
     * @param <C>  the collection type
     * @param map  the map to wrap, must not be null
     * @param collectionClazz  the collection class
     * @throws IllegalArgumentException if the map is null
     */
    @SuppressWarnings("unchecked")
    protected <C extends Collection<V>> AbstractMultiValuedMap(final Map<K, ? super C> map,
                                                               final Class<C> collectionClazz) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        this.map = (Map<K, Collection<V>>) map;
        this.collectionFactory = new InstantiateFactory<C>(collectionClazz);
    }

    /**
     * Gets the map being wrapped.
     *
     * @return the wrapped map
     */
    protected Map<K, Collection<V>> getMap() {
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(final Object value) {
        final Set<Map.Entry<K, Collection<V>>> pairs = getMap().entrySet();
        if (pairs != null) {
            for (final Map.Entry<K, Collection<V>> entry : pairs) {
                if (entry.getValue().contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsMapping(Object key, Object value) {
        final Collection<V> col = get(key);
        if (col == null) {
            return false;
        }
        return col.contains(value);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Entry<K, V>> entries() {
        return entryValuesView != null ? entryValuesView : (entryValuesView = new EntryValues());
    }

    /**
     * Gets the collection of values associated with the specified key.
     *
     * @param key the key to retrieve
     * @return the <code>Collection</code> of values, will return
     *         <code>null</code> for no mapping
     * @throws ClassCastException if the key is of an invalid type
     */
    public Collection<V> get(Object key) {
        return getMap().get(key);
    }

    /**
     * Removes all values associated with the specified key.
     * <p>
     * A subsequent <code>get(Object)</code> would return null collection.
     *
     * @param key the key to remove values from
     * @return the <code>Collection</code> of values removed, will return
     *         <code>null</code> for no mapping found.
     * @throws ClassCastException if the key is of an invalid type
     */
    public Collection<V> remove(Object key) {
        return getMap().remove(key);
    }

    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, <code>null</code> would be
     * returned from a subsequent <code>get(Object)</code>.
     *
     * @param key the key to remove from
     * @param item the item to remove
     * @return {@code true} if the mapping was removed, {@code false} otherwise
     */
    public boolean removeMapping(K key, V item) {
        boolean result = false;
        final Collection<V> col = get(key);
        if (col == null) {
            return false;
        }
        result = col.remove(item);
        if (!result) {
            return false;
        }
        if (col.isEmpty()) {
            remove(key);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return getMap().keySet();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        int size = 0;
        for (Collection<V> col : getMap().values()) {
            size += col.size();
        }
        return size;
    }

    /**
     * Gets a collection containing all the values in the map.
     * <p>
     * Returns a collection containing all the values from all keys.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
        final Collection<V> vs = valuesView;
        return vs != null ? vs : (valuesView = new Values());
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        getMap().clear();
    }

    /**
     * Adds the value to the collection associated with the specified key.
     * <p>
     * Unlike a normal <code>Map</code> the previous value is not replaced.
     * Instead the new value is added to the collection stored against the key.
     *
     * @param key the key to store against
     * @param value the value to add to the collection at the key
     * @return the value added if the map changed and null if the map did not
     *         change
     */
    public V put(K key, V value) {
        boolean result = false;
        Collection<V> coll = get(key);
        if (coll == null) {
            coll = createCollection();
            coll.add(value);
            if (coll.size() > 0) {
                // only add if non-zero size to maintain class state
                getMap().put(key, coll);
                result = true; // map definitely changed
            }
        } else {
            result = coll.add(value);
        }
        return result ? value : null;
    }

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling {@link #put(Object,Object)
     * put(k, v)} on this map once for each mapping from key <tt>k</tt> to value
     * <tt>v</tt> in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in
     * progress.
     *
     * @param map mappings to be stored in this map
     */
    public void putAll(final Map<? extends K, ? extends V> map) {
        if (map != null) {
            for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                put((K) entry.getKey(), (V) entry.getValue());
            }
        }
    }

    /**
     * Copies all of the mappings from the specified MultiValuedMap to this map.
     * The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object) put(k, v)} on this map once for each mapping
     * from key <tt>k</tt> to value <tt>v</tt> in the specified map. The
     * behavior of this operation is undefined if the specified map is modified
     * while the operation is in progress.
     *
     * @param map mappings to be stored in this map
     */
    @SuppressWarnings("unchecked")
    public void putAll(MultiValuedMap<? extends K, ? extends V> map) {
        if (map != null) {
            for (final K key : map.keySet()) {
                putAll(key, (Collection<V>) map.get(key));
            }
        }
    }

    /**
     * Returns a {@link Bag} view of the key mapping contained in this map.
     * <p>
     * Returns a Bag of keys with its values count as the count of the Bag. This
     * bag is backed by the map, so any changes in the map is reflected here.
     * Any method which modifies this bag like <tt>add</tt>, <tt>remove</tt>,
     * <tt>Iterator.remove</tt> etc throws
     * <code>UnsupportedOperationException</code>
     *
     * @return a bag view of the key mapping contained in this map
     */
    public Bag<K> keys() {
        return keysBagView != null ? keysBagView : (keysBagView = new KeysBag());
    }

    /**
     * Adds Iterable values to the collection associated with the specified key.
     *
     * @param key the key to store against
     * @param values the values to add to the collection at the key, null
     *        ignored
     * @return true if this map changed
     */
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        if (values == null || values.iterator() == null || !values.iterator().hasNext()) {
            return false;
        }
        Iterator<? extends V> it = values.iterator();
        boolean result = false;
        Collection<V> coll = get(key);
        if (coll == null) {
            coll = createCollection(); // might produce a non-empty collection
            while (it.hasNext()) {
                coll.add(it.next());
            }
            if (coll.size() > 0) {
                // only add if non-zero size to maintain class state
                getMap().put(key, coll);
                result = true; // map definitely changed
            }
        } else {
            while (it.hasNext()) {
                boolean tmpResult = coll.add(it.next());
                if (!result && tmpResult) {
                    // If any one of the values have been added, the map has
                    // changed
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Gets an iterator for the collection mapped to the specified key.
     *
     * @param key the key to get an iterator for
     * @return the iterator of the collection at the key, empty iterator if key
     *         not in map
     */
    public Iterator<V> iterator(final Object key) {
        if (!containsKey(key)) {
            return EmptyIterator.<V> emptyIterator();
        }
        return new ValuesIterator(key);
    }

    /**
     * Gets the size of the collection mapped to the specified key.
     *
     * @param key the key to get size for
     * @return the size of the collection at the key, zero if key not in map
     */
    public int size(final Object key) {
        final Collection<V> coll = get(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof MultiValuedMap == false) {
            return false;
        }
        MultiValuedMap<?, ?> other = (MultiValuedMap<?, ?>) obj;
        if (other.size() != size()) {
            return false;
        }
        Iterator it = keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Collection<?> col = get(key);
            Collection<?> otherCol = other.get(key);
            if (otherCol == null) {
                return false;
            }
            if (col.size() != otherCol.size()) {
                return false;
            }
            for (Object value : col) {
                if (!otherCol.contains(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getMap().hashCode();
    }

    @Override
    public String toString() {
        return getMap().toString();
    }

    // -----------------------------------------------------------------------

    protected Collection<V> createCollection() {
        return collectionFactory.create();
    }

    // -----------------------------------------------------------------------

    /**
     * Inner class that provides a Bag<K> keys view
     */
    private class KeysBag implements Bag<K> {

        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object o) {
            return getMap().containsKey(o);
        }

        public boolean isEmpty() {
            return getMap().isEmpty();
        }

        public Object[] toArray() {
            final Object[] result = new Object[size()];
            int i = 0;
            final Iterator<K> it = getMap().keySet().iterator();
            while (it.hasNext()) {
                final K current = it.next();
                for (int index = getCount(current); index > 0; index--) {
                    result[i++] = current;
                }
            }
            return result;
        }

        public <T> T[] toArray(T[] array) {
            final int size = size();
            if (array.length < size) {
                @SuppressWarnings("unchecked")
                // safe as both are of type T
                final T[] unchecked = (T[]) Array.newInstance(array.getClass().getComponentType(), size);
                array = unchecked;
            }

            int i = 0;
            final Iterator<K> it = getMap().keySet().iterator();
            while (it.hasNext()) {
                final K current = it.next();
                for (int index = getCount(current); index > 0; index--) {
                    // unsafe, will throw ArrayStoreException if types are not
                    // compatible, see javadoc
                    @SuppressWarnings("unchecked")
                    final T unchecked = (T) current;
                    array[i++] = unchecked;
                }
            }
            while (i < array.length) {
                array[i++] = null;
            }
            return array;
        }

        public int getCount(Object object) {
            int count = 0;
            Collection<V> col = AbstractMultiValuedMap.this.getMap().get(object);
            if (col != null) {
                count = col.size();
            }
            return count;
        }

        public boolean add(K object) {
            throw new UnsupportedOperationException();
        }

        public boolean add(K object, int nCopies) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object object, int nCopies) {
            throw new UnsupportedOperationException();
        }

        public Set<K> uniqueSet() {
            return keySet();
        }

        public int size() {
            return AbstractMultiValuedMap.this.size();
        }

        public boolean containsAll(Collection<?> coll) {
            if (coll instanceof Bag) {
                return containsAll((Bag<?>) coll);
            }
            return containsAll(new HashBag<Object>(coll));
        }

        private boolean containsAll(final Bag<?> other) {
            final Iterator<?> it = other.uniqueSet().iterator();
            while (it.hasNext()) {
                final Object current = it.next();
                if (getCount(current) < other.getCount(current)) {
                    return false;
                }
            }
            return true;
        }

        public boolean removeAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        public Iterator<K> iterator() {
            return new LazyIteratorChain<K>() {

                final Iterator<K> keyIterator = getMap().keySet().iterator();

                @Override
                protected Iterator<? extends K> nextIterator(int count) {
                    if (!keyIterator.hasNext()) {
                        return null;
                    }
                    final K key = keyIterator.next();
                    final Iterator<V> colIterator = getMap().get(key).iterator();
                    Iterator<K> nextIt = new Iterator<K>() {

                        public boolean hasNext() {
                            return colIterator.hasNext();
                        }

                        public K next() {
                            colIterator.next();// Increment the iterator
                            // The earlier statement would throw
                            // NoSuchElementException anyway in case it ends
                            return key;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                    return nextIt;
                }
            };
        }

    }

    /**
     * Inner class that provides the Entry<K, V> view
     */
    private class EntryValues extends AbstractCollection<Entry<K, V>> {

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new LazyIteratorChain<Entry<K, V>>() {

                final Collection<K> keysCol = new ArrayList<K>(getMap().keySet());
                final Iterator<K> keyIterator = keysCol.iterator();

                @Override
                protected Iterator<? extends Entry<K, V>> nextIterator(int count) {
                    if (!keyIterator.hasNext()) {
                        return null;
                    }
                    final K key = keyIterator.next();
                    final Transformer<V, Entry<K, V>> entryTransformer = new Transformer<V, Entry<K, V>>() {

                        public Entry<K, V> transform(final V input) {
                            return new Entry<K, V>() {

                                public K getKey() {
                                    return key;
                                }

                                public V getValue() {
                                    return input;
                                }

                                public V setValue(V value) {
                                    throw new UnsupportedOperationException();
                                }
                            };
                        }
                    };
                    return new TransformIterator<V, Entry<K, V>>(new ValuesIterator(key), entryTransformer);
                }
            };
        }

        @Override
        public int size() {
            return AbstractMultiValuedMap.this.size();
        }

    }

    /**
     * Inner class that provides the values view.
     */
    private class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            final IteratorChain<V> chain = new IteratorChain<V>();
            for (final K k : keySet()) {
                chain.addIterator(new ValuesIterator(k));
            }
            return chain;
        }

        @Override
        public int size() {
            return AbstractMultiValuedMap.this.size();
        }

        @Override
        public void clear() {
            AbstractMultiValuedMap.this.clear();
        }
    }

    /**
     * Inner class that provides the values iterator.
     */
    private class ValuesIterator implements Iterator<V> {
        private final Object key;
        private final Collection<V> values;
        private final Iterator<V> iterator;

        public ValuesIterator(final Object key) {
            this.key = key;
            this.values = get(key);
            this.iterator = values.iterator();
        }

        public void remove() {
            iterator.remove();
            if (values.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public V next() {
            return iterator.next();
        }
    }

}
