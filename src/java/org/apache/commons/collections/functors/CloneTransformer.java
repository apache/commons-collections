/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections.functors;

import java.io.Serializable;

import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that returns a clone of the input object.
 * <p>
 * Clone is performed using <code>PrototypeFactory.getInstance(input).create()</code>.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/02/18 00:59:20 $
 *
 * @author Stephen Colebourne
 */
public class CloneTransformer implements Transformer, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = -8188742709499652567L;

    /** Singleton predicate instance */    
    public static final Transformer INSTANCE = new CloneTransformer();
    
    /**
     * Constructor
     */
    private CloneTransformer() {
        super();
    }

    /**
     * Do nothing
     */
    public Object transform(Object input) {
        if (input == null) {
            return null;
        }
        return PrototypeFactory.getInstance(input).create();
    }
    
}
