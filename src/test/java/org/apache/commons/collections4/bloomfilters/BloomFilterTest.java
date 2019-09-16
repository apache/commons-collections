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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

public class BloomFilterTest {

	@Test
	public void getLogTest() {
		BitSet bs = new BitSet();
		bs.set(2);
		BloomFilter bf = new BloomFilter(bs);
		assertEquals(2.0, bf.getLog(), 0.0000001);

		bs.set(20);
		bf = new BloomFilter(bs);
		assertEquals(20.000003814697266, bf.getLog(), 0.000000000000001);

		bs.set(40);
		bs.clear(20);
		// show it is approximate bit 40 and bit 2 yeilds same as 40 alone
		bf = new BloomFilter(bs);

		assertEquals(40.0, bf.getLog(), 0.000000000000001);

	}

	@Test
	public void getHammingWeight() {
		BitSet bs = new BitSet();
		bs.set(2);
		BloomFilter bf = new BloomFilter(bs);
		assertEquals(1, bf.getHammingWeight());

		bs.set(20);
		bf = new BloomFilter(bs);
		assertEquals(2, bf.getHammingWeight());

		bs.set(40);
		bf = new BloomFilter(bs);
		assertEquals(3, bf.getHammingWeight());

	}

	@Test
	public void showBitSetChangeNotAffectFilter() {
		BitSet bs = new BitSet();
		bs.set(2);
		BloomFilter bf = new BloomFilter(bs);
		assertEquals(1, bf.getHammingWeight());
		// changing the bit set after constructor does not impact filter.
		bs.set(20);
		assertEquals(1, bf.getHammingWeight());
	}

	@Test
	public void distanceTest_Not0() {
		BitSet bs = new BitSet();
		bs.set(2);
		BloomFilter bf1 = new BloomFilter(bs);
		bs.set(4);
		BloomFilter bf2 = new BloomFilter(bs);

		assertEquals(1, bf1.distance(bf2));
		assertEquals(1, bf2.distance(bf1));

		// show now shared bits
		bs = new BitSet();
		bs.set(4);
		bf2 = new BloomFilter(bs);
		assertEquals(2, bf1.distance(bf2));
		assertEquals(2, bf2.distance(bf1));

	}

	@Test
	public void distanceTest_0() {
		BitSet bs = new BitSet();
		bs.nextSetBit(2);
		BloomFilter bf1 = new BloomFilter(bs);
		BloomFilter bf2 = new BloomFilter(bs);

		assertEquals(0, bf1.distance(bf2));
		assertEquals(0, bf2.distance(bf1));
	}

	@Test
	public void equalityTest() {
		BitSet bs = new BitSet();
		bs.nextSetBit(2);
		BloomFilter bf1 = new BloomFilter(bs);
		BloomFilter bf2 = new BloomFilter(bs);

		assertEquals(bf1, bf2);
		assertEquals(0, bf1.distance(bf2));

		bs.set(40);
		bs.clear(2);
		bf2 = new BloomFilter(bs);
		assertNotEquals(bf1, bf2);
	}

	@Test
	public void matchTest() {
		BitSet bs = new BitSet();
		bs.nextSetBit(2);
		BloomFilter bf1 = new BloomFilter(bs);

		bs.set(40);
		BloomFilter bf2 = new BloomFilter(bs);
		assertFalse(bf2.match(bf1));
		assertTrue(bf1.match(bf2));
	}

	@Test
	public void inverseMatchTest() {
		BitSet bs = new BitSet();
		bs.nextSetBit(2);
		BloomFilter bf1 = new BloomFilter(bs);

		bs.set(40);
		BloomFilter bf2 = new BloomFilter(bs);
		assertTrue(bf2.inverseMatch(bf1));
		assertFalse(bf1.inverseMatch(bf2));
	}

	@Test
	public void mergeTest() {
		BitSet bs = new BitSet();
		bs.nextSetBit(2);
		BloomFilter bf1 = new BloomFilter(bs);

		bs.set(40);
		BloomFilter bf2 = new BloomFilter(bs);

		BloomFilter bf3 = bf1.merge(bf2);

		assertTrue(bf1.match(bf3));
		assertTrue(bf2.match(bf3));

	}
}
