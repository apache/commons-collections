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
/**
 * <h1>Bloom Filter Overview</h1>
 * <p>
 * First it is important to remember that Bloom filters tell you where things
 * are NOT. Second it is important to understand that Bloom filters can give
 * false positives but never false negatives. Seems kind of pointless I know but
 * consider the case where you have 10K buckets that may contain the item you
 * are looking for. If you can reduce the number of buckets you are searching
 * you can significantly speed up the search. In a case like this a bloom filter
 * could be used "in front" of each bucket as a gatekeeper. Whenever an object
 * goes in the bucket the object's bloom filter is added to the bucket bloom
 * filter. If you want to search the 10K buckets for an item then you build the
 * bloom filter for the item you are looking for and check the bloom filter on
 * each bucket. If the filter says that the item is not in the bucket then you
 * can skip that bucket, if the filter says it is in the bucket you search the
 * bucket to verify that it is not a false positive. A common use for bloom
 * filters is to determine if an expensive call should be made. For example many
 * browsers have a bloom filter that comprises all the known bad URLs (ones that
 * serve malware, etc). When the URL is entered in the browser it is checked
 * against the bloom filter. If it is not there the request goes through as
 * normal. If it is there then the browser makes the expensive lookup call to a
 * server to determine if the URL really is in the database of bad URLs.
 * </p>
 * <p>
 * So a bloom filter is generally used to front a collection to determine if the
 * collection should be searched. And as has been pointed out it doesn't make
 * much sense to use it in front of an in-memory hash table. However,
 * applications like Cassandra and Hadoop use bloom filters for various reasons.
 * Other uses for bloom filters include sharding data. There is a measure of
 * difference between filters called a hamming distance. This is the number of
 * bits that have to be "flipped" to turn one filter into another, and is very
 * similar to Hamming measures found in string and other similar comparisons. By
 * using the hamming value it is possible to distribute data among a set of
 * buckets by simply putting the value in the bucket that it is "closest" to in
 * terms of Hamming distance. Searching takes place as noted above. However this
 * has some interesting properties. For example you can add new buckets at any
 * time simply by adding an empty bucket and bloom filter to the collection of
 * buckets and the system will start filling the bucket as appropriate. In
 * addition if a bucket/shard becomes "full", where "full" is an implementation
 * dependent decision (e.g. the index on a DB table reaches the inflection point
 * where performance degradation begins), you can pull a bucket out of
 * consideration for inserts but still search it without significant stress or
 * change to the system.
 * </p>
 * <p>
 * Internally Bloom filters are bit vectors. The length of the vector being
 * determined by the number of items that are to be placed in the bucket and the
 * acceptable hash collision rate. There is a function that will calculate the
 * length of the vector and the number of functions to use to turn on the bits
 * (see Bloom Filter calculator below). In general you build a bloom filter by
 * creating a hash and using the modulus of that to determine which bit in the
 * vector to turn on. You then generate a second hash, usually the same hash
 * function with a different seed to determine the next bit and so on until the
 * number of functions has been executed. Importantly, there are comments in the
 * Cassandra code that describe a much faster way of doing this using 128-bit
 * hashes and splitting them into a pair of longs. This method is implemented in
 * this code.
 * </p>
 * <p>
 * To check membership in a bloom filter you build the bloom-filter for the
 * target (T - the thing we are looking for) and get the filter for the
 * candidate (C - the bucket) and evaluate T &amp; C = T if it evaluates as true
 * there is a match if it not then T is guaranteed not to be in the bucket.
 * </p>
 * <h1>Object hashing strategies</h1>
 * <p>
 * A bloom filter is simply a set of bits that represents the presence of an
 * object. The strategy used to build the hash will determine a number of values
 * used in the system.
 * </p>
 * <p>
 * One strategy is to build a single hashable item for each object, for instance
 * a string representation, and hash it. This will yield one hash for each
 * object. In this case the number of items defined in the FilterConfig is the
 * number of unique objects. This is the common strategy to hash objects by a
 * unique identifier.
 * </p>
 * <p>
 * Another strategy is to hash components of the object separately, this allows
 * for searching by each component. For example the color, type, and general
 * size of a car might be hashed separately and used to create the
 * ProtoBloomFilter. the number of items for the FilterConfig is the product of
 * the cardinality of each property. So if cars come in white, red, black, blue
 * and brown colors, coupe, sedan, hatchback, saloon, and van body types and
 * compact, medium and large sizes then the total number of items for
 * FilterConfig purposes is (<code>5 x 5 x 3 = 75</code>).
 * </p>
 * <p>
 * The advantage of the second component indexing strategy is that it is
 * possible to search for objects with partial matches. For example building a
 * bloom filter for "red" and "saloon" and then searching the collection will
 * yeild all vehicles that have both "red" and "saloon" property regardless of
 * the size.
 * </p>
 * <h1>Usage Pattern</h1>
 * <p>
 * <ul>
 * <li>Use a ProtoBloomFilter.Builder to digest items and create a
 * ProtoBloomFilter.</li>
 * <li>Create a FilterConfig defining the number of items that will be added to
 * the filter and the probability of collisions.</li>
 * <li>Use the ProtoBloomFilter to create a BloomFilter or CountingBloomFilter
 * with the shape defined by the FilterConfig.</li>
 * </ul>
 * </p>
 * <h2>Notes</h2>
 * <p>
 * <ul>
 * <li>The ProtoBloomFilter can be used to generate multiple BloomFilters using
 * multiple FilterConfigs.</li>
 * <li>Creation of the ProtoBloomFilter is far more intensive than the creation
 * of the subsequent BloomFilter.</li>
 * </ul>
 * </p>
 *
 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter
 *      calculator</a>
 *
 */
package org.apache.commons.collections4.bloomfilters;