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

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
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
     *  {@link HashFunction#apply(byte[], int)} to initialise the state. Subsequent
     *  calls can generate hash values without calling the underlying algorithm.</dd>
     * </dl>
     */
    enum ProcessType {
        /**
         * Call the underlying hash algorithm for a (buffer, seed) pair passed to
         * {@link HashFunction#apply(byte[], int)} when the state is uninitialised or
         * the seed is zero. This initialises the state. Subsequent calls with a non-zero
         * seed use the state to generate a new value.</dd>
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
     */
    enum Signedness {
        SIGNED, UNSIGNED
    }

    /**
     * A comparator implementation that performs the most common comparison using the
     * HashFunctionIdentity name, signedness, and process.
     */
    Comparator<HashFunctionIdentity> COMMON_COMPARATOR = new Comparator<HashFunctionIdentity>() {
        @Override
        public int compare(final HashFunctionIdentity identity1, final HashFunctionIdentity identity2) {
            int result = identity1.getName().compareToIgnoreCase(identity2.getName());
            if (result == 0) {
                result = identity1.getSignedness().compareTo(identity2.getSignedness());
            }
            if (result == 0) {
                result = identity1.getProcessType().compareTo(identity2.getProcessType());
            }
            return result;
        }
    };

    /**
     * A comparator implementation that performs the comparison using all the properties of the
     * HashFunctionIdentity: name, signedness, process, and provider.
     */
    Comparator<HashFunctionIdentity> DEEP_COMPARATOR = new Comparator<HashFunctionIdentity>() {
        @Override
        public int compare(final HashFunctionIdentity identity1, final HashFunctionIdentity identity2) {
            int result = COMMON_COMPARATOR.compare(identity1, identity2);
            if (result == 0) {
                result = identity1.getProvider().compareToIgnoreCase(identity2.getProvider());
            }
            return result;
        }
    };

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
     * Gets the signature buffer for a HashFunctionIdentity.
     * <p>
     * The signature of this function is calculated as:
     * {@code
     * apply( String.format( "%s-%s-%s", getName().toUpperCase( Locale.ROOT ), getSignedness(), getProcess() )
     *     .getBytes( "UTF-8" ), 0 );
     * }
     * </p>
     * @param identity The HashFunctionIdentity to create the buffer for.
     * @return the signature buffer for the identity
     */
    static byte[] prepareSignatureBuffer(final HashFunctionIdentity identity) {

       return String.format("%s-%s-%s",
           identity.getName().toUpperCase(Locale.ROOT), identity.getSignedness(),
           identity.getProcessType() ).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Gets the name of this hash function.
     * <p> Hash function should be the common name
     * for the hash. This may include indications as to hash length
     * </p><p>
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
     * Gets the signature of this function. <p> The signature of this function is
     * calculated as: {@code
     * apply( String.format( "%s-%s-%s", getName(), getSignedness(), getProcess() )
     *     .getBytes( "UTF-8" ), 0 );
     * } </p>
     *
     * @return the signature of this function.
     */
    long getSignature();

    /**
     * Gets the signedness of this function.
     *
     * @return signedness of this function.
     */
    Signedness getSignedness();
}
