/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/ArrayIterator.java,v 1.12 2002/03/19 01:33:12 mas Exp $
 * $Revision: 1.12 $
 * $Date: 2002/03/19 01:33:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
package org.apache.commons.collections;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Implements an {@link Iterator} over an array of objects.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Mauricio S. Moura
  * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
  * @version $Revision: 1.12 $
  */
public class ArrayIterator implements Iterator {
    
    private Object array;
    private int length = 0;
    private int index = 0;
  
    
    /**
     *  Construct an ArrayIterator.  Using this constructor, the iterator is
     *  equivalent to an empty iterator until {@link #setArray(Object)} is
     *  called to establish the array to iterate over.
     **/
    public ArrayIterator() {
    }
   
    /**
     *  Construct an ArrayIterator that will iterate over the values in the
     *  specified array.
     *
     *  @param array the array to iterate over.
     *
     *  @exception IllegalArgumentException if <code>array</code> is not an
     *  array.
     *
     *  @exception NullPointerException 
     *  if <code>array</code> is <code>null</code>
     **/
    public ArrayIterator(Object array) {
        setArray( array );
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        return index < length;
    }

    public Object next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return Array.get( array, index++ );
    }

    public void remove() {
        throw new UnsupportedOperationException( "remove() method is not supported" );
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     *  Retrieves the array that this iterator is iterating over. 
     *
     *  @return the array this iterator iterates over, or <code>null</code> if
     *  the no-arg constructor was used and {@link #setArray(Object)} has never
     *  been called with a valid array.
     **/
    public Object getArray() {
        return array;
    }
    
    /**
     *  Changes the array that the ArrayIterator should iterate over.  If an
     *  array has previously been set (using the single-arg constructor or this
     *  method), that array along with the current iterator position within
     *  that array is discarded in favor of the argument to this method.  This
     *  method can be used in combination with {@link #getArray()} to "reset"
     *  the iterator to the beginning of the array:
     *
     *  <pre>
     *    ArrayIterator iterator = ...
     *    ...
     *    iterator.setArray(iterator.getArray());
     *  </pre>
     *
     *  Note: Using i.setArray(i.getArray()) may throw a NullPointerException
     *  if no array has ever been set for the iterator (see {@link
     *  #getArray()})
     *
     *  @param array the array that the iterator should iterate over.
     *
     *  @exception IllegalArgumentException if <code>array</code> is not an
     *  array.
     *
     *  @exception NullPointerException 
     *  if <code>array</code> is <code>null</code>
     **/
    public void setArray( Object array ) {
        // Array.getLength throws IllegalArgumentException if the object is not
        // an array or NullPointerException if the object is null.  This call
        // is made before saving the array and resetting the index so that the
        // array iterator remains in a consistent state if the argument is not
        // an array or is null.
        this.length = Array.getLength( array );
        this.array = array;
        this.index = 0;
    }
}
