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
 * Hasher implementations and tools.
 *
 *
 * <h2>Hasher</h2>
 *
 * <p>A Hasher converts one or more items into an {@code IndexProducer} series of integers based on a {@code Shape}.
 *
 *
 * The base Hasher implementations
 * are as follows:</p>
 *
 *  <h3>SimpleHasher</h3>
 *
 *  <p>The SimpleHasher represents one item being added to the Bloom filter.  It utilizes the combinatorial strategy
 *  as described by Krisch and Mitzenmacher[<a href='#footnote1'>1</a>].  Generally, a hash value is created by hashing
 *  together multiple properties of the item being added. The hash value is then used to create a SimpleHasher.</p>
 *
 *  <p>This hasher represents a single item and thus does not return duplicate indices.</p>
 *
 *  <h3>HasherCollection</h3>
 *
 *  <p>The HasherCollection is a collection of Hashers that implemehts the Hasher interface.  Each hasher within the collection
 *  represents a single item, or in the case of a HasherCollections multiple items.</p>
 *
 *  <p>This hahser represents multiple items and thus may return duplicate indices.</p>
 *
 *  <h3>SingleItemHasherCollection</h3>
 *
 *  <p>A collection of Hashers that are combined to represent a single item.  Like the HasherCollection this Hasher is composed
 *  of multiple Hashers.  Unlike the HasherCollection, this hasher reports that it is only one item.</p>
 *
 *
 *  <p>This hasher represents a single item and thus does not return duplicate indices.</p>
 *
 *  <h3>Other Implementations</h3>
 *
 *  <p>Other implementations of the Hasher are easy to implement.  Hashers that represent single items should make use of the
 *  {@code Hahser.Filter} and/or {@code Hahser.FileredIntConsumer} classes to filter out duplicate indices.</p>
 *
 *
 *
 *
 * With the exception of the HasherCollection, a Hasher represents an item of arbitrary
 * byte size as multiple byte representations of fixed size (multiple hashes). The hashers
 * are be used to create indices for a Bloom filter.</p>
 *
 * <p>Hashers create @{code IndexProducer} instances for hashed items based
 * on a @{code Shape}.</p>
 *
 * <p>The method used to generate the multiple hashes is dependent upon the Hasher
 * implementation.  The SimpleHasher uses a combinatorial strategy to create the
 * multiple hashes from a single starting hash.</p>
 *
 * <p>Note that the process of generating hashes and mapping them to a Bloom
 * filter shape may create duplicate indexes. The Hasher implementation is required to
 * remove all duplicate values for a single item.  Thus the hasher may generate fewer
 * than the required number of hash values per item after duplicates have been
 * removed.</p>
 *
 * <h2>Footnotes</a>
 *
 * <a name="footnote1>1.</a> Kirsch, Adam and Michael Mitzenmacher,
 * <a href='https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf'>"Building a Better Bloom Filter"</a>,
 * Harvard Computer Science Group Technical Report TR-02-05.
 *
 * @see org.apache.commons.collections4.bloomfilter.IndexProducer
 * @since 4.5
 */
package org.apache.commons.collections4.bloomfilter.hasher;
