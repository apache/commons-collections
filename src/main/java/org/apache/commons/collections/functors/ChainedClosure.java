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
import java.util.Collection;

import org.apache.commons.collections.Closure;

/**
 * Closure implementation that chains the specified closures together.
 *
 * @since 3.0
 * @version $Id$
 */
public class ChainedClosure<E> implements Closure<E>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -3520677225766901240L;

    /** The closures to call in turn */
    private final Closure<? super E>[] iClosures;

    /**
     * Factory method that performs validation and copies the parameter array.
     * 
     * @param <E> the type that the closure acts on
     * @param closures  the closures to chain, copied, no nulls
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures array is null
     * @throws IllegalArgumentException if any closure in the array is null
     */
    public static <E> Closure<E> chainedClosure(final Closure<? super E>... closures) {
        FunctorUtils.validate(closures);
        if (closures.length == 0) {
            return NOPClosure.<E>nopClosure();
        }
        return new ChainedClosure<E>(FunctorUtils.copy(closures));
    }

    /**
     * Create a new Closure that calls each closure in turn, passing the 
     * result into the next closure. The ordering is that of the iterator()
     * method on the collection.
     * 
     * @param <E> the type that the closure acts on
     * @param closures  a collection of closures to chain
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures collection is null
     * @throws IllegalArgumentException if any closure in the collection is null
     */
    @SuppressWarnings("unchecked")
    public static <E> Closure<E> chainedClosure(final Collection<Closure<E>> closures) {
        if (closures == null) {
            throw new IllegalArgumentException("Closure collection must not be null");
        }
        if (closures.size() == 0) {
            return NOPClosure.<E>nopClosure();
        }
        // convert to array like this to guarantee iterator() ordering
        final Closure<? super E>[] cmds = new Closure[closures.size()];
        int i = 0;
        for (final Closure<? super E> closure : closures) {
            cmds[i++] = closure;
        }
        FunctorUtils.validate(cmds);
        return new ChainedClosure<E>(cmds);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param closures  the closures to chain, not copied, no nulls
     */
    public ChainedClosure(final Closure<? super E>[] closures) {
        super();
        iClosures = closures;
    }

    /**
     * Execute a list of closures.
     * 
     * @param input  the input object passed to each closure
     */
    public void execute(final E input) {
        for (final Closure<? super E> iClosure : iClosures) {
            iClosure.execute(input);
        }
    }

    /**
     * Gets the closures.
     *
     * @return a copy of the closures
     * @since 3.1
     */
    public Closure<? super E>[] getClosures() {
        return FunctorUtils.<E>copy(iClosures);
    }

}
