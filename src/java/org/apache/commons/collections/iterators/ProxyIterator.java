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

/** A Proxy {@link Iterator Iterator} which delegates its methods to a proxy instance.
  *
  * @since 1.0
  * @see ProxyListIterator
  * @version $Revision: 1.1.2.1 $ $Date: 2004/05/22 12:14:04 $
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class ProxyIterator implements Iterator {
    
    /** Holds value of property iterator. */
    private Iterator iterator;
    
    /**
     *  Constructs a new <Code>ProxyIterator</Code> that will not function
     *  until {@link #setIterator(Iterator)} is called.
     */
    public ProxyIterator() {
    }
    
    /**
     *  Constructs a new <Code>ProxyIterator</Code> that will use the
     *  given iterator.
     *
     *  @param iterator  the underyling iterator
     */
    public ProxyIterator( Iterator iterator ) {
        this.iterator = iterator;
    }

    // Iterator interface
    //-------------------------------------------------------------------------

    /**
     *  Returns true if the underlying iterator has more elements.
     *
     *  @return true if the underlying iterator has more elements
     */
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    /**
     *  Returns the next element from the underlying iterator.
     *
     *  @return the next element from the underlying iterator
     *  @throws NoSuchElementException  if the underlying iterator 
     *    raises it because it has no more elements
     */
    public Object next() {
        return getIterator().next();
    }

    /**
     *  Removes the last returned element from the collection that spawned
     *  the underlying iterator.
     */
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
