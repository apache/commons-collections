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

import java.util.ListIterator;

/**
 * A proxy {@link ListIterator ListIterator} which delegates its
 * methods to a proxy instance.
 *
 * @since 2.0
 * @see ProxyIterator
 * @version $Revision: 1.1.2.1 $ $Date: 2004/05/22 12:14:04 $
 * @author Rodney Waldhoff
 */
public class ProxyListIterator implements ListIterator {

    // Constructor
    //-------------------------------------------------------------------------

    /**
     *  Constructs a new <Code>ProxyListIterator</Code> that will not 
     *  function until {@link #setListIterator(ListIterator) setListIterator}
     *  is invoked.
     */
    public ProxyListIterator() {
    }

    /**
     *  Constructs a new <Code>ProxyListIterator</Code> that will use the
     *  given list iterator.
     *
     *  @param iterator  the list iterator to use
     */
    public ProxyListIterator(ListIterator iterator) {
        this.iterator = iterator;
    }

    // ListIterator interface
    //-------------------------------------------------------------------------

    /**
     *  Invokes the underlying {@link ListIterator#add(Object)} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public void add(Object o) {
        getListIterator().add(o);
    }

    /**
     *  Invokes the underlying {@link ListIterator#hasNext()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public boolean hasNext() {
        return getListIterator().hasNext();
    }

    /**
     *  Invokes the underlying {@link ListIterator#hasPrevious()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public boolean hasPrevious() {
        return getListIterator().hasPrevious();
    }

    /**
     *  Invokes the underlying {@link ListIterator#next()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public Object next() {
        return getListIterator().next();
    }

    /**
     *  Invokes the underlying {@link ListIterator#nextIndex()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public int nextIndex() {
        return getListIterator().nextIndex();
    }

    /**
     *  Invokes the underlying {@link ListIterator#previous()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public Object previous() {
        return getListIterator().previous();
    }

    /**
     *  Invokes the underlying {@link ListIterator#previousIndex()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public int previousIndex() {
        return getListIterator().previousIndex();
    }

    /**
     *  Invokes the underlying {@link ListIterator#remove()} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public void remove() {
        getListIterator().remove();
    }

    /**
     *  Invokes the underlying {@link ListIterator#set(Object)} method.
     *
     *  @throws NullPointerException  if the underyling iterator is null
     */
    public void set(Object o) {
        getListIterator().set(o);
    }

    // Properties
    //-------------------------------------------------------------------------

    /** 
     * Getter for property iterator.
     * @return Value of property iterator.
     */
    public ListIterator getListIterator() {
        return iterator;
    }

    /**
     * Setter for property iterator.
     * @param iterator New value of property iterator.
     */
    public void setListIterator(ListIterator iterator) {
        this.iterator = iterator;
    }

    // Attributes
    //-------------------------------------------------------------------------

    /** Holds value of property "iterator". */
    private ListIterator iterator;

}

