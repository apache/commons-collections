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

/** Adapter to make {@link Enumeration Enumeration} instances appear to be {@link Iterator Iterator} instances
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class EnumerationIterator implements Iterator {
    
    private Enumeration enumeration;

    
    public EnumerationIterator() {
    }
    
    public EnumerationIterator( Enumeration enumeration ) {
        this.enumeration = enumeration;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    public Object next() {
        return enumeration.nextElement();
    }

    public void remove() {
        throw new UnsupportedOperationException( "remove() method is not supported" );
    }

    // Properties
    //-------------------------------------------------------------------------
    public Enumeration getEnumeration() {
        return enumeration;
    }
    
    public void setEnumeration( Enumeration enumeration ) {
        this.enumeration = enumeration;
    }
}
