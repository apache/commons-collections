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
  * A proxy {@link ListIterator ListIterator} which 
  * takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying <code>ListIterator</code> 
  * instance. Only objects for which the specified 
  * <code>Predicate</code> evaluates to <code>true</code> are
  * returned by the iterator.
  * 
  * @since 2.0
  * @version $Revision: 1.7.2.1 $ $Date: 2004/05/22 12:14:02 $
  * @author Rodney Waldhoff
  * @deprecated this class has been moved to the iterators subpackage
  */
public class FilterListIterator 
extends org.apache.commons.collections.iterators.FilterListIterator {

    // Constructors    
    //-------------------------------------------------------------------------
    
    /**
     *  Constructs a new <Code>FilterListIterator</Code> that will not 
     *  function until 
     *  {@link ProxyListIterator#setListIterator(ListIterator) setListIterator}
     *  and {@link #setPredicate(Predicate) setPredicate} are invoked.
     */
    public FilterListIterator() {
        super();
    }

    /**
     *  Constructs a new <Code>FilterListIterator</Code> that will not 
     *  function until {@link #setPredicate(Predicate) setPredicate} is invoked.
     *
     *  @param iterator  the iterator to use
     */
    public FilterListIterator(ListIterator iterator ) {
        super(iterator);
    }

    /**
     *  Constructs a new <Code>FilterListIterator</Code>.
     *
     *  @param iterator  the iterator to use
     *  @param predicate  the predicate to use
     */
    public FilterListIterator(ListIterator iterator, Predicate predicate) {
        super(iterator, predicate);
    }

    /**
     *  Constructs a new <Code>FilterListIterator</Code> that will not 
     *  function until 
     *  {@link ProxyListIterator#setListIterator(ListIterator) setListIterator}
     *  is invoked.
     *
     *  @param predicate  the predicate to use.
     */
    public FilterListIterator(Predicate predicate) {
        super(predicate);
    }

}
