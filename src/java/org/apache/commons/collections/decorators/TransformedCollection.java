/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/TransformedCollection.java,v 1.3 2003/08/31 17:24:46 scolebourne Exp $
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
package org.apache.commons.collections.decorators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Transformer;

/**
 * <code>TransformedCollection</code> decorates another <code>Collection</code>
 * to transform objects that are added.
 * <p>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/08/31 17:24:46 $
 * 
 * @author Stephen Colebourne
 */
public class TransformedCollection extends AbstractCollectionDecorator {

    /** The transformer to use */
    protected final Transformer transformer;

    /**
     * Factory method to create a transforming collection.
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are NOT transformed.
     * 
     * @param coll  the collection to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if collection or transformer is null
     */
    public static Collection decorate(Collection coll, Transformer transformer) {
        return new TransformedCollection(coll, transformer);
    }
    
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are NOT transformed.
     * 
     * @param coll  the collection to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if collection or transformer is null
     */
    protected TransformedCollection(Collection coll, Transformer transformer) {
        super(coll);
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        this.transformer = transformer;
    }

    /**
     * Transforms an object.
     * <p>
     * The transformer itself may throw an exception if necessary.
     * 
     * @param object  the object to transform
     * @throws the transformed object
     */
    protected Object transform(Object object) {
        return transformer.transform(object);
    }

    /**
     * Transforms a collection.
     * <p>
     * The transformer itself may throw an exception if necessary.
     * 
     * @param coll  the collection to transform
     * @throws the transformed object
     */
    protected Collection transform(Collection coll) {
        List list = new ArrayList(coll.size());
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            list.add(transform(it.next()));
        }
        return list;
    }

    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        object = transform(object);
        return getCollection().add(object);
    }

    public boolean addAll(Collection coll) {
        coll = transform(coll);
        return getCollection().addAll(coll);
    }

}
