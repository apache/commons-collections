/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/AbstractDoubleCollectionCollection.java,v 1.3 2003/11/07 20:09:15 rwaldhoff Exp $
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

package org.apache.commons.collections.primitives.adapters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.primitives.DoubleCollection;

/**
 * 
 * @deprecated This code has been moved to Jakarta Commons Primitives (http://jakarta.apache.org/commons/primitives/)
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.3 $ $Date: 2003/11/07 20:09:15 $
 * @author Rodney Waldhoff 
 */
abstract class AbstractDoubleCollectionCollection implements Collection {
    
    public boolean add(Object element) {
        return getDoubleCollection().add(((Number)element).doubleValue());
    }

    public boolean addAll(Collection c) {
        return getDoubleCollection().addAll(CollectionDoubleCollection.wrap(c));
    }
        
    public void clear() {
        getDoubleCollection().clear();
    }

    public boolean contains(Object element) {
        return getDoubleCollection().contains(((Number)element).doubleValue());
    }
   
    
    public boolean containsAll(Collection c) {
        return getDoubleCollection().containsAll(CollectionDoubleCollection.wrap(c));
    }        
        
    public String toString() {
        return getDoubleCollection().toString();
    }
    
    public boolean isEmpty() {
        return getDoubleCollection().isEmpty();
    }
    
    /**
     * {@link DoubleIteratorIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.DoubleIterator DoubleIterator}
     * returned by my underlying 
     * {@link DoubleCollection DoubleCollection}, 
     * if any.
     */
    public Iterator iterator() {
        return DoubleIteratorIterator.wrap(getDoubleCollection().iterator());
    }
     
    public boolean remove(Object element) {
        return getDoubleCollection().removeElement(((Number)element).doubleValue());
    }
    
    public boolean removeAll(Collection c) {
        return getDoubleCollection().removeAll(CollectionDoubleCollection.wrap(c));
    }
    
    public boolean retainAll(Collection c) {
        return getDoubleCollection().retainAll(CollectionDoubleCollection.wrap(c));
    }
    
    public int size() {
        return getDoubleCollection().size();
    }
    
    public Object[] toArray() {
        double[] a = getDoubleCollection().toArray();
        Object[] A = new Object[a.length];
        for(int i=0;i<a.length;i++) {
            A[i] = new Double(a[i]);
        }
        return A;
    }
    
    public Object[] toArray(Object[] A) {
        double[] a = getDoubleCollection().toArray();
        if(A.length < a.length) {
            A = (Object[])(Array.newInstance(A.getClass().getComponentType(), a.length));
        }
        for(int i=0;i<a.length;i++) {
            A[i] = new Double(a[i]);
        }
        if(A.length > a.length) {
            A[a.length] = null;
        }

        return A;
    }

    protected abstract DoubleCollection getDoubleCollection();            
}
