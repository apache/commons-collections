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

import java.util.Objects;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * A Hasher that returns no values.
 *
 * @since 4.5
 */
public final class NullHasher implements Hasher {

    public static final NullHasher INSTANCE = new NullHasher();

    private NullHasher() {
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");

        return new IndexProducer() {
            @Override
            public void forEachIndex(IntConsumer consumer) {
                // do nothing
            }
        };
    }

    @Override
    public int size() {
        return 0;
    }
}
