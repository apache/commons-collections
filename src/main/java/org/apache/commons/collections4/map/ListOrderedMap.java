/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.list.UnmodifiableList;

/**
 * Decorates a {@code Map} to ensure that the order of addition is retained
 * using a {@code List} to maintain order.
 * <p>
 * The order will be used via the iterators and toArray methods on the views.
 * The order is also returned by the {@code MapIterator}.
 * The {@code orderedMapIterator()} method accesses an iterator that can
 * iterate both forwards and backwards through the map.
 * In addition, non-interface methods are provided to access the map by index.
 * </p>
 * <p>
 * If an object is added to the Map for a second time, it will remain in the
 * original position in the iteration.
 * </p>
 * <p>
 * <strong>Note that ListOrderedMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedMap(Map)}. This class may throw
 * exceptions when accessed by concurrent threads without synchronization.
 * </p>
 * <p>
 * <strong>Note that ListOrderedMap doesn't work with
 * {@link java.util.IdentityHashMap IdentityHashMap}, {@link CaseInsensitiveMap},
 * or similar maps that violate the general contract of {@link java.util.Map}.</strong>
 * The {@code ListOrderedMap} (or, more precisely, the underlying {@code List})
 * is relying on {@link Object#equals(Object) equals()}. This is fine, as long as the
 * decorated {@code Map} is also based on {@link Object#equals(Object) equals()},
 * and {@link Object#hashCode() hashCode()}, which
 * {@link java.util.IdentityHashMap IdentityHashMap}, and
 * {@link CaseInsensitiveMap} don't: The former uses {@code ==}, and
 * the latter uses {@link Object#equals(Object) equals()} on a lower-cased
 * key.
 * </p>
 * <p>
 * This class is {@link Serializable} starting with Commons Collections 3.1.
 * </p>
 *
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 * @since 3.0
 */
