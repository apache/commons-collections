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

import java.util.Objects;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;

/**
 * Closure implementation that executes a closure repeatedly until a condition is met,
 * like a do-while or while loop.
 * <p>
 * <strong>WARNING:</strong> from v4.1 onwards this class will <strong>not</strong> be serializable anymore
 * in order to prevent potential remote code execution exploits. Please refer to
 * <a href="https://issues.apache.org/jira/browse/COLLECTIONS-580">COLLECTIONS-580</a>
 * for more details.
 * </p>
 *
 * @param <T> the type of the input to the operation.
 * @since 3.0
 */
public class WhileClosure<T> implements Closure<T> {

    /**
     * Factory method that performs validation.
     *
     * @param <E> the type that the closure acts on
     * @param predicate  the predicate used to evaluate when the loop terminates, not null
     * @param closure  the closure to execute, not null
     * @param doLoop  true to act as a do-while loop, always executing the closure once
     * @return the {@code while} closure
     * @throws NullPointerException if the predicate or closure is null
     */
    public static <E> Closure<E> whileClosure(final Predicate<? super E> predicate,
                                              final Closure<? super E> closure, final boolean doLoop) {
        return new WhileClosure<>(Objects.requireNonNull(predicate, "predicate"),
                Objects.requireNonNull(closure, "closure"), doLoop);
    }
    /** The test condition */
    private final Predicate<? super T> iPredicate;
    /** The closure to call */
    private final Closure<? super T> iClosure;

    /** The flag, true is a do loop, false is a while */
    private final boolean iDoLoop;

    /**
     * Constructor that performs no validation.
     * Use {@code whileClosure} if you want that.
     *
     * @param predicate  the predicate used to evaluate when the loop terminates, not null
     * @param closure  the closure to execute, not null
     * @param doLoop  true to act as a do-while loop, always executing the closure once
     */
    public WhileClosure(final Predicate<? super T> predicate, final Closure<? super T> closure, final boolean doLoop) {
        iPredicate = predicate;
        iClosure = closure;
        iDoLoop = doLoop;
    }

    /**
     * Executes the closure until the predicate is false.
     *
     * @param input  the input object
     */
    @Override
    public void execute(final T input) {
        if (iDoLoop) {
            iClosure.accept(input);
        }
        while (iPredicate.test(input)) {
            iClosure.accept(input);
        }
    }

    /**
     * Gets the closure.
     *
     * @return the closure
     * @since 3.1
     */
    public Closure<? super T> getClosure() {
        return iClosure;
    }

    /**
     * Gets the predicate in use.
     *
     * @return the predicate
     * @since 3.1
     */
    public Predicate<? super T> getPredicate() {
        return iPredicate;
    }

    /**
     * Is the loop a do-while loop.
     *
     * @return true is do-while, false if while
     * @since 3.1
     */
    public boolean isDoLoop() {
        return iDoLoop;
    }

}
