/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/functors/ChainedClosure.java,v 1.1 2003/11/23 17:01:35 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.Closure;

/**
 * Closure implementation that chains the specifed closures together.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/23 17:01:35 $
 *
 * @author Stephen Colebourne
 */
public class ChainedClosure implements Closure, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = -3520677225766901240L;

    /** The closures to call in turn */
    private final Closure[] iClosures;

    /**
     * Factory method that performs validation and copies the parameter array.
     * 
     * @param closures  the closures to chain, copied, no nulls
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures array is null
     * @throws IllegalArgumentException if any closure in the array is null
     */
    public static Closure getInstance(Closure[] closures) {
        FunctorUtils.validate(closures);
        if (closures.length == 0) {
            return NOPClosure.INSTANCE;
        }
        closures = FunctorUtils.copy(closures);
        return new ChainedClosure(closures);
    }
    
    /**
     * Create a new Closure that calls each closure in turn, passing the 
     * result into the next closure. The ordering is that of the iterator()
     * method on the collection.
     * 
     * @param closures  a collection of closures to chain
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures collection is null
     * @throws IllegalArgumentException if any closure in the collection is null
     */
    public static Closure getInstance(Collection closures) {
        if (closures == null) {
            throw new IllegalArgumentException("Closure collection must not be null");
        }
        if (closures.size() == 0) {
            return NOPClosure.INSTANCE;
        }
        // convert to array like this to guarantee iterator() ordering
        Closure[] cmds = new Closure[closures.size()];
        int i = 0;
        for (Iterator it = closures.iterator(); it.hasNext();) {
            cmds[i++] = (Closure) it.next();
        }
        FunctorUtils.validate(cmds);
        return new ChainedClosure(cmds);
    }

    /**
     * Factory method that performs validation.
     * 
     * @param closure1  the first closure, not null
     * @param closure2  the second closure, not null
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if either closure is null
     */
    public static Closure getInstance(Closure closure1, Closure closure2) {
        if (closure1 == null || closure2 == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        Closure[] closures = new Closure[] { closure1, closure2 };
        return new ChainedClosure(closures);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param closures  the closures to chain, not copied, no nulls
     */
    public ChainedClosure(Closure[] closures) {
        super();
        iClosures = closures;
    }

    /**
     * Execute a list of closures.
     * 
     * @param input  the input object passed to each closure
     */
    public void execute(Object input) {
        for (int i = 0; i < iClosures.length; i++) {
            iClosures[i].execute(input);
        }
    }
    
}
