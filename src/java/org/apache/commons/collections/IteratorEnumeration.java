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

/** Adapter to make an {@link Iterator Iterator} instance appear to be an {@link java.util.Enumeration Enumeration} instances
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @deprecated this class has been moved to the iterators subpackage
  */

public class IteratorEnumeration 
extends org.apache.commons.collections.iterators.IteratorEnumeration {
    
    /**
     *  Constructs a new <Code>IteratorEnumeration</Code> that will not 
     *  function until {@link #setIterator(Iterator) setIterator} is  
     *  invoked.
     */
    public IteratorEnumeration() {
        super();
    }

    /**
     *  Constructs a new <Code>IteratorEnumeration</Code> that will use
     *  the given iterator. 
     * 
     *  @param iterator  the iterator to use
     */
    public IteratorEnumeration( Iterator iterator ) {
        super(iterator);
    }

    
}
