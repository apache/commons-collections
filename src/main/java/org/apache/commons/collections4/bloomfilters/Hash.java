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
import java.util.Objects;


/**
 * A Bloom Filter hash calculation.  This class only stores the result of an
 * external hashs calculation.  It does not perform the calculation itself.
 * 
 * The hash is calculated as a 128-bit value. We store this as two 64-bit
 * values. We can then rapidly calculate the bloom filter for any given
 * configuration.
 *
 */
public class Hash implements Comparable<Hash>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5511398264986350484L;
	private long h1;
	private long h2;
	private transient Integer hashCode;

	public Hash(long h1, long h2) {
		this.h1 = h1;
		this.h2 = h2;
	}

	public BitSet populate(BitSet set, FilterConfig config) {
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

	public long h1() {
		return h1;
	}

	public long h2() {
		return h2;
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