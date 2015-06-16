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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.InstantiateFactory;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.set.UnmodifiableSet;

/**
 * Abstract implementation of the {@link MultiValuedMap} interface to simplify
 * the creation of subclass implementations.
 * <p>
 * Subclasses specify a Map implementation to use as the internal storage.
 *
 * @since 4.1
 * @version $Id$
 */
public abstract class AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V>, Serializable {

    /** Serialization Version */
    private static final long serialVersionUID = 20150612L;

    /** The factory for creating value collections. */
    private final Factory<? extends Collection<V>> collectionFactory;

    /** The values view */
    private transient Collection<V> valuesView;

    /** The EntryValues view */
    private transient EntryValues entryValuesView;

    /** The KeyMultiSet view */
    private transient KeysMultiSet keysMultiSetView;

    /** The map used to store the data */
    private final Map<K, Collection<V>> map;

    /**
     * Constructor that wraps (not copies).
     *
     * @param <C> the collection type
     * @param map  the map to wrap, must not be null
     * @param collectionClazz  the collection class
     * @throws NullPointerException if the map is null
     */
    @SuppressWarnings("unchecked")
    protected <C extends Collection<V>> AbstractMultiValuedMap(final Map<K, ? super C> map,
                                                               final Class<C> collectionClazz) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        this.map = (Map<K, Collection<V>>) map;
        this.collectionFactory = new InstantiateFactory<C>(collectionClazz);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param <C> the collection type
     * @param map  the map to wrap, must not be null
     * @param collectionClazz  the collection class
     * @param initialCollectionCapacity  the initial capacity of the collection
     * @throws NullPointerException  if the map is null
     * @throws IllegalArgumentException  if initialCollectionCapacity is negative
     */
    @SuppressWarnings("unchecked")
    protected <C extends Collection<V>> AbstractMultiValuedMap(final Map<K, ? super C> map,
            final Class<C> collectionClazz, final int initialCollectionCapacity) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        if (initialCollectionCapacity < 0) {
            throw new IllegalArgumentException("InitialCapacity must not be negative.");
        }
        this.map = (Map<K, Collection<V>>) map;
        this.collectionFactory = new InstantiateFactory<C>(collectionClazz,
                new Class[] { Integer.TYPE },
                new Object[] { Integer.valueOf(initialCollectionCapacity) });
    }

    /**
     * Gets the map being wrapped.
     *
     * @return the wrapped map
     */
    protected Map<K, Collection<V>> getMap() {
        return map;
    }

    @Override
    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return values().contains(value);
    }

    @Override
    public boolean containsMapping(Object key, Object value) {
        Collection<V> coll = getMap().get(key);
        return coll != null && coll.contains(value);
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return entryValuesView != null ? entryValuesView : (entryValuesView = new EntryValues());
    }

    /**
     * Gets the collection of values associated with the specified key. This
     * would return an empty collection in case the mapping is not present
     *
     * @param key the key to retrieve
     * @return the {@code Collection} of values, will return an empty {@code Collection} for no mapping
     */
    @Override
    public Collection<V> get(final K key) {
        // TODO: wrap collection based on class type - needed for proper equals
        return new WrappedCollection(key);
    }

    /**
     * Removes all values associated with the specified key.
     * <p>
     * A subsequent <code>get(Object)</code> would return an empty collection.
     *
     * @param key  the key to remove values from
     * @return the <code>Collection</code> of values removed, will return an
     *   empty, unmodifiable collection for no mapping found
     */
    @Override
    public Collection<V> remove(Object key) {
        return CollectionUtils.emptyIfNull(getMap().remove(key));
    }

    /**
     * Removes a specific key/value mapping from the multi-valued map.
     * <p>
     * The value is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, an empty collection would be
     * returned from a subsequent {@link #get(Object)}.
     *
     * @param key the key to remove from
     * @param value the value to remove
     * @return true if the mapping was removed, false otherwise
     */
    @Override
    public boolean removeMapping(final Object key, final Object value) {
        final Collection<V> coll = getMap().get(key);
        if (coll == null) {
            return false;
        }
        boolean changed = coll.remove(value);
        if (coll.isEmpty()) {
            getMap().remove(key);
        }
        return changed;
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return getMap().keySet();
    }

    @Override
    public int size() {
        // TODO: cache the total size
        int size = 0;
        for (final Collection<V> col : getMap().values()) {
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
    @Override
    public Collection<V> values() {
        final Collection<V> vs = valuesView;
        return vs != null ? vs : (valuesView = new Values());
    }

    @Override
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
     * @return the value added if the map changed and null if the map did not change
     */
    @Override
    public boolean put(final K key, final V value) {
        Collection<V> coll = getMap().get(key);
        if (coll == null) {
            coll = createCollection();
            if (coll.add(value)) {
                getMap().put(key, coll);
                return true;
            } else {
                return false;
            }
        } else {
            return coll.add(value);
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling {@link #put(Object,Object)
     * put(k, v)} on this map once for each mapping from key {@code k} to value
     * {@code v} in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in
     * progress.
     *
     * @param map mappings to be stored in this map, may not be null
     * @throws NullPointerException if map is null
     */
    @Override
    public boolean putAll(final Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            changed |= put(entry.getKey(), entry.getValue());
        }
        return changed;
    }

    /**
     * Copies all of the mappings from the specified MultiValuedMap to this map.
     * The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object) put(k, v)} on this map once for each mapping
     * from key {@code k} to value {@code v} in the specified map. The
     * behavior of this operation is undefined if the specified map is modified
     * while the operation is in progress.
     *
     * @param map mappings to be stored in this map, may not be null
     * @throws NullPointerException if map is null
     */
    @Override
    public boolean putAll(final MultiValuedMap<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (Map.Entry<? extends K, ? extends V> entry : map.entries()) {
            changed |= put(entry.getKey(), entry.getValue());
        }
        return changed;
    }

    /**
     * Returns a {@link MultiSet} view of the key mapping contained in this map.
     * <p>
     * Returns a MultiSet of keys with its values count as the count of the MultiSet.
     * This multiset is backed by the map, so any changes in the map is reflected here.
     * Any method which modifies this multiset like {@code add}, {@code remove},
     * {@link Iterator#remove()} etc throws {@code UnsupportedOperationException}.
     *
     * @return a bag view of the key mapping contained in this map
     */
    @Override
    public MultiSet<K> keys() {
        return keysMultiSetView != null ? keysMultiSetView
                                        : (keysMultiSetView = new KeysMultiSet());
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return getMap();
    }

    /**
     * Adds Iterable values to the collection associated with the specified key.
     *
     * @param key the key to store against
     * @param values the values to add to the collection at the key, may not be null
     * @return true if this map changed
     * @throws NullPointerException if values is null
     */
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        if (values == null) {
            throw new NullPointerException("Values must not be null.");
        }

        if (values instanceof Collection<?>) {
            Collection<? extends V> valueCollection = (Collection<? extends V>) values;
            return !valueCollection.isEmpty() && get(key).addAll(valueCollection);
        } else {
            Iterator<? extends V> it = values.iterator();
            return it.hasNext() && CollectionUtils.addAll(get(key), it);
        }
    }

    @Override
    public MapIterator<K, V> mapIterator() {
        if (size() == 0) {
            return EmptyMapIterator.emptyMapIterator();
        }
        return new MultiValuedMapIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MultiValuedMap) {
            return asMap().equals(((MultiValuedMap<?, ?>) obj).asMap());
        }
        return false;
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
     * Wrapped collection to handle add and remove on the collection returned by get(object)
     */
    protected class WrappedCollection implements Collection<V> {

        protected final K key;

        public WrappedCollection(final K key) {
            this.key = key;
        }

        protected Collection<V> getMapping() {
            return getMap().get(key);
        }

        @Override
        public boolean add(V value) {
            Collection<V> coll = getMapping();
            if (coll == null) {
                coll = createCollection();
                AbstractMultiValuedMap.this.map.put(key, coll);
            }
            return coll.add(value);
        }

        @Override
        public boolean addAll(Collection<? extends V> other) {
            Collection<V> coll = getMapping();
            if (coll == null) {
                coll = createCollection();
                AbstractMultiValuedMap.this.map.put(key, coll);
            }
            return coll.addAll(other);
        }

        @Override
        public void clear() {
            final Collection<V> coll = getMapping();
            if (coll != null) {
                coll.clear();
                AbstractMultiValuedMap.this.remove(key);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<V> iterator() {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return IteratorUtils.EMPTY_ITERATOR;
            }
            return new ValuesIterator(key);
        }

        @Override
        public int size() {
            final Collection<V> coll = getMapping();
            return coll == null ? 0 : coll.size();
        }

        @Override
        public boolean contains(Object obj) {
            final Collection<V> coll = getMapping();
            return coll == null ? false : coll.contains(obj);
        }

        @Override
        public boolean containsAll(Collection<?> other) {
            final Collection<V> coll = getMapping();
            return coll == null ? false : coll.containsAll(other);
        }

        @Override
        public boolean isEmpty() {
            final Collection<V> coll = getMapping();
            return coll == null ? true : coll.isEmpty();
        }

        @Override
        public boolean remove(Object item) {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return false;
            }

            boolean result = coll.remove(item);
            if (coll.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
            return result;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return false;
            }

            boolean result = coll.removeAll(c);
            if (coll.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
            return result;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return false;
            }

            boolean result = coll.retainAll(c);
            if (coll.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
            return result;
        }

        @Override
        public Object[] toArray() {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return CollectionUtils.EMPTY_COLLECTION.toArray();
            }
            return coll.toArray();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return (T[]) CollectionUtils.EMPTY_COLLECTION.toArray(a);
            }
            return coll.toArray(a);
        }

        @Override
        public String toString() {
            final Collection<V> coll = getMapping();
            if (coll == null) {
                return CollectionUtils.EMPTY_COLLECTION.toString();
            }
            return coll.toString();
        }

    }

    /**
     * Inner class that provides a MultiSet<K> keys view.
     */
    private class KeysMultiSet implements MultiSet<K> {

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return getMap().containsKey(o);
        }

        @Override
        public boolean isEmpty() {
            return getMap().isEmpty();
        }

        @Override
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

        @Override
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

        @Override
        public int getCount(Object object) {
            int count = 0;
            Collection<V> col = AbstractMultiValuedMap.this.getMap().get(object);
            if (col != null) {
                count = col.size();
            }
            return count;
        }

        @Override
        public int setCount(K object, int count) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(K object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int add(K object, int nCopies) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remove(Object object, int nCopies) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<K> uniqueSet() {
            return UnmodifiableSet.unmodifiableSet(keySet());
        }

        @Override
        public Set<MultiSet.Entry<K>> entrySet() {
            // TODO: implement
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return AbstractMultiValuedMap.this.size();
        }

        @Override
        public boolean containsAll(Collection<?> coll) {
            final Iterator<?> e = coll.iterator();
            while (e.hasNext()) {
                if(!contains(e.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        @Override
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

                        @Override
                        public boolean hasNext() {
                            return colIterator.hasNext();
                        }

                        @Override
                        public K next() {
                            colIterator.next();// Increment the iterator
                            // The earlier statement would throw
                            // NoSuchElementException anyway in case it ends
                            return key;
                        }

                        @Override
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

                        @Override
                        public Entry<K, V> transform(final V input) {
                            return new MultiValuedMapEntry(key, input);
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
     * Inner class for MultiValuedMap Entries.
     */
    private class MultiValuedMapEntry extends AbstractMapEntry<K, V> {

        public MultiValuedMapEntry(K key, V value) {
            super(key, value);
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Inner class for MapIterator.
     */
    private class MultiValuedMapIterator implements MapIterator<K, V> {

        private final Iterator<Entry<K, V>> it;

        private Entry<K, V> current = null;

        public MultiValuedMapIterator() {
            this.it = AbstractMultiValuedMap.this.entries().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public K next() {
            current = it.next();
            return current.getKey();
        }

        @Override
        public K getKey() {
            if (current == null) {
                throw new IllegalStateException();
            }
            return current.getKey();
        }

        @Override
        public V getValue() {
            if (current == null) {
                throw new IllegalStateException();
            }
            return current.getValue();
        }

        @Override
        public void remove() {
            it.remove();
        }

        @Override
        public V setValue(V value) {
            if (current == null) {
                throw new IllegalStateException();
            }
            return current.setValue(value);
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
            this.values = getMap().get(key);
            this.iterator = values.iterator();
        }

        @Override
        public void remove() {
            iterator.remove();
            if (values.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public V next() {
            return iterator.next();
        }
    }

}
