/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/Attic/ModificationHandler.java,v 1.8 2003/10/09 20:50:04 scolebourne Exp $
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
 * To write your own handler, you will normally subclass and override the
 * <code>preEvent</code> and <code>postEvent</code> methods. However, you
 * could choose to override any individual event method.
 * <p>
 * This class could have been implemented as an interface, however to do so
 * would prevent the addition of extra events in the future. It does mean
 * that if you subclass this class, you must check it when you upgrade to a
 * later collections release.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.8 $ $Date: 2003/10/09 20:50:04 $
 * 
 * @author Stephen Colebourne
 */
public class ModificationHandler {
    
    /** Singleton factory */
    static final ModificationHandlerFactory FACTORY = new Factory();
    
    /** The collection being observed */
    private ObservableCollection obsCollection = null;
    /** The underlying base collection being decorated */
    private Collection baseCollection = null;
    /** The root handler */
    private final ModificationHandler rootHandler;
    /** The view offset, 0 if not a view */
    private final int viewOffset;
    
    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     */
    protected ModificationHandler() {
        super();
        this.rootHandler = this;
        this.viewOffset = 0;
    }

    /**
     * Constructor.
     * 
     * @param rootHandler  the base underlying handler
     * @param viewOffset  the offset on the base collection
     */
    protected ModificationHandler(ModificationHandler rootHandler, int viewOffset) {
        super();
        this.rootHandler = rootHandler;
        this.viewOffset = viewOffset;
    }

    /**
     * Initialize the handler.
     * <p>
     * The handler cannot be used until this method is called.
     * However, the handler's setup methods can be called.
     * All other methods will throw NullPointerException until then.
     * 
     * @param coll  the observed collection, must not be null
     * @param baseColl  the base collection, must not be null
     * @throws IllegalArgumentException if the collection is null
     * @throws IllegalStateException if init has already been called
     */
    void init(final ObservableCollection coll, Collection baseColl) {
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        if (baseColl == null) {
            throw new IllegalArgumentException("Base Collection must not be null");
        }
        if (this.obsCollection != null) {
            throw new IllegalArgumentException("init() has already been called");
        }
        this.obsCollection = coll;
        this.baseCollection = baseColl;
    }

    // Field access
    //-----------------------------------------------------------------------
    /**
     * Gets the observed collection.
     * 
     * @return the observed collection
     */
    public ObservableCollection getObservedCollection() {
        return obsCollection;
    }
    
    /**
     * Gets the base collection.
     * 
     * @return the base collection
     */
    protected Collection getBaseCollection() {
        return baseCollection;
    }
    
    /**
     * Gets the root handler.
     * 
     * @return the root handler
     */
    protected ModificationHandler getRootHandler() {
        return rootHandler;
    }
    
