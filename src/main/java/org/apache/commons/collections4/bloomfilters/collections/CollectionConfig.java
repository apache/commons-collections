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

import org.apache.commons.collections4.bloomfilters.StandardBloomFilter;
import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;

/**
 * The configuration of a gated bloom collection.
 *
 */
public class CollectionConfig {
    // the configuration for the gate filter.
    private final FilterConfig gateConfig;
    // the gate filter.
    private StandardBloomFilter gate;
    // the collection stats.
    private CollectionStats collectionStats;

    /**
     * Constructor.
     *
     * @param gateConfig the configuration for the gating filter.
     */
    public CollectionConfig(FilterConfig gateConfig) {
        this.gateConfig = gateConfig;
        this.gate = StandardBloomFilter.EMPTY;
        this.collectionStats = new CollectionStats();
    }

    /**
     * Get the gate bloom filter.
     *
     * @return the gating bloom filter.
     */
    public StandardBloomFilter getGate() {
        return gate;
    }

    /**
     * Get the filter configuration for the gating filter.
     *
     * @return the gating filter configuration.
     */
    public FilterConfig getConfig() {
        return gateConfig;
    }

    /**
     * Get the collection stats for this collection.
     *
     * @return the collections stats.
     */
    public CollectionStats getStats() {
        return collectionStats;
    }

    /**
     * Merge a proto filter into the gating filter.
     *
     * @param proto the protofilter to merge.
     */
    public synchronized void merge(ProtoBloomFilter proto) {
        merge(new StandardBloomFilter(proto, gateConfig));
    }

    /**
     * Merge a bloom filter into the gating filter.
     *
     * @param filter the bloom filter to merge.
     */
    public synchronized void merge(BloomFilter filter) {
        if (!gate.inverseMatch(filter)) {
            gate = gate.merge(filter);
        }
        collectionStats.insert();
    }

    /**
     * Clear the bloom filter and the statistics.
     */
    public synchronized void clear() {
        gate = StandardBloomFilter.EMPTY;
        collectionStats.clear();
    }
}
