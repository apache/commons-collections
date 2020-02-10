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

import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;

/**
 * An implementation of HashFunction that
 * performs Murmur128 hashing using a signed cyclic method.
 *
 * <p>Requires the optional commons-codec library.</p>
 *
 * @since 4.5
 */
public final class Murmur128x86Cyclic implements HashFunction {
    /**
     * The result of the hash 0 call.
     */
    private long[] parts = null;

    /**
     * The signature for this hash function.
     */
    private final long signature;

    /**
     * The name of this hash method.
     */
    public static final String NAME = "Murmur3_x64_128";

    /**
     * Constructs a Murmur3 x64 128 hash.
     */
    public Murmur128x86Cyclic() {
        signature = apply( HashFunctionIdentity.prepareSignatureBuffer(this), 0);
    }


    @Override
    public long apply(final byte[] buffer, final int seed) {
        if (parts == null || seed == 0) {
            parts = MurmurHash3.hash128x64(buffer, 0, buffer.length, 0);
        } else {
            parts[0] += parts[1];
        }
        return parts[0];
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
        return ProcessType.CYCLIC;
    }

    @Override
    public long getSignature() {
        return signature;
    }

}
