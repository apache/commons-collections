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

/** A Proxy {@link Iterator Iterator} which uses a {@link Transformer Transformer} instance to 
  * transform the contents of the {@link Iterator Iterator} into some other form
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class TransformIterator extends ProxyIterator {
    
    /** Holds value of property transformer. */
    private Transformer transformer;
    
    
    public TransformIterator() {
    }
    
    public TransformIterator( Iterator iterator ) {
        super( iterator );
    }

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
    protected Object transform( Object source ) {
        Transformer transformer = getTransformer();
        if ( transformer != null ) {
            return transformer.transform( source );
        }
        return source;
    }
}
