/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/functors/SwitchClosure.java,v 1.1 2003/11/23 17:01:35 scolebourne Exp $
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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

/**
 * Closure implementation calls the closure whose predicate returns true,
 * like a switch statement.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/23 17:01:35 $
 *
 * @author Stephen Colebourne
 */
public class SwitchClosure implements Closure, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = 3518477308466486130L;

    /** The tests to consider */
    private final Predicate[] iPredicates;
    /** The matching closures to call */
    private final Closure[] iClosures;
    /** The default closure to call if no tests match */
    private final Closure iDefault;

    /**
     * Factory method that performs validation and copies the parameter arrays.
     * 
     * @param predicates  array of predicates, cloned, no nulls
     * @param closures  matching array of closures, cloned, no nulls
     * @param defaultClosure  the closure to use if no match, null means nop
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if array is null
     * @throws IllegalArgumentException if any element in the array is null
     */
    public static Closure getInstance(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(closures);
        if (predicates.length != closures.length) {
            throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
        }
        if (predicates.length == 0) {
            return (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
        }
        predicates = FunctorUtils.copy(predicates);
        closures = FunctorUtils.copy(closures);
        return new SwitchClosure(predicates, closures, defaultClosure);
    }

    /**
     * Create a new Closure that calls one of the closures depending 
     * on the predicates. 
     * <p>
     * The Map consists of Predicate keys and Closure values. A closure 
     * is called if its matching predicate returns true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * closure is called. The default closure is set in the map with a 
     * null key. The ordering is that of the iterator() method on the entryset 
     * collection of the map.
     * 
     * @param predicatesAndClosures  a map of predicates to closures
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if any closure in the map is null
     * @throws ClassCastException  if the map elements are of the wrong type
     */
    public static Closure getInstance(Map predicatesAndClosures) {
        Closure[] closures = null;
        Predicate[] preds = null;
        if (predicatesAndClosures == null) {
            throw new IllegalArgumentException("The predicate and closure map must not be null");
        }
        if (predicatesAndClosures.size() == 0) {
            return NOPClosure.INSTANCE;
        }
        // convert to array like this to guarantee iterator() ordering
        Closure defaultClosure = (Closure) predicatesAndClosures.remove(null);
        int size = predicatesAndClosures.size();
        if (size == 0) {
            return (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
        }
        closures = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        for (Iterator it = predicatesAndClosures.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            preds[i] = (Predicate) entry.getKey();
            closures[i] = (Closure) entry.getValue();
            i++;
        }
        return new SwitchClosure(preds, closures, defaultClosure);
    }
    
    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicates  array of predicates, not cloned, no nulls
     * @param closures  matching array of closures, not cloned, no nulls
     * @param defaultClosure  the closure to use if no match, null means nop
     */
    public SwitchClosure(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
        super();
        iPredicates = predicates;
        iClosures = closures;
        iDefault = (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
    }

    /**
     * Execute the closure whose predicate returns true
     */
    public void execute(Object input) {
        for (int i = 0; i < iPredicates.length; i++) {
            if (iPredicates[i].evaluate(input) == true) {
                iClosures[i].execute(input);
                return;
            }
        }
        iDefault.execute(input);
    }
    
}
