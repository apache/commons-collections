/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/Attic/ModificationHandler.java,v 1.1 2003/09/03 23:54:26 scolebourne Exp $
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
package org.apache.commons.collections.observed;

import java.util.Collection;


/**
 * Defines a handler for collection modification events.
 * <p>
 * This class defines the event handling methods, following the 
 * <code>preXxx</code> and <code>postXxx</code> naming convention.
 * It also provides a default implementation that forwards to single methods.
 * <p>
 * This class could have been implemented as an interface, however to do so
 * would prevent the addition of extra events in the future. It does mean
 * that if you subclass this class, you must check it when you upgrade to a
 * later collections release.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/09/03 23:54:26 $
 * 
 * @author Stephen Colebourne
 */
public abstract class ModificationHandler {
    
    /** Singleton factory */
    static final ModificationHandlerFactory FACTORY = new Factory();
    
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
    void init(final ObservedCollection coll) {
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
    
    // PreListeners
    //----------------------------------------------------------------------
    /**
     * Gets an array of all the pre listeners active in the handler.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @return the listeners
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public Object[] getPreModificationListeners() {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    /**
     * Adds a pre listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is <code>null</code>.
     * <p>
     * The listener does not necessarily have to be a listener in the classic
     * JavaBean sense. It is entirely up to the handler as to how it interprets
     * the listener parameter. A ClassCastException is thrown if the handler
     * cannot interpret the parameter.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @param listener  the listener to add, may be null (ignored)
     * @throws ClassCastException if the listener is not of the correct type
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void addPreModificationListener(Object listener) {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    /**
     * Removes a pre listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is not in the list or the type
     * of the listener is incorrect.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @param listener  the listener to remove, may be null (ignored)
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void removePreModificationListener(Object listener) {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    // PostListeners
    //----------------------------------------------------------------------
    /**
     * Gets an array of all the post listeners active in the handler.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @return the listeners
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public Object[] getPostModificationListeners() {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    /**
     * Adds a post listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is <code>null</code>.
     * <p>
     * The listener does not necessarily have to be a listener in the classic
     * JavaBean sense. It is entirely up to the handler as to how it interprets
     * the listener parameter. A ClassCastException is thrown if the handler
     * cannot interpret the parameter.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @param listener  the listener to add, may be null (ignored)
     * @throws ClassCastException if the listener is not of the correct type
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void addPostModificationListener(Object listener) {
        throw new UnsupportedOperationException("Listeners not supported by " + getClass().getName());
    }
    
    /**
     * Removes a post listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is not in the list or the type
     * of the listener is incorrect.
     * <p>
     * This implementation throws UnsupportedOperationException.
     * 
     * @param listener  the listener to remove, may be null (ignored)
     * @throws UnsupportedOperationException if the handler does not support listeners
     */
    public void removePostModificationListener(Object listener) {
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
     * Handles the post event where the collections method returns boolean for success.
     * 
     * @param modified  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     */
    protected abstract void postEvent(boolean modified, int type, int index, Object object, int repeat);

    /**
     * Handles the post event where the collections method has a return value.
     * 
     * @param modified  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     * @param result  the result of the method
     */
    protected abstract void postEvent(boolean modified, int type, int index, Object object, int repeat, Object result);

    // Event handling
    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preAdd(Object object) {
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
    protected void postAdd(Object object, boolean result) {
        postEvent(result, ModificationEventType.ADD, -1, object, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preAdd(int index, Object object) {
        return preEvent(ModificationEventType.ADD_INDEXED, index, object, 1);
    }

    /**
     * Send an event after add(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     */
    protected void postAdd(int index, Object object) {
        postEvent(true, ModificationEventType.ADD_INDEXED, index, object, 1, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param object  the object being added
     * @param nCopies  the number of copies being added
     * @return true to process modification
     */
    protected boolean preAdd(Object object, int nCopies) {
        return preEvent(ModificationEventType.ADD_NCOPIES, -1, object, nCopies);
    }

    /**
     * Send an event after add(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param object  the object being added
     * @param nCopies  the number of copies being added
     */
    protected void postAdd(Object object, int nCopies, boolean result) {
        postEvent(true, ModificationEventType.ADD_NCOPIES, -1, object, nCopies, (result ? Boolean.TRUE : Boolean.FALSE));
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before addAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param coll  the collection being added
     * @return true to process modification
     */
    protected boolean preAddAll(Collection coll) {
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
    protected void postAddAll(Collection coll, boolean result) {
        postEvent(result, ModificationEventType.ADD_ALL, -1, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before addAll(int,coll) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to addAll at
     * @param coll  the collection being added
     * @return true to process modification
     */
    protected boolean preAddAll(int index, Collection coll) {
        return preEvent(ModificationEventType.ADD_ALL_INDEXED, index, coll, 1);
    }

    /**
     * Send an event after addAll(int,coll) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     * 
     * @param index  the index to addAll at
     * @param coll  the collection being added
     * @param result  the result from the addAll method
     */
    protected void postAddAll(int index, Collection coll, boolean result) {
        postEvent(result, ModificationEventType.ADD_ALL_INDEXED, index, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before clear() is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @return true to process modification
     */
    protected boolean preClear() {
        return preEvent(ModificationEventType.CLEAR, -1, null, 1);
    }

    /**
     * Send an event after clear() is called.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int)}.
     */
    protected void postClear() {
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
     * @return true to process modification
     */
    protected boolean preRemove(Object object) {
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
    protected void postRemove(Object object, boolean result) {
        postEvent(result, ModificationEventType.REMOVE, -1, object, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(int) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to remove at
     * @return true to process modification
     */
    protected boolean preRemove(int index) {
        return preEvent(ModificationEventType.REMOVE_INDEXED, index, null, 1);
    }

    /**
     * Send an event after remove(int) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param index  the index to remove at
     * @param result  the result from the remove method
     */
    protected void postRemove(int index, Object result) {
        postEvent(true, ModificationEventType.REMOVE_INDEXED, index, null, 1, result);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param object  the object being removed
     * @param nCopies  the number of copies being removed
     * @return true to process modification
     */
    protected boolean preRemove(Object object, int nCopies) {
        return preEvent(ModificationEventType.REMOVE_NCOPIES, -1, object, nCopies);
    }

    /**
     * Send an event after remove(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param object  the object being removed
     * @param nCopies  the number of copies being removed
     */
    protected void postRemove(Object object, int nCopies, boolean result) {
        postEvent(result, ModificationEventType.REMOVE_NCOPIES, -1, object, nCopies);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before removeAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param coll  the collection being removed
     * @return true to process modification
     */
    protected boolean preRemoveAll(Collection coll) {
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
    protected void postRemoveAll(Collection coll, boolean result) {
        postEvent(result, ModificationEventType.REMOVE_ALL, -1, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before retainAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param coll  the collection being retained
     * @return true to process modification
     */
    protected boolean preRetainAll(Collection coll) {
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
    protected void postRetainAll(Collection coll, boolean result) {
        postEvent(result, ModificationEventType.RETAIN_ALL, -1, coll, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before set(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent(int, int, Object, int)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preSet(int index, Object object) {
        return preEvent(ModificationEventType.SET_INDEXED, index, object, 1);
    }

    /**
     * Send an event after set(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent(boolean, int, int, Object, int, Object)}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @param result  the result from the set method
     */
    protected void postSet(int index, Object object, Object result) {
        // reference check for modification, in case equals() has issues (eg. performance)
        postEvent((object != result), ModificationEventType.SET_INDEXED, index, object, 1, result);
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

    // Factory to create handler from handler
    //-----------------------------------------------------------------------
    /**
     * Factory that casts the listener to a handler.
     */
    static class Factory implements ModificationHandlerFactory {
        public ModificationHandler createHandler(Collection coll, Object listener) {
            if (listener instanceof ModificationHandler) {
                return (ModificationHandler) listener;
            }
            return null;
        }
    }

}
