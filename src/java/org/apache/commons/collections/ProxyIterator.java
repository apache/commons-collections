/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.util.Enumeration;
import java.util.Iterator;

/** A Proxy {@link Iterator Iterator} which delegates its methods to a proxy instance.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class ProxyIterator implements Iterator {
    
    /** Holds value of property iterator. */
    private Iterator iterator;
    
    
    public ProxyIterator() {
    }
    
    public ProxyIterator( Iterator iterator ) {
        this.iterator = iterator;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    public Object next() {
        return getIterator().next();
    }

    public void remove() {
        getIterator().remove();
    }

    // Properties
    //-------------------------------------------------------------------------
    /** Getter for property iterator.
     * @return Value of property iterator.
     */
    public Iterator getIterator() {
        return iterator;
    }
    /** Setter for property iterator.
     * @param iterator New value of property iterator.
     */
    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
    }
}
