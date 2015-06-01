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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.bag.HashBag;
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
     * @param map the map to wrap, must not be null
     * @param collectionClazz the collection class
     * @param initialCollectionCapacity the initial capacity of the collection
     * @throws NullPointerException if the map is null
     * @throws IllegalArgumentException if initialCollectionCapacity is negative
     */
    @SuppressWarnings("unchecked")
    protected <C extends Collection<V>> AbstractMultiValuedMap(final Map<K, ? super C> map,
            final Class<C> collectionClazz, final int initialCollectionCapacity) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        if (initialCollectionCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCollectionCapacity);
        }
        this.map = (Map<K, Collection<V>>) map;
        this.collectionFactory = new InstantiateFactory<C>(collectionClazz, new Class[] { Integer.TYPE },
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
        final Collection<V> col = getMap().get(key);
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
     * Gets the collection of values associated with the specified key. This
     * would return an empty collection in case the mapping is not present
     *
     * @param key the key to retrieve
     * @return the <code>Collection</code> of values, will return an empty
     *         <code>Collection</code> for no mapping
     * @throws ClassCastException if the key is of an invalid type
     */
    public Collection<V> get(Object key) {
        return new WrappedCollection(key);
    }

    /**
     * Removes all values associated with the specified key.
     * <p>
     * A subsequent <code>get(Object)</code> would return an empty collection.
     *
     * @param key the key to remove values from
     * @return the <code>Collection</code> of values removed, will return an
     *         empty, unmodifiable collection for no mapping found.
     * @throws ClassCastException if the key is of an invalid type
     */
    public Collection<V> remove(Object key) {
        return CollectionUtils.emptyIfNull(getMap().remove(key));
    }

    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, an empty collection would be
     * returned from a subsequent <code>get(Object)</code>.
     *
     * @param key the key to remove from
     * @param item the item to remove
     * @return {@code true} if the mapping was removed, {@code false} otherwise
     */
    public boolean removeMapping(K key, V item) {
        boolean result = false;
        final Collection<V> col = getMap().get(key);
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
     * @return the value added if the map changed and null if the map did not change
     */
    public boolean put(K key, V value) {
        boolean result = false;
        Collection<V> coll = getMap().get(key);
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
        return result;
    }

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling {@link #put(Object,Object)
     * put(k, v)} on this map once for each mapping from key {@code k} to value
     * {@code v} in the specified map. The behavior of this operation is
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
     * from key {@code k} to value {@code v} in the specified map. The
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
     * Any method which modifies this bag like {@code add}, {@code remove},
     * {@code Iterator.remove} etc throws
     * <code>UnsupportedOperationException</code>
     *
     * @return a bag view of the key mapping contained in this map
     */
    public Bag<K> keys() {
        return keysBagView != null ? keysBagView : (keysBagView = new KeysBag());
    }

    /**
     * {@inheritDoc}
     */
    public Map<K, Collection<V>> asMap() {
        return getMap();
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
        Collection<V> coll = getMap().get(key);
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
     * {@inheritDoc}
     */
    public MapIterator<K, V> mapIterator() {
        if (size() == 0) {
            return EmptyMapIterator.<K, V>emptyMapIterator();
        }
        return new MultiValuedMapIterator();
    }

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
        Iterator<?> it = keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Collection<?> col = get(key);
            Collection<?> otherCol = other.get(key);
            if (otherCol == null) {
                return false;
            }
            if (CollectionUtils.isEqualCollection(col, otherCol) == false) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        Iterator<Entry<K, Collection<V>>> it = getMap().entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, Collection<V>> entry = it.next();
            K key = entry.getKey();
            Collection<V> valueCol = entry.getValue();
            int vh = 0;
            if (valueCol != null) {
                Iterator<V> colIt = valueCol.iterator();
                while (colIt.hasNext()) {
                    V val = colIt.next();
                    if (val != null) {
                        vh += val.hashCode();
                    }
                }
            }
            h += (key == null ? 0 : key.hashCode()) ^ vh;
        }
        return h;
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

        protected final Object key;

        public WrappedCollection(Object key) {
            this.key = key;
        }

        protected Collection<V> getMapping() {
            return getMap().get(key);
        }

        @SuppressWarnings("unchecked")
        public boolean add(V value) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return AbstractMultiValuedMap.this.put((K) key, value);
            }
            return col.add(value);
        }

        @SuppressWarnings("unchecked")
        public boolean addAll(Collection<? extends V> c) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return AbstractMultiValuedMap.this.putAll((K) key, c);
            }
            return col.addAll(c);
        }

        public void clear() {
            final Collection<V> col = getMapping();
            if (col != null) {
                col.clear();
                AbstractMultiValuedMap.this.remove(key);
            }
        }

        @SuppressWarnings("unchecked")
        public Iterator<V> iterator() {
            final Collection<V> col = getMapping();
            if (col == null) {
                return (Iterator<V>) IteratorUtils.EMPTY_ITERATOR;
            }
            return new ValuesIterator(key);
        }

        public int size() {
            final Collection<V> col = getMapping();
            if (col == null) {
                return 0;
            }
            return col.size();
        }

        public boolean contains(Object o) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return false;
            }
            return col.contains(o);
        }

        public boolean containsAll(Collection<?> o) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return false;
            }
            return col.containsAll(o);
        }

        public boolean isEmpty() {
            final Collection<V> col = getMapping();
            if (col == null) {
                return true;
            }
            return col.isEmpty();
        }

        public boolean remove(Object item) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return false;
            }

            boolean result = col.remove(item);
            if (col.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
            return result;
        }

        public boolean removeAll(Collection<?> c) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return false;
            }

            boolean result = col.removeAll(c);
            if (col.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
            return result;
        }

        public boolean retainAll(Collection<?> c) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return false;
            }

            boolean result = col.retainAll(c);
            if (col.isEmpty()) {
                AbstractMultiValuedMap.this.remove(key);
            }
            return result;
        }

        public Object[] toArray() {
            final Collection<V> col = getMapping();
            if (col == null) {
                return CollectionUtils.EMPTY_COLLECTION.toArray();
            }
            return col.toArray();
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            final Collection<V> col = getMapping();
            if (col == null) {
                return (T[]) CollectionUtils.EMPTY_COLLECTION.toArray(a);
            }
            return col.toArray(a);
        }

        @Override
        public String toString() {
            final Collection<V> col = getMapping();
            if (col == null) {
                return CollectionUtils.EMPTY_COLLECTION.toString();
            }
            return col.toString();
        }

    }

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
            return UnmodifiableSet.<K>unmodifiableSet(keySet());
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
     * Inner class for MultiValuedMap Entries
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
     * Inner class for MapIterator
     */
    private class MultiValuedMapIterator implements MapIterator<K, V> {

        private final Iterator<Entry<K, V>> it;

        private Entry<K, V> current = null;

        public MultiValuedMapIterator() {
            this.it = AbstractMultiValuedMap.this.entries().iterator();
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public K next() {
            current = it.next();
            return current.getKey();
        }

        public K getKey() {
            if (current == null) {
                throw new IllegalStateException();
            }
            return current.getKey();
        }

        public V getValue() {
            if (current == null) {
                throw new IllegalStateException();
            }
            return current.getValue();
        }

        public void remove() {
            it.remove();
        }

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
