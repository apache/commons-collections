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
 * Contains validation for hash functions.
 */
final class HashFunctionValidator {
    /** Do not instantiate. */
    private HashFunctionValidator() {}

    /**
     * Compares the identity of the two hash functions. The functions are considered
     * equal if the signedness, process type and name are equal. The name is not
     * case specific.
     *
     * <p>A pair of functions that are equal would be expected to produce the same
     * hash output from the same input.
     *
     * @param a First hash function.
     * @param b Second hash function.
     * @return true, if successful
     * @see String#equalsIgnoreCase(String)
     */
    static boolean areEqual(HashFunctionIdentity a, HashFunctionIdentity b) {
        return (a.getSignedness() == b.getSignedness() &&
                a.getProcessType() == b.getProcessType() &&
                a.getName().equalsIgnoreCase(b.getName()));
    }

    /**
     * Compares the identity of the two hash functions and throws an exception if they
     * are not equal.
     *
     * @param a First hash function.
     * @param b Second hash function.
     * @see #areEqual(HashFunctionIdentity, HashFunctionIdentity)
     * @throws IllegalArgumentException if the hash functions are not equal
     */
    static void checkAreEqual(HashFunctionIdentity a, HashFunctionIdentity b) {
        if (!areEqual(a, b)) {
            throw new IllegalArgumentException(String.format("Hash functions are not equal: (%s) != (%s)",
                HashFunctionIdentity.asCommonString(a), HashFunctionIdentity.asCommonString(b)));
        }
    }
}
