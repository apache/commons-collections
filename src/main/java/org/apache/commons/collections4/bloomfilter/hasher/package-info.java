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
 * remove all duplicate values for a single item.  Thus tge hasher may generate fewer
 * than the required number of hash values per item after duplicates have been
 * removed.</p>
 *
 *
 * @since 4.5
 */
package org.apache.commons.collections4.bloomfilter.hasher;

