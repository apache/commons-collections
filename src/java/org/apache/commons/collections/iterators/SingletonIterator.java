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
/** 
 * <p><code>SingletonIterator</code> is an {@link Iterator} over a single 
 * object instance.</p>
 *
 * @since 2.0
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Revision: 1.2.2.1 $
 */
public class SingletonIterator implements Iterator {

    private boolean first = true;
    private Object object;

    /**
     * Constructs a new <Code>SingletonIterator</Code>.
     *
     * @param object  the single object to return from the iterator
     */
    public SingletonIterator(Object object) {
        super();
        this.object = object;
    }

    /**
     * Is another object available from the iterator.
     * <p>
     * This returns true if the single object hasn't been returned yet.
     * 
     * @return true if the single object hasn't been returned yet
     */
    public boolean hasNext() {
        return first;
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
        if (!first) {
            throw new NoSuchElementException();
        }
        Object answer = object;
        object = null;
        first = false;
        return answer;
    }

    /**
     * Remove always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by this iterator");
    }
    
}
