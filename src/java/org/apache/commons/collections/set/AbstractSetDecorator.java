/*
 *  Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.collections.set;

import java.util.Set;

import org.apache.commons.collections.collection.AbstractCollectionDecorator;

/**
 * Decorates another <code>Set</code> to provide additional behaviour.
 * <p>
 * Methods are forwarded directly to the decorated set.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/02/18 01:14:27 $
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractSetDecorator extends AbstractCollectionDecorator implements Set {

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected AbstractSetDecorator(Set set) {
        super(set);
    }

    /**
     * Gets the set being decorated.
     * 
     * @return the decorated set
     */
    protected Set getSet() {
        return (Set) getCollection();
    }

}
