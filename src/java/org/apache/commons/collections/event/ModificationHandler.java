/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/event/Attic/ModificationHandler.java,v 1.2 2003/08/31 17:25:49 scolebourne Exp $
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
package org.apache.commons.collections.event;

import java.util.Collection;

import org.apache.commons.collections.decorators.ObservedCollection;

/**
 * Abstract base implementation of a handler for collection modification.
 * <p>
 * All data storage and event sending is performed by a subclass.
 * This class provides a default implementation for the event handling methods
 * that forwards to single points.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:25:49 $
 * 
 * @author Stephen Colebourne
 */
public abstract class ModificationHandler {
    
    /** The collection being observed */
    private ObservedCollection collection = null;
    
    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     */
    protected ModificationHandler() {
        super();
    }

    /**
     * Initialize the handler.
     * <p>
     * The handler cannot be used until this method is called.
     * However, the handler's setup methods can be called.
     * All other methods will throw NullPointerException until then.
     * 
     * @param coll  the observed collection, must not be null
     * @throws IllegalArgumentException if the collection is null
     * @throws IllegalStateException if init has already been called
     */
    public void init(final ObservedCollection coll) {
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        if (this.collection != null) {
            throw new IllegalArgumentException("init() has already been called");
        }
        this.collection = coll;
    }

    // Collection access
    //-----------------------------------------------------------------------
    /**
     * Gets the observed collection.
     * 
     * @return the observed collection
     */
    public Collection getCollection() {
        return collection;
    }
    
    // Listeners
    //----------------------------------------------------------------------
    /**
     * Gets an array of all the listeners active in the handler.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @return the listeners
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public ModificationListener[] getModificationListeners() {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    /**
     * Adds a listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is <code>null</code>.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @param listener  the listener to add, may be null (ignored)
     * @throws ClassCastException if the listener is not of the correct type
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void addModificationListener(ModificationListener listener) {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    /**
     * Removes a listener to the list held in the handler.
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
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    // Event sending
    //-----------------------------------------------------------------------
    /**
     * Handles the pre event.
     * 
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     */
    protected abstract boolean preEvent(int type, int index, Object object, int repeat);

    /**
     * Handles the post event.
     * 
     * @param success  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     */
    protected abstract void postEvent(boolean success, int type, int index, Object object, int repeat);

    /**
     * Handles the post event.
     * 
     * @param success  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     * @param result  the result of the method
     */
    protected abstract void postEvent(boolean success, int type, int index, Object object, int repeat, Object result);

    // Event handling
    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param object  the object being added
     * @return true
     */
    public boolean preAdd(Object object) {
        return preEvent(ModificationEventType.ADD, -1, object, 1);
    }

    /**
     * Send an event after add(obj) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param object  the object being added
     * @param result  the result from the add method
     */
    public void postAdd(Object object, boolean result) {
        postEvent(result, ModificationEventType.ADD, -1, object, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(int,obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @return true
     */
    public boolean preAdd(int index, Object object) {
        return preEvent(ModificationEventType.ADD_INDEXED, index, object, 1);
    }

    /**
     * Send an event after add(int,obj) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     */
    public void postAdd(int index, Object object) {
        postEvent(true, ModificationEventType.ADD_INDEXED, index, object, 1, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before addAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param coll  the collection being added
     * @return true
     */
    public boolean preAddAll(Collection coll) {
        return preEvent(ModificationEventType.ADD_ALL, -1, coll, 1);
    }

    /**
     * Send an event after addAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param coll  the collection being added
     * @param result  the result from the addAll method
     */
    public void postAddAll(Collection coll, boolean result) {
        postEvent(result, ModificationEventType.ADD_ALL, -1, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before addAll(int,coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to addAll at
     * @param coll  the collection being added
     * @return true
     */
    public boolean preAddAll(int index, Collection coll) {
        return preEvent(ModificationEventType.ADD_ALL_INDEXED, index, coll, 1);
    }

    /**
     * Send an event after addAll(int,coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param index  the index to addAll at
     * @param coll  the collection being added
     * @param result  the result from the addAll method
     */
    public void postAddAll(int index, Collection coll, boolean result) {
        postEvent(result, ModificationEventType.ADD_ALL_INDEXED, index, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before clear() is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @return true
     */
    public boolean preClear() {
        return preEvent(ModificationEventType.CLEAR, -1, null, 1);
    }

    /**
     * Send an event after clear() is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     */
    public void postClear() {
        // assumes a modification occurred
        postEvent(true, ModificationEventType.CLEAR, -1, null, 1, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param object  the object being removed
     * @return true
     */
    public boolean preRemove(Object object) {
        return preEvent(ModificationEventType.REMOVE, -1, object, 1);
    }

    /**
     * Send an event after remove(obj) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param object  the object being removed
     * @param result  the result from the remove method
     */
    public void postRemove(Object object, boolean result) {
        postEvent(result, ModificationEventType.REMOVE, -1, object, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(int) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to remove at
     * @return true
     */
    public boolean preRemove(int index) {
        return preEvent(ModificationEventType.REMOVE_INDEXED, index, null, 1);
    }

    /**
     * Send an event after remove(int) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param index  the index to remove at
     * @param result  the result from the remove method
     */
    public void postRemove(int index, Object result) {
        postEvent(true, ModificationEventType.REMOVE_INDEXED, index, null, 1, result);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before removeAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param coll  the collection being removed
     * @return true
     */
    public boolean preRemoveAll(Collection coll) {
        return preEvent(ModificationEventType.REMOVE_ALL, -1, coll, 1);
    }

    /**
     * Send an event after removeAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param coll  the collection being removed
     * @param result  the result from the removeAll method
     */
    public void postRemoveAll(Collection coll, boolean result) {
        postEvent(result, ModificationEventType.REMOVE_ALL, -1, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before retainAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param coll  the collection being retained
     * @return true
     */
    public boolean preRetainAll(Collection coll) {
        return preEvent(ModificationEventType.RETAIN_ALL, -1, coll, 1);
    }

    /**
     * Send an event after retainAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param coll  the collection being retained
     * @param result  the result from the retainAll method
     */
    public void postRetainAll(Collection coll, boolean result) {
        postEvent(result, ModificationEventType.RETAIN_ALL, -1, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before set(int,obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @return true
     */
    public boolean preSet(int index, Object object) {
        return preEvent(ModificationEventType.SET_INDEXED, index, object, 1);
    }

    /**
     * Send an event after set(int,obj) is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @param result  the result from the set method
     */
    public void postSet(int index, Object object, Object result) {
        postEvent(true, ModificationEventType.SET_INDEXED, index, object, 1, result);
    }

    // toString
    //-----------------------------------------------------------------------
    /**
     * Gets a debugging string version of this object.
     * 
     * @return a debugging string
     */
    public String toString() {
        String name = getClass().getName();
        int pos = name.lastIndexOf('.');
        if (pos != -1) {
            name = name.substring(pos + 1);
        }
        return name + '[' + (collection == null ? "" : "initialised") + ']';
    }
    
}
