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

/**
 * An implementation of {@code ToLongBiFunction<byte[], Integer>} that
 * performs Murmur32 hashing using a signed iterative method.
 * @since 4.5
 */
public class Murmur32 implements HashFunction {

    /**
     * The name of this hash function.
     */
    public static final String NAME = "Murmur3_x86_32-SI";

    @Override
    public long applyAsLong(byte[] buffer, Integer seed) {
        return MurmurHash3.hash32x86(buffer, 0, buffer.length, seed);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
