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
package org.apache.commons.collections4.bloomfilter.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.BloomFilterConfiguration;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter;
import org.apache.commons.collections4.bloomfilter.StandardBloomFilter;

/**
 * A collection gated by a Bloom filter. The Bloom filter only determines if
 * the objects are in the collection before performing the actual manipulation
 * of the underlying collection.
 * <p>
 * The statistics produced by this class are sensitive to the cardinality of the
 * underlying collection. If the underlying collection permits duplicates the
 * statistics will count each insert as a Bloom filter being added. Thus adding
 * "cat" twice will result in the statistics showing 2 Bloom filters added when
 * in actuality there is only 1.
 * </p>
 * <p>
 * This class can serve an an example of how to implement BloomFilterGated.
 * </p>
 *
 * @param <T> the type of object in the collection.
 *
 * @since 4.5
 */
public final class BloomCollection<T> implements BloomFilterGated<T>, Collection<T> {

    /**
     * The collection this implementation wraps.
     */
    private final Collection<T> wrapped;

    /**
     * The function to convert T to ProtoBloomFilter filter.
     */
    private final Function<T, ProtoBloomFilter> func;

    /**
     * Collection configuration
     */
    private final BloomFilterGatedConfiguration config;

    /**
     * Construct a BloomCollection from a collection, a filter configuration and
     * a mapping function.
     * <p>
     * The function {@code func} should use a {@code ProtoBloomFilter.Builder} to
     * hash various values from a {@code T} instance.
     * </p>
     *
     * @param wrapped      a Collection implementation to wrap with Bloom filter
     *                     checks.
     * @param filterConfig the FilterConfiguration for the gateing filter.
     * @param func         a function to convert from T to ProtoBloomFilter.
     * @see ProtoBloomFilter.Builder
     */
    public BloomCollection(Collection<T> wrapped, BloomFilterConfiguration filterConfig,
        Function<T, ProtoBloomFilter> func) {
        this.wrapped = wrapped;
        this.config = new BloomFilterGatedConfiguration(filterConfig);
        this.func = func;
        for (T o : wrapped) {
            config.merge(fromT(o));
        }
    }

    /**
     * Constructs a Bloom filter for this collection from the ProtoBloomFilter.
     *
     * @param proto the ProtoBloomFilter to create the BloomFilter from.
     * @return the BloomFilter for the gate definition.
     */
    private BloomFilter fromProto(ProtoBloomFilter proto) {
        return new StandardBloomFilter(proto, config.getGateConfig());
    }

    /**
     * Constructs a Bloom filter for this collection from the object of type T.
     *
     * @param t An object of type T.
     * @return the BloomFilter for the gate definition.
     */
    private BloomFilter fromT(T t) {
        return fromProto(func.apply(t));
    }

    @Override
    public BloomFilterConfiguration getGateConfig() {
        return config.getGateConfig();
    }

    @Override
    public BloomFilterGatedStatistics getStats() {
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
        return config.getGateConfig().getNumberOfItems() <= size();
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
        return config.getGate().matches(other);
    }

    @Override
    public boolean matches(ProtoBloomFilter proto) {
        return matches(fromProto(proto));
    }

    @Override
    public boolean inverseMatch(ProtoBloomFilter proto) {
        return inverseMatch(fromProto(proto));
    }

    @Override
    public boolean inverseMatch(BloomFilter filter) {
        return config.getGate().inverseMatches(filter);
    }

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

    @Override
    public boolean contains(ProtoBloomFilter proto, T obj) {
        if (fromProto(proto).matches(config.getGate())) {
            return wrapped.contains(obj);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object obj) {
        return contains(func.apply((T) obj), (T) obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection<?> objs) {
        if (objs.isEmpty()) {
            return true;
        }
        BloomFilter total = StandardBloomFilter.EMPTY;
        for (Object o : objs) {
            total = total.merge(fromT((T)o));
        }
        if (total.matches(config.getGate())) {
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

    @Override
    public boolean remove(ProtoBloomFilter proto, T obj) {
        if (fromProto(proto).matches(config.getGate())) {
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
    public boolean retainAll(MultiValuedMap<ProtoBloomFilter, T> map) {
        List<T> keep = new ArrayList<T>();
        for (ProtoBloomFilter proto : map.keySet()) {
            if (inverseMatch(new StandardBloomFilter(proto, getGateConfig()))) {
                keep.addAll(map.get(proto));
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
        return filter.matches(config.getGate()) ? getData() : Stream.empty();
    }

    @Override
    public Stream<T> getCandidates(ProtoBloomFilter proto) {
        return (fromProto(proto).matches(config.getGate())) ? getData() : Stream.empty();
    }

}
