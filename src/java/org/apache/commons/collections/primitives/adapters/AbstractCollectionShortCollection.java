/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/AbstractCollectionShortCollection.java,v 1.1 2003/04/11 00:55:35 rwaldhoff Exp $
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

import java.util.Collection;

import org.apache.commons.collections.primitives.ShortCollection;
import org.apache.commons.collections.primitives.ShortIterator;

/**
 * @since Commons Collections 2.2
 * @version $Revision: 1.1 $ $Date: 2003/04/11 00:55:35 $
 * @author Rodney Waldhoff 
 */
abstract class AbstractCollectionShortCollection implements ShortCollection {
    protected AbstractCollectionShortCollection() {
    }

    public boolean add(short element) {
        return getCollection().add(new Short(element));
    }
        
    public boolean addAll(ShortCollection c) {
        return getCollection().addAll(ShortCollectionCollection.wrap(c));
    }
    
    public void clear() {
        getCollection().clear();
    }

    public boolean contains(short element) {
        return getCollection().contains(new Short(element));
    }
    
    public boolean containsAll(ShortCollection c) {
        return getCollection().containsAll(ShortCollectionCollection.wrap(c));
    }        
    
    public String toString() {
        return getCollection().toString();
    }

    public boolean isEmpty() {
        return getCollection().isEmpty();
    }
    
    /**
     * {@link IteratorShortIterator#wrap wraps} the 
     * {@link java.util.Iterator Iterator}
     * returned by my underlying 
     * {@link Collection Collection}, 
     * if any.
     */
    public ShortIterator iterator() {
        return IteratorShortIterator.wrap(getCollection().iterator());
    }
     
    public boolean removeElement(short element) {
        return getCollection().remove(new Short(element));
    }
    
    public boolean removeAll(ShortCollection c) {
        return getCollection().removeAll(ShortCollectionCollection.wrap(c));
    }
        
    public boolean retainAll(ShortCollection c) {
        return getCollection().retainAll(ShortCollectionCollection.wrap(c));
    }
    
    public int size() {
        return getCollection().size();
    }
    
    public short[] toArray() {
        Object[] src = getCollection().toArray();
        short[] dest = new short[src.length];
        for(int i=0;i<src.length;i++) {
            dest[i] = ((Number)(src[i])).shortValue();
        }
        return dest;
    }
    
    public short[] toArray(short[] dest) {
        Object[] src = getCollection().toArray();
        if(dest.length < src.length) {
            dest = new short[src.length];
        }
        for(int i=0;i<src.length;i++) {
            dest[i] = ((Number)(src[i])).shortValue();
        }
        return dest;
    }
    
    protected abstract Collection getCollection();
    
}
