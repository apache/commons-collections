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

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.Predicate;


/** A Proxy {@link Iterator Iterator} which takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying {@link Iterator Iterator} instance.
  * Only objects for which the
  * specified <code>Predicate</code> evaluates to <code>true</code> are
  * returned.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Jan Sorensen
  */

public class FilterIterator extends ProxyIterator {
    
    /** Holds value of property predicate. */
    private Predicate predicate;

    private Object nextObject;
    private boolean nextObjectSet = false;
    
    
    //-------------------------------------------------------------------------

    /**
     *  Constructs a new <Code>FilterIterator</Code> that will not function
     *  until {@link #setIterator(Iterator) setIterator} is invoked.
     */
    public FilterIterator() {
    }
    
    /**
     *  Constructs a new <Code>FilterIterator</Code> that will not function
     *  until {@link #setPredicate(Predicate) setPredicate} is invoked.
     *
     *  @param iterator  the iterator to use
     */
    public FilterIterator( Iterator iterator ) {
        super( iterator );
    }

    /**
     *  Constructs a new <Code>FilterIterator</Code> that will use the
     *  given iterator and predicate.
     *
     *  @param iterator  the iterator to use
     *  @param predicate  the predicate to use
     */
    public FilterIterator( Iterator iterator, Predicate predicate ) {
        super( iterator );
        this.predicate = predicate;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    
    /** 
     *  Returns true if the underlying iterator contains an object that 
     *  matches the predicate.
     *
     *  @return true if there is another object that matches the predicate 
     */
    public boolean hasNext() {
        if ( nextObjectSet ) {
            return true;
        } else {
            return setNextObject();
        }
    }

    /** 
     *  Returns the next object that matches the predicate.
     * 
     *  @return the next object which matches the given predicate
     *  @throws NoSuchElementException if there are no more elements that
     *   match the predicate 
     */
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
     *
     * @throws UnsupportedOperationException  always
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
