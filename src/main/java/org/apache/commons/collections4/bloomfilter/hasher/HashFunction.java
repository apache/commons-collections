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
package org.apache.commons.collections4.bloomfilter.hasher;

/**
 * Defines a Hash Function used by Hashers.
 * @since 4.5
 */
public interface HashFunction extends HashFunctionIdentity {

    /**
     * Applies the hash function to the buffer.
     *
     * @param buffer the buffer to apply the hash function to.
     * @param seed the seed for the hashing.
     * @return the long value of the hash.
     */
    long apply(byte[] buffer, int seed);

    /**
     * Gets the signature of this function.
     *
     * <p>The signature of this function is calculated as:
     * <pre><code>
     * int seed = 0;
     * apply(String.format("%s-%s-%s",
     *                     getName().toUpperCase(Locale.ROOT), getSignedness(), getProcess())
     *             .getBytes("UTF-8"), seed);
     * </code></pre>
     *
     * @see HashFunctionIdentity#prepareSignatureBuffer(HashFunctionIdentity)
     */
    @Override
    long getSignature();
}
