/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/LazyList.java,v 1.4 2003/11/16 00:39:37 scolebourne Exp $
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

import java.util.List;

import org.apache.commons.collections.Factory;

/**
 * <code>LazyList</code> decorates another <code>List</code>
 * to create objects in the list on demand.
 * <p>
 * When the {@link #get(int)} method is called with an index greater than
 * the size of the list, the list will automatically grow in size and return
 * a new object from the specified factory. The gaps will be filled by null.
 * If a get method call encounters a null, it will be replaced with a new
 * object from the factory. Thus this list is unsuitable for storing null
 * objects.
 * <p>
 * For instance:
 *
 * <pre>
 * Factory factory = new Factory() {
 *     public Object create() {
 *         return new Date();
 *     }
 * }
 * List lazy = LazyList.decorate(new ArrayList(), factory);
 * Object obj = lazy.get(3);
 * </pre>
 *
 * After the above code is executed, <code>obj</code> will contain
 * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
 * instance is the fourth element in the list.  The first, second, 
 * and third element are all set to <code>null</code>.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/11/16 00:39:37 $
 * @deprecated TO BE REMOVED BEFORE v3.0
 * 
 * @author Stephen Colebourne
 * @author Arron Bates
 * @author Paul Jack
 */
public class LazyList extends AbstractListDecorator {
    
    /** The factory to use to lazily instantiate the objects */
    protected final Factory factory;

    /**
     * Factory method to create a lazily instantiating list.
     * 
     * @param list  the list to decorate, must not be null
     * @param factory  the factory to use for creation, must not be null
     * @throws IllegalArgumentException if list or factory is null
     */
    public static List decorate(List list, Factory factory) {
        return new LazyList(list, factory);
    }
    
    /**
     * Constructor that wraps (not copies).
     * 
     * @param list  the list to decorate, must not be null
     * @param factory  the factory to use for creation, must not be null
     * @throws IllegalArgumentException if list or factory is null
     */
    protected LazyList(List list, Factory factory) {
        super(list);
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        this.factory = factory;
    }

    //-----------------------------------------------------------------------
    /**
     * Decorate the get method to perform the lazy behaviour.
     * <p>
     * If the requested index is greater than the current size, the list will 
     * grow to the new size and a new object will be returned from the factory.
     * Indexes inbetween the old size and the requested size are left with a 
     * placeholder that is replaced with a factory object when requested.
     * 
     * @param index  the index to retrieve
     */
    public Object get(int index) {
        int size = getList().size();
        if (index < size) {
            // within bounds, get the object
            Object object = getList().get(index);
            if (object == null) {
                // item is a place holder, create new one, set and return
                object = factory.create();
                getList().set(index, object);
                return object;
            } else {
                // good and ready to go
                return object;
            }
        } else {
            // we have to grow the list
            for (int i = size; i < index; i++) {
                getList().add(null);
            }
            // create our last object, set and return
            Object object = factory.create();
            getList().add(object);
            return object;
        }
    }


    public List subList(int fromIndex, int toIndex) {
        List sub = getList().subList(fromIndex, toIndex);
        return new LazyList(sub, factory);
    }

}
