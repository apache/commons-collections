/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/standard/Attic/StandardModificationEvent.java,v 1.1 2003/09/03 23:54:26 scolebourne Exp $
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

/**
 * Event class that encapsulates all the event information for a
 * standard collection event.
 * <p>
 * The information stored in this event is all that is available as
 * parameters or return values.
 * In addition, the <code>size</code> method is used on the collection.
 * All objects used are the real objects from the method calls, not clones.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/09/03 23:54:26 $
 * 
 * @author Stephen Colebourne
 */
public class StandardModificationEvent extends ModificationEvent {

    /** The size before the event */
    protected final int preSize;
    /** The size after the event */
    protected final int postSize;
    /** The index of the change */
    protected final int index;
    /** The object of the change */
    protected final Object object;
    /** The number of changes */
    protected final int repeat;
    /** The result of the method call */
    protected final Object result;

    // Constructor
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param collection  the event source
     * @param handler  the handler
     * @param type  the event type
     * @param preSize  the size before the change
     * @param index  the index that changed
     * @param object  the value that changed
     * @param repeat  the number of repeats
     * @param result  the method result
     */
    public StandardModificationEvent(
        final Collection collection,
        final ModificationHandler handler,
        final int type,
        final int preSize,
        final int index,
        final Object object,
        final int repeat,
        final Object result) {

        super(collection, handler, type);
        this.preSize = preSize;
        this.postSize = collection.size();
        this.index = index;
        this.object = object;
        this.repeat = repeat;
        this.result = result;
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
     * Gets the result of the method call.
     * <p>
     * For set(int) and remove(int) this will be the previous value
     * being replaced.
     * <p>
     * If there is no result yet, <code>null</code> will be returned.
     * If the result was a <code>boolean</code>, a <code>Boolean</code> is returned.
     * If the result was void, <code>null</code> will be returned.
     * 
     * @return the repeat
     */
    public Object getResult() {
        return result;
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

    /**
     * Gets the size after the change.
     * <p>
     * This method will return the same as <code>getPreSzie</code> if
     * called when handling a pre event.
     * 
     * @return the size before the change
     */
    public int getPostSize() {
        return postSize;
    }

    /**
     * Gets the size change, negative for remove/clear.
     * <p>
     * This method will return <code>zero</code> if called when handling a pre event.
     * 
     * @return the size before the change
     */
    public int getSizeChange() {
        return postSize - preSize;
    }

    /**
     * Returns true if the size of the collection changed.
     * <p>
     * This method will return <code>false</code> if called when handling a pre event.
     * 
     * @return true is the size changed
     */
    public boolean isSizeChanged() {
        return (preSize != postSize);
    }

    // Event type
    //-----------------------------------------------------------------------
    /**
     * Checks to see if the event is of the specified type.
     * <p>
     * This is any combination of constants from {@link ObservedEventType}.
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
        buf.append("ObservedEvent[type=");
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
