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

/** A Proxy {@link Iterator Iterator} which takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying {@link Iterator Iterator} instance.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Jan Sorensen
  */

public class FilterIterator extends ProxyIterator {
    
    /** Holds value of property predicate. */
    private Predicate predicate;

    private Object nextObject;
    private boolean nextObjectSet = false;
    
    
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
        if ( nextObjectSet ) {
            return true;
        } else {
            return setNextObject();
        }
    }

    public Object next() {
        if ( !nextObjectSet ) {
            if (!setNextObject()) {
                throw new NoSuchElementException();
            }
        }
        nextObjectSet = false;
        return nextObject;
    }

    /**
     * Always throws UnsupportedOperationException as this class 
     * does look-ahead with its internal iterator.
     */
    public void remove() {
        throw new UnsupportedOperationException();
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

    /**
     * Set nextObject to the next object. If there are no more 
     * objects then return false. Otherwise, return true.
     */
    private boolean setNextObject() {
        Iterator iterator = getIterator();
        Predicate predicate = getPredicate();
        while ( iterator.hasNext() ) {
            Object object = iterator.next();
            if ( predicate.evaluate( object ) ) {
                nextObject = object;
                nextObjectSet = true;
                return true;
            }
        }
        return false;
    }
}
