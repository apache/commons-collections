/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Implements {@link Iterator} over an array of objects
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Mauricio S. Moura
  * @
  * @version $Revision: 1.3 $
  */
public class ArrayIterator implements Iterator {
    
    private Object[] array;
    private int index = 0;
  
    
    public ArrayIterator() {
    }
    
    public ArrayIterator(Object[] array) {
        this.array = array;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        return  (index < array.length);
    }

    public Object next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return array[ index++ ];
    }

    public void remove() {
        throw new UnsupportedOperationException( "remove() method is not supported" );
    }

    // Properties
    //-------------------------------------------------------------------------
    public Object[] getArray() {
        return array;
    }
    
    public void setArray( Object[] array ) {
        this.array = array;
        this.index = -1;
    }
}
