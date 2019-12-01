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
package org.apache.commons.collections4.bloomfilter.hasher;

import java.util.function.ToLongBiFunction;

public interface HashFunction extends ToLongBiFunction<byte[], Integer> {

    /**
     * Gets the name of this hash function.
     * <p>
     * Hash function should have the form: [hashName]-[S|U][C|I]
     * where
     * <dl>
     * <dt>hashName</dt>
     * <dd>Is the common name for the hash.  This may include indications as to hash length</dd>
     * <dt>S|U</dt>
     * <dd>Expresses how the calculations are performed and is either<ul>
     * <li>{@code S } for signed; or</li>
     * <li>{@code U } for unsigned</li>
     * </ul></dd>
     * <dt>C|I</dt>
     * <dd>Expresses how additional hashes are implemented and Is either<ul>
     * <li>{@code C } for cyclic hashing.  Using 2 calculated hash values to generate the series of
     * hash values; or</li>
     * <li>{@code I } for iterative hashing.  Using the hash to generate each hash value as needed</li>
     * </ul></dd>
     * </dl>
     * @return the Hash name
     */
    String getName();

}
