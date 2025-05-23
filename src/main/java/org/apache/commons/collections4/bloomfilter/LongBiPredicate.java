/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter;

/**
 * Represents a function that accepts a two long-valued argument and produces a binary result.
 * This is the long-consuming primitive specialization for {@code BiPredicate}.
 * <p>
 * This is a functional interface whose functional method is {@code test(long,long)}.
 * </p>
 *
 * @since 4.5.0-M1
 */
@FunctionalInterface
public interface LongBiPredicate {

    /**
     * A function that takes to long arguments and returns a boolean.
     *
     * @param x the first long argument.
     * @param y the second long argument.
     * @return true or false.
     */
    boolean test(long x, long y);
}
