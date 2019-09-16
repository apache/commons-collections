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

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
 * Concrete implementations of BloomFilter can be built from the
 * ProtoBloomFilter.
 *
 */
public final class ProtoBloomFilter implements Comparable<ProtoBloomFilter> {

	private final Set<Hash> hashes;
	private transient Integer hashCode;

	/* package private for testing */
	/**
	 * Constructor
	 * 
	 * @param hashes the two longs that were created by the murmur hash function.
	 */
	ProtoBloomFilter(Collection<Hash> hashes) {
		this.hashes = new TreeSet<Hash>();
		this.hashes.addAll( hashes );
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
	
	private Object writeReplace() {
		return new ProtoSerProxy(this);
	}
	
	/**
	 * Get the number of unique hashed items included in this proto bloom filter.
	 * 
	 * @return The number of unique items in this proto filter.
	 */
	public final int getItemCount() {
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
		return (otherIter.hasNext()) ? -1 : 0;
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

	/**
	 * A Serialization proxy for a ProtoBloomFilter.
	 *
	 */
	private static class ProtoSerProxy implements Serializable {
		private Hash[] hashes;
		
		ProtoSerProxy(ProtoBloomFilter protoFilter) {
			this.hashes = protoFilter.hashes.toArray( new Hash[protoFilter.hashes.size()] );
		}
		
		private Object readResolve() {
			return new ProtoBloomFilter( Arrays.asList( hashes ) );
		}
	}
	
	/**
	 * A Bloom Filter hash calculation.  This class only stores the result of an
	 * external hash calculation.  It does not perform the calculation itself.
	 * 
	 * The hash is calculated as a 128-bit value. We store this as two 64-bit
	 * values. We can then rapidly calculate the bloom filter for any given
	 * configuration.
	 *
	 */
	/* package private for testing */
	 final static class Hash implements Comparable<Hash> {

		private final long h1;
		private final long h2;
		
		private transient Integer hashCode;

		Hash(long h1, long h2) {
			this.h1 = h1;
			this.h2 = h2;
		}

		BitSet populate(BitSet set, FilterConfig config) {
			if (set.size() < config.getNumberOfBits()) {
				throw new IllegalArgumentException(
						String.format("Bitset had %s bits, %s required", set.size(), config.getNumberOfBits()));
			}
			for (int i = 0; i < config.getNumberOfHashFunctions(); i++) {
				int j = Math.abs((int) ((h1 + (i * h2)) % config.getNumberOfBits()));
				set.set(j, true);
			}
			return set;
		}

		@Override
		public int compareTo(Hash other) {
			int result = Long.compare(h1, other.h1);
			if (result == 0) {
				result = Long.compare(h2, other.h2);
			}
			return result;
		}

		@Override
		public int hashCode() {
			if (hashCode == null) {
				hashCode = Objects.hash(h1,h2);
			}
			return hashCode.intValue();

		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Hash) {
				Hash other = (Hash) o;
				return h1 == other.h1 && h2 == other.h2;
			}
			return false;
		}

		@Override
		public String toString() {
			return String.format("Hash[ %s %s ]", Long.toHexString(h1), Long.toHexString(h2));
		}

	}
	
	/**
	 * A ProtoBloomFilter builder.
	 * 

	 * 
	 * A bloom filter may contain one or more items hashed together to make the filter.
	 * 
	 * There are two ways to hash the properties of objects.  
	 * <ol>
	 * <li>One is to create a buffer containing all the properties and hash that.  This means that 
	 * the search for the object must construct the same filter and search for it.  It is not 
	 * possible to locate an object by a partial property match.  In this case each object is counted
	 * as a single item as specified in the FilterConfig. 
	 * </li>
	 * <li>The other is to hash each item separately.  In this case each of the properties can be 
	 * searched for individually.  However, in this case the number of items that should be specified
	 * in the FilterConfig is the sum of the cardinality of the properties being hashed.
	 * </li>
	 * </ol>
	 */
	public static final class Builder {
		private Set<Hash> hashes;

		/**
		 * Constructor.
		 * 
		 */
		public Builder() {
			hashes = new HashSet<Hash>();
		}

		/**
		 * Add the proto bloom filter to this proto bloom filter. The items included in
		 * the parameter bloom filter are added to the filter being built without modification.
		 * 
		 * @param protoBloomFilter The proto bloom filter to add.
		 * @return this for chaining
		 */
		public Builder update(ProtoBloomFilter protoBloomFilter) {
			hashes.addAll(protoBloomFilter.hashes);
			return this;
		}

		/**
		 * Add the byte buffer to the proto bloom filter as a new hashed value.
		 * 
		 * @param buffer The buffer to hash.
		 * @return The ProtoBloomFilterBuilder for chaining.
		 */
		public Builder update(ByteBuffer buffer) {
			hashes.add(hash3_x64_128(buffer, 0, buffer.limit(), 0L));
			return this;
		}

		/**
		 * Add the byte to the proto bloom filter filter as a new hashed value.
		 * 
		 * @param b The byte to add.
		 * @return this for chaining
		 */
		public Builder update(byte b) {
			return update(ByteBuffer.wrap(new byte[] { b }));
		}

		/**
		 * Add the bytes from the string to the proto bloom filter as a new hashed value.
		 * 
		 * The bytes are interpreted as UTF-8 chars.
		 * 
		 * @param string The string to add.
		 * @return this for chaining
		 */
		public Builder update(String string) {
			return update(string.getBytes(StandardCharsets.UTF_8));
		}

		/**
		 * Add a byte array to the proto bloom filter filter as a new hashed value.
		 * 
		 * @param buffer The buffer to add.
		 * @return this for chaining
		 */
		public Builder update(byte[] buffer) {
			return update(ByteBuffer.wrap(buffer));
		}

		/**
		 * Build the ProtoBloomFilter.
		 * 
		 * @return the defined ProtoBloomFilter.
		 */
		public ProtoBloomFilter build() {
			try {
				return new ProtoBloomFilter(hashes);
			} finally {
				hashes.clear();
			}
		}

		/**
		 * Add the proto bloom filter to the filter as a new hash and build it.
		 * 
		 * This is a convenience method for update(protoBloomFilter).build()
		 * 
		 * @param protoBloomFilter The proto bloom filter to add.
		 * @return the defined ProtoBloomFilter.
		 */
		public ProtoBloomFilter build(ProtoBloomFilter protoBloomFilter) {
			hashes.addAll(protoBloomFilter.hashes);
			return build();
		}

		/**
		 * Add the byte buffer to the proto bloom filter as a new hash and build it.
		 * 
		 * This is a convenience method for update(buffer).build()
		 * 
		 * @param buffer The buffer to add.
		 * @return the defined ProtoBloomFilter.
		 */
		public ProtoBloomFilter build(ByteBuffer buffer) {
			return update(buffer).build();
		}

		/**
		 * Add the byte to the proto bloom filter as a new hash and build it.
		 * 
		 * This is a convenience method for update(b).build()
		 * 
		 * @param b The byte to add.
		 * @return the defined ProtoBloomFilter.
		 */
		public ProtoBloomFilter build(byte b) {
			return update(b).build();
		}

		/**
		 * Add the bytes from the string to the proto bloom filter as a new hash and build it.
		 * 
		 * The bytes are interpreted as UTF-8 chars.
		 * 
		 * This is a convenience method for update(string).build()
		 * 
		 * @param string The string to add.
		 * @return the defined ProtoBloomFilter.
		 */
		public ProtoBloomFilter build(String string) {
			return update(string).build();
		}

		/**
		 * Add the byte array to the proto bloom filter as a new hash and build it.
		 * 
		 * This is a convenience method for update(bufer).build()
		 * 
		 * @param pbf The proto bloom filter to add.
		 * @return the defined ProtoBloomFilter.
		 */
		public ProtoBloomFilter build(byte[] buffer) throws IOException {
			return update(buffer).build();
		}

		/**************************************
		 * Methods to perform murmur 128 hash.
		 **************************************/
		private long getblock(ByteBuffer key, int offset, int index) {
			int i_8 = index << 3;
			int blockOffset = offset + i_8;
			return ((long) key.get(blockOffset + 0) & 0xff) + (((long) key.get(blockOffset + 1) & 0xff) << 8)
					+ (((long) key.get(blockOffset + 2) & 0xff) << 16) + (((long) key.get(blockOffset + 3) & 0xff) << 24)
					+ (((long) key.get(blockOffset + 4) & 0xff) << 32) + (((long) key.get(blockOffset + 5) & 0xff) << 40)
					+ (((long) key.get(blockOffset + 6) & 0xff) << 48) + (((long) key.get(blockOffset + 7) & 0xff) << 56);
		}

		private long rotl64(long v, int n) {
			return ((v << n) | (v >>> (64 - n)));
		}

		private long fmix(long k) {
			k ^= k >>> 33;
			k *= 0xff51afd7ed558ccdL;
			k ^= k >>> 33;
			k *= 0xc4ceb9fe1a85ec53L;
			k ^= k >>> 33;
			return k;
		}

		private Hash hash3_x64_128(ByteBuffer key, int offset, int length, long seed) {
			final int nblocks = length >> 4; // Process as 128-bit blocks.
			long h1 = seed;
			long h2 = seed;
			long c1 = 0x87c37b91114253d5L;
			long c2 = 0x4cf5ad432745937fL;
			// ----------
			// body
			for (int i = 0; i < nblocks; i++) {
				long k1 = getblock(key, offset, i * 2 + 0);
				long k2 = getblock(key, offset, i * 2 + 1);
				k1 *= c1;
				k1 = rotl64(k1, 31);
				k1 *= c2;
				h1 ^= k1;
				h1 = rotl64(h1, 27);
				h1 += h2;
				h1 = h1 * 5 + 0x52dce729;
				k2 *= c2;
				k2 = rotl64(k2, 33);
				k2 *= c1;
				h2 ^= k2;
				h2 = rotl64(h2, 31);
				h2 += h1;
				h2 = h2 * 5 + 0x38495ab5;
			}
			// ----------
			// tail
			// Advance offset to the unprocessed tail of the data.
			offset += nblocks * 16;
			long k1 = 0;
			long k2 = 0;
			switch (length & 15) {
			case 15:
				k2 ^= ((long) key.get(offset + 14)) << 48;
			case 14:
				k2 ^= ((long) key.get(offset + 13)) << 40;
			case 13:
				k2 ^= ((long) key.get(offset + 12)) << 32;
			case 12:
				k2 ^= ((long) key.get(offset + 11)) << 24;
			case 11:
				k2 ^= ((long) key.get(offset + 10)) << 16;
			case 10:
				k2 ^= ((long) key.get(offset + 9)) << 8;
			case 9:
				k2 ^= ((long) key.get(offset + 8)) << 0;
				k2 *= c2;
				k2 = rotl64(k2, 33);
				k2 *= c1;
				h2 ^= k2;
			case 8:
				k1 ^= ((long) key.get(offset + 7)) << 56;
			case 7:
				k1 ^= ((long) key.get(offset + 6)) << 48;
			case 6:
				k1 ^= ((long) key.get(offset + 5)) << 40;
			case 5:
				k1 ^= ((long) key.get(offset + 4)) << 32;
			case 4:
				k1 ^= ((long) key.get(offset + 3)) << 24;
			case 3:
				k1 ^= ((long) key.get(offset + 2)) << 16;
			case 2:
				k1 ^= ((long) key.get(offset + 1)) << 8;
			case 1:
				k1 ^= (key.get(offset));
				k1 *= c1;
				k1 = rotl64(k1, 31);
				k1 *= c2;
				h1 ^= k1;
			}
			;
			// ----------
			// finalization
			h1 ^= length;
			h2 ^= length;
			h1 += h2;
			h2 += h1;
			h1 = fmix(h1);
			h2 = fmix(h2);
			h1 += h2;
			h2 += h1;
			return new Hash(h1, h2);
		}

	}
}
