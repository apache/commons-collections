/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/AbstractCollectionIntCollection.java,v 1.2 2003/08/31 17:21:17 scolebourne Exp $
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

import java.util.Collection;

import org.apache.commons.collections.primitives.IntCollection;
import org.apache.commons.collections.primitives.IntIterator;

/**
 * @since Commons Collections 2.2
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:21:17 $
 * @author Rodney Waldhoff 
 */
abstract class AbstractCollectionIntCollection implements IntCollection {
    protected AbstractCollectionIntCollection() {
    }

    public boolean add(int element) {
        return getCollection().add(new Integer(element));
    }
        
    public boolean addAll(IntCollection c) {
        return getCollection().addAll(IntCollectionCollection.wrap(c));
    }
    
    public void clear() {
        getCollection().clear();
    }

    public boolean contains(int element) {
        return getCollection().contains(new Integer(element));
    }
    
    public boolean containsAll(IntCollection c) {
        return getCollection().containsAll(IntCollectionCollection.wrap(c));
    }        
    
    public String toString() {
        return getCollection().toString();
    }

    public boolean isEmpty() {
        return getCollection().isEmpty();
    }
    
    /**
     * {@link IteratorIntIterator#wrap wraps} the 
     * {@link java.util.Iterator Iterator}
     * returned by my underlying 
     * {@link Collection Collection}, 
     * if any.
     */
    public IntIterator iterator() {
        return IteratorIntIterator.wrap(getCollection().iterator());
    }
     
    public boolean removeElement(int element) {
        return getCollection().remove(new Integer(element));
    }
    
    public boolean removeAll(IntCollection c) {
        return getCollection().removeAll(IntCollectionCollection.wrap(c));
    }
        
    public boolean retainAll(IntCollection c) {
        return getCollection().retainAll(IntCollectionCollection.wrap(c));
    }
    
    public int size() {
        return getCollection().size();
    }
    
    public int[] toArray() {
        Object[] src = getCollection().toArray();
        int[] dest = new int[src.length];
        for(int i=0;i<src.length;i++) {
            dest[i] = ((Number)(src[i])).intValue();
        }
        return dest;
    }
    
    public int[] toArray(int[] dest) {
        Object[] src = getCollection().toArray();
        if(dest.length < src.length) {
            dest = new int[src.length];
        }
        for(int i=0;i<src.length;i++) {
            dest[i] = ((Number)(src[i])).intValue();
        }
        return dest;
    }
    
    protected abstract Collection getCollection();
    
}
