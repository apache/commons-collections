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
import org.apache.commons.collections.Transformer;

/** A Proxy {@link Iterator Iterator} which uses a {@link Transformer Transformer} instance to 
  * transform the contents of the {@link Iterator Iterator} into some other form
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class TransformIterator extends ProxyIterator {
    
    /** Holds value of property transformer. */
    private Transformer transformer;
    
    
    /**
     *  Constructs a new <Code>TransformIterator</Code> that will not function
     *  until the {@link #setIterator(Iterator) setIterator} method is 
     *  invoked.
     */
    public TransformIterator() {
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
        super( iterator );
        this.transformer = transformer;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public Object next() {
        return transform( super.next() );
    }

    // Properties
    //-------------------------------------------------------------------------
    /** Getter for property transformer.
     * @return Value of property transformer.
     */
    public Transformer getTransformer() {
        return transformer;
    }
    /** Setter for property transformer.
     * @param transformer New value of property transformer.
     */
    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     *  Transforms the given object using the transformer.  If the 
     *  transformer is null, the original object is returned as-is.
     *
     *  @param source  the object to transform
     *  @return  the transformed object
     */
    protected Object transform( Object source ) {
        Transformer transformer = getTransformer();
        if ( transformer != null ) {
            return transformer.transform( source );
        }
        return source;
    }
}
