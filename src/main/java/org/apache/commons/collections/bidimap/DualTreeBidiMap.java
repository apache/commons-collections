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
package org.apache.commons.collections.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.SortedBidiMap;
import org.apache.commons.collections.map.AbstractSortedMapDecorator;

/**
 * Implementation of <code>BidiMap</code> that uses two <code>TreeMap</code> instances.
 * <p>
 * The setValue() method on iterators will succeed only if the new value being set is
 * not already in the bidimap.
 * <p>
 * When considering whether to use this class, the {@link TreeBidiMap} class should
 * also be considered. It implements the interface using a dedicated design, and does
 * not store each object twice, which can save on memory use.
 * <p>
 * NOTE: From Commons Collections 3.1, all subclasses will use <code>TreeMap</code>
 * and the flawed <code>createMap</code> method is ignored.
 *
 * @since Commons Collections 3.0
 * @version $Id$
 *
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public class DualTreeBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements
        SortedBidiMap<K, V>, Serializable {

    /** Ensure serialization compatibility */
    private static final long serialVersionUID = 721969328361809L;

    /** The key comparator to use */
    protected final Comparator<? super K> comparator;

    /** The value comparator to use */
    protected final Comparator<? super V> valueComparator;
    
    /**
     * Creates an empty <code>DualTreeBidiMap</code>
     */
    public DualTreeBidiMap() {
        super(new TreeMap<K, V>(), new TreeMap<V, K>());
        this.comparator = null;
        this.valueComparator = null;
    }

    /**
     * Constructs a <code>DualTreeBidiMap</code> and copies the mappings from
     * specified <code>Map</code>.
     *
     * @param map  the map whose mappings are to be placed in this map
     */
    public DualTreeBidiMap(Map<K, V> map) {
        super(new TreeMap<K, V>(), new TreeMap<V, K>());
        putAll(map);
        this.comparator = null;
        this.valueComparator = null;
    }

    /**
     * Constructs a <code>DualTreeBidiMap</code> using the specified Comparator.
     *
     * @param keyComparator  the Comparator
     */
    public DualTreeBidiMap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
        super(new TreeMap<K, V>(keyComparator), new TreeMap<V, K>(valueComparator));
        this.comparator = keyComparator;
        this.valueComparator = valueComparator;
    }

    /**
     * Constructs a <code>DualTreeBidiMap</code> that decorates the specified maps.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     */
    protected DualTreeBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
        this.comparator = ((SortedMap<K, V>) normalMap).comparator();
        this.valueComparator = ((SortedMap<V, K>) reverseMap).comparator();
    }

    /**
     * Creates a new instance of this object.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseMap  the inverse BidiMap
     * @return new bidi map
     */
    @Override
    protected DualTreeBidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseMap) {
        return new DualTreeBidiMap<V, K>(normalMap, reverseMap, inverseMap);
    }

    //-----------------------------------------------------------------------
    public Comparator<? super K> comparator() {
        return ((SortedMap<K, V>) normalMap).comparator();
    }

    public Comparator<? super V> valueComparator() {
        return ((SortedMap<V, K>) reverseMap).comparator();
        
    }

    public K firstKey() {
        return ((SortedMap<K, V>) normalMap).firstKey();
    }

    public K lastKey() {
        return ((SortedMap<K, V>) normalMap).lastKey();
    }

    public K nextKey(K key) {
        if (isEmpty()) {
            return null;
        }
        if (normalMap instanceof OrderedMap) {
            return ((OrderedMap<K, ?>) normalMap).nextKey(key);
        }
        SortedMap<K, V> sm = (SortedMap<K, V>) normalMap;
        Iterator<K> it = sm.tailMap(key).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public K previousKey(K key) {
        if (isEmpty()) {
            return null;
        }
        if (normalMap instanceof OrderedMap) {
            return ((OrderedMap<K, V>) normalMap).previousKey(key);
        }
        SortedMap<K, V> sm = (SortedMap<K, V>) normalMap;
        SortedMap<K, V> hm = sm.headMap(key);
        if (hm.isEmpty()) {
            return null;
        }
        return hm.lastKey();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an ordered map iterator.
     * <p>
     * This implementation copies the elements to an ArrayList in order to
     * provide the forward/backward behaviour.
     *
     * @return a new ordered map iterator
     */
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return new BidiOrderedMapIterator<K, V>(this);
    }

    public SortedBidiMap<V, K> inverseSortedBidiMap() {
        return inverseBidiMap();
    }

    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        return inverseBidiMap();
    }

    //-----------------------------------------------------------------------
    public SortedMap<K, V> headMap(K toKey) {
        SortedMap<K, V> sub = ((SortedMap<K, V>) normalMap).headMap(toKey);
        return new ViewMap<K, V>(this, sub);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> sub = ((SortedMap<K, V>) normalMap).tailMap(fromKey);
        return new ViewMap<K, V>(this, sub);
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> sub = ((SortedMap<K, V>) normalMap).subMap(fromKey, toKey);
        return new ViewMap<K, V>(this, sub);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedBidiMap<V, K> inverseBidiMap() {
        return (SortedBidiMap<V, K>) super.inverseBidiMap();
    }

    //-----------------------------------------------------------------------
    /**
     * Internal sorted map view.
     */
    protected static class ViewMap<K, V> extends AbstractSortedMapDecorator<K, V> {
        /** The parent bidi map. */
        final DualTreeBidiMap<K, V> bidi;

        /**
         * Constructor.
         * @param bidi  the parent bidi map
         * @param sm  the subMap sorted map
         */
        protected ViewMap(DualTreeBidiMap<K, V> bidi, SortedMap<K, V> sm) {
            // the implementation is not great here...
            // use the normalMap as the filtered map, but reverseMap as the full map
            // this forces containsValue and clear to be overridden
            super(new DualTreeBidiMap<K, V>(sm, bidi.reverseMap, bidi.inverseBidiMap));
            this.bidi = decorated();
        }

        @Override
        public boolean containsValue(Object value) {
            // override as default implementation uses reverseMap
            return decorated().normalMap.containsValue(value);
        }

        @Override
        public void clear() {
            // override as default implementation uses reverseMap
            for (Iterator<K> it = keySet().iterator(); it.hasNext();) {
                it.next();
                it.remove();
            }
        }

        @Override
        public SortedMap<K, V> headMap(K toKey) {
            return new ViewMap<K, V>(decorated(), super.headMap(toKey));
        }

        @Override
        public SortedMap<K, V> tailMap(K fromKey) {
            return new ViewMap<K, V>(decorated(), super.tailMap(fromKey));
        }

        @Override
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return new ViewMap<K, V>(decorated(), super.subMap(fromKey, toKey));
        }

        @Override
        protected DualTreeBidiMap<K, V> decorated() {
            return (DualTreeBidiMap<K, V>) super.decorated();
        }

        @Override
        public K previousKey(K key) {
            return decorated().previousKey(key);
        }

        @Override
        public K nextKey(K key) {
            return decorated().nextKey(key);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class MapIterator.
     */
    protected static class BidiOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {

        /** The parent map */
        protected final AbstractDualBidiMap<K, V> parent;

        /** The iterator being decorated */
        protected ListIterator<Map.Entry<K, V>> iterator;

        /** The last returned entry */
        private Map.Entry<K, V> last = null;

        /**
         * Constructor.
         * @param parent  the parent map
         */
        protected BidiOrderedMapIterator(AbstractDualBidiMap<K, V> parent) {
            super();
            this.parent = parent;
            iterator = new ArrayList<Map.Entry<K, V>>(parent.entrySet()).listIterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public K next() {
            last = iterator.next();
            return last.getKey();
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public K previous() {
            last = iterator.previous();
            return last.getKey();
        }

        public void remove() {
            iterator.remove();
            parent.remove(last.getKey());
            last = null;
        }

        public K getKey() {
            if (last == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return last.getKey();
        }

        public V getValue() {
            if (last == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return last.getValue();
        }

        public V setValue(V value) {
            if (last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (parent.reverseMap.containsKey(value) &&
                parent.reverseMap.get(value) != last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            return parent.put(last.getKey(), value);
        }

        public void reset() {
            iterator = new ArrayList<Map.Entry<K, V>>(parent.entrySet()).listIterator();
            last = null;
        }

        @Override
        public String toString() {
            if (last != null) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            }
            return "MapIterator[]";
        }
    }

    // Serialization
    //-----------------------------------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(normalMap);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        normalMap = new TreeMap<K, V>(comparator);
        reverseMap = new TreeMap<V, K>(valueComparator);
        @SuppressWarnings("unchecked") // will fail at runtime if the stream is incorrect
        Map<K, V> map = (Map<K, V>) in.readObject();
        putAll(map);
    }

}
