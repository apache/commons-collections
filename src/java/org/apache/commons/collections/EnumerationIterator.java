/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/** Adapter to make {@link Enumeration Enumeration} instances appear
  * to be {@link Iterator Iterator} instances.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
  */
public class EnumerationIterator implements Iterator {
    
    private Collection collection;

    private Enumeration enumeration;

    private Object last;
    
    public EnumerationIterator() {
        this(null, null);
    }
    
    public EnumerationIterator( Enumeration enumeration ) {
        this(enumeration, null);
    }

    public EnumerationIterator( Enumeration enum, Collection collection ) {
        this.enumeration = enum;
        this.collection = collection;
        this.last = null;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    public Object next() {
        last = enumeration.nextElement();
        return last;
    }

    /**
     * Functions if an associated <code>Collection</code> is known.
     *
     * @exception IllegalStateException <code>next()</code> not called.
     * @exception UnsupportedOperationException No associated
     * <code>Collection</code>.
     */
    public void remove() {
        if (collection != null) {
            if (last != null) {
                collection.remove(last);
            }
            else {
                throw new IllegalStateException
                    ("next() must have been called for remove() to function");
            }
        }
        else {
            throw new UnsupportedOperationException
                ("No Collection associated with this Iterator");
        }
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
