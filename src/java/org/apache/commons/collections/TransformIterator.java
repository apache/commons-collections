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

/** A Proxy {@link Iterator Iterator} which uses a {@link Transformer Transformer} instance to 
  * transform the contents of the {@link Iterator Iterator} into some other form
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @deprecated this class has been moved to the iterators subpackage
  */

public class TransformIterator 
extends org.apache.commons.collections.iterators.TransformIterator {
    
    /**
     *  Constructs a new <Code>TransformIterator</Code> that will not function
     *  until the {@link #setIterator(Iterator) setIterator} method is 
     *  invoked.
     */
    public TransformIterator() {
        super();
    }
    
    /**
     *  Constructs a new <Code>TransformIterator</Code> that won't transform
     *  elements from the given iterator.
     *
     *  @param iterator  the iterator to use
     */
    public TransformIterator( Iterator iterator ) {
        super( iterator );
    }

    /**
     *  Constructs a new <Code>TransformIterator</Code> that will use the
     *  given iterator and transformer.  If the given transformer is null,
     *  then objects will not be transformed.
     *
     *  @param iterator  the iterator to use
     *  @param transformer  the transformer to use
     */
    public TransformIterator( Iterator iterator, Transformer transformer ) {
        super( iterator, transformer );
    }

}
