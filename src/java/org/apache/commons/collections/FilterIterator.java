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

/** A Proxy {@link Iterator Iterator} which takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying {@link Iterator Iterator} instance.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class FilterIterator extends ProxyIterator {
    
    /** Holds value of property predicate. */
    private Predicate predicate;
    private Object nextObject;
    
    
    //-------------------------------------------------------------------------
    public FilterIterator() {
    }
    
    public FilterIterator( Iterator iterator ) {
        super( iterator );
    }

    public FilterIterator( Iterator iterator, Predicate predicate ) {
        super( iterator );
        this.predicate = predicate;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        Iterator iterator = getIterator();
        Predicate predicate = getPredicate();
        while ( iterator.hasNext() ) {
            Object object = iterator.next();
            if ( predicate.evaluate( object ) ) {
                nextObject = object;
                return true;
            }
        }
        return false;
    }

    public Object next() {
        return nextObject;
    }

    // Properties
    //-------------------------------------------------------------------------
    /** Getter for property predicate.
     * @return Value of property predicate.
     */
    public Predicate getPredicate() {
        return predicate;
    }
    /** Setter for property predicate.
     * @param predicate New value of property predicate.
     */
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }
}
