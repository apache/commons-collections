/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/ObservedCollection.java,v 1.2 2003/08/31 17:24:46 scolebourne Exp $
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
import java.util.Iterator;

import org.apache.commons.collections.event.ModificationHandler;
import org.apache.commons.collections.event.ModificationListener;
import org.apache.commons.collections.event.StandardModificationHandler;
import org.apache.commons.collections.event.StandardModificationListener;

/**
 * <code>ObservedCollection</code> decorates a <code>Collection</code>
 * implementation to observe changes in the collection.
 * <p>
 * Each modifying method call made on this <code>Collection</code> is forwarded to a
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
public class ObservedCollection extends AbstractCollectionDecorator {
    
    /** The handler to delegate event handling to */
    protected final ModificationHandler handler;

    // Factories
    //-----------------------------------------------------------------------
    /**
     * Factory method to create an observable collection.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * This can be accessed by {@link #getHandler()} to add listeners.
     *
     * @param coll  the collection to decorate, must not be null
     * @return the observed collection
     * @throws IllegalArgumentException if the collection is null
     */
    public static ObservedCollection decorate(final Collection coll) {
        return new ObservedCollection(coll, null);
    }

    /**
     * Factory method to create an observable collection and register one
     * listener to receive all events.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * The listener will be added to the handler.
     *
     * @param coll  the collection to decorate, must not be null
     * @param listener  collection listener, must not be null
     * @return the observed collection
     * @throws IllegalArgumentException if the collection or listener is null
     */
    public static ObservedCollection decorate(
            final Collection coll,
            final StandardModificationListener listener) {
        
        return decorate(coll, listener, -1, -1);
    }

    /**
     * Factory method to create an observable collection and register one
     * listener to receive all post events.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * The listener will be added to the handler.
     *
     * @param coll  the collection to decorate, must not be null
     * @param listener  collection listener, must not be null
     * @return the observed collection
     * @throws IllegalArgumentException if the collection or listener is null
     */
    public static ObservedCollection decoratePostEventsOnly(
            final Collection coll,
            final StandardModificationListener listener) {
        
        return decorate(coll, listener, 0, -1);
    }

    /**
     * Factory method to create an observable collection and
     * register one listener using event masks.
     * <p>
     * A {@link StandardModificationHandler} will be created.
     * The listener will be added to the handler.
     * The masks are defined in 
     * {@link org.apache.commons.collections.event.ModificationEventType ModificationEventType}.
     *
     * @param coll  the collection to decorate, must not be null
     * @param listener  collection listener, must not be null
     * @param preEventMask  mask for pre events (0 for none, -1 for all)
     * @param postEventMask  mask for post events (0 for none, -1 for all)
     * @return the observed collection
     * @throws IllegalArgumentException if the collection or listener is null
     */
    public static ObservedCollection decorate(
            final Collection coll,
            final StandardModificationListener listener,
            final int preEventMask,
            final int postEventMask) {
            
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        StandardModificationHandler handler = new StandardModificationHandler();
        ObservedCollection oc = new ObservedCollection(coll, handler);
        handler.addModificationListener(listener, preEventMask, postEventMask);
        return oc;
    }

    /**
     * Factory method to create an observable collection using a
     * specific handler.
     * <p>
     * The handler may be configured independently with listeners or other
     * event recognition.
     *
     * @param coll  the collection to decorate, must not be null
     * @param handler  observed handler, must not be null
     * @return the observed collection
     * @throws IllegalArgumentException if the collection or handler is null
     */
    public static ObservedCollection decorate(
            final Collection coll,
            final ModificationHandler handler) {
                
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        return new ObservedCollection(coll, handler);
    }

    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies) and takes a handler.
     * <p>
     * If a <code>null</code> handler is specified, an 
     * <code>ObservedHandler</code> is created. 
     * 
     * @param coll  the collection to decorate, must not be null
     * @param handler  the observing handler, may be null
     * @throws IllegalArgumentException if the collection is null
     */
    protected ObservedCollection(
            final Collection coll,
            final ModificationHandler handler) {
        super(coll);
        this.handler = (handler == null ? new StandardModificationHandler() : handler);
        this.handler.init(this);
    }

    // Handler access
    //-----------------------------------------------------------------------
    /**
     * Gets the handler that is observing this collection.
     * 
     * @return the observing handler, never null
     */
    public ModificationHandler getHandler() {
        return handler;
    }
    
    // Listener convenience methods
    //----------------------------------------------------------------------
    /**
     * Gets an array of all the listeners active in the handler.
     * This method simply delegates to the handler.
     * 
     * @return the listeners
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public ModificationListener[] getModificationListeners() {
        return getHandler().getModificationListeners();
    }
    
    /**
     * Adds a listener to the list held in the handler.
     * This method simply delegates to the handler.
     * <p>
     * No error occurs if the listener is <code>null</code>.
     * 
     * @param listener  the listener to add, may be null (ignored)
     * @throws ClassCastException if the listener is not of the correct type
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void addModificationListener(ModificationListener listener) {
        getHandler().addModificationListener(listener);
    }
    
    /**
     * Removes a listener to the list held in the handler. 
     * This method simply delegates to the handler.
     * <p>
     * No error occurs if the listener is not in the list or the type
     * of the listener is incorrect.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @param listener  the listener to remove, may be null (ignored)
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void removeModificationListener(ModificationListener listener) {
        getHandler().removeModificationListener(listener);
    }
    
    // Collection
    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        boolean result = false;
        if (handler.preAdd(object)) {
            result = collection.add(object);
            handler.postAdd(object, result);
        }
        return result;
    }

    public boolean addAll(Collection coll) {
        boolean result = false;
        if (handler.preAddAll(coll)) {
            result = collection.addAll(coll);
            handler.postAddAll(coll, result);
        }
        return result;
    }

    public void clear() {
        if (handler.preClear()) {
            collection.clear();
            handler.postClear();
        }
    }

    public Iterator iterator() {
        return new ObservedIterator(collection.iterator());
    }

    public boolean remove(Object object) {
        boolean result = false;
        if (handler.preRemove(object)) {
            result = collection.remove(object);
            handler.postRemove(object, result);
        }
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = false;
        if (handler.preRemoveAll(coll)) {
            result = collection.removeAll(coll);
            handler.postRemoveAll(coll, result);
        }
        return result;
    }

    public boolean retainAll(Collection coll) {
        boolean result = false;
        if (handler.preRetainAll(coll)) {
            result = collection.retainAll(coll);
            handler.postRetainAll(coll, result);
        }
        return result;
    }

    // Iterator
    //-----------------------------------------------------------------------
    /**
     * Inner class Iterator for the ObservedCollection.
     */
    protected class ObservedIterator extends AbstractIteratorDecorator {
        
        protected Object last;
        
        protected ObservedIterator(Iterator iterator) {
            super(iterator);
        }
        
        public Object next() {
            last = super.next();
            return last;
        }

        public void remove() {
            if (handler.preRemove(last)) {
                iterator.remove();
                handler.postRemove(last, true);
            }
        }
    }

}
