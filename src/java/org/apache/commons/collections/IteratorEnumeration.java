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

/** Adapter to make an {@link Iterator Iterator} instance appear to be an {@link Enumeration Enumeration} instances
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class IteratorEnumeration implements Enumeration {
    
    private Iterator iterator;
    
    public IteratorEnumeration() {
    }

    public IteratorEnumeration( Iterator iterator ) {
        this.iterator = iterator;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public Object nextElement() {
        return iterator.next();
    }

    // Properties
    //-------------------------------------------------------------------------
    public Iterator getIterator() {
        return iterator;
    }
    
    public void setIterator( Iterator iterator ) {
        this.iterator = iterator;
    }
    
}