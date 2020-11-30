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

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Defines the hash function used by a {@link Hasher}.
 *
 * @since 4.5
 */
public interface HashFunctionIdentity {

    /**
     * Identifies the process type of this function.
     *
     * <dl>
     *  <dt>Iterative processes</dt>
     *  <dd>Call the underlying hash algorithm for each (buffer, seed) pair passed to
     *  {@link HashFunction#apply(byte[], int)}.</dd>
     *  <dt>Cyclic processes</dt>
     *  <dd>Call the underlying hash algorithm using a (buffer, seed) pair passed to
     *  {@link HashFunction#apply(byte[], int)} to initialize the state. Subsequent
     *  calls can generate hash values without calling the underlying algorithm.</dd>
     * </dl>
     */
    enum ProcessType {
        /**
         * Call the underlying hash algorithm for a (buffer, seed) pair passed to
         * {@link HashFunction#apply(byte[], int)} when the state is uninitialized or
         * the seed is zero. This initializes the state. Subsequent calls with a non-zero
         * seed use the state to generate a new value.
         */
        CYCLIC,
        /**
         * Call the underlying hash algorithm for each (buffer, seed) pair passed to
         * {@link HashFunction#apply(byte[], int)}.
         */
        ITERATIVE
    }

    /**
     * Identifies the signedness of the calculations for this function.
     * <p>
     * When the hash function executes it typically returns an array of bytes.
     * That array is converted into one or more numerical values which will be provided
     * as a {@code long} primitive type.
     * The signedness identifies if those {@code long} values are signed or unsigned.
     * For example a hash function that outputs only 32-bits can be unsigned if converted
     * using {@link Integer#toUnsignedLong(int)}. A hash function that outputs more than
     * 64-bits is typically signed.
     * </p>
     */
    enum Signedness {
        /**
         * The result of {@link HashFunction#apply(byte[], int)} is signed,
         * thus the sign bit may be set.
         *
         * <p>
         * The result can be used with {@code Math.floorMod(x, y)} to generate a positive
         * value if y is positive.
         * </p>
         *
         * @see Math#floorMod(int, int)
         */
        SIGNED,
        /**
         * The result of {@link HashFunction#apply(byte[], int)} is unsigned,
         * thus the sign bit is never set.
         *
         * <p>
         * The result can be used with {@code x % y} to generate a positive
         * value if y is positive.
         * </p>
         */
        UNSIGNED
    }

    /**
     * Gets a common formatted string for general display.
     *
     * @param identity the identity to format.
     * @return the String representing the identity.
     */
    static String asCommonString(final HashFunctionIdentity identity) {
        return String.format("%s-%s-%s", identity.getName(), identity.getSignedness(), identity.getProcessType());
    }

    /**
     * Gets a {@code byte[]} buffer for a HashFunctionIdentity to create a signature. The
     * {@code byte[]} is composed using properties of the hash function as:
     *
     * <pre><code>
     * String.format("%s-%s-%s",
     *               getName().toUpperCase(Locale.ROOT), getSignedness(), getProcess())
     *       .getBytes("UTF-8");
     * </code></pre>
     *
     * @param identity The HashFunctionIdentity to create the buffer for.
     * @return the signature buffer for the identity
     * @see #getSignature()
     */
    static byte[] prepareSignatureBuffer(final HashFunctionIdentity identity) {
        return String.format("%s-%s-%s",
            identity.getName().toUpperCase(Locale.ROOT), identity.getSignedness(),
            identity.getProcessType()).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Gets the name of this hash function.
     * <p>
     * Hash function should be the common name
     * for the hash. This may include indications as to hash length
     * </p>
     * <p>
     * Names are not case specific.  Thus, "MD5" and "md5" should be considered as the same.
     * </p>
     * @return the Hash name
     */
    String getName();

    /**
     * Gets the process type of this function.
     *
     * @return process type of this function.
     */
    ProcessType getProcessType();

    /**
     * Gets the name of the provider of this hash function implementation.
     * <p>
     * Provider names are not case specific.  Thus, "Apache Commons Collection" and
     * "apache commons collection" should be considered as the same.
     * </p>
     * @return the name of the provider of this hash implementation.
     */
    String getProvider();

    /**
     * Gets the signature of this function. The signature is the output of the hash function
     * when applied to a set of bytes composed using properties of the hash function.
     *
     * <p>
     * Implementations should define the method used to generate the signature.
     * </p>
     *
     * @return the signature of this function.
     * @see #prepareSignatureBuffer(HashFunctionIdentity)
     */
    long getSignature();

    /**
     * Gets the signedness of this function.
     *
     * @return signedness of this function.
     */
    Signedness getSignedness();
}
