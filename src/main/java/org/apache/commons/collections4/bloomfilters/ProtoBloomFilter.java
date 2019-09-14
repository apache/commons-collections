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
package org.apache.commons.collections4.bloomfilters;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A prototypical bloom filter definition.
 * 
 * This is the information necessary to create a concrete bloom filter given a
 * filter configuration.
 *
 * The construction of the ProtoBloomFilter is far more compute expensive than
 * making the concrete bloom filter from the proto filter.
 * 
 * The proto bloom filter contains one hash for each item that was hashed to the
 * proto filter.  
 *
 */
public class ProtoBloomFilter implements Comparable<ProtoBloomFilter>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1293273984765868571L;
	private Set<Hash> hashes;
	private transient Integer hashCode;

	/**
	 * Constructor
	 * 
	 * @param hashes the two longs that were created by the murmur hash function.
	 */
	public ProtoBloomFilter(Set<Hash> hashes) {
		this.hashes = new TreeSet<Hash>();
		this.hashes.addAll(hashes);
	}

	/**
	 * Create a concrete bloom filter from this proto type given the filter
	 * configuration.
	 * 
	 * @param cfg The filter configuration to use.
	 * @return the Concreate Bloom Filter.
	 */
	public final BloomFilter create(FilterConfig cfg) {
		BitSet set = new BitSet(cfg.getNumberOfBits());
		for (Hash hash : hashes) {
			hash.populate(set, cfg);
		}
		return new BloomFilter(set);
	}

	/**
	 * Get a list of the hashes that this proto bloom filter uses.
	 * 
	 * @return the list of hashes.
	 */
	public Set<Hash> getHashes() {
		return Collections.unmodifiableSet(hashes);
	}
	
	/**
	 * Get the number of hashed items included in this proto bloom filter.
	 * 
	 * @return The number of items in this proto filter.
	 */
	public int getItemCount() {
		return hashes.size();
	}

	@Override
	public int hashCode() {
		if (hashCode == null) {
			hashCode = Objects.hash( hashes );			
		}
		return hashCode.intValue();
	}

	@Override
	public int compareTo(ProtoBloomFilter other) {
		Iterator<Hash> otherIter = other.hashes.iterator();
		Iterator<Hash> iter = hashes.iterator();
		int result;
		while (iter.hasNext() && otherIter.hasNext()) {
			result = iter.next().compareTo(otherIter.next());
			if (result != 0) {
				return result;
			}
		}
		return (otherIter.hasNext()) ? -1 : 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProtoBloomFilter) {
			return compareTo((ProtoBloomFilter) o) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("ProtoBloomFilter[ %s, %s]", hashes.size(), hashCode());
	}

}
