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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * A ProtoBloomFilter builder.
 * 
 * Concrete implementations of BloomFilter can be built from the
 * ProtoBloomFilter.
 *
 */
public class ProtoBloomFilterBuilder {
	private Set<Hash> hashes;

	/**
	 * Constructor.
	 * 
	 */
	public ProtoBloomFilterBuilder() {
		hashes = new HashSet<Hash>();
	}

	/**
	 * Add the proto bloom filter the the proto bloom filter.
	 * 
	 * @param pbf The proto bloom filter to add.
	 * @return this for chaining
	 */
	public ProtoBloomFilterBuilder update(ProtoBloomFilter pbf) {
		hashes.addAll(pbf.getHashes());
		return this;
	}

	/**
	 * Add the byte buffer to the proto bloom filter.
	 * 
	 * @param buffer The buffer to add.
	 * @return The ProtoBloomFilterBuilder for chaining
	 * 
	 */
	public ProtoBloomFilterBuilder update(ByteBuffer buffer) {
		hashes.add(hash3_x64_128(buffer, 0, buffer.limit(), 0L));
		return this;
	}

	/**
	 * Add the byte to the proto bloom filter filter.
	 * 
	 * @param b The byte to add.
	 * @return this for chaining
	 */
	public ProtoBloomFilterBuilder update(byte b) {
		return update(ByteBuffer.wrap(new byte[] { b }));
	}

	/**
	 * Add the bytes from the string to the proto bloom filter.
	 * 
	 * The bytes are interpreted as UTF-8 chars.
	 * 
	 * @param string The string to add.
	 * @return this for chaining
	 */
	public ProtoBloomFilterBuilder update(String string) {
		return update(string.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Add the byte buffer to the proto bloom filter filter.
	 * 
	 * @param buffer The buffer to add.
	 * @return this for chaining
	 */
	public ProtoBloomFilterBuilder update(byte[] buffer) {
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
	 * Add the proto bloom filter to the filter and build it.
	 * 
	 * @param pbf The proto bloom filter to add.
	 * @return the defined ProtoBloomFilter.
	 */
	public ProtoBloomFilterBuilder build(ProtoBloomFilter pbf) {
		hashes.addAll(pbf.getHashes());
		return this;
	}

	/**
	 * Add the byte buffer to the proto bloom filter and build it.
	 * 
	 * @param buffer The buffer to add.
	 * @return the defined ProtoBloomFilter.
	 */
	public ProtoBloomFilter build(ByteBuffer buffer) {
		return update(buffer).build();
	}

	/**
	 * Add the byte to the proto bloom filter filter and build it.
	 * 
	 * @param b The byte to add.
	 * @return the defined ProtoBloomFilter.
	 */
	public ProtoBloomFilter build(byte b) {
		return update(b).build();
	}

	/**
	 * Add the bytes from the string to the proto bloom filter and build it.
	 * 
	 * The bytes are interpreted as UTF-8 chars.
	 * 
	 * @param string The string to add.
	 * @return the defined ProtoBloomFilter.
	 */
	public ProtoBloomFilter build(String string) {
		return update(string).build();
	}

	/**
	 * Add the proto bloom filter the the proto bloom filter and build it.
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
