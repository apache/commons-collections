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
package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;

/**
 * Closure implementation acts as an if statement calling one or other closure
 * based on a predicate.
 *
 * @param <T> the type of the input to the operation.
 * @since 3.0
 */
public class IfClosure<T> implements Closure<T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 3518477308466486130L;

    /**
     * Factory method that performs validation.
     * <p>
     * This factory creates a closure that performs no action when
     * the predicate is false.
     *
     * @param <E> the type that the closure acts on
     * @param predicate  predicate to switch on
     * @param trueClosure  closure used if true
     * @return the {@code if} closure
     * @throws NullPointerException if either argument is null
     * @since 3.2
     */
    public static <E> Closure<E> ifClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure) {
        return IfClosure.<E>ifClosure(predicate, trueClosure, NOPClosure.<E>nopClosure());
    }
    /**
     * Factory method that performs validation.
     *
     * @param <E> the type that the closure acts on
     * @param predicate  predicate to switch on
     * @param trueClosure  closure used if true
     * @param falseClosure  closure used if false
     * @return the {@code if} closure
     * @throws NullPointerException if any argument is null
     */
    public static <E> Closure<E> ifClosure(final Predicate<? super E> predicate,
                                           final Closure<? super E> trueClosure,
                                           final Closure<? super E> falseClosure) {
        return new IfClosure<>(Objects.requireNonNull(predicate, "predicate"),
                Objects.requireNonNull(trueClosure, "trueClosure"),
                Objects.requireNonNull(falseClosure, "falseClosure"));
    }
    /** The test */
    private final Predicate<? super T> iPredicate;

    /** The closure to use if true */
    private final Closure<? super T> iTrueClosure;

    /** The closure to use if false */
    private final Closure<? super T> iFalseClosure;

    /**
     * Constructor that performs no validation.
     * Use {@code ifClosure} if you want that.
     * <p>
     * This constructor creates a closure that performs no action when
     * the predicate is false.
     *
     * @param predicate  predicate to switch on, not null
     * @param trueClosure  closure used if true, not null
     * @since 3.2
     */
    public IfClosure(final Predicate<? super T> predicate, final Closure<? super T> trueClosure) {
        this(predicate, trueClosure, NOPClosure.nopClosure());
    }

    /**
     * Constructor that performs no validation.
     * Use {@code ifClosure} if you want that.
     *
     * @param predicate  predicate to switch on, not null
     * @param trueClosure  closure used if true, not null
     * @param falseClosure  closure used if false, not null
     */
    public IfClosure(final Predicate<? super T> predicate, final Closure<? super T> trueClosure,
                     final Closure<? super T> falseClosure) {
        iPredicate = predicate;
        iTrueClosure = trueClosure;
        iFalseClosure = falseClosure;
    }

    /**
     * Executes the true or false closure according to the result of the predicate.
     *
     * @param input  the input object
     */
    @Override
    public void execute(final T input) {
        if (iPredicate.test(input)) {
            iTrueClosure.accept(input);
        } else {
            iFalseClosure.accept(input);
        }
    }

    /**
     * Gets the closure called when false.
     *
     * @return the closure
     * @since 3.1
     */
    public Closure<? super T> getFalseClosure() {
        return iFalseClosure;
    }

    /**
     * Gets the predicate.
     *
     * @return the predicate
     * @since 3.1
     */
    public Predicate<? super T> getPredicate() {
        return iPredicate;
    }

    /**
     * Gets the closure called when true.
     *
     * @return the closure
     * @since 3.1
     */
    public Closure<? super T> getTrueClosure() {
        return iTrueClosure;
    }

}
