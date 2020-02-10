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
package org.apache.commons.collections4.bloomfilter.hasher.function;

import java.util.Arrays;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;


/**
 * An implementation of HashFunction that
 * performs {@code Objects.hash} hashing using a signed iterative method.
 * <p>
 * Except in the case of seed 0, the value of the previous hash is
 * used as a seed for the next hash.  Hashes are seeded by calling
 * {@code Arrays.deepHashCode( new Object[]{seed, buffer} )}.
 * </p>
 * @since 4.5
 */
public final class ObjectsHashIterative implements HashFunction {

    /**
     * The name of the hash function.
     */
    public static final String NAME = "Objects32";

    /**
     * The signature for this hash function.
     */
    private final long signature;

    /**
     * The value of the last hash.
     */
    private long last = 0;

    /**
     * Constructs a hash that uses the Objects.hash method to has values.
     */
    public ObjectsHashIterative() {
        signature = apply( HashFunctionIdentity.prepareSignatureBuffer(this), 0);
    }

    @Override
    public long apply(final byte[] buffer, final int seed) {
        if (seed == 0) {
            last = 0;
        }
        final long result = Arrays.deepHashCode( new Object[] {last, buffer});
        last += result;
        return result;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getProvider() {
        return "Apache Commons Collections";
    }

    @Override
    public Signedness getSignedness() {
        return Signedness.SIGNED;
    }

    @Override
    public ProcessType getProcessType() {
        return ProcessType.ITERATIVE;
    }

    @Override
    public long getSignature() {
        return signature;
    }
}
