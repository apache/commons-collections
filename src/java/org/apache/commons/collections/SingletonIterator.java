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

/** <p><code>SingleIterator</code> is an {@link Iterator} over a single 
  * object instance.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class SingletonIterator implements Iterator {

    private boolean first = true;
    private Object object;
    
    public SingletonIterator(Object object) {
        this.object = object;
    }

    public boolean hasNext() {
        return first;
    }

    public Object next() {
        if (! first ) {
            throw new NoSuchElementException();
        }
        Object answer = object;
        object = null;
        first = false;
        return answer;
    }

    public void remove() {
        throw new UnsupportedOperationException( "remove() is not supported by this iterator" );
    }
}
