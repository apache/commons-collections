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

/**
 * Closure implementation that calls another closure n times, like a for loop.
 *
 * @since 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class ForClosure<E> implements Closure<E>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -1190120533393621674L;

    /** The number of times to loop */
    private final int iCount;
    /** The closure to call */
    private final Closure<? super E> iClosure;

    /**
     * Factory method that performs validation.
     * <p>
     * A null closure or zero count returns the <code>NOPClosure</code>.
     * A count of one returns the specified closure.
     * 
     * @param count  the number of times to execute the closure
     * @param closure  the closure to execute, not null
     * @return the <code>for</code> closure
     */
    @SuppressWarnings("unchecked")
    public static <E> Closure<E> forClosure(int count, Closure<? super E> closure) {
        if (count <= 0 || closure == null) {
            return NOPClosure.<E>nopClosure();
        }
        if (count == 1) {
            return (Closure<E>) closure;
        }
        return new ForClosure<E>(count, closure);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param count  the number of times to execute the closure
     * @param closure  the closure to execute, not null
     */
    public ForClosure(int count, Closure<? super E> closure) {
        super();
        iCount = count;
        iClosure = closure;
    }

    /**
     * Executes the closure <code>count</code> times.
     * 
     * @param input  the input object
     */
    public void execute(E input) {
        for (int i = 0; i < iCount; i++) {
            iClosure.execute(input);
        }
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
     * Gets the count.
     * 
     * @return the count
     * @since 3.1
     */
    public int getCount() {
        return iCount;
    }

}
