/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/AbstractByteCollectionCollection.java,v 1.1 2003/04/15 01:55:22 rwaldhoff Exp $
 * ====================================================================
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

package org.apache.commons.collections.primitives.adapters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.primitives.ByteCollection;

/**
 * @since Commons Collections 2.2
 * @version $Revision: 1.1 $ $Date: 2003/04/15 01:55:22 $
 * @author Rodney Waldhoff 
 */
abstract class AbstractByteCollectionCollection implements Collection {
    
    public boolean add(Object element) {
        return getByteCollection().add(((Number)element).byteValue());
    }

    public boolean addAll(Collection c) {
        return getByteCollection().addAll(CollectionByteCollection.wrap(c));
    }
        
    public void clear() {
        getByteCollection().clear();
    }

    public boolean contains(Object element) {
        return getByteCollection().contains(((Number)element).byteValue());
    }
   
    
    public boolean containsAll(Collection c) {
        return getByteCollection().containsAll(CollectionByteCollection.wrap(c));
    }        
        
    public String toString() {
        return getByteCollection().toString();
    }
    
    public boolean isEmpty() {
        return getByteCollection().isEmpty();
    }
    
    /**
     * {@link ByteIteratorIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.ByteIterator ByteIterator}
     * returned by my underlying 
     * {@link ByteCollection ByteCollection}, 
     * if any.
     */
    public Iterator iterator() {
        return ByteIteratorIterator.wrap(getByteCollection().iterator());
    }
     
    public boolean remove(Object element) {
        return getByteCollection().removeElement(((Number)element).byteValue());
    }
    
    public boolean removeAll(Collection c) {
        return getByteCollection().removeAll(CollectionByteCollection.wrap(c));
    }
    
    public boolean retainAll(Collection c) {
        return getByteCollection().retainAll(CollectionByteCollection.wrap(c));
    }
    
    public int size() {
        return getByteCollection().size();
    }
    
    public Object[] toArray() {
        byte[] a = getByteCollection().toArray();
        Object[] A = new Object[a.length];
        for(int i=0;i<a.length;i++) {
            A[i] = new Byte(a[i]);
        }
        return A;
    }
    
    public Object[] toArray(Object[] A) {
        byte[] a = getByteCollection().toArray();
        if(A.length < a.length) {
            A = (Object[])(Array.newInstance(A.getClass().getComponentType(), a.length));
        }
        for(int i=0;i<a.length;i++) {
            A[i] = new Byte(a[i]);
        }
        if(A.length > a.length) {
            A[a.length] = null;
        }

        return A;
    }

    protected abstract ByteCollection getByteCollection();            
}
