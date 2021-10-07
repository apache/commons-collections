/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A collection of extensible Bloom filter classes and interfaces.
 *
 * <h2>
 *  Background:</h2>
 *  <p>
 * The Bloom filter is a probabilistic data structure that indicates where things are not.
 * Conceptually it is a a bit vector. You create a Bloom filter by creating hashes
 * and converting those to enabled bits in the vector. Multiple Bloom filters may be merged
 * together into one Bloom filter.  It is possible to test if a filter @{code B} as merged into
 * another filter @{code A} by verifying that @{code (A & B) == B}.
 * </p>
 * <p>Bloom filters are generally used where hash
 * tables would be too large, or as a filter front end for longer processes. For example
 * most browsers have a Bloom filter that is built from all known bad URLs (ones that
 * serve up malware). When you enter a URL the browser builds a Bloom filter and checks to
 * see if it is "in" the bad URL filter. If not the URL is good, if it matches, then the
 * expensive lookup on a remote system is made to see if it actually is in the list. There
 * are lots of other uses, and in most cases the reason is to perform a fast check as a
 * gateway for a longer operation. </p>
 *  <h3>
 *  BloomFilter</h3>
 *   *  <p>
 *  The bloom filter code is
 * an interface that requires implementation of 6 methods: <ul>
 * <li>
 *  @{code cardinality()}
 * returns the number of bits enabled in the Bloom filter.</li>
 * <li>
 *  @{code contains(BitMapProducer)} which
 * returns true if the bits specified by the BitMaps generated by the BitMapProducer are enabled in the Bloom filter.</li>
 *  <li>
 *  @{code contains(IndexProducer)} which
 * returns true if the bits specified by the Indices generated  by IndexProducer are enabled in the Bloom filter.</li>
 * <li>
 *  @{code getShape()} which
 * returns shape the Bloom filter was created with.</li>
 * <li>
 *  @{code isSparse()} which
 * returns true if an the implementation tracks indices natively, false if BitMaps are used.  In cases where
 * neither are used the @{code isSparse} return value should reflect which is faster to produce.</li>
 * <li>
 *  @{code mergeInPlace(BloomFilter)} which
 * utilizes either the @{code BitMapProducer} or @{code IndexProducer} from the argument to enable extra bits
 * in the internal representation of the Bloom filter..</li>
 * </ul>
 * </p><p>
 * Other methods should be implemented where they can be done so more efficiently than the default implementations.
 * </p>
 *
 * <3>CountingBloomFilter</h3>
 * <p>The counting bloom filter extends the Bloom filter by counting the number of times a specific bit has been
 * enabled or disabled.  This allows the removal (opposite of merge) of Bloom filters at the expense of additional
 * overhead.</p>
 *  <li>
 *  HasherBloomFilter - implements bloom
 * filter on a Hasher. A rather slow implementation but convenient in some
 * situations.</li>
 *  </ul>
 *
 *  <h3>
 *  Shape</h3>
 *  <p>
 *  The Shape describes the Bloom filter using the number of bits and the number of hash functions</p>
 *
 *  <h3>
 *  Hasher</h3>
 *  <p>
 *  A Hasher converts bytes into an series of integers based on a Shape.  With the exception of the HasherCollecton,
 *  Each hasher represents one item being added to the Bloom filter.  The HasherCollection represents the
 *  number of items as the sum of the number of items represented by Hashers in the collection.</p>
 *  <p>The SimpleHasher uses a combinatorial generation technique to create the integers. It is easily
 *  initialized by using a standard @{code MessageDigest} or other Hash function to hash the item to insert and
 *  then splitting the hash bytes in half and considering each as a long value.
 *  Other implementations of the Hasher are easy to implement.</p>
 *
 * <h2>References</h2>
 *
 * <ol>
 * <li> https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf</li>
 * <li> https://github.com/apache/cassandra/blob/trunk/src/java/org/apache/cassandra/utils/BloomFilter.java#L60</li>
 * </ol>
 *
 * @since 4.5
 */
package org.apache.commons.collections4.bloomfilter;
