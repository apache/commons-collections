/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/ObservedList.java,v 1.2 2003/08/31 17:24:46 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections.decorators;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.event.ModificationHandler;
import org.apache.commons.collections.event.StandardModificationHandler;
import org.apache.commons.collections.event.StandardModificationListener;

/**
 * <code>ObservedList</code> decorates a <code>List</code>
 * implementation to observe changes.
 * <p>
 * Each modifying method call made on this <code>List</code> is forwarded to a
 * {@link ModificationHandler}.
 * The handler manages the event, notifying listeners and optionally vetoing changes.
 * The default handler is {@link StandardModificationHandler}.
 * See this class for details of configuration available.
 * <p>
 * For convenience, add, remove and get listener methods are made available on
 * this class. They accept a generic listener type, whereas handlers generally
 * require a specific type. Thus a ClassCastException may be thrown from these
 * methods. They may also throw UnsupportedOperationException if the handler
 * uses a technique other than listeners to communicate events.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:24:46 $
 * 
 * @author Stephen Colebourne
 */
public class ObservedList extends ObservedCollection implements List {
    
    // Factories
    //-----------------------------------------------------------------------
    /**
     * Factory method to create an observable list.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * This can be accessed by {@link #getHandler()} to add listeners.
     *
     * @param list  the list to decorate, must not be null
     * @return the observed List
     * @throws IllegalArgumentException if the list is null
     */
    public static ObservedList decorate(final List list) {
        return new ObservedList(list, null);
    }

    /**
     * Factory method to create an observable list and register one
     * listener to receive all events.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * The listener will be added to the handler.
     *
     * @param list  the list to decorate, must not be null
     * @param listener  the listener, must not be null
     * @return the observed list
     * @throws IllegalArgumentException if the list or listener is null
     */
    public static ObservedList decorate(
            final List list,
            final StandardModificationListener listener) {
        
        return decorate(list, listener, -1, -1);
    }

    /**
     * Factory method to create an observable list and register one
     * listener to receive all post events.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * The listener will be added to the handler.
     *
     * @param list  the list to decorate, must not be null
     * @param listener  the listener, must not be null
     * @return the observed list
     * @throws IllegalArgumentException if the list or listener is null
     */
    public static ObservedList decoratePostEventsOnly(
            final List list,
            final StandardModificationListener listener) {
        
        return decorate(list, listener, 0, -1);
    }

    /**
     * Factory method to create an observable list and
     * register one listener using event masks.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * The listener will be added to the handler.
     * The masks are defined in 
     * {@link org.apache.commons.collections.event.ModificationEventType ModificationEventType}.
     *
     * @param list  the list to decorate, must not be null
     * @param listener  the listener, must not be null
     * @param preEventMask  mask for pre events (0 for none, -1 for all)
     * @param postEventMask  mask for post events (0 for none, -1 for all)
     * @return the observed list
     * @throws IllegalArgumentException if the list or listener is null
     */
    public static ObservedList decorate(
            final List list,
            final StandardModificationListener listener,
            final int preEventMask,
            final int postEventMask) {
            
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        StandardModificationHandler handler = new StandardModificationHandler();
        ObservedList oc = new ObservedList(list, handler);
        handler.addModificationListener(listener, preEventMask, postEventMask);
        return oc;
    }

    /**
     * Factory method to create an observable list using a
     * specific handler.
     * <p>
     * The handler may be configured independently with listeners or other
     * event recognition.
     *
     * @param list  the list to decorate, must not be null
     * @param handler  observed handler, must not be null
     * @return the observed list
     * @throws IllegalArgumentException if the list or handler is null
     */
    public static ObservedList decorate(
            final List list,
            final ModificationHandler handler) {
                
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        return new ObservedList(list, handler);
    }

    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies) and takes a handler.
     * <p>
     * If a <code>null</code> handler is specified, an 
     * <code>ObservedHandler</code> is created. 
     * 
     * @param list  the list to decorate, must not be null
     * @param handler  the observing handler, may be null
     * @throws IllegalArgumentException if the list is null
     */
    protected ObservedList(
            final List list,
            final ModificationHandler handler) {
        super(list, handler);
    }
    
    /**
     * Typecast the collection to a List.
     * 
     * @return the wrapped collection as a List
     */
    private List getList() {
        return (List) getCollection();
    }

    // List API
    //-----------------------------------------------------------------------
    public Object get(int index) {
        return getList().get(index);
    }

    public int indexOf(Object object) {
        return getList().indexOf(object);
    }

    public int lastIndexOf(Object object) {
        return getList().lastIndexOf(object);
    }

    //-----------------------------------------------------------------------
    public void add(int index, Object object) {
        if (handler.preAdd(index, object)) {
            getList().add(index, object);
            handler.postAdd(index, object);
        }
    }

    public boolean addAll(int index, Collection coll) {
        boolean result = false;
        if (handler.preAddAll(index, coll)) {
            result = getList().addAll(index, coll);
            handler.postAddAll(index, coll, result);
        }
        return result;
    }

    public Object remove(int index) {
        Object result = null;
        if (handler.preRemove(index)) {
            result = getList().remove(index);
            handler.postRemove(index, result);
        }
        return result;
    }

    public Object set(int index, Object object) {
        Object result = null;
        if (handler.preSet(index, object)) {
            result = getList().set(index, object);
            handler.postSet(index, object, result);
        }
        return result;
    }

    public ListIterator listIterator() {
        return new ObservedListIterator(getList().listIterator());
    }

    public ListIterator listIterator(int index) {
        return new ObservedListIterator(getList().listIterator(index));
    }

    public List subList(int fromIndex, int toIndex) {
        // TODO: This list needs to be a special impl, as the index is offset
        throw new UnsupportedOperationException();
    }

    // ListIterator
    //-----------------------------------------------------------------------
    /**
     * Inner class ListIterator for the ObservedList.
     */
    protected class ObservedListIterator extends AbstractListIteratorDecorator {
        
        protected Object last;
        
        protected ObservedListIterator(ListIterator iterator) {
            super(iterator);
        }
        
        public Object next() {
            last = super.next();
            return last;
        }

        public Object previous() {
            last = iterator.previous();
            return last;
        }

        public void remove() {
            int index = iterator.previousIndex();
            if (handler.preRemove(index)) {
                iterator.remove();
                handler.postRemove(index, last);
            }
        }
        
        public void add(Object object) {
            int index = iterator.nextIndex();
            if (handler.preAdd(index, object)) {
                iterator.add(object);
                handler.postAdd(index, object);
            }
        }

        public void set(Object object) {
            int index = iterator.previousIndex();
            if (handler.preSet(index, object)) {
                iterator.set(object);
                handler.postSet(index, object, last);
            }
        }
    }

}
