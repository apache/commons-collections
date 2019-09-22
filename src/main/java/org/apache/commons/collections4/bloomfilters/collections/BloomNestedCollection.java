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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

/**
 * An implementation of BloomFilterGated that uses BloomFilterGated internally
 * to further segregate the data. Each {@code BloomFilterGated} is called a
 * bucket.
 * <p>
 * The constructor defines the minimum number free buckets. This is the number
 * created at creation. If any bucket fills up a new bucket is created so that
 * there are always the minimum number of free buckets to accept data.
 * </p>
 * <p>
 * This structure is sensitive  to the filter sizing.  When the same filter configurations
 * are used for the gate and buckets then methods that use the a BloomFilter parameter and
 * will return the same results as the same method that uses a ProtoBloomFilter.  When the 
 * filter configurations differ the ProtoBloomFilter methods will return fewer false positives.
 * </p>
 * 
 *
 * @param <T> the type of object in the collection.
 */
public class BloomNestedCollection<T> implements BloomFilterGated<T>, Collection<T> {

    /* package private for testing */
    final List<BloomFilterGated<T>> buckets;
    private final Function<T, ProtoBloomFilter> func;
    private final CollectionConfig collectionConfig;
    private final BucketFactory<T> bucketFactory;

    /**
     * Constructor.
     *
     * @param func         the function that converts object of type {@code T} to
     *                     ProtoBloomFilters.
     * @param minFree      the minimum number of free buckets.
     * @param gateConfig   the filter configuration for the gating filter.
     * @param bucketFactory the bucketFactory for the buckets.
     */
    public BloomNestedCollection(Function<T, ProtoBloomFilter> func, int minFree, FilterConfig gateConfig,
            BucketFactory<T> bucketFactory) {
        this.func = func;
        this.buckets = new ArrayList<BloomFilterGated<T>>();
        this.collectionConfig = new CollectionConfig(gateConfig);
        this.bucketFactory = bucketFactory;

        for (int i = 0; i < minFree; i++) {
            newBucket();
        }
    }
    
    @Override
    public FilterConfig getGateConfig() {
        return collectionConfig.getConfig();
    }

    @Override
    public CollectionStats getStats() {
        return collectionConfig.getStats();
    }

    /**
     * Constructs a new bucket, adds it to the collection and registers statistics
     * action mapper.
     */
    private BloomFilterGated<T> newBucket() {
        BloomFilterGated<T> newBucket = bucketFactory.newBucket();
        newBucket.getStats().addConsumer(collectionConfig.getStats().getActionMapper());
        buckets.add(newBucket);
        return newBucket;
    }

    private BloomFilter fromProto(ProtoBloomFilter proto) {
        return new BloomFilter(proto, collectionConfig.getConfig());
    }

    @Override
    public BloomFilter getGate() {
        return collectionConfig.getGate();
    }

    @Override
    public boolean isFull() {
        return collectionConfig.getConfig().getNumberOfItems() <= size();
    }

    @Override
    public int distance(BloomFilter filter) {
        return collectionConfig.getGate().distance(filter);
    }

    @Override
    public int distance(ProtoBloomFilter proto) {
        return distance(fromProto(proto));
    }

    @Override
    public boolean matches(BloomFilter filter) {
        return collectionConfig.getGate().match(filter);
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
        return collectionConfig.getGate().inverseMatch(filter);

    }

    @Override
    public void clear() {
        collectionConfig.clear();
        for (BloomFilterGated<T> bc : buckets) {
            bc.clear();
        }
    }

    @Override
    public boolean add(T t) {
        return add(func.apply(t), t);
    }

