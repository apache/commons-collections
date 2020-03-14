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
package org.apache.commons.collections4.bloomfilter.hasher.function;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;

/**
 * Allow computation of HashFunction signatures.
 * @since 4.5
 */
final class Signatures {

    /** No instances. */
    private Signatures() {}

    /**
     * Gets the standard signature for the hash function. The signature is prepared as:
     * <pre><code>
     * int seed = 0;
     * return hashFunction.apply(HashFunctionIdentity.prepareSignatureBuffer(hashFunction), seed);
     * </code></pre>
     *
     * @param hashFunction the hash function
     * @return the signature
     * @see HashFunctionIdentity#prepareSignatureBuffer(HashFunctionIdentity)
     * @see HashFunction#apply(byte[], int)
     */
    static long getSignature(HashFunction hashFunction) {
        return hashFunction.apply(HashFunctionIdentity.prepareSignatureBuffer(hashFunction), 0);
    }
}
