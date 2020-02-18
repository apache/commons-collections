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

import java.nio.ByteBuffer;

import java.nio.LongBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;

/**
 * An implementation of HashFunction that
 * performs MD5 hashing using a signed cyclic method.
 * @since 4.5
 */
public final class MD5Cyclic implements HashFunction {

    /**
     * The name of this hash function.
     */
    public static final String NAME = "MD5";

    /**
     * The MD5 digest implementation.
     */
    private final MessageDigest messageDigest;

    /**
     * The signature for this hash function.
     */
    private final long signature;

    /**
     * The result from the digest 0
     */
    private final long[] result = new long[2];

    /**
     * Constructs the MD5 hashing function.
     */
    public MD5Cyclic() {
        try {
            messageDigest = MessageDigest.getInstance(NAME);
        } catch (final NoSuchAlgorithmException e) {
            // This should not happen
            throw new IllegalStateException("Missing the standard MD5 message digest algorithm", e);
        }
        signature = apply(HashFunctionIdentity.prepareSignatureBuffer(this), 0);
    }

    @Override
    public long apply(final byte[] buffer, final int seed) {

        if (seed == 0) {
            byte[] hash;
            synchronized (messageDigest) {
                messageDigest.update(buffer);
                hash = messageDigest.digest();
                messageDigest.reset();
            }

            final LongBuffer lb = ByteBuffer.wrap(hash).asLongBuffer();
            result[0] = lb.get(0);
            result[1] = lb.get(1);
        } else {
            result[0] += result[1];
        }
        return result[0];
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
