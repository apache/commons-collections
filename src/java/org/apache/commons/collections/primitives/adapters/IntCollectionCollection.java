/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/IntCollectionCollection.java,v 1.4 2003/02/26 19:17:23 rwaldhoff Exp $
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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.primitives.IntCollection;

/**
 * Adapts an {@link IntCollection IntCollection}
 * to the {@link java.util.Collection Collection}
 * interface.
 * <p />
 * This implementation delegates most methods
 * to the provided {@link IntCollection IntCollection} 
 * implementation in the "obvious" way.
 * 
 * @since Commons Collections 2.2
 * @version $Revision: 1.4 $ $Date: 2003/02/26 19:17:23 $
 * @author Rodney Waldhoff 
 */
public class IntCollectionCollection implements Collection, Serializable {
    
    /**
     * Create a {@link Collection Collection} wrapping
     * the specified {@link IntCollection IntCollection}.  When
     * the given <i>collection</i> is <code>null</code>,
     * returns <code>null</code>.
     * 
     * @param collection the (possibly <code>null</code>) 
     *        {@link IntCollection IntCollection} to wrap
     * @return a {@link Collection Collection} wrapping the given 
     *         <i>collection</i>, or <code>null</code> when <i>collection</i> is
     *         <code>null</code>.
     */
    public static Collection wrap(IntCollection collection) {
        return null == collection ? null : new IntCollectionCollection(collection);
    }
    
    /**
     * No-arg constructor, for serialization purposes.
     */
    protected IntCollectionCollection() {
    }

    /**
     * Creates a {@link Collection Collection} wrapping
     * the specified {@link IntCollection IntCollection}.
     * @see #wrap
     */
    public IntCollectionCollection(IntCollection collection) {
        _collection = collection;
    }
    
    public boolean add(Object element) {
        return _collection.add(((Number)element).intValue());
    }

    public boolean addAll(Collection c) {
        return _collection.addAll(CollectionIntCollection.wrap(c));
    }
        
    public void clear() {
        _collection.clear();
    }

    public boolean contains(Object element) {
        return _collection.contains(((Number)element).intValue());
    }
   
    
    public boolean containsAll(Collection c) {
        return _collection.containsAll(CollectionIntCollection.wrap(c));
    }        
    
    /**
     * If <i>that</i> is a {@link Collection Collection}, 
     * it is {@link CollectionIntCollection#wrap wrapped} and
     * compared to my underlying 
     * {@link org.apache.commons.collections.primitives.IntCollection IntCollection},
     * otherwise this method simply delegates to my underlying 
     * <code>IntCollection</code>.
     */
    public boolean equals(Object that) {
        if(that instanceof Collection) {
            try {
                return _collection.equals(CollectionIntCollection.wrap((Collection)that));
            } catch(ClassCastException e) {
                return false;
            } catch(NullPointerException e) {
                return false;
            }
        } else {
            return _collection.equals(that);
        }
    }
    
    public int hashCode() {
        return _collection.hashCode();
    }
    
    public String toString() {
        return _collection.toString();
    }
    
    public boolean isEmpty() {
        return _collection.isEmpty();
    }
    
    /**
     * {@link IntIteratorIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.IntIterator IntIterator}
     * returned by my underlying 
     * {@link IntCollection IntCollection}, 
     * if any.
     */
    public Iterator iterator() {
        return IntIteratorIterator.wrap(_collection.iterator());
    }
     
    public boolean remove(Object element) {
        return _collection.removeElement(((Number)element).intValue());
    }
    
    public boolean removeAll(Collection c) {
        return _collection.removeAll(CollectionIntCollection.wrap(c));
    }
    
    public boolean retainAll(Collection c) {
        return _collection.retainAll(CollectionIntCollection.wrap(c));
    }
    
    public int size() {
        return _collection.size();
    }
    
    public Object[] toArray() {
        int[] a = _collection.toArray();
        Object[] A = new Object[a.length];
        for(int i=0;i<a.length;i++) {
            A[i] = new Integer(a[i]);
        }
        return A;
    }
    
    public Object[] toArray(Object[] A) {
        int[] a = _collection.toArray();
        if(A.length < a.length) {
            A = (Object[])(Array.newInstance(A.getClass().getComponentType(), a.length));
        }
        for(int i=0;i<a.length;i++) {
            A[i] = new Integer(a[i]);
        }
        if(A.length > a.length) {
            A[a.length] = null;
        }

        return A;
    }
    
    private IntCollection _collection = null;
}
