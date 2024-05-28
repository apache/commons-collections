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
 * Collects extensible Bloom filter classes and interfaces.
 *
 * <h2>Background:</h2>
 *
 * <p>The Bloom filter is a probabilistic data structure that indicates where things are not. Conceptually it is a bit
 * vector or BitMap. You create a Bloom filter by creating hashes and converting those to enabled bits in the map. Multiple
 * Bloom filters may be merged together into one Bloom filter. It is possible to test if a filter {@code B} has merged
 * into another filter {@code A} by verifying that {@code (A & B) == B}.</p>
 *
 * <p>Bloom filters are generally used where hash tables would be too large, or as a filter front end for longer processes.
 * For example most browsers have a Bloom filter that is built from all known bad URLs (ones that serve up malware).
 * When you enter a URL the browser builds a Bloom filter and checks to see if it is "in" the bad URL filter. If not the
 * URL is good, if it matches, then the expensive lookup on a remote system is made to see if it actually is in the
 * list. There are lots of other uses, and in most cases the reason is to perform a fast check as a gateway for a longer
 * operation.</p>
 *
 * <p>Some Bloom filters (e.g. CountingBloomFilter) use counters rather than bits. In this case each counter
 * is called a {@code cell}.</p>
 *
 * <h3>BloomFilter</h3>
 *
 * <p>The Bloom filter architecture here is designed for speed of execution, so some methods like {@code merge}, {@code remove},
 * {@code add}, and {@code subtract} may throw exceptions. Once an exception is thrown the state of the Bloom filter is unknown.
 * The choice to use not use atomic transactions was made to achieve maximum performance under correct usage.</p>
 *
 * <h4>Nomenclature</h4>
 *
 * <ul>
 *     <li>BitMap - In the bloomfilter package a BitMap is not a structure but a logical construct.  It is conceptualized
 *     as an ordered collection of {@code long} values each of which is interpreted as the enabled true/false state of 64 continuous indices.  The mapping of
 *     bits into the {@code long} values is described in the {@code BitMaps} javadoc.</li>
 *
 *     <li>Index - In the bloomfilter package an Index is a logical collection of {@code int}s specifying the enabled
 *     bits in the BitMap.</li>
 *
 *     <li>Cell - Some Bloom filters (e.g. CountingBloomFilter) use counters rather than bits.  In the bloomfilter package
 *     Cells are pairs of ints representing an index and a value.  They are not {@code Pair} objects.  </li>
 *
 *     <li>Extractor - The Extractors are {@code FunctionalInterfaces} that are conceptually iterators on a {@code BitMap}, an {@code Index}, or a
 *     collection of {@code Cell}s, with an early termination switch.  Extractors have
 *     names like {@code BitMapExtractor} or {@code IndexExtractor} and have a {@code processXs} methods that take a
 *     {@code Predicate<X>} argument (e.g. {@code processBitMaps(LongPredicate)} or {@code processIndicies(IntPredicate)}).
 *     That predicate is expected to process each of the Xs in turn and return {@code true} if the processing should continue
 *     or {@code false} to stop it. </li>
 * </ul>
 *
 * <p>There is an obvious association between the BitMap and the Index, as defined above, in that if bit 5 is enabled in the
 * BitMap than the Index must contain the value 5.</p>
 *
 *
 * <h4>Implementation Notes</h4>
 *
 * <p>The architecture is designed so that the implementation of the storage of bits is abstracted. Rather than specifying a
 * specific state representation we require that all Bloom filters implement the BitMapExtractor and IndexExtractor interfaces,
 * Counting-based Bloom filters implement {@code CellExtractor} as well.  There are static
 * methods in the various Extractor interfaces to convert from one type to another.</p>
 *
 * <p>Programs that utilize the Bloom filters may use the {@code BitMapExtractor} or {@code IndexExtractor} to retrieve
 * or process a representation of the internal structure.
 * Additional methods are available in the {@code BitMaps} class to assist in manipulation of BitMap representations.</p>
 *
 * <p>The Bloom filter is an interface that requires implementation of 9 methods:</p>
 * <ul>
 * <li>{@link BloomFilter#cardinality()} returns the number of bits enabled in the Bloom filter.</li>
 *
 * <li>{@link BloomFilter#characteristics()} which returns an integer of characteristics flags.</li>
 *
 * <li>{@link BloomFilter#clear()} which resets the Bloomfilter to its initial empty state.</li>
 *
 * <li>{@link BloomFilter#contains(IndexExtractor)} which returns true if the bits specified by the indices generated by
 * IndexExtractor are enabled in the Bloom filter.</li>
 *
 * <li>{@link BloomFilter#copy()} which returns a fresh copy of the bitmap.</li>
 *
 * <li>{@link BloomFilter#getShape()} which returns the shape the Bloom filter was created with.</li>
 *
 * <li>{@link BloomFilter#merge(BitMapExtractor)} which merges the BitMaps from the BitMapExtractor into the internal
 * representation of the Bloom filter.</li>
 *
 * <li>{@link BloomFilter#merge(IndexExtractor)} which merges the indices from the IndexExtractor into the internal
 * representation of the Bloom filter.</li>
 * </ul>
 *
 * <p>Other methods should be implemented where they can be done so more efficiently than the default implementations.</p>
 *
 * <h3>CountingBloomFilter</h3>
 *
 * <p>The counting Bloom filter extends the Bloom filter by counting the number of times a specific bit has been
 * enabled or disabled. This allows the removal (opposite of merge) of Bloom filters at the expense of additional
 * overhead.</p>
 *
 * <h3>LayeredBloomFilter</h3>
 *
 * <p>The layered Bloom filter extends the Bloom filter by creating layers of Bloom filters that can be queried as a single
 * Filter or as a set of filters.  This adds the ability to perform windowing on streams of data.</p>
 *
 * <h3>Shape</h3>
 *
 * <p>The Shape describes the Bloom filter using the number of bits and the number of hash functions.  It can be specified
 * by the number of expected items and desired false positive rate.</p>
 *
 * <h3>Hasher</h3>
 *
 * <p>A Hasher converts bytes into a series of integers based on a Shape. Each hasher represents one item being added
 * to the Bloom filter.</p>
 *
 * <p>The EnhancedDoubleHasher uses a combinatorial generation technique to create the integers. It is easily
 * initialized by using a byte array returned by the standard {@code MessageDigest} or other hash function to
 * initialize the Hasher. Alternatively a pair of a long values may also be used.</p>
 *
 * <p>Other implementations of the Hasher are easy to implement, and should make use of the {@code Hasher.Filter}
 * and/or {@code Hasher.FileredIntConsumer} classes to filter out duplicate indices when implementing
 * {@code Hasher.uniqueIndices(Shape)}.</p>
 *
 * <h2>References</h2>
 *
 * <ol>
 * <li>https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf</li>
 * <li>https://github.com/apache/cassandra/blob/trunk/src/java/org/apache/cassandra/utils/BloomFilter.java#L60</li>
 * </ol>
 *
 * @since 4.5
 */
package org.apache.commons.collections4.bloomfilter;
