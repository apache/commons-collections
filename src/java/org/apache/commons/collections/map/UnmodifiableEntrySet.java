/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/map/UnmodifiableEntrySet.java,v 1.1 2003/12/03 12:27:36 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.pairs.AbstractMapEntryDecorator;
import org.apache.commons.collections.set.AbstractSetDecorator;

/**
 * Decorates a map entry <code>Set</code> to ensure it can't be altered.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/03 12:27:36 $
 * 
 * @author Stephen Colebourne
 */
public final class UnmodifiableEntrySet extends AbstractSetDecorator implements Unmodifiable {
    
    /**
     * Factory method to create an unmodifiable set of Map Entry objects.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static Set decorate(Set set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableEntrySet(set);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    private UnmodifiableEntrySet(Set set) {
        super(set);
    }

    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    public Iterator iterator() {
        return new UnmodifiableEntrySetIterator(collection.iterator());
    }
    
    public Object[] toArray() {
        Object[] array = collection.toArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = new UnmodifiableEntry((Map.Entry) array[i]);
        }
        return array;
    }
    
    public Object[] toArray(Object array[]) {
        Object[] result = array;
        if (array.length > 0) {
            // we must create a new array to handle multi-threaded situations
            // where another thread could access data before we decorate it
            result = (Object[]) Array.newInstance(array.getClass().getComponentType(), 0);
        }
        result = collection.toArray(result);
        for (int i = 0; i < result.length; i++) {
            result[i] = new UnmodifiableEntry((Map.Entry) result[i]);
        }

        // check to see if result should be returned straight
        if (result.length > array.length) {
            return result;
        }

        // copy back into input array to fulfil the method contract
        System.arraycopy(result, 0, array, 0, result.length);
        if (array.length > result.length) {
            array[result.length] = null;
        }
        return array;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set iterator.
     */
    final static class UnmodifiableEntrySetIterator extends AbstractIteratorDecorator {
        
        protected UnmodifiableEntrySetIterator(Iterator iterator) {
            super(iterator);
        }
        
        public Object next() {
            Map.Entry entry = (Map.Entry) iterator.next();
            return new UnmodifiableEntry(entry);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a map entry that is unmodifiable.
     */
    final static class UnmodifiableEntry extends AbstractMapEntryDecorator {

        protected UnmodifiableEntry(Map.Entry entry) {
            super(entry);
        }

        public Object setValue(Object o) {
            throw new UnsupportedOperationException();
        }
    }

}
