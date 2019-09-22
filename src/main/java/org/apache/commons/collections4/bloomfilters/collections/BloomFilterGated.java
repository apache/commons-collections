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

import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;

/**
 * A collection of objects gated by a bloom filter.
 *
 * @param <T> t The type of the objects.
 */
public interface BloomFilterGated<T> {

    /**
     * Get the gating bloom filter. All objects in the collection are represented in
     * this bloom filter.
     *
     * @return the gating bloom filter.
     */
    BloomFilter getGate();

    /**
     * Get the filter configuration for the gate.
     * 
     * @return the gate filter configuration.
     */
    FilterConfig getGateConfig();

    /**
     * Return true if this bloom filter is full. A full bloom filter is one that has
     * the number of items defined in the gate filter configuration in the list.
     *
     * A full collection is not prohibited or otherwise restricted from accepting
     * more entries but will be less efficient during searches.
     *
     * @return true if the list is full.
     */
    boolean isFull();

    /**
     * Calculates the hamming distance from the gate filter to a filter.
     *
     * @param filter The filter to calculate distance to.
     * @return the distance
     */
    int distance(BloomFilter filter);

    /**
     * Calculates the hamming distance to a proto filter.
     *
     * @param proto The proto filter to calculate distance to.
     * @return the distance
     */
    int distance(ProtoBloomFilter proto);

    /**
     * Returns true {@code gate & filter == gate }.
     *
     * @param filter The filter to look for.
     * @return true if {@code gate & filter == gate }
     */
    boolean matches(BloomFilter filter);

    /**
     * Return true if {@code gate & proto == this }.
     *
     * @param proto the proto bloom filter to check.
     * @return true if {@code gate & proto == this }.
     */
    boolean matches(ProtoBloomFilter proto);

    /**
     * returns true if {@code proto & this == proto }.
     *
     * @param proto the proto bloom filter to check.
     * @return true if the proto bloom filter contains the gate for this list.
     */
    boolean inverseMatch(ProtoBloomFilter proto);

    /**
     * returns true if {@code filter & this == filter }.
     *
     * @param filter the bloom filter to check.
     * @return true if {@code filter & this == filter }.
     */
    boolean inverseMatch(BloomFilter filter);

    /**
     * Get a stream of candidates that match the filter.
     * <p>
     * Some gated containers have a single gating bloom filter. In this case
     * {@code getCandidates()} returns the same stream as {@code getData{}}.
     * However, there are some cases where the collection may be a set of other
     * gated collections, in this case {@code getCandidates()} will check the
     * internal collections for matches and only return those that match.
     * </p>
     *
     * @param filter the BloomFilter to match.
     * @return the stream of values that match the filter.
     */
    Stream<T> getCandidates(BloomFilter filter);

    /**
     * Get a stream of candidates that match the filter.
     * <p>
     * Some gated containers have a single gating bloom filter. In this case
     * {@code getCandidates()} returns the same stream as {@code getData{}}.
     * However, there are some cases where the collection may be a set of other
     * gated collections, in this case {@code getCandidates()} will check the
     * internal collections for matches and only return those that match.
     * </p>
     *
     * @param proto the ProtoBloomFilter to match.
     * @return the stream of values that match the filter.
     */
    Stream<T> getCandidates(ProtoBloomFilter proto);

    /**
     * Get all the data from this container.
     *
     * @return a stream of all the data in this container.
     */
    Stream<T> getData();

    /**
     * Return true if the collection contains the object specified by the bloom
     * filter. The bloom filter is used to locate candidates which are then checked
     * for equality with obj.
     *
     * @param proto the proto bloom filter to search with.
     * @param obj   the object to locate.
     * @return true if the object is found.
     */
    boolean contains(ProtoBloomFilter proto, T obj);

    /**
     * Get the collection statistics for this gated filter.
     *
     * @return the collection statistics.
     */
    CollectionStats getStats();

    /**
     * Clear this collection. Must remove all stored data as well as clear the
     * filter and reset the collection statistics.
     */
    void clear();

    /**
     * Add an object to the collection.
     *
     * @param proto the proto bloom filter for the object.
     * @param t     the object to add.
     * @return true if the collection changed as a result of the call.
     */
    boolean add(ProtoBloomFilter proto, T t);

    /**
     * Remove an object from, the collection.
     *
     * @param proto the proto bloom filter for the object.
     * @param t     the object to remove.
     * @return true if the collection changed as a result of the call.
     */
    boolean remove(ProtoBloomFilter proto, T t);

    /**
     * Retain only the in the map in the collection. The map is keyed by proto bloom
     * filter by has multiple T objects.
     *
     * @param map A proto bloom filter to object map.
     * @return true if the collection changed as a result of the call.
     */
    boolean retainAll(MultiValuedMap<ProtoBloomFilter, T> map);

    /**
     * Return true if the collection is empty
     *
     * @return true if the collection is empty.
     */
    boolean isEmpty();

    /**
     * Return a count of the objects in the collection.
     *
     * @return the number of objects in the collection.
     */
    long count();

}
