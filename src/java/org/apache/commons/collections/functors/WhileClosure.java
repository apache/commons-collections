/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

/**
 * Closure implementation that executes a closure repeatedly until a condition is met,
 * like a do-while or while loop.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2004/01/14 21:43:09 $
 *
 * @author Stephen Colebourne
 */
public class WhileClosure implements Closure, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = -3110538116913760108L;

    /** The test condition */
    private final Predicate iPredicate;
    /** The closure to call */
    private final Closure iClosure;
    /** The flag, true is a do loop, false is a while */
    private final boolean iDoLoop;

    /**
     * Factory method that performs validation.
     * 
     * @param predicate  the predicate used to evaluate when the loop terminates, not null
     * @param closure  the closure the execute, not null
     * @param doLoop  true to act as a do-while loop, always executing the closure once
     * @return the <code>while</code> closure
     * @throws IllegalArgumentException if the predicate or closure is null
     */
    public static Closure getInstance(Predicate predicate, Closure closure, boolean doLoop) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new WhileClosure(predicate, closure, doLoop);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicate  the predicate used to evaluate when the loop terminates, not null
     * @param closure  the closure the execute, not null
     * @param doLoop  true to act as a do-while loop, always executing the closure once
     */
    public WhileClosure(Predicate predicate, Closure closure, boolean doLoop) {
        super();
        iPredicate = predicate;
        iClosure = closure;
        iDoLoop = doLoop;
    }

    /**
     * Execute the closure until the predicate is false.
     */
    public void execute(Object input) {
        if (iDoLoop) {
            iClosure.execute(input);
        }
        while (iPredicate.evaluate(input)) {
            iClosure.execute(input);
        }
    }
    
}

