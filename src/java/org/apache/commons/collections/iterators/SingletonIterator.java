/*
 *  Copyright 1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.ResettableIterator;

/** 
 * <code>SingletonIterator</code> is an {@link Iterator} over a single 
 * object instance.
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.12 $ $Date: 2004/02/18 00:59:50 $
 * 
 * @author James Strachan
 * @author Stephen Colebourne
 * @author Rodney Waldhoff
 */
public class SingletonIterator
		implements Iterator, ResettableIterator {

    private boolean beforeFirst = true;
    private boolean removed = false;
    private Object object;

    /**
     * Constructs a new <code>SingletonIterator</code>.
     *
     * @param object  the single object to return from the iterator
     */
    public SingletonIterator(Object object) {
        super();
        this.object = object;
    }

    /**
     * Is another object available from the iterator?
     * <p>
     * This returns true if the single object hasn't been returned yet.
     * 
     * @return true if the single object hasn't been returned yet
     */
    public boolean hasNext() {
        return (beforeFirst && !removed);
    }

    /**
     * Get the next object from the iterator.
     * <p>
     * This returns the single object if it hasn't been returned yet.
     *
     * @return the single object
     * @throws NoSuchElementException if the single object has already 
     *    been returned
     */
    public Object next() {
        if (!beforeFirst || removed) {
            throw new NoSuchElementException();
        }
        beforeFirst = false;
        return object;
    }

    /**
     * Remove the object from this iterator.
     * @throws IllegalStateException if the <tt>next</tt> method has not
     *        yet been called, or the <tt>remove</tt> method has already
     *        been called after the last call to the <tt>next</tt>
     *        method.
     */
    public void remove() {       
        if(removed || beforeFirst) {
            throw new IllegalStateException();
        } else {
            object = null;
            removed = true;
        }
    }
    
    /**
     * Reset the iterator to the start.
     */
    public void reset() {
        beforeFirst = true;
    }
    
}
