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
package org.apache.commons.collections;


/** Implements an {@link java.util.Iterator} over an array of objects.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Mauricio S. Moura
  * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
  * @version $Revision: 1.17.2.1 $
  * @deprecated this class has been moved to the iterators subpackage
  */
public class ArrayIterator 
extends org.apache.commons.collections.iterators.ArrayIterator {
    
    /**
     *  Construct an ArrayIterator.  Using this constructor, the iterator is
     *  equivalent to an empty iterator until {@link #setArray(Object)} is
     *  called to establish the array to iterate over.
     **/
    public ArrayIterator() {
        super();
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
        super(array);
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
        super(array, start);
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
        super(array, start, end);
    }

}
