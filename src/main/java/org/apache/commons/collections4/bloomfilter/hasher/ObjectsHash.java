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

import java.util.Objects;
import java.util.function.ToLongBiFunction;

/**
 * An implementation of ToLongBiFunction<ByteBuffer, Integer> that
 * performs {@code Objects.hash} hashing using a signed iterative method.
 * <p>
 * Except in the case of seed 0, the value of the previous hash is
 * used as a seed for the next hash.  Hashes are seeded by calling
 * {@code Objects.hash( seed, buffer )}.
 * </p>
 */
public class ObjectsHash implements ToLongBiFunction<byte[], Integer> {

    /**
     * The name of the hash function.
     */
    public static final String NAME = "Objects32-SI";

    /**
     * The value of the last hash.
     */
    private long last = 0;

    @Override
    public long applyAsLong(byte[] buffer, Integer seed) {
        if (seed == 0) {
            last = 0;
        }
        long result = Objects.hash(last, buffer);
        last += result;
        return result;
    }

}
