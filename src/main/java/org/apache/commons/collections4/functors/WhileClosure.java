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

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;

/**
 * Closure implementation that executes a closure repeatedly until a condition is met,
 * like a do-while or while loop.
 * <p>
 * <b>WARNING:</b> from v4.1 onwards this class will <b>not</b> be serializable anymore
 * in order to prevent potential remote code execution exploits. Please refer to
 * <a href="https://issues.apache.org/jira/browse/COLLECTIONS-580">COLLECTIONS-580</a>
 * for more details.
 *
 * @since 3.0
 */
public class WhileClosure<E> implements Closure<E> {

    /** The test condition */
    private final Predicate<? super E> iPredicate;
    /** The closure to call */
    private final Closure<? super E> iClosure;
    /** The flag, true is a do loop, false is a while */
    private final boolean iDoLoop;

    /**
     * Factory method that performs validation.
     *
     * @param <E> the type that the closure acts on
     * @param predicate  the predicate used to evaluate when the loop terminates, not null
     * @param closure  the closure the execute, not null
     * @param doLoop  true to act as a do-while loop, always executing the closure once
     * @return the <code>while</code> closure
     * @throws NullPointerException if the predicate or closure is null
     */
    public static <E> Closure<E> whileClosure(final Predicate<? super E> predicate,
                                              final Closure<? super E> closure, final boolean doLoop) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (closure == null) {
            throw new NullPointerException("Closure must not be null");
        }
        return new WhileClosure<>(predicate, closure, doLoop);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>whileClosure</code> if you want that.
     *
     * @param predicate  the predicate used to evaluate when the loop terminates, not null
     * @param closure  the closure the execute, not null
     * @param doLoop  true to act as a do-while loop, always executing the closure once
     */
    public WhileClosure(final Predicate<? super E> predicate, final Closure<? super E> closure, final boolean doLoop) {
        super();
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
    public void execute(final E input) {
        if (iDoLoop) {
            iClosure.execute(input);
        }
        while (iPredicate.evaluate(input)) {
            iClosure.execute(input);
        }
    }

    /**
     * Gets the predicate in use.
     *
     * @return the predicate
     * @since 3.1
     */
    public Predicate<? super E> getPredicate() {
        return iPredicate;
    }

    /**
     * Gets the closure.
     *
     * @return the closure
     * @since 3.1
     */
    public Closure<? super E> getClosure() {
        return iClosure;
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
