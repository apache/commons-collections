/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/event/Attic/StandardModificationHandler.java,v 1.1 2003/08/28 18:31:13 scolebourne Exp $
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

/**
 * The standard implementation of a <code>ModificationHandler</code> that
 * sends standard JavaBean style events to listeners.
 * <p>
 * The information gathered by this implementation is all that is available
 * as parameters or return values.
 * In addition, the <code>size</code> method is used on the collection.
 * All objects used are the real objects from the method calls, not clones.
 * <p>
 * Each listener can be filtered. There are separate filters for pre events
 * (modificationOccurring) and post events (modificationOccurred).
 * <p>
 * This implementation is the standard one. Most listeners will probably be
 * content with the events generated from here. However, if you need something
 * extra then this class can be subclassed or replaced as required. For example:
 * <ul>
 * <li>to store the state of the collection before the change
 * <li>to change the event classes
 * <li>to change the event dispatch mechanism to something other than listeners
 * <li>to clone the objects before placing them in the event
 * </ul>
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/08/28 18:31:13 $
 * 
 * @author Stephen Colebourne
 */
public class StandardModificationHandler extends ModificationHandler {

    /** A reusable empty holders array. */    
    protected static final Holder[] EMPTY_HOLDERS = new Holder[0];
    
    /** The event mask as to which event types to send on pre events. */
    protected int preMask = ModificationEventType.GROUP_NONE;
    /** The event mask as to which event types to send on post events. */
    protected int postMask = ModificationEventType.GROUP_NONE;
    /** The event listeners. */
    protected Holder[] holders = EMPTY_HOLDERS;
    /**
     * Temporary store for the size.
     * This makes the class thread-unsafe, but you should sync collections anyway.
     */
    protected int preSize;
    
    // Constructors
    //-----------------------------------------------------------------------
    /**
     * Constructor the creates the handler but leaves it invalid.
     * <p>
     * The handler can only be used after {@link #init(ObservedCollection)} is
     * called. This is normally done automatically by
     * {@link ObservedCollection#decorate(Collection, ModificationHandler)}.
     */
    public StandardModificationHandler() {
        super();
    }

    // Listeners
    //----------------------------------------------------------------------
    /**
     * Gets an array of all the listeners active in the handler.
     * <p>
     * All listeners will be instances of StandardModificationListener.
     * 
     * @return the listeners
     */
    public synchronized ModificationListener[] getModificationListeners() {
        ModificationListener[] lnrs = new ModificationListener[holders.length];
        for (int i = 0; i < holders.length; i++) {
            lnrs[i] = holders[i].listener;
        }
        return lnrs;
    }
    
    /**
     * Adds a listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is <code>null</code>.
     * 
     * @param listener  the listener to add, may be null (ignored)
     * @throws ClassCastException if the listener is not a StandardModificationListener
     */
    public void addModificationListener(ModificationListener listener) {
        addModificationListener(listener, -1, -1);
    }
    
    /**
     * Adds a listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is <code>null</code>.
     * 
     * @param listener  the listener to add, may be null (ignored)
     * @param preMask  the mask for pre events (0 for none, -1 for all)
     * @param postMask  the mask for post events (0 for none, -1 for all)
     * @throws ClassCastException if the listener is not a StandardModificationListener
     */
    public synchronized void addModificationListener(ModificationListener listener, int preMask, int postMask) {
        if (listener != null) {
            int oldSize = holders.length;
            Holder[] array = new Holder[oldSize + 1];
            System.arraycopy(holders, 0, array, 0, oldSize);
            array[oldSize] = new Holder((StandardModificationListener) listener, preMask, postMask);
            holders = array;
            calculateMasks();
        }
    }
    
    /**
     * Removes a listener to the list held in the handler.
     * <p>
     * No error occurs if the listener is not in the list or the type
     * of the listener is incorrect.
     * The listener is matched using ==.
     * 
     * @param listener  the listener to remove, may be null (ignored)
     */
    public synchronized void removeModificationListener(ModificationListener listener) {
        if (listener != null) {
            switch (holders.length) {
                case 0:
                return;
                
                case 1:
                if (holders[0].listener == listener) {
                    holders = EMPTY_HOLDERS;
                    calculateMasks();
                }
                return;
                
                default:
                Holder[] array = new Holder[holders.length - 1];
                boolean match = false;
                for (int i = 0; i < holders.length; i++) {
                    if (match) {
                        array[i - 1] = holders[i];
                    } else if (holders[i].listener == listener) {
                        match = true;
                    } else {
                        array[i] = holders[i];
                    }
                }
                holders = array;
                calculateMasks();
                return;
            }
        }
    }
    
