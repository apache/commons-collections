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
 *  A Bloom filter is conceptually a bit vector. It is used to
 * tell you where things are not. Basically, you create a Bloom filter by creating hashes
 * and converting those to enabled bits in a vector. You can merge the Bloom filters
 * together with logical "or" (call this filter "B"). You can then check to see if filter
 * "A" was "or"ed into "B" by testing A &amp; B == A. if the statement is false then "A" was
 * not merged into "B", otherwise it _might_ have. They are generally used where hash
 * tables would be too large or as a filter front end for longer processes. For example
 * most browsers have a Bloom filter that is built from all known bad URLs (ones that
 * serve up malware). When you enter a URL the browser builds a Bloom filter and checks to
 * see if it is "in" the bad URL filter. If not the URL is good, if it matches, then the
 * expensive lookup on a remote system is made to see if it actually is in the list. There
 * are lots of other uses, and in most cases the reason is to perform a fast check as a
 * gateway for a longer operation. </p>
 *  <h3>
 *  BloomFilter</h3>
 *  <p>
 *  The bloom filter code is
 * an abstract class that requires implementation of 4 methods: <ul>
 *  <li>
 *  getBits() which
 * returns the set bits as a buffer encoded into an array of long.</li>
 *  <li>
 *  getHasher()
 * which returns a list of integers that are indexes of the bits that are enabled. These
 * are returned in a Hasher construct.</li>
 *  <li>
 *  merge( BloomFilter ) to merge another
 * Bloom filter into this one.</li>
 *  <li>
 *  merge( Hasher ) to merge the values in a hasher
 * into this Bloom filter.</li>
 *  </ul>
 *  There are 3 implementations of Bloom filter
 * provided: <ul>
 *  <li>
 *  BitSetBloomFilter - based on the Java BitSet class.</li>
 *  <li>
 *
 * CountingBloomFilter - uses a sparse array of integers (Map) to implement a counting
 * Bloom filter. This filter also implements remove() methods as that is the great
 * advantage of a counting Bloom filter.</li>
 *  <li>
 *  HasherBloomFilter - implements bloom
 * filter on a Hasher. A rather slow implementation but convenient in some
 * situations.</li>
 *  </ul>
 *
 *  <h3>
 *  Shape</h3>
 *  <p>
 *  Describes the Bloom filter using the
 * standard number of bits, number of hash functions and number of items along with a
 * description of the HashFunction. It is this description that has caused the most issues
 * of late. </p>
 *  <h3>
 *  Hasher</h3>
 *  <p>
 *  converts byte buffers into an iterator if int based
 * on a Shape. There are 2 implementations of Hasher provided <ul>
 *  <li>
 *  Dynamic - calls
 * the HashFunction for each value required in the Bloom filter.</li>
 *  <li>
 *  Static - based
 * on a pre-calculated list of Bloom filter index values. It is also limited to generating
 * values for a specific Shape.</li>
 *  </ul>
 *
 *  <h3>
 *  Hash Functions</h3>
 *  <p>
 *  Hash
 * functions generate individual index values for the filter from a byte buffer. There are
 * four implementations provided. </p>
 *  <h3>
 *  HashFunctionIdentity</h3>
 *  <p>
 *  The
 * HashFunctionIdentity is the base interface for the HashFunction. It tracks three (3)
 * properties: <ul>
 *  <li>
 *  The Hashing algorithm</li>
 *  <li>
 *  Whether the contents of the
 * resulting hash buffer are read as signed or unsigned values.</li>
 *  <li>
 *  Whether the hash
 * function uses an iterative or cyclic method. In traditional iterative methods this is
 * done by calling the selected hash function with a different seed for each hash
 * required. The second method described by Adam Kirsch and Micheal Mitzenmacher[1] has
 * become more common and is used in applications like Cassandra[2].</li>
 *  </ul>
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
