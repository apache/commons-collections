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

import org.apache.commons.collections4.bloomfilter.AbstractBloomFilter;
import org.apache.commons.collections4.bloomfilter.BloomFilterConfiguration;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter;
import org.apache.commons.collections4.bloomfilter.StandardBloomFilter;

/**
 * The configuration of a gated Bloom collection.
 * @since 4.5
 */
public class BloomFilterGatedConfiguration {
    /**
     * The configuration for the gating BloomFilter.
     */
    private final BloomFilterConfiguration gateConfig;
    /**
     * The gating BloomFilter.
     */
    private AbstractBloomFilter gate;
    /**
     * The collection statistics.
     */
    private BloomFilterGatedStatistics collectionStats;

    /**
     * Construct a collection configuration with the specified gate configuration.
     *
     * @param gateConfig the configuration for the gating filter.
     */
    public BloomFilterGatedConfiguration(BloomFilterConfiguration gateConfig) {
        this.gateConfig = gateConfig;
        this.gate = StandardBloomFilter.EMPTY;
        this.collectionStats = new BloomFilterGatedStatistics();
    }

    /**
     * Gets the gate Bloom filter.
     *
     * @return the gating Bloom filter.
     */
    public AbstractBloomFilter getGate() {
        return gate;
    }

    /**
     * Gets the filter configuration for the gating filter.
     *
     * @return the gating filter configuration.
     */
    public BloomFilterConfiguration getGateConfig() {
        return gateConfig;
    }

    /**
     * Gets the collection stats for this collection.
     *
     * @return the collections stats.
     */
    public BloomFilterGatedStatistics getStats() {
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
     * Merges a Bloom filter into the gating filter.
     *
     * @param filter the Bloom filter to merge.
     */
    public synchronized void merge(AbstractBloomFilter filter) {
        if (!gate.inverseMatches(filter)) {
            gate = gate.merge(filter);
        }
        collectionStats.insert();
    }

    /**
     * Clear the Bloom filter and the statistics.
     */
    public synchronized void clear() {
        gate = StandardBloomFilter.EMPTY;
        collectionStats.clear();
    }
}