    /**
     * Gets the view offset.
     * 
     * @return the view offset
     */
    protected int getViewOffset() {
        return viewOffset;
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
     * <p>
     * This implementation does nothing.
     * 
     * @param type  the event type to send
     * @param index  the index where the change starts, the method param or derived
     * @param object  the object that will be added/removed/set, the method param or derived
     * @param repeat  the number of repeats of the add/remove, the method param or derived
     * @param previous  the previous value that will be removed/replaced, must exist in coll
     * @param view  the view collection that the change was actioned on, null if no view
     * @param viewOffset  the offset of the subList view, -1 if unknown
     */
    protected boolean preEvent(
            int type, int index, Object object, int repeat,
            Object previous, ObservableCollection view, int viewOffset) {
        return true;
    }

    /**
     * Handles the post event.
     * <p>
     * This implementation does nothing.
     * 
     * @param modified  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts, the method param or derived
     * @param object  the object that was added/removed/set, the method param or derived
     * @param repeat  the number of repeats of the add/remove, the method param or derived
     * @param previous  the previous value that was removed/replace, must have existed in coll
     * @param view  the view collection that the change was actioned on, null if no view
     * @param viewOffset  the offset of the subList view, -1 if unknown
     */
    protected void postEvent(
            boolean modified, int type, int index, Object object, int repeat,
            Object previous, ObservableCollection view, int viewOffset) {
    }

    // Event handling
    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * It does not set the index for List implementations.
     * 
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preAdd(Object object) {
        return preEvent(ModificationEventType.ADD, -1, object, 1, null, null, -1);
    }

    /**
     * Send an event after add(obj) is called.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * It does not set the index for List implementations.
     * 
     * @param object  the object being added
     * @param result  the result from the add method
     */
    protected void postAdd(Object object, boolean result) {
        postEvent(result, ModificationEventType.ADD, -1, object, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preAddIndexed(int index, Object object) {
        return preEvent(ModificationEventType.ADD_INDEXED, index + viewOffset, object, 1, null, null, -1);
    }

    /**
     * Send an event after add(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     */
    protected void postAddIndexed(int index, Object object) {
        postEvent(true, ModificationEventType.ADD_INDEXED, index + viewOffset, object, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param object  the object being added
     * @param nCopies  the number of copies being added
     * @return true to process modification
     */
    protected boolean preAddNCopies(Object object, int nCopies) {
        return preEvent(ModificationEventType.ADD_NCOPIES, -1, object, nCopies, null, null, -1);
    }

    /**
     * Send an event after add(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * The method result is not used by this implementation (Bag violates the
     * Collection contract)
     * 
     * @param object  the object being added
     * @param nCopies  the number of copies being added
     * @param result  the method result
     */
    protected void postAddNCopies(Object object, int nCopies, boolean result) {
        postEvent(true, ModificationEventType.ADD_NCOPIES, -1, object, nCopies, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before add(obj) is called on a ListIterator.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index of the iterator
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preAddIterated(int index, Object object) {
        return preEvent(ModificationEventType.ADD_ITERATED, index + viewOffset, object, 1, null, null, -1);
    }

    /**
     * Send an event after add(obj) is called on a ListIterator.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index of the iterator
     * @param object  the object being added
     */
    protected void postAddIterated(int index, Object object) {
        // assume collection changed
        postEvent(true, ModificationEventType.ADD_ITERATED, index + viewOffset, object, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before addAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param coll  the collection being added
     * @return true to process modification
     */
    protected boolean preAddAll(Collection coll) {
        return preEvent(ModificationEventType.ADD_ALL, -1, coll, 1, null, null, -1);
    }

    /**
     * Send an event after addAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param coll  the collection being added
     * @param collChanged  the result from the addAll method
     */
    protected void postAddAll(Collection coll, boolean collChanged) {
        postEvent(collChanged, ModificationEventType.ADD_ALL, -1, coll, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before addAll(int,coll) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index to addAll at
     * @param coll  the collection being added
     * @return true to process modification
     */
    protected boolean preAddAllIndexed(int index, Collection coll) {
        return preEvent(ModificationEventType.ADD_ALL_INDEXED, index + viewOffset, coll, 1, null, null, -1);
    }

    /**
     * Send an event after addAll(int,coll) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index to addAll at
     * @param coll  the collection being added
     * @param collChanged  the result from the addAll method
     */
    protected void postAddAllIndexed(int index, Collection coll, boolean collChanged) {
        postEvent(collChanged, ModificationEventType.ADD_ALL_INDEXED, index + viewOffset, coll, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before clear() is called.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @return true to process modification
     */
    protected boolean preClear() {
        return preEvent(ModificationEventType.CLEAR, -1, null, 1, null, null, -1);
    }

    /**
     * Send an event after clear() is called.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     */
    protected void postClear() {
        // assumes a modification occurred
        postEvent(true, ModificationEventType.CLEAR, -1, null, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(obj) is called.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param object  the object being removed
     * @return true to process modification
     */
    protected boolean preRemove(Object object) {
        return preEvent(ModificationEventType.REMOVE, -1, object, 1, null, null, -1);
    }

    /**
     * Send an event after remove(obj) is called.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param object  the object being removed
     * @param collChanged  the result from the remove method
     */
    protected void postRemove(Object object, boolean collChanged) {
        postEvent(collChanged, ModificationEventType.REMOVE, -1, object, 1, (collChanged ? object : null), null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(int) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index to remove at
     * @return true to process modification
     */
    protected boolean preRemoveIndexed(int index) {
        // could do a get(index) to determine previousValue
        // we don't for performance, but subclass may override
        return preEvent(ModificationEventType.REMOVE_INDEXED, index + viewOffset, null, 1, null, null, -1);
    }

    /**
     * Send an event after remove(int) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index to remove at
     * @param previousValue  the result from the remove method
     */
    protected void postRemoveIndexed(int index, Object previousValue) {
        postEvent(true, ModificationEventType.REMOVE_INDEXED, index + viewOffset, null, 1, previousValue, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param object  the object being removed
     * @param nCopies  the number of copies being removed
     * @return true to process modification
     */
    protected boolean preRemoveNCopies(Object object, int nCopies) {
        return preEvent(ModificationEventType.REMOVE_NCOPIES, -1, object, nCopies, null, null, -1);
    }

    /**
     * Send an event after remove(obj,int) is called on a Bag.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param object  the object being removed
     * @param nCopies  the number of copies being removed
     * @param collChanged  the result from the remove method
     */
    protected void postRemoveNCopies(Object object, int nCopies, boolean collChanged) {
        postEvent(collChanged, ModificationEventType.REMOVE_NCOPIES, -1, object, nCopies, (collChanged ? object : null), null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove() is called on a Buffer.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @return true to process modification
     */
    protected boolean preRemoveNext() {
        return preEvent(ModificationEventType.REMOVE_NEXT, -1, null, 1, null, null, -1);
    }

    /**
     * Send an event after remove() is called on a Buffer.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param removedValue  the previous value at this index
     */
    protected void postRemoveNext(Object removedValue) {
        // assume collection changed
        postEvent(true, ModificationEventType.REMOVE_NEXT, -1, removedValue, 1, removedValue, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before remove(obj) is called on an Iterator.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index of the iterator
     * @param removedValue  the object being removed
     * @return true to process modification
     */
    protected boolean preRemoveIterated(int index, Object removedValue) {
        return preEvent(ModificationEventType.REMOVE_ITERATED, index + viewOffset, removedValue, 1, removedValue, null, -1);
    }

    /**
     * Send an event after remove(obj) is called on an Iterator.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index of the iterator
     * @param removedValue  the previous value at this index
     */
    protected void postRemoveIterated(int index, Object removedValue) {
        // assume collection changed
        postEvent(true, ModificationEventType.REMOVE_ITERATED, index + viewOffset, removedValue, 1, removedValue, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before removeAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param coll  the collection being removed
     * @return true to process modification
     */
    protected boolean preRemoveAll(Collection coll) {
        return preEvent(ModificationEventType.REMOVE_ALL, -1, coll, 1, null, null, -1);
    }

    /**
     * Send an event after removeAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param coll  the collection being removed
     * @param collChanged  the result from the removeAll method
     */
    protected void postRemoveAll(Collection coll, boolean collChanged) {
        postEvent(collChanged, ModificationEventType.REMOVE_ALL, -1, coll, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before retainAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param coll  the collection being retained
     * @return true to process modification
     */
    protected boolean preRetainAll(Collection coll) {
        return preEvent(ModificationEventType.RETAIN_ALL, -1, coll, 1, null, null, -1);
    }

    /**
     * Send an event after retainAll(coll) is called.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param coll  the collection being retained
     * @param collChanged  the result from the retainAll method
     */
    protected void postRetainAll(Collection coll, boolean collChanged) {
        postEvent(collChanged, ModificationEventType.RETAIN_ALL, -1, coll, 1, null, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before set(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @return true to process modification
     */
    protected boolean preSetIndexed(int index, Object object) {
        // could do a get(index) to determine previousValue
        // we don't for performance, but subclass may override
        return preEvent(ModificationEventType.SET_INDEXED, index + viewOffset, object, 1, null, null, -1);
    }

    /**
     * Send an event after set(int,obj) is called on a List.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index to add at
     * @param object  the object being added
     * @param previousValue  the result from the set method
     */
    protected void postSetIndexed(int index, Object object, Object previousValue) {
        // reference check for modification, in case equals() has issues (eg. performance)
        postEvent((object != previousValue), ModificationEventType.SET_INDEXED, index + viewOffset, object, 1, previousValue, null, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Store data and send event before set(obj) is called on a ListIterator.
     * <p>
     * This implementation forwards to {@link #preEvent}.
     * 
     * @param index  the index to set at
     * @param object  the object being added
     * @param previousValue  the previous value at this index
     * @return true to process modification
     */
    protected boolean preSetIterated(int index, Object object, Object previousValue) {
        return preEvent(ModificationEventType.SET_ITERATED, index + viewOffset, object, 1, previousValue, null, -1);
    }

    /**
     * Send an event after set(obj) is called on a ListIterator.
     * <p>
     * This implementation forwards to {@link #postEvent}.
     * 
     * @param index  the index to set at
     * @param object  the object being added
     * @param previousValue  the previous value at this index
     */
    protected void postSetIterated(int index, Object object, Object previousValue) {
        // reference check for modification, in case equals() has issues (eg. performance)
        postEvent((object != previousValue), ModificationEventType.SET_ITERATED, index + viewOffset, object, 1, previousValue, null, -1);
    }

    // SortedSet Views
    //-----------------------------------------------------------------------
    /**
     * Creates a new handler for SortedSet subSet.
     * 
     * @param fromElement  the from element
     * @param toElement  the to element
     */
    protected ModificationHandler createSubSetHandler(Object fromElement, Object toElement) {
        return new SetViewHandler(rootHandler);
    }
    
    /**
     * Creates a new handler for SortedSet headSet.
     * 
     * @param toElement  the to element
     */
    protected ModificationHandler createHeadSetHandler(Object toElement) {
        return new SetViewHandler(rootHandler);
    }
    
    /**
     * Creates a new handler for SortedSet tailSet.
     * 
     * @param fromElement  the from element
     */
    protected ModificationHandler createTailSetHandler(Object fromElement) {
        return new SetViewHandler(rootHandler);
    }
    
    /**
     * Inner class for views.
     */    
    protected static class SetViewHandler extends ModificationHandler {
        
        /**
         * Constructor.
         * 
         * @param rootHandler  the base underlying handler
         */
        protected SetViewHandler(ModificationHandler rootHandler) {
            super(rootHandler, 0);
        }

        /**
         * Override the preEvent method to forward all events to the 
         * underlying handler. This method also inserts details of the view
         * that caused the event.
         */
        protected boolean preEvent(
                int type, int index, Object object, int repeat,
                Object previous, ObservableCollection ignoredView, int offset) {

            return getRootHandler().preEvent(
                type, index, object, repeat,
                previous, getObservedCollection(), offset);
        }

        /**
         * Override the postEvent method to forward all events to the 
         * underlying handler. This method also inserts details of the view
         * that caused the event.
         */
        protected void postEvent(
                boolean modified, int type, int index, Object object, int repeat,
                Object previous, ObservableCollection ignoredView, int offset) {

            getRootHandler().postEvent(
                modified, type, index, object, repeat,
                previous, getObservedCollection(), offset);
        }
    }
    
    // List View
    //-----------------------------------------------------------------------
    /**
     * Creates a new handler for subLists that is aware of the offset.
     * 
     * @param fromIndex  the sublist fromIndex (inclusive)
     * @param toIndex  the sublist toIndex (exclusive)
     */
    protected ModificationHandler createSubListHandler(int fromIndex, int toIndex) {
        return new SubListHandler(rootHandler, fromIndex + viewOffset);
    }

    /**
     * Inner class for subLists.
     */    
    protected static class SubListHandler extends ModificationHandler {
        
        /**
         * Constructor.
         * 
         * @param rootHandler  the base underlying handler
         * @param viewOffset  the offset on the base collection
         */
        protected SubListHandler(ModificationHandler rootHandler, int viewOffset) {
            super(rootHandler, viewOffset);
        }

        /**
         * Override the preEvent method to forward all events to the 
         * underlying handler. This method also inserts details of the view
         * that caused the event.
         */
        protected boolean preEvent(
                int type, int index, Object object, int repeat,
                Object previous, ObservableCollection ignoredView, int ignoredOffset) {

            return getRootHandler().preEvent(
                type, index, object, repeat,
                previous, getObservedCollection(), getViewOffset());
        }

        /**
         * Override the postEvent method to forward all events to the 
         * underlying handler. This method also inserts details of the view
         * that caused the event.
         */
        protected void postEvent(
                boolean modified, int type, int index, Object object, int repeat,
                Object previous, ObservableCollection ignoredView, int ignoredOffset) {

            getRootHandler().postEvent(
                modified, type, index, object, repeat,
                previous, getObservedCollection(), getViewOffset());
        }
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
        return name + '[' + (obsCollection == null ? "" : "initialised") + ']';
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
