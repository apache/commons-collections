/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/IntCollections.java,v 1.3 2003/08/31 17:21:15 scolebourne Exp $
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

package org.apache.commons.collections.primitives;

import org.apache.commons.collections.primitives.decorators.UnmodifiableIntIterator;
import org.apache.commons.collections.primitives.decorators.UnmodifiableIntList;
import org.apache.commons.collections.primitives.decorators.UnmodifiableIntListIterator;

/**
 * This class consists exclusively of static methods that operate on or
 * return IntCollections.
 * <p>
 * The methods of this class all throw a NullPointerException is the 
 * provided collections are null.
 * 
 * @version $Revision: 1.3 $ $Date: 2003/08/31 17:21:15 $
 * 
 * @author Rodney Waldhoff 
 */
public final class IntCollections {

    /**
     * Returns an unmodifiable IntList containing only the specified element.
     * @param value the single value
     * @return an unmodifiable IntList containing only the specified element.
     */    
    public static IntList singletonIntList(int value) {
        // TODO: a specialized implementation of IntList may be more performant
        IntList list = new ArrayIntList(1);
        list.add(value);
        return UnmodifiableIntList.wrap(list);
    }

    /**
     * Returns an unmodifiable IntIterator containing only the specified element.
     * @param value the single value
     * @return an unmodifiable IntIterator containing only the specified element.
     */    
    public static IntIterator singletonIntIterator(int value) {
        return singletonIntList(value).iterator();
    }

    /**
     * Returns an unmodifiable IntListIterator containing only the specified element.
     * @param value the single value
     * @return an unmodifiable IntListIterator containing only the specified element.
     */    
    public static IntListIterator singletonIntListIterator(int value) {
        return singletonIntList(value).listIterator();
    }

    /**
     * Returns an unmodifiable version of the given non-null IntList.
     * @param list the non-null IntList to wrap in an unmodifiable decorator
     * @return an unmodifiable version of the given non-null IntList
     * @throws NullPointerException if the given IntList is null
     * @see org.apache.commons.collections.primitives.decorators.UnmodifiableIntList#wrap
     */    
    public static IntList unmodifiableIntList(IntList list) throws NullPointerException {
        if(null == list) {
            throw new NullPointerException();
        }
        return UnmodifiableIntList.wrap(list);
    }
    
    /**
     * Returns an unmodifiable version of the given non-null IntIterator.
     * @param list the non-null IntIterator to wrap in an unmodifiable decorator
     * @return an unmodifiable version of the given non-null IntIterator
     * @throws NullPointerException if the given IntIterator is null
     * @see org.apache.commons.collections.primitives.decorators.UnmodifiableIntIterator#wrap
     */    
    public static IntIterator unmodifiableIntIterator(IntIterator iter) {
        if(null == iter) {
            throw new NullPointerException();
        }
        return UnmodifiableIntIterator.wrap(iter);
    }
        
    /**
     * Returns an unmodifiable version of the given non-null IntListIterator.
     * @param list the non-null IntListIterator to wrap in an unmodifiable decorator
     * @return an unmodifiable version of the given non-null IntListIterator
     * @throws NullPointerException if the given IntListIterator is null
     * @see org.apache.commons.collections.primitives.decorators.UnmodifiableIntListIterator#wrap
     */    
    public static IntListIterator unmodifiableIntListIterator(IntListIterator iter) {
        if(null == iter) {
            throw new NullPointerException();
        }
        return UnmodifiableIntListIterator.wrap(iter);
    }
    
    /**
     * Returns an unmodifiable, empty IntList.
     * @return an unmodifiable, empty IntList.
     * @see #EMPTY_INT_LIST
     */    
    public static IntList getEmptyIntList() {
        return EMPTY_INT_LIST;
    }
    
    /**
     * Returns an unmodifiable, empty IntIterator
     * @return an unmodifiable, empty IntIterator.
     * @see #EMPTY_INT_ITERATOR
     */    
    public static IntIterator getEmptyIntIterator() {
        return EMPTY_INT_ITERATOR;
    }
    
    /**
     * Returns an unmodifiable, empty IntListIterator
     * @return an unmodifiable, empty IntListIterator.
     * @see #EMPTY_INT_LIST_ITERATOR
     */    
    public static IntListIterator getEmptyIntListIterator() {
        return EMPTY_INT_LIST_ITERATOR;
    }    

    /**
     * An unmodifiable, empty IntList
     * @see #getEmptyIntList
     */    
    public static final IntList EMPTY_INT_LIST = unmodifiableIntList(new ArrayIntList(0));

    /**
     * An unmodifiable, empty IntIterator
     * @see #getEmptyIntIterator
     */    
    public static final IntIterator EMPTY_INT_ITERATOR = unmodifiableIntIterator(EMPTY_INT_LIST.iterator());

    /**
     * An unmodifiable, empty IntListIterator
     * @see #getEmptyIntListIterator
     */    
    public static final IntListIterator EMPTY_INT_LIST_ITERATOR = unmodifiableIntListIterator(EMPTY_INT_LIST.listIterator());
}