public class ListOrderedMap<K, V>
        extends AbstractMapDecorator<K, V>
        implements OrderedMap<K, V>, Serializable {

    static class EntrySetView<K, V> extends AbstractSet<Map.Entry<K, V>> {
        private final ListOrderedMap<K, V> parent;
        private final List<K> insertOrder;
        private Set<Map.Entry<K, V>> entrySet;

        EntrySetView(final ListOrderedMap<K, V> parent, final List<K> insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public boolean contains(final Object obj) {
            return getEntrySet().contains(obj);
        }
        @Override
        public boolean containsAll(final Collection<?> coll) {
            return getEntrySet().containsAll(coll);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            return getEntrySet().equals(obj);
        }

        private Set<Map.Entry<K, V>> getEntrySet() {
            if (entrySet == null) {
                entrySet = parent.decorated().entrySet();
            }
            return entrySet;
        }

        @Override
        public int hashCode() {
            return getEntrySet().hashCode();
        }

        @Override
        public boolean isEmpty() {
            return parent.isEmpty();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new ListOrderedIterator<>(parent, insertOrder);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (getEntrySet().contains(obj)) {
                final Object key = ((Map.Entry<K, V>) obj).getKey();
                parent.remove(key);
                return true;
            }
            return false;
        }

        @Override
        public int size() {
            return parent.size();
        }

        @Override
        public String toString() {
            return getEntrySet().toString();
        }
    }

    static class KeySetView<K> extends AbstractSet<K> {
        private final ListOrderedMap<K, Object> parent;

        @SuppressWarnings("unchecked")
        KeySetView(final ListOrderedMap<K, ?> parent) {
            this.parent = (ListOrderedMap<K, Object>) parent;
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public boolean contains(final Object value) {
            return parent.containsKey(value);
        }

        @Override
        public Iterator<K> iterator() {
            return new AbstractUntypedIteratorDecorator<Map.Entry<K, Object>, K>(parent.entrySet().iterator()) {
                @Override
                public K next() {
                    return getIterator().next().getKey();
                }
            };
        }

        @Override
        public int size() {
            return parent.size();
        }
    }

    static class ListOrderedIterator<K, V> extends AbstractUntypedIteratorDecorator<K, Map.Entry<K, V>> {
        private final ListOrderedMap<K, V> parent;
        private K last;

        ListOrderedIterator(final ListOrderedMap<K, V> parent, final List<K> insertOrder) {
            super(insertOrder.iterator());
            this.parent = parent;
        }

        @Override
        public Map.Entry<K, V> next() {
            last = getIterator().next();
            return new ListOrderedMapEntry<>(parent, last);
        }

        @Override
        public void remove() {
            super.remove();
            parent.decorated().remove(last);
        }
    }

    static class ListOrderedMapEntry<K, V> extends AbstractMapEntry<K, V> {
        private final ListOrderedMap<K, V> parent;

        ListOrderedMapEntry(final ListOrderedMap<K, V> parent, final K key) {
            super(key, null);
            this.parent = parent;
        }

        @Override
        public V getValue() {
            return parent.get(getKey());
        }

        @Override
        public V setValue(final V value) {
            return parent.decorated().put(getKey(), value);
        }
    }

    static class ListOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
        private final ListOrderedMap<K, V> parent;
        private ListIterator<K> iterator;
        private K last;
        private boolean readable;

        ListOrderedMapIterator(final ListOrderedMap<K, V> parent) {
            this.parent = parent;
            this.iterator = parent.insertOrder.listIterator();
        }

        @Override
        public K getKey() {
            if (!readable) {
                throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);
            }
            return last;
        }

        @Override
        public V getValue() {
            if (!readable) {
                throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);
            }
            return parent.get(last);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public K next() {
            last = iterator.next();
            readable = true;
            return last;
        }

        @Override
        public K previous() {
            last = iterator.previous();
            readable = true;
            return last;
        }

        @Override
        public void remove() {
            if (!readable) {
                throw new IllegalStateException(AbstractHashedMap.REMOVE_INVALID);
            }
            iterator.remove();
            parent.map.remove(last);
            readable = false;
        }

        @Override
        public void reset() {
            iterator = parent.insertOrder.listIterator();
            last = null;
            readable = false;
        }

        @Override
        public V setValue(final V value) {
            if (!readable) {
                throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);
            }
            return parent.map.put(last, value);
        }

        @Override
        public String toString() {
            if (readable) {
                return "Iterator[" + getKey() + "=" + getValue() + "]";
            }
            return "Iterator[]";
        }
    }

    static class ValuesView<V> extends AbstractList<V> {
        private final ListOrderedMap<Object, V> parent;

        @SuppressWarnings("unchecked")
        ValuesView(final ListOrderedMap<?, V> parent) {
            this.parent = (ListOrderedMap<Object, V>) parent;
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public boolean contains(final Object value) {
            return parent.containsValue(value);
        }

        @Override
        public V get(final int index) {
            return parent.getValue(index);
        }

        @Override
        public Iterator<V> iterator() {
            return new AbstractUntypedIteratorDecorator<Map.Entry<Object, V>, V>(parent.entrySet().iterator()) {
                @Override
                public V next() {
                    return getIterator().next().getValue();
                }
            };
        }

        @Override
        public V remove(final int index) {
            return parent.remove(index);
        }

        @Override
        public V set(final int index, final V value) {
            return parent.setValue(index, value);
        }

        @Override
        public int size() {
            return parent.size();
        }
    }

    /** Serialization version */
    private static final long serialVersionUID = 2728177751851003750L;

    /**
     * Factory method to create an ordered map.
     * <p>
     * An {@code ArrayList} is used to retain order.
     * </p>
     *
     * @param <K>  the key type
     * @param <V>  the value type
     * @param map  the map to decorate, must not be null
     * @return a new list ordered map
     * @throws NullPointerException if map is null
     * @since 4.0
     */
    public static <K, V> ListOrderedMap<K, V> listOrderedMap(final Map<K, V> map) {
        return new ListOrderedMap<>(map);
    }

    /** Internal list to hold the sequence of objects */
    private final List<K> insertOrder = new ArrayList<>();

    /**
     * Constructs a new empty {@code ListOrderedMap} that decorates
     * a {@code HashMap}.
     *
     * @since 3.1
     */
    public ListOrderedMap() {
        this(new HashMap<>());
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param map  the map to decorate, must not be null
     * @throws NullPointerException if map is null
     */
    protected ListOrderedMap(final Map<K, V> map) {
        super(map);
        insertOrder.addAll(decorated().keySet());
    }

    /**
     * Gets an unmodifiable List view of the keys which changes as the map changes.
     * <p>
     * The returned list is unmodifiable because changes to the values of
     * the list (using {@link java.util.ListIterator#set(Object)}) will
     * effectively remove the value from the list and reinsert that value at
     * the end of the list, which is an unexpected side effect of changing the
     * value of a list.  This occurs because changing the key, changes when the
     * mapping is added to the map and thus where it appears in the list.
     * </p>
     * <p>
     * An alternative to this method is to use the better named
     * {@link #keyList()} or {@link #keySet()}.
     * </p>
     *
     * @see #keyList()
     * @see #keySet()
     * @return The ordered list of keys.
     */
    public List<K> asList() {
        return keyList();
    }

    @Override
    public void clear() {
        decorated().clear();
        insertOrder.clear();
    }

    /**
     * Gets a view over the entries in the map.
     * <p>
     * The Set will be ordered by object insertion into the map.
     * </p>
     *
     * @return the fully modifiable set view over the entries
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySetView<>(this, insertOrder);
    }

    /**
     * Gets the first key in this map by insert order.
     *
     * @return the first key currently in this map
     * @throws NoSuchElementException if this map is empty
     */
    @Override
    public K firstKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Map is empty");
        }
        return insertOrder.get(0);
    }

    /**
     * Gets the key at the specified index.
     *
     * @param index  the index to retrieve
     * @return the key at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public K get(final int index) {
        return insertOrder.get(index);
    }

    /**
     * Gets the value at the specified index.
     *
     * @param index  the index to retrieve
     * @return the key at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public V getValue(final int index) {
        return get(insertOrder.get(index));
    }

    /**
     * Gets the index of the specified key.
     *
     * @param key  the key to find the index of
     * @return the index, or -1 if not found
     */
    public int indexOf(final Object key) {
        return insertOrder.indexOf(key);
    }

    /**
     * Gets a view over the keys in the map as a List.
     * <p>
     * The List will be ordered by object insertion into the map.
     * The List is unmodifiable.
     * </p>
     *
     * @see #keySet()
     * @return the unmodifiable list view over the keys
     * @since 3.2
     */
    public List<K> keyList() {
        return UnmodifiableList.unmodifiableList(insertOrder);
    }

    /**
     * Gets a view over the keys in the map.
     * <p>
     * The Collection will be ordered by object insertion into the map.
     * </p>
     *
     * @see #keyList()
     * @return the fully modifiable collection view over the keys
     */
    @Override
    public Set<K> keySet() {
        return new KeySetView<>(this);
    }

    /**
     * Gets the last key in this map by insert order.
     *
     * @return the last key currently in this map
     * @throws NoSuchElementException if this map is empty
     */
    @Override
    public K lastKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Map is empty");
        }
        return insertOrder.get(size() - 1);
    }

    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return new ListOrderedMapIterator<>(this);
    }

    /**
     * Gets the next key to the one specified using insert order.
     * This method performs a list search to find the key and is O(n).
     *
     * @param key  the key to find previous for
     * @return the next key, null if no match or at start
     */
    @Override
    public K nextKey(final Object key) {
        final int index = insertOrder.indexOf(key);
        if (index >= 0 && index < size() - 1) {
            return insertOrder.get(index + 1);
        }
        return null;
    }

    /**
     * Gets the previous key to the one specified using insert order.
     * This method performs a list search to find the key and is O(n).
     *
     * @param key  the key to find previous for
     * @return the previous key, null if no match or at start
     */
    @Override
    public K previousKey(final Object key) {
        final int index = insertOrder.indexOf(key);
        if (index > 0) {
            return insertOrder.get(index - 1);
        }
        return null;
    }

    /**
     * Puts a key-value mapping into the map at the specified index.
     * <p>
     * If the map already contains the key, then the original mapping
     * is removed and the new mapping added at the specified index.
     * The remove may change the effect of the index. The index is
     * always calculated relative to the original state of the map.
     * </p>
     * <p>
     * Thus, the steps are: (1) remove the existing key-value mapping,
     * then (2) insert the new key-value mapping at the position it
     * would have been inserted had the remove not occurred.
     * </p>
     *
     * @param index  the index at which the mapping should be inserted
     * @param key  the key
     * @param value  the value
     * @return the value previously mapped to the key
     * @throws IndexOutOfBoundsException if the index is out of range [0, size]
     * @since 3.2
     */
    public V put(int index, final K key, final V value) {
        if (index < 0 || index > insertOrder.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + insertOrder.size());
        }

        final Map<K, V> m = decorated();
        if (m.containsKey(key)) {
            final V result = m.remove(key);
            final int pos = insertOrder.indexOf(key);
            insertOrder.remove(pos);
            if (pos < index) {
                index--;
            }
            insertOrder.add(index, key);
            m.put(key, value);
            return result;
        }
        insertOrder.add(index, key);
        m.put(key, value);
        return null;
    }

    @Override
    public V put(final K key, final V value) {
        if (decorated().containsKey(key)) {
            // re-adding doesn't change order
            return decorated().put(key, value);
        }
        // first add, so add to both map and list
        final V result = decorated().put(key, value);
        insertOrder.add(key);
        return result;
    }

    /**
     * Puts the values contained in a supplied Map into the Map starting at
     * the specified index.
     *
     * @param index the index in the Map to start at.
     * @param map the Map containing the entries to be added.
     * @throws IndexOutOfBoundsException if the index is out of range [0, size]
     */
    public void putAll(int index, final Map<? extends K, ? extends V> map) {
        if (index < 0 || index > insertOrder.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + insertOrder.size());
        }
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            final K key = entry.getKey();
            final boolean contains = containsKey(key);
            // The return value of put is null if the key did not exist OR the value was null
            // so it cannot be used to determine whether the key was added
            put(index, entry.getKey(), entry.getValue());
            if (!contains) {
                // if no key was replaced, increment the index
                index++;
            } else {
                // otherwise put the next item after the currently inserted key
                index = indexOf(entry.getKey()) + 1;
            }
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Deserializes the map in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     * @since 3.1
     */
    @SuppressWarnings("unchecked") // (1) should only fail if input stream is incorrect
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject(); // (1)
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index  the index of the object to remove
     * @return the removed value, or {@code null} if none existed
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public V remove(final int index) {
        return remove(get(index));
    }

    @Override
    public V remove(final Object key) {
        V result = null;
        if (decorated().containsKey(key)) {
            result = decorated().remove(key);
            insertOrder.remove(key);
        }
        return result;
    }

    /**
     * Sets the value at the specified index.
     *
     * @param index  the index of the value to set
     * @param value  the new value to set
     * @return the previous value at that index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @since 3.2
     */
    public V setValue(final int index, final V value) {
        final K key = insertOrder.get(index);
        return put(key, value);
    }

    /**
     * Returns the Map as a string.
     *
     * @return the Map as a String
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        boolean first = true;
        for (final Map.Entry<K, V> entry : entrySet()) {
            final K key = entry.getKey();
            final V value = entry.getValue();
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            buf.append(value == this ? "(this Map)" : value);
        }
        buf.append('}');
        return buf.toString();
    }

    /**
     * Gets a view over the values in the map as a List.
     * <p>
     * The List will be ordered by object insertion into the map.
     * The List supports remove and set, but does not support add.
     * </p>
     *
     * @see #values()
     * @return the partially modifiable list view over the values
     * @since 3.2
     */
    public List<V> valueList() {
        return new ValuesView<>(this);
    }

    /**
     * Gets a view over the values in the map.
     * <p>
     * The Collection will be ordered by object insertion into the map.
     * </p>
     * <p>
     * From Commons Collections 3.2, this Collection can be cast
     * to a list, see {@link #valueList()}
     * </p>
     *
     * @see #valueList()
     * @return the fully modifiable collection view over the values
     */
    @Override
    public Collection<V> values() {
        return new ValuesView<>(this);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     * @since 3.1
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

}
