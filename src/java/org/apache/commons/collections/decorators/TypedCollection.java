/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/TypedCollection.java,v 1.2 2003/05/05 23:25:22 rwaldhoff Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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
package org.apache.commons.collections.decorators;

import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * <code>TypedCollection</code> decorates another <code>Collection</code>
 * to validate that elements added are of a specific type.
 * <p>
 * The validation of additions is performed via an instanceof test against 
 * a specified <code>Class</code>. If an object cannot be addded to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/05/05 23:25:22 $
 * 
 * @author Stephen Colebourne
 * @author Matthew Hawthorne
 */
public class TypedCollection extends PredicatedCollection {

    /**
     * Factory method to create a typed collection.
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     * 
     * @param coll  the collection to decorate, must not be null
     * @param type  the type to allow into the collection, must not be null
     * @throws IllegalArgumentException if collection or type is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    public static Collection decorate(Collection coll, Class type) {
        return new TypedCollection(coll, type);
    }
    
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     * 
     * @param coll  the collection to decorate, must not be null
     * @param type  the type to allow into the collection, must not be null
     * @throws IllegalArgumentException if collection or type is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    protected TypedCollection(Collection coll, Class type) {
        super(coll, getPredicate(type));
    }

    /**
     * Gets a predicate to validate the additions to the collection.
     * 
     * @param type  the type, must not be null
     * @throws IllegalArgumentException if type is null
     */
    static Predicate getPredicate(Class type) {
        return new InstanceofPredicate(type);
    }

    /**
     * <code>Predicate</code> implementation that checks the type of an object.
     * This class may eventually be replaced by 
     * <code>org.apache.commons.lang.functor.PredicateUtils.instanceofPredicate()</code>.
     */
    static class InstanceofPredicate implements Predicate {
        /** The type to check for */
        private final Class type;

        /**
         * Constructor
         */
        public InstanceofPredicate(Class type) {
            if (type == null) {
                throw new IllegalArgumentException("Type must not be null");
            }
            this.type = type;
        }

        /**
         * Return true if the object is an instanceof the type of the predicate.
         * @param object an <code>Object</code>
         * @return <code>true</code> if the object is an instanceof the type of the predicate
         */
        public boolean evaluate(Object object) {
            return type.isInstance(object);
        }
    }

}
