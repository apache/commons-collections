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
package org.apache.commons.collections;

import java.util.Iterator;

/** A Proxy {@link Iterator Iterator} which takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying {@link Iterator Iterator} instance.
  * Only objects for which the
  * specified <code>Predicate</code> evaluates to <code>true</code> are
  * returned.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Jan Sorensen
  * @deprecated this class has been moved to the iterators subpackage
  */

public class FilterIterator 
extends org.apache.commons.collections.iterators.FilterIterator {

    /**
     *  Constructs a new <Code>FilterIterator</Code> that will not function
     *  until {@link #setIterator(Iterator) setIterator} is invoked.
     */
    public FilterIterator() {
        super();
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
        super( iterator, predicate );
    }

}
