/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/functors/FunctorUtils.java,v 1.4 2004/01/05 21:31:48 scolebourne Exp $
 * ====================================================================
 *
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
 *
 */
package org.apache.commons.collections.functors;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

/**
 * Internal utilities for functors.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/01/05 21:31:48 $
 *
 * @author Stephen Colebourne
 */
class FunctorUtils {
    
    /**
     * Restricted constructor.
     */
    private FunctorUtils() {
    }
    
    /**
     * Clone the predicates to ensure that the internal reference can't be messed with.
     * 
     * @param predicates  the predicates to copy
     * @return the cloned predicates
     */
    static Predicate[] copy(Predicate[] predicates) {
        if (predicates == null) {
            return null;
        }
        return (Predicate[]) predicates.clone();
    }
    
    /**
     * Validate the predicates to ensure that all is well.
     * 
     * @param predicates  the predicates to validate
     */
    static void validate(Predicate[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        for (int i = 0; i < predicates.length; i++) {
            if (predicates[i] == null) {
                throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }
    
    /**
     * Validate the predicates to ensure that all is well.
     * 
     * @param predicates  the predicates to validate
     */
    static void validateMin2(Predicate[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        if (predicates.length < 2) {
            throw new IllegalArgumentException(
                "At least 2 predicates must be specified in the predicate array, size was " + predicates.length);
        }
        for (int i = 0; i < predicates.length; i++) {
            if (predicates[i] == null) {
                throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }

    /**
     * Validate the predicates to ensure that all is well.
     * 
     * @param predicates  the predicates to validate
     * @return predicate array
     */
    static Predicate[] validate(Collection predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate collection must not be null");
        }
        if (predicates.size() < 2) {
            throw new IllegalArgumentException(
                "At least 2 predicates must be specified in the predicate collection, size was " + predicates.size());
        }
        // convert to array like this to guarantee iterator() ordering
        Predicate[] preds = new Predicate[predicates.size()];
        int i = 0;
        for (Iterator it = predicates.iterator(); it.hasNext();) {
            preds[i] = (Predicate) it.next();
            if (preds[i] == null) {
                throw new IllegalArgumentException("The predicate collection must not contain a null predicate, index " + i + " was null");
            }
            i++;
        }
        return preds;
    }
    
    /**
     * Clone the closures to ensure that the internal reference can't be messed with.
     * 
     * @param closures  the closures to copy
     * @return the cloned closures
     */
    static Closure[] copy(Closure[] closures) {
        if (closures == null) {
            return null;
        }
        return (Closure[]) closures.clone();
    }
    
    /**
     * Validate the closures to ensure that all is well.
     * 
     * @param closures  the closures to validate
     */
    static void validate(Closure[] closures) {
        if (closures == null) {
            throw new IllegalArgumentException("The closure array must not be null");
        }
        for (int i = 0; i < closures.length; i++) {
            if (closures[i] == null) {
                throw new IllegalArgumentException("The closure array must not contain a null closure, index " + i + " was null");
            }
        }
    }

    /**
     * Copy method
     * 
     * @param transformers  the transformers to copy
     */
    static Transformer[] copy(Transformer[] transformers) {
        if (transformers == null) {
            return null;
        }
        return (Transformer[]) transformers.clone();
    }
    
    /**
     * Validate method
     * 
     * @param transformers  the transformers to validate
     */
    static void validate(Transformer[] transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("The transformer array must not be null");
        }
        for (int i = 0; i < transformers.length; i++) {
            if (transformers[i] == null) {
                throw new IllegalArgumentException(
                    "The transformer array must not contain a null transformer, index " + i + " was null");
            }
        }
    }

}
