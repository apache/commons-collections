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

import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;

/**
 * An implementation of HashFunction that
 * uses an underlying Murmur3 128-bit hash with a signed cyclic method.
 *
 * <p>Requires the optional <a href="https://commons.apache.org/codec/">Apache Commons Codec</a>
 * library which contains a Java port of the 128-bit hash function
 * {@code MurmurHash3_x64_128} from Austin Applyby's original {@code c++}
 * code in SMHasher.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 4.5
 */
public final class Murmur128x64Cyclic implements HashFunction {

    /**
     * The name of this hash method.
     */
    public static final String NAME = "Murmur3_x64_128";

    /**
     * The result of the hash 0 call.
     */
    private long[] parts = null;

    /**
     * The signature for this hash function.
     *
     * <p>TODO: Make static akin to a serialVersionUID?
     */
    private final long signature;

    /**
     * Constructs a Murmur3 x64 128 hash.
     */
    public Murmur128x64Cyclic() {
        signature = Signatures.getSignature(this);
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
    public ProcessType getProcessType() {
        return ProcessType.CYCLIC;
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
