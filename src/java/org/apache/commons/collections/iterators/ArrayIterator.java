/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.iterators;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Implements an {@link Iterator} over an array of objects.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Mauricio S. Moura
  * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
  * @version $Revision: 1.1.2.1 $
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

    /**
     *  Construct an ArrayIterator that will iterate over the values in the
     *  specified array.
     *
     *  @param array the array to iterate over.
     *  @param start the index to start iterating at.
     *
     *  @exception IllegalArgumentException if <code>array</code> is not an
     *  array.
     *
     *  @exception NullPointerException 
     *  if <code>array</code> is <code>null</code>
     **/
    public ArrayIterator(Object array, int start) {
        setArray( array );
        checkBound(start, "start");
        this.index = start;
    }

    /**
     *  Construct an ArrayIterator that will iterate over the values in the
     *  specified array.
     *
     *  @param array the array to iterate over.
     *  @param start the index to start iterating at.
     *  @param end the index to finish iterating at.
     *
     *  @exception IllegalArgumentException if <code>array</code> is not an
     *  array.
     *
     *  @exception NullPointerException 
     *  if <code>array</code> is <code>null</code>
     **/
    public ArrayIterator(Object array, int start, int end) {
        setArray( array );
        checkBound(start, "start");
        checkBound(end, "end");
        if(end <= start) {
            throw new IllegalArgumentException(
                "End index must be greater than start index. "
            );
        }
        this.index = start;
        this.length = end;
    }

    private void checkBound(int bound, String type ) {
        if(bound > this.length) {
            throw new ArrayIndexOutOfBoundsException(
              "Attempt to make an ArrayIterator that "+type+
              "s beyond the end of the array. "
            );
        }
        if(bound < 0) {
            throw new ArrayIndexOutOfBoundsException(
              "Attempt to make an ArrayIterator that "+type+
              "s before the start of the array. "
            );
        }
    }

    // Iterator interface
    //-------------------------------------------------------------------------

    /**
     *  Returns true if there are more elements to return from the array.
     *
     *  @return true if there is a next element to return
     */
    public boolean hasNext() {
        return index < length;
    }

    /**
     *  Returns the next element in the array.
     *
     *  @return the next element in the array
     *  @throws NoSuchElementException if all the elements in the array
     *    have already been returned
     */
    public Object next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return Array.get( array, index++ );
    }

    /**
     *  Throws {@link UnsupportedOperationException}.
     *
     *  @throws UnsupportedOperationException always
     */
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
