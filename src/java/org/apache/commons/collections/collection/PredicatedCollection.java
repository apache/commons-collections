/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.collection;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.Predicate;

/**
 * Decorates another <code>Collection</code> to validate that additions
 * match a specified predicate.
 * <p>
 * If an object cannot be added to the collection, an IllegalArgumentException
 * is thrown.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2004/01/14 21:43:18 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedCollection extends AbstractCollectionDecorator {

    /** The predicate to use */
    protected final Predicate predicate;

    /**
     * Factory method to create a predicated (validating) collection.
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     * 
     * @param coll  the collection to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if collection or predicate is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    public static Collection decorate(Collection coll, Predicate predicate) {
        return new PredicatedCollection(coll, predicate);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     * 
     * @param coll  the collection to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if collection or predicate is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    protected PredicatedCollection(Collection coll, Predicate predicate) {
        super(coll);
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        this.predicate = predicate;
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            validate(it.next());
        }
    }

    /**
     * Validates the object being added to ensure it matches the predicate.
     * <p>
     * The predicate itself should not throw an exception, but return false to
     * indicate that the object cannot be added.
     * 
     * @param object  the object being added
     * @throws IllegalArgumentException if the add is invalid
     */
    protected void validate(Object object) {
        if (predicate.evaluate(object) == false) {
            throw new IllegalArgumentException("Cannot add Object '" + object + "' - Predicate rejected it");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Override to validate the object being added to ensure it matches
     * the predicate.
     * 
     * @param object  the object being added
     * @return the result of adding to the underlying collection
     * @throws IllegalArgumentException if the add is invalid
     */
    public boolean add(Object object) {
        validate(object);
        return getCollection().add(object);
    }

    /**
     * Override to validate the objects being added to ensure they match
     * the predicate. If any one fails, no update is made to the underlying
     * collection.
     * 
     * @param coll  the collection being added
     * @return the result of adding to the underlying collection
     * @throws IllegalArgumentException if the add is invalid
     */
    public boolean addAll(Collection coll) {
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            validate(it.next());
        }
        return getCollection().addAll(coll);
    }

}
