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
 * Defines the a Hash Function used by Hashers.
 *
 * @since 4.5
 */
public interface HashFunctionIdentity {

    /**
     * A comparator implementation that performs the most common comparison using the
     * HashFunctionIdentity name, signedness, and process.
     */
    Comparator<HashFunctionIdentity> COMMON_COMPARATOR = new Comparator<HashFunctionIdentity>() {

        @Override
        public int compare(HashFunctionIdentity identity1, HashFunctionIdentity identity2) {
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
     * A comparator implementation that performs the most common comparison using the
     * HashFunctionIdentity name, signedness, process, and privider..
     */
    Comparator<HashFunctionIdentity> DEEP_COMPARATOR = new Comparator<HashFunctionIdentity>() {

        @Override
        public int compare(HashFunctionIdentity identity1, HashFunctionIdentity identity2) {
            int result = COMMON_COMPARATOR.compare(identity1, identity2);
            if (result == 0) {
                result = identity1.getProvider().compareToIgnoreCase(identity2.getProvider());
            }
            return result;
        }
    };

    /**
     * Get a common formatted string for general display.
     *
     * @param identity the identity to format.
     * @return the String representing the identity.
     */
    static String asCommonString(HashFunctionIdentity identity) {
        return String.format("%s-%s-%s", identity.getName(), identity.getSignedness(), identity.getProcessType());
    }

    /**
     * Get the signature buffer for a HashFunctionIdentity.
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
    static byte[] prepareSignatureBuffer(HashFunctionIdentity identity) {

       return String.format( "%s-%s-%s",
           identity.getName().toUpperCase( Locale.ROOT ), identity.getSignedness(),
           identity.getProcessType() ).getBytes( StandardCharsets.UTF_8 );

    }

    /**
     * An enum that identifies the Signedness of the calculations for this function.
     */
    enum Signedness {
        SIGNED, UNSIGNED
    };

    /**
     * An enum that identfies the process type of this function. <dl> <dt>Iterative
     * processes</dt> <dd>Call the underlying algorithm for each buffer, seed pair call to
     * {@code apply}.</dd> <dt>Cyclic processes</dt> <dd>Call the underlying algorithm to
     * generate two values for each buffer. It returns the first value on the call with
     * seed 0, and increments the result with the second value before returning it on all
     * subsequent calls.</dd> </dl>
     */
    enum ProcessType {
        CYCLIC, ITERATIVE
    };

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
     * Gets the name of the provider of this hash function implementation.
     * <p>
     * Provider names are not case specific.  Thus, "Apache Commons Collection" and
     * "apache commons collection" should be considered as the same.
     * </p>
     * @return the name of the provider of this hash implementation.
     */
    String getProvider();

    /**
     * Gets the signedness of this function.
     *
     * @return signedness of this function.
     */
    Signedness getSignedness();

    /**
     * Gets the process of this function.
     *
     * @return process of this function.
     */
    ProcessType getProcessType();

    /**
     * Get the signature of this function. <p> The signature of this function is
     * calculated as: {@code
     * apply( String.format( "%s-%s-%s", getName(), getSignedness(), getProcess() )
     *     .getBytes( "UTF-8" ), 0 );
     * } </p>
     *
     * @return the signature of this function.
     */
    long getSignature();

}
