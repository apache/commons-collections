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
 * Closure implementation acts as an if statement calling one or other closure
 * based on a predicate.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/01/14 21:43:09 $
 *
 * @author Stephen Colebourne
 */
public class IfClosure implements Closure, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = 3518477308466486130L;

    /** The test */
    private final Predicate iPredicate;
    /** The closure to use if true */
    private final Closure iTrueClosure;
    /** The closure to use if false */
    private final Closure iFalseClosure;

    /**
     * Factory method that performs validation.
     * 
     * @param predicate  predicate to switch on
     * @param trueClosure  closure used if true
     * @param falseClosure  closure used if false
     * @return the <code>if</code> closure
     * @throws IllegalArgumentException if any argument is null
     */
    public static Closure getInstance(Predicate predicate, Closure trueClosure, Closure falseClosure) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (trueClosure == null || falseClosure == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        return new IfClosure(predicate, trueClosure, falseClosure);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicate  predicate to switch on, not null
     * @param trueClosure  closure used if true, not null
     * @param falseClosure  closure used if false, not null
     */
    public IfClosure(Predicate predicate, Closure trueClosure, Closure falseClosure) {
        super();
        iPredicate = predicate;
        iTrueClosure = trueClosure;
        iFalseClosure = falseClosure;
    }

    /**
     * Execute the correct closure.
     */
    public void execute(Object input) {
        if (iPredicate.evaluate(input) == true) {
            iTrueClosure.execute(input);
        } else {
            iFalseClosure.execute(input);
        }
    }
    
}
