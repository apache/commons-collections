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
package org.apache.commons.collections.functors;

import java.io.Serializable;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

/**
 * Closure implementation acts as an if statement calling one or other closure
 * based on a predicate.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Matt Benson
 */
public class IfClosure<E> implements Closure<E>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 3518477308466486130L;

    /** The test */
    private final Predicate<? super E> iPredicate;
    /** The closure to use if true */
    private final Closure<? super E> iTrueClosure;
    /** The closure to use if false */
    private final Closure<? super E> iFalseClosure;

    /**
     * Factory method that performs validation.
     * <p>
     * This factory creates a closure that performs no action when
     * the predicate is false.
     * 
     * @param predicate  predicate to switch on
     * @param trueClosure  closure used if true
     * @return the <code>if</code> closure
     * @throws IllegalArgumentException if either argument is null
     * @since Commons Collections 3.2
     */
    public static <E> Closure<E> getInstance(Predicate<? super E> predicate, Closure<? super E> trueClosure) {
        return IfClosure.<E>getInstance(predicate, trueClosure, NOPClosure.<E>getInstance());
    }

    /**
     * Factory method that performs validation.
     * 
     * @param predicate  predicate to switch on
     * @param trueClosure  closure used if true
     * @param falseClosure  closure used if false
     * @return the <code>if</code> closure
     * @throws IllegalArgumentException if any argument is null
     */
    public static <E> Closure<E> getInstance(Predicate<? super E> predicate, Closure<? super E> trueClosure, Closure<? super E> falseClosure) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (trueClosure == null || falseClosure == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        return new IfClosure<E>(predicate, trueClosure, falseClosure);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * <p>
     * This constructor creates a closure that performs no action when
     * the predicate is false.
     * 
     * @param predicate  predicate to switch on, not null
     * @param trueClosure  closure used if true, not null
     * @since Commons Collections 3.2
     */
    public IfClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure) {
        this(predicate, trueClosure, NOPClosure.INSTANCE);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicate  predicate to switch on, not null
     * @param trueClosure  closure used if true, not null
     * @param falseClosure  closure used if false, not null
     */
    public IfClosure(Predicate<? super E> predicate, Closure<? super E> trueClosure, Closure<? super E> falseClosure) {
        super();
        iPredicate = predicate;
        iTrueClosure = trueClosure;
        iFalseClosure = falseClosure;
    }

    /**
     * Executes the true or false closure accoring to the result of the predicate.
     * 
     * @param input  the input object
     */
    public void execute(E input) {
        if (iPredicate.evaluate(input)) {
            iTrueClosure.execute(input);
        } else {
            iFalseClosure.execute(input);
        }
    }

    /**
     * Gets the predicate.
     * 
     * @return the predicate
     * @since Commons Collections 3.1
     */
    public Predicate<? super E> getPredicate() {
        return iPredicate;
    }

    /**
     * Gets the closure called when true.
     * 
     * @return the closure
     * @since Commons Collections 3.1
     */
    public Closure<? super E> getTrueClosure() {
        return iTrueClosure;
    }

    /**
     * Gets the closure called when false.
     * 
     * @return the closure
     * @since Commons Collections 3.1
     */
    public Closure<? super E> getFalseClosure() {
        return iFalseClosure;
    }

}
