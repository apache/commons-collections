/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilters.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;

/**
 * A collection fronted by a bloom filter. The bloom filter only determines if
 * the objects are in the collection before performing the actual manipulation of 
 * the underlying collection..
 *
 * @param <T> the type of object in the collection.
 */
public class BloomCollection<T> implements BloomFilterGated<T>, Collection<T> {

    private final Collection<T> wrapped;
    /*
     * Function to convert T to bloom filter.
     */
    private final Function<T, ProtoBloomFilter> func;

    /**
     * Collection configuration
     */
    private final CollectionConfig config;

    /**
     * Constructor.
     * <p>
     * The function {@code func} should use a {@code ProtoBloomFilter.Builder} to
     * hash various values from a {@code T} instance.
     * </p>
     *
     * @param wrapped      a Collection implementation to wrap with bloom filter
     *                     checks.
     * @param filterConfig the FilterConfiguration for the gateing filter.
     * @param func         a function to convert from T to ProtoBloomFilter.
     * @see ProtoBloomFilter.Builder
     */
    public BloomCollection(Collection<T> wrapped, FilterConfig filterConfig, Function<T, ProtoBloomFilter> func) {
        this.wrapped = wrapped;
        this.config = new CollectionConfig(filterConfig);
        this.func = func;
        for (Object o : wrapped) {
            config.merge(fromT(o));
        }
    }

    // make a bloom filter for this collection from the proto type.
    private BloomFilter fromProto(ProtoBloomFilter pf) {
        return new BloomFilter(pf, config.getConfig());
    }

    // make a bloom filter for this collection from the object of type T.
    @SuppressWarnings("unchecked")
    private BloomFilter fromT(Object t) {
        return fromProto(func.apply((T) t));
    }

    /**
     * Get the Filter configuration for the gateway bloom filter.
     *
     * @return the filter configuration.
     */
    public FilterConfig getGateConfig() {
        return config.getConfig();
    }

    /**
     * Get the collections statistics.
     *
     * @return the collection stats for this collection.
     */
    @Override
    public CollectionStats getStats() {
        return config.getStats();
    }

    @Override
    public Stream<T> getData() {
        return wrapped.stream();
    }

    @Override
    public BloomFilter getGate() {
        return config.getGate();
    }

    @Override
    public boolean isFull() {
        return config.getConfig().getNumberOfItems() <= size();
    }

    @Override
    public int distance(BloomFilter other) {
        return config.getGate().distance(other);
    }

    @Override
    public int distance(ProtoBloomFilter pf) {
        return distance(fromProto(pf));
    }

    @Override
    public boolean matches(BloomFilter other) {
        return config.getGate().match(other);
    }

    @Override
    public boolean matches(ProtoBloomFilter pbf) {
        return matches(fromProto(pbf));
    }

    @Override
    public boolean inverseMatch(ProtoBloomFilter pbf) {
        return inverseMatch(fromProto(pbf));
    }

    @Override
    public boolean inverseMatch(BloomFilter other) {
        return config.getGate().inverseMatch(other);
    }

    /**
     * Add an object of type @{code T} using a previously calculated proto bloom
     * filter.
     *
     * @param proto the proto bloom filter.
     * @param obj   the object to add.
     * @return true if the object was added to the wrapped collection.
     */
    @Override
    public boolean add(ProtoBloomFilter proto, T obj) {
        boolean retval = wrapped.add(obj);
        if (retval) {
            config.merge(proto);
        }
        return retval;
    }

    @Override
    public boolean add(T obj) {
        return add(func.apply(obj), obj);
    }

    @Override
    public boolean addAll(Collection<? extends T> objs) {
        boolean result = false;
        for (T t : objs) {
            result |= add(t);
        }
        return result;
    }

    @Override
    public void clear() {
        config.clear();
        wrapped.clear();
    }

    /**
     * Check if the collection contains an object with a known proto bloom filter.
     *
     * @param proto the proto bloom filter to use for checking.
     * @param obj   the object to look for.
     * @return true if the collection contains the object.
     */
    @Override
    public boolean contains(ProtoBloomFilter proto, T obj) {
        if (fromProto(proto).match(config.getGate())) {
            return wrapped.contains(obj);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object obj) {
        return contains(func.apply((T) obj), (T) obj);
    }

    @Override
    public boolean containsAll(Collection<?> objs) {
        if (objs.isEmpty()) {
            return true;
        }
        BloomFilter total = BloomFilter.EMPTY;
        for (Object o : objs) {
            total = total.merge(fromT(o));
        }
        if (total.match(config.getGate())) {
            return wrapped.containsAll(objs);
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped.iterator();
    }

    /**
     * Remove an object from the collection if it is in it.
     *
     * @param proto the proto bloom filter to use for checking.
     * @param obj   the object to remove.
     * @return true if the object was removed.
     */
    @Override
    public boolean remove(ProtoBloomFilter proto, T obj) {
        if (fromProto(proto).match(config.getGate())) {
            boolean result = wrapped.remove(obj);
            if (result) {
                config.getStats().delete();
            }
            return result;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object obj) {
        return remove(func.apply((T) obj), (T) obj);
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        boolean removed = false;
        for (Object t : arg0) {
            removed |= remove(t);
        }
        return removed;
    }

    @Override
    public boolean retainAll(ListValuedMap<ProtoBloomFilter, T> map) {
        List<T> keep = new ArrayList<T>();
        for (ProtoBloomFilter proto : map.keySet()) {
            if (inverseMatch(new BloomFilter(proto, getGateConfig()))) {
                keep.addAll( map.get(proto));         
            }
        }
        return retainAll(keep);
    }

    @Override
    public boolean retainAll(Collection<?> objs) {
        int size = wrapped.size();
        boolean result = wrapped.retainAll(objs);
        config.getStats().delete(size - wrapped.size());
        return result;
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public long count() {
        return size();
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <E> E[] toArray(E[] ary) {
        return wrapped.toArray(ary);
    }

    @Override
    public Stream<T> getCandidates(BloomFilter filter) {
        return filter.match(config.getGate()) ? 
            getData() : Stream.empty();
    }

    @Override
    public Stream<T> getCandidates(ProtoBloomFilter proto) {
        return (fromProto(proto).match(config.getGate()))?
             getData() :  Stream.empty();
    }

}
