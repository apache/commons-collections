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

import java.util.ListIterator;

/**
 * A proxy {@link ListIterator ListIterator} which delegates its
 * methods to a proxy instance.
 *
 * @since 2.0
 * @see ProxyIterator
 * @version $Revision: 1.4.2.1 $ $Date: 2004/05/22 12:14:02 $
 * @author Rodney Waldhoff
 * @deprecated this class has been moved to the iterators subpackage
 */
public class ProxyListIterator 
extends org.apache.commons.collections.iterators.ProxyListIterator {

    // Constructor
    //-------------------------------------------------------------------------

    /**
     *  Constructs a new <Code>ProxyListIterator</Code> that will not 
     *  function until {@link #setListIterator(ListIterator) setListIterator}
     *  is invoked.
     */
    public ProxyListIterator() {
        super();
    }

    /**
     *  Constructs a new <Code>ProxyListIterator</Code> that will use the
     *  given list iterator.
     *
     *  @param iterator  the list iterator to use
     */
    public ProxyListIterator(ListIterator iterator) {
        super(iterator);
    }

}

