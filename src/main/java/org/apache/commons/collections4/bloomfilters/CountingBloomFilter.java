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

import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter.Hash;

/**
 * A counting bloom filter.  This bloom filter maintains a count of the number
 * of times a bit has been turned on.  This allows for removal of bloom filters
 * from the filter.
 * 
 * Instances are immutable.
 * 
 * @since 4.5
 *
 */
public final class CountingBloomFilter extends BloomFilter {
	
	// the count of entries
	/*package private for testing */
	final TreeMap<Integer,Integer> counts;


	/**
	 * Constructor.
	 * 
	 * @param protoFilter the protoFilter to build this bloom filter from.
	 * @param config the Filter configuration to use to build the bloom filter.
	 */
	public CountingBloomFilter(ProtoBloomFilter protoFilter, FilterConfig config)
	{
		super( protoFilter, config);
		int[] intArry = new int[config.getNumberOfBits()];
		
		counts = new TreeMap<Integer,Integer>();
		for (Hash hash : protoFilter.hashes) {
			for (int i : hash.getBits( config ))
			{
				intArry[i]++;
			}
		}
			
		for (int i=0;i<config.getNumberOfBits();i++)
		{
			if (intArry[i] != 0)
			{
				counts.put(i, intArry[i] );
			}
		}
	}


	/**
	 * Constructor.
	 * 
	 * A copy of the bitSet parameter is made so that the bloom filter is isolated
	 * from any further changes in the bitSet.
	 * 
	 * @param bitSet The bit set that was built by the config.
	 * @param counts the Map of set bits to counts for that bit.
	 */
	private CountingBloomFilter( BitSet bits, Map<Integer,Integer> counts)
	{
		super( bits );
		this.counts = new TreeMap<Integer,Integer>( counts );
	}

	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder( "{ ");
		for (Map.Entry<Integer,Integer> e : counts.entrySet() )
		{
			sb.append( String.format( "(%s,%s) ", e.getKey(), e.getValue()));
		}
		return sb.append( "}").toString();
	}
	
	@Override
	public int hashCode() {
		return this.counts.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof CountingBloomFilter) ? this.counts.equals(((CountingBloomFilter) other).counts) : false;
	}

	/**
	 * Merge this bloom filter with the other creating a new filter.
	 * The counts for bits that are on in the other filter are incremented.
	 * 
	 * This does not add the counts if the other is a CountingBloomFilter.
	 * 
	 * @param other the other filter.
	 * @return a new filter.
	 */
	public CountingBloomFilter merge(BloomFilter other) {
		BitSet next = (BitSet) this.bitSet.clone();
		next.or(other.bitSet);
		TreeMap<Integer,Integer> newSet = new TreeMap<Integer,Integer>( counts );
		Integer one = Integer.valueOf(1);
		other.bitSet.stream().forEach( key -> 
		{
			Integer count = newSet.get(key);
			if (count == null)
			{
				newSet.put( key, one);
			} else {
				newSet.put( key, count + 1);
			}
		} );
		return new CountingBloomFilter( next, newSet );
	}
	
	/**
	 * Decrement the counts for the bits that are on in the other BloomFilter from this one.
	 * 
	 * @param other the other filter.
	 * @return a new filter.
	 */
	public CountingBloomFilter remove(BloomFilter other) {
		BitSet next = (BitSet) this.bitSet.clone();
		TreeMap<Integer,Integer> newSet = new TreeMap<Integer,Integer>( counts );
		
		other.bitSet.stream().forEach( key -> 
		{
			Integer count = newSet.get(key);
			if (count != null)
			{
				int c = count -1;
				if (c == 0)
				{
					next.clear(key);
					newSet.remove(key);
				} else {
					newSet.put(key, c);
				}
				
			} 
		} );
		
		
		return new CountingBloomFilter( next, newSet );
	}
}
