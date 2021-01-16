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

import java.util.Arrays;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;

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
     *
     * <p>TODO: Make static akin to a serialVersionUID?
     */
    private final long signature;

    /**
     * The value of the last hash.
     */
    private long last;

    /**
     * Constructs a hash that uses the Objects.hash method to has values.
     */
    public ObjectsHashIterative() {
        signature = Signatures.getSignature(this);
    }

    @Override
    public long apply(final byte[] buffer, final int seed) {
        if (seed == 0) {
            last = 0;
        }
        // Effectively:
        // result = Arrays.deepHashCode(new Object[] { last, buffer });
        // The method loops over items starting with result=1
        // for i in items:
        //    result = 31 * result + hashCode(i)
        // Here we unroll the computation to 2 iterations.
        // The computation is done using 32-bit integers then cast to a long
        final long result = 31 * (31 + Long.hashCode(last)) + Arrays.hashCode(buffer);
        last += result;
        return result;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ProcessType getProcessType() {
        return ProcessType.ITERATIVE;
    }

    @Override
    public String getProvider() {
        return "Apache Commons Collections";
    }

    @Override
    public long getSignature() {
        return signature;
    }

    @Override
    public Signedness getSignedness() {
        return Signedness.SIGNED;
    }
}
