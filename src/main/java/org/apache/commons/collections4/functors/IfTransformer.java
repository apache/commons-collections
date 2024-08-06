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
package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

/**
 * Transformer implementation that will call one of two closures based on whether a predicate evaluates
 * as true or false.
 *
 * @param <T> the type of the input to the function.
 * @param <R> the type of the result of the function.
 * @since 4.1
 */
public class IfTransformer<T, R> implements Transformer<T, R>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 8069309411242014252L;

    /**
     * Factory method that performs validation.
     *
     * @param <I>  input type for the transformer
     * @param <O>  output type for the transformer
     * @param predicate  predicate to switch on
     * @param trueTransformer  transformer used if true
     * @param falseTransformer  transformer used if false
     * @return the {@code if} transformer
     * @throws NullPointerException if either argument is null
     */
    public static <I, O> Transformer<I, O> ifTransformer(final Predicate<? super I> predicate,
                                                         final Transformer<? super I, ? extends O> trueTransformer,
                                                         final Transformer<? super I, ? extends O> falseTransformer) {
        return new IfTransformer<>(Objects.requireNonNull(predicate, "predicate"),
                Objects.requireNonNull(trueTransformer, "trueTransformer"),
                Objects.requireNonNull(falseTransformer, "falseTransformer"));
    }
    /**
     * Factory method that performs validation.
     * <p>
     * This factory creates a transformer that just returns the input object when
     * the predicate is false.
     *
     * @param <T>  input and output type for the transformer
     * @param predicate  predicate to switch on
     * @param trueTransformer  transformer used if true
     * @return the {@code if} transformer
     * @throws NullPointerException if either argument is null
     */
    public static <T> Transformer<T, T> ifTransformer(
            final Predicate<? super T> predicate,
            final Transformer<? super T, ? extends T> trueTransformer) {
        return new IfTransformer<>(Objects.requireNonNull(predicate, "predicate"),
                Objects.requireNonNull(trueTransformer, "trueTransformer"), NOPTransformer.<T>nopTransformer());
    }
    /** The test */
    private final Predicate<? super T> iPredicate;

    /** The transformer to use if true */
    private final Transformer<? super T, ? extends R> iTrueTransformer;

    /** The transformer to use if false */
    private final Transformer<? super T, ? extends R> iFalseTransformer;

    /**
     * Constructor that performs no validation.
     * Use the static factory method {@code ifTransformer} if you want that.
     *
     * @param predicate  predicate to switch on, not null
     * @param trueTransformer  transformer used if true, not null
     * @param falseTransformer  transformer used if false, not null
     */
    public IfTransformer(final Predicate<? super T> predicate,
        final Transformer<? super T, ? extends R> trueTransformer,
        final Transformer<? super T, ? extends R> falseTransformer) {

        iPredicate = predicate;
        iTrueTransformer = trueTransformer;
        iFalseTransformer = falseTransformer;
    }

    /**
     * Gets the transformer used when false.
     *
     * @return the transformer
     */
    public Transformer<? super T, ? extends R> getFalseTransformer() {
        return iFalseTransformer;
    }

    /**
     * Gets the predicate.
     *
     * @return the predicate
     */
    public Predicate<? super T> getPredicate() {
        return iPredicate;
    }

    /**
     * Gets the transformer used when true.
     *
     * @return the transformer
     */
    public Transformer<? super T, ? extends R> getTrueTransformer() {
        return iTrueTransformer;
    }

    /**
     * Transforms the input using the true or false transformer based to the result of the predicate.
     *
     * @param input  the input object to transform
     * @return the transformed result
     */
    @Override
    public R transform(final T input) {
        if (iPredicate.test(input)) {
            return iTrueTransformer.apply(input);
        }
        return iFalseTransformer.apply(input);
    }
}
