/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/standard/Attic/StandardModificationEvent.java,v 1.7 2003/09/21 20:00:29 scolebourne Exp $
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
package org.apache.commons.collections.observed.standard;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.observed.ModificationEvent;
import org.apache.commons.collections.observed.ModificationEventType;
import org.apache.commons.collections.observed.ModificationHandler;
import org.apache.commons.collections.observed.ObservableCollection;

/**
 * Event class that encapsulates the event information for a
 * standard collection event. Two subclasses are provided, one for
 * pre and one for post events.
 * <p>
 * The information stored in this event is all that is available as
 * parameters or return values.
 * In addition, the <code>size</code> method is used on the collection.
 * All objects used are the real objects from the method calls, not clones.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.7 $ $Date: 2003/09/21 20:00:29 $
 * 
 * @author Stephen Colebourne
 */
public class StandardModificationEvent extends ModificationEvent {

    /** The size before the event */
    protected final int preSize;
    /** The index of the change */
    protected final int index;
    /** The object of the change */
    protected final Object object;
    /** The number of changes */
    protected final int repeat;
    /** The result of the method call */
    protected final Object previous;
    /** The view that the event came from, null if none */
    protected final ObservableCollection view;
    /** The offset index within the main collection of the view, -1 if none */
    protected final int viewOffset;

    // Constructor
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param obsCollection  the event source
     * @param handler  the handler
     * @param type  the event type
     * @param preSize  the size before the change
     * @param index  the index that changed
     * @param object  the value that changed
     * @param repeat  the number of repeats
     * @param previous  the previous value being removed/replaced
     * @param view  the view collection, null if event from main collection
     * @param viewOffset  the offset within the main collection of the view, -1 if unknown
     */
    public StandardModificationEvent(
        final ObservableCollection obsCollection,
        final ModificationHandler handler,
        final int type,
        final int preSize,
        final int index,
        final Object object,
        final int repeat,
        final Object previous,
        final ObservableCollection view,
        final int viewOffset) {

        super(obsCollection, handler, type);
        this.preSize = preSize;
        this.index = index;
        this.object = object;
        this.repeat = repeat;
        this.previous = previous;
        this.view = view;
        this.viewOffset = viewOffset;
    }

    // Change info
    //-----------------------------------------------------------------------
    /**
     * Gets the index of the change.
     * <p>
     * This is <code>-1</code> when not applicable. Typically only used
     * for {@link java.util.List} events.
     * 
     * @return the change index
     */
    public int getChangeIndex() {
        return index;
    }

    /**
     * Gets the object that was added/removed/set.
     * <p>
     * This is <code>null</code> when not applicable, such as for clear().
     * 
     * @return the changing object
     */
    public Object getChangeObject() {
        return object;
    }

    /**
     * Gets the collection of changed objects.
     * <p>
     * For clear, it is an empty list.
     * For bulk operations, it is the collection.
     * For non-bulk operations, it is a size one list.
     * 
     * @return the changing collection, never null
     */
    public Collection getChangeCollection() {
        if (object == null) {
            return Collections.EMPTY_LIST;
        } else if (isType(ModificationEventType.GROUP_BULK)) {
            if (object instanceof Collection) {
                return (Collection) object;
            } else {
                throw new IllegalStateException(
                    "Bulk operations must involve a Collection, but was " + object.getClass().getName());
            }
        } else {
            return Collections.singletonList(object);
        }
    }

    /**
     * Gets the number of times the object was added/removed.
     * <p>
     * This is normally <code>1</code>, but will be used for 
     * {@link org.apache.commons.collections.Bag Bag} events.
     * 
     * @return the repeat
     */
    public int getChangeRepeat() {
        return repeat;
    }

    /**
     * Gets the previous value that is being replaced or removed.
     * <p>
     * This is only returned if the value definitely was previously in the
     * collection. Bulk operatons will not return this.
     * 
     * @return the previous value that was removed/replaced
     */
    public Object getPrevious() {
        return previous;
    }

    // Size info
    //-----------------------------------------------------------------------
    /**
     * Gets the size before the change.
     * 
     * @return the size before the change
     */
    public int getPreSize() {
        return preSize;
    }

    // View info
    //-----------------------------------------------------------------------
    /**
     * Gets the view, <code>null</code> if none.
     * <p>
     * A view is a subSet, headSet, tailSet, subList and so on.
     * 
     * @return the view
     */
    public ObservableCollection getView() {
        return view;
    }

    /**
     * Checks whether the event originated from a view.
     * 
     * @return true if event came from a view
     */
    public boolean isView() {
        return (view != null);
    }

    /**
     * Gets the view offset, <code>-1</code> if no view or unknown offset.
     * <p>
     * This refers to the index of the start of the view within the main collection.
     * 
     * @return the view offset
     */
    public int getViewOffset() {
        return viewOffset;
    }

    // Event type
    //-----------------------------------------------------------------------
    /**
     * Checks to see if the event is an add event (add/addAll).
     * 
     * @return true if of the specified type
     */
    public boolean isTypeAdd() {
        return (type & ModificationEventType.GROUP_ADD) > 0;
    }

    /**
     * Checks to see if the event is a remove event (remove/removeAll/retainAll/clear).
     * 
     * @return true if of the specified type
     */
    public boolean isTypeReduce() {
        return (type & ModificationEventType.GROUP_REDUCE) > 0;
    }

    /**
     * Checks to see if the event is a change event (set).
     * 
     * @return true if of the specified type
     */
    public boolean isTypeChange() {
        return (type & ModificationEventType.GROUP_CHANGE) > 0;
    }

    /**
     * Checks to see if the event is a bulk event (addAll/removeAll/retainAll/clear).
     * 
     * @return true if of the specified type
     */
    public boolean isTypeBulk() {
        return (type & ModificationEventType.GROUP_BULK) > 0;
    }

    /**
     * Checks to see if the event is of the specified type.
     * <p>
     * This is any combination of constants from {@link ModificationEventType}.
     * 
     * @param eventType  an event type constant
     * @return true if of the specified type
     */
    public boolean isType(final int eventType) {
        return (type & eventType) > 0;
    }

    // toString
    //-----------------------------------------------------------------------
    /**
     * Gets a debugging string version of the event.
     * 
     * @return a debugging string
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(64);
        buf.append("ModificationEvent[type=");
        buf.append(ModificationEventType.toString(type));
        if (index >= 0) {
            buf.append(",index=");
            buf.append(index);
        }
        if (type != ModificationEventType.CLEAR) {
            buf.append(",object=");
            if (object instanceof List) {
                buf.append("List:size:");
                buf.append(((List) object).size());
            } else if (object instanceof Set) {
                buf.append("Set:size:");
                buf.append(((Set) object).size());
            } else if (object instanceof Bag) {
                buf.append("Bag:size:");
                buf.append(((Bag) object).size());
            } else if (object instanceof Collection) {
                buf.append("Collection:size:");
                buf.append(((Collection) object).size());
            } else if (object instanceof Map) {
                buf.append("Map:size:");
                buf.append(((Map) object).size());
            } else if (object instanceof Object[]) {
                buf.append("Array:size:");
                buf.append(((Object[]) object).length);
            } else if (object == null) {
                buf.append("null");
            } else {
                buf.append(object.toString());
            }
        }
        buf.append(']');
        return buf.toString();
    }

}