    /**
     * Sets the masks of a listener.
     * <p>
     * No error occurs if the listener is not in the list.
     * The listener is matched using ==.
     * 
     * @return a non-null array of listeners
     */
    public synchronized void setModificationListenerMasks(StandardModificationListener listener, int preMask, int postMask) {
        if (listener != null) {
            for (int i = 0; i < holders.length; i++) {
                if (holders[i].listener == listener) {
                    holders[i].preMask = preMask;
                    holders[i].postMask = postMask;
                    calculateMasks();
                    break;
                }
            }
        }
    }
    
    // Holder for listener and masks
    //-----------------------------------------------------------------------
    protected static class Holder {
        StandardModificationListener listener;
        int preMask;
        int postMask;
        
        Holder(StandardModificationListener listener, int preMask, int postMask) {
            this.listener = listener;
            this.preMask = preMask;
            this.postMask = postMask;
        }
        
        public String toString() {
            return "[" + listener + ","
                + ModificationEventType.toString(preMask) + ","
                + ModificationEventType.toString(postMask) + "]";
        }

    }
    
    // Masks
    //-----------------------------------------------------------------------
    /**
     * Calculate the combined masks.
     */
    protected void calculateMasks() {
        preMask = ModificationEventType.GROUP_NONE;
        postMask = ModificationEventType.GROUP_NONE;
        for (int i = 0; i < holders.length; i++) {
            preMask |= holders[i].preMask;
            postMask |= holders[i].postMask;
        }
    }

    // Pre event sending
    //-----------------------------------------------------------------------
    /**
     * Handles the pre event.
     * 
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     * @return true to call the decorated collection
     */
    protected boolean preEvent(int type, int index, Object object, int repeat) {
        preSize = getCollection().size();
        return firePreEvent(type, index, object, repeat);
    }

    /**
     * Sends the pre event to the listeners.
     * 
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     * @return true to call the decorated collection
     */
    protected boolean firePreEvent(int type, int index, Object object, int repeat) {
        if ((preMask & type) > 0) {
            StandardModificationEvent event = null;
            synchronized (this) {
                for (int i = 0; i < holders.length; i++) {
                    Holder holder = holders[i];
                    if ((holder.preMask & type) > 0) {
                        if (event == null) {
                            event = new StandardModificationEvent(
                                getCollection(), this, type, preSize, index, object, repeat, null);
                        }
                        holder.listener.modificationOccurring(event);
                    }
                }
            }
        }
        return true;
    }

    // Post event sending
    //-----------------------------------------------------------------------
    /**
     * Handles the post event.
     * 
     * @param success  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     */
    protected void postEvent(boolean success, int type, int index, Object object, int repeat) {
        if (success) {
            firePostEvent(type, index, object, repeat, (success ? Boolean.TRUE : Boolean.FALSE));
        }
    }
    
    /**
     * Handles the post event.
     * 
     * @param success  true if the method succeeded in changing the collection
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     * @param result  the method result
     */
    protected void postEvent(boolean success, int type, int index, Object object, int repeat, Object result) {
        if (success) {
            firePostEvent(type, index, object, repeat, result);
        }
    }
    
    /**
     * Sends the post event to the listeners.
     * 
     * @param type  the event type to send
     * @param index  the index where the change starts
     * @param object  the object that was added/removed
     * @param repeat  the number of repeats of the add/remove
     * @param result  the method result
     */
    protected void firePostEvent(int type, int index, Object object, int repeat, Object result) {
        if ((postMask & type) > 0) {
            StandardModificationEvent event = null;
            synchronized (this) {
                for (int i = 0; i < holders.length; i++) {
                    Holder holder = holders[i];
                    if ((holder.postMask & type) > 0) {
                        if (event == null) {
                            event = new StandardModificationEvent(
                                getCollection(), this, type, preSize, index, object, repeat, result);
                        }
                        holder.listener.modificationOccurred(event);
                    }
                }
            }
        }
    }

    // Event handling
    //-----------------------------------------------------------------------
    /**
     * Send an event after clear() is called.
     * <p>
     * Override to only send event if something actually cleared.
     */
    public void postClear() {
        postEvent(preSize > 0, ModificationEventType.CLEAR, -1, null, 1, null);
    }

}