    @Override
    public boolean add(ProtoBloomFilter proto, T t) {
        BloomFilter bf = fromProto(proto);
        int dist = Integer.MAX_VALUE;
        BloomFilterGated<T> bucket = null;
        for (BloomFilterGated<T> candidate : buckets) {
            if (!candidate.isFull()) {
                int candidateDist = candidate.distance(bf);
                if (candidateDist < dist) {
                    bucket = candidate;
                    dist = candidateDist;
                }
            }
        }
        if (bucket == null) {

            // should not happen
            bucket = newBucket();
        }
        boolean result = bucket.add(proto, t);
        if (result) {
            collectionConfig.merge(proto);
        }
        if (bucket.isFull()) {
            newBucket();
        }
        return result;
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
    public boolean contains(ProtoBloomFilter proto, T t) {
        if (fromProto(proto).match(collectionConfig.getGate())) {
            for (BloomFilterGated<T> candidate : buckets) {
                if (candidate.contains(proto, t)) {
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public boolean contains(Object obj) {
        @SuppressWarnings("unchecked")
        T t = (T) obj;
        return contains(func.apply(t), t);
    }

    @Override
    public boolean containsAll(Collection<?> objs) {
        if (objs.isEmpty()) {
            return true;
        }
        // builder for complete filter
        ProtoBloomFilter.Builder builder = ProtoBloomFilter.builder();
        // list of protos that built the complete
        List<ProtoBloomFilter> protos = new ArrayList<ProtoBloomFilter>();
        for (Object o : objs) {
            @SuppressWarnings("unchecked")
            ProtoBloomFilter proto = func.apply((T) o);
            builder.with(proto);
            protos.add(proto);
        }

        /* if the complete filter matches the collection then all items
         *  may be in the collection.
        */
        if (fromProto(builder.build()).match(collectionConfig.getGate())) {
            // check candidates for matches
            Iterator<ProtoBloomFilter> iter = protos.iterator();
            for (Object o : objs) {
                boolean result = false;
                @SuppressWarnings("unchecked")
                T t = (T) o;
                ProtoBloomFilter proto = iter.next();
                for (BloomFilterGated<T> candidate : buckets) {
                    result |= candidate.contains(proto, t);
                    if (result)
                    {
                        break;
                    }
                }
                if (!result) {
                    // object was not found in buckets.
                    return false;
                }
            }
            // all objects were found in buckets.
            return true;
        }
        // gate filter not matched so not here.
        return false;
    }

    @Override
    public boolean isEmpty() {
        for (BloomFilterGated<T> candidate : buckets) {
            if (!candidate.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return getData().iterator();
    }

    @Override
    public boolean remove(Object obj) {
        @SuppressWarnings("unchecked")
        T t = (T) obj;
        return remove(func.apply(t), t);
    }

    @Override
    public boolean remove(ProtoBloomFilter proto, T t) {

        boolean result = false;
        if (fromProto(proto).match(collectionConfig.getGate())) {
            for (BloomFilterGated<T> candidate : buckets) {
                result |= candidate.remove(proto, t);
            }
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> objs) {
        boolean removed = false;

        for (Object o : objs) {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            ProtoBloomFilter proto = func.apply(t);
            if (fromProto(proto).match(collectionConfig.getGate())) {
                for (BloomFilterGated<T> candidate : buckets) {
                    removed |= candidate.remove(proto, t);
                }

            }
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean retainAll(Collection<?> objs) {
        ListValuedMap<ProtoBloomFilter, T> map = new ArrayListValuedHashMap<ProtoBloomFilter, T>();
        objs.stream().map(obj -> (T) obj).forEach(t -> map.put(func.apply(t), t));
        return retainAll(map);
    }

    @Override
    public boolean retainAll(ListValuedMap<ProtoBloomFilter, T> map) {
        boolean result = false;
        for (BloomFilterGated<T> candidate : buckets) {
            result |= candidate.retainAll(map);
        }
        return result;
    }

    @Override
    public int size() {
        return CollectionStats.asInt(count());
    }

    @Override
    public long count() {
        return buckets.stream().mapToLong(bc -> bc.count()).sum();
    }

    @Override
    public Object[] toArray() {
        return getData().toArray();
    }

    @Override
    public <E> E[] toArray(E[] arry) {
        return getData().collect(Collectors.toList()).toArray(arry);
    }

    @Override
    public Stream<T> getCandidates(BloomFilter filter) {
        Stream<T> result = Stream.empty();
        if (filter.match(collectionConfig.getGate()))
        {
            if (collectionConfig.getConfig().equals( bucketFactory.getConfig()))
            {
            
            for (BloomFilterGated<T> bucket : buckets) {
                result = Stream.concat(result, bucket.getCandidates(filter));
            }
            
            }
            else {
                result=getData();
            }
        }
        return result;
    }

    @Override
    public Stream<T> getCandidates(ProtoBloomFilter proto) {
        Stream<T> result = Stream.empty();
        if (collectionConfig.getGate().inverseMatch( new BloomFilter( proto, collectionConfig.getConfig())))
        {
            for (BloomFilterGated<T> bucket : buckets) {
                result = Stream.concat(result, bucket.getCandidates(proto));
            }
        }
        return result;
    }

    @Override
    public Stream<T> getData() {
        Stream<T> result = Stream.empty();
        for (BloomFilterGated<T> bucket : buckets) {
            result = Stream.concat(result, bucket.getData());
        }
        return result;
    }

    /**
     * An interface defining the bucket factory for the nexted collection.
     *
     * @param <T> the type of object in the collection.
     */
    public interface BucketFactory<T> {
        /**
         * Get the filter configuration for the buckets produced by the factory.
         *
         * @return the filter configuration used by the factory.
         */
        FilterConfig getConfig();

        /**
         * Create a new BloomFilterGated object.
         *
         * @return a new bucket.
         */
        BloomFilterGated<T> newBucket();
    }

    /**
     * An implementation of BucketFactory that produces ArrayList base BloomCollections. 
     *
     *
     * @param <T> the type of object in the collection.
     */
    public static class BloomArrayListFactory<T> implements BucketFactory<T> {

        private final FilterConfig filterConfig;
        private final Function<T, ProtoBloomFilter> func;

        /**
         * Constructor.
         *
         * @param func         the function to convert from T to ProtoBloomFilter.
         * @param filterConfig the FilterConfiguration for the gating filter.
         */
        public BloomArrayListFactory(Function<T, ProtoBloomFilter> func, FilterConfig filterConfig) {
            this.func = func;
            this.filterConfig = filterConfig;
        }

        @Override
        public FilterConfig getConfig() {
            return filterConfig;
        }

        @Override
        public BloomCollection<T> newBucket() {
            return new BloomCollection<T>(new ArrayList<T>(), filterConfig, func);
        }

    }

    
    /**
     * An implementation of BucketFactory that produces HashSet base BloomCollections. 
     *
     * @param <T> the type of object in the collection.
     */
    public static class BloomHashSetFactory<T> implements BucketFactory<T> {

        private final FilterConfig filterConfig;
        private final Function<T, ProtoBloomFilter> func;

        /**
         * Constructor.
         *
         * @param func         the function to convert from T to ProtoBloomFilter.
         * @param filterConfig the FilterConfiguration for the gating filter.
         */
        public BloomHashSetFactory(Function<T, ProtoBloomFilter> func, FilterConfig filterConfig) {
            this.func = func;
            this.filterConfig = filterConfig;
        }

        @Override
        public FilterConfig getConfig() {
            return filterConfig;
        }

        @Override
        public BloomCollection<T> newBucket() {
            return new BloomCollection<T>(new HashSet<T>(), filterConfig, func);
        }

    }
    

}
