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
package org.apache.commons.collections4;

import java.util.function.Function;

/**
 * Defines a functor interface implemented by classes that transform one
 * object into another.
 * <p>
 * A {@code Transformer} converts the input object to the output object.
 * The input object SHOULD be left unchanged.
 * Transformers are typically used for type conversions, or extracting data
 * from an object.
 * </p>
 * <p>
 * Standard implementations of common transformers are provided by
 * {@link TransformerUtils}. These include method invocation, returning a constant,
 * cloning and returning the string value.
 * </p>
 *
 * @param <T> the type of the input to the function.
 * @param <R> the type of the result of the function.
 * @since 1.0
 * This will be deprecated in 5.0 in favor of  {@link Function}.
 */
//@Deprecated
@FunctionalInterface
public interface Transformer<T, R> extends Function<T, R> {

    @Override
    default R apply(final T t) {
        return transform(t);
    }

    /**
     * Transforms the input object into some output object.
     * <p>
     * The input object SHOULD be left unchanged.
     * </p>
     *
     * @param input  the object to be transformed, should be left unchanged
     * @return a transformed object
     * @throws ClassCastException (runtime) if the input is the wrong class
     * @throws IllegalArgumentException (runtime) if the input is invalid
     * @throws FunctorException (runtime) if the transform cannot be completed
     */
    R transform(T input);

}
