/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/Attic/ModificationEvent.java,v 1.3 2003/09/21 16:00:28 scolebourne Exp $
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
import java.util.EventObject;

/**
 * Base event class extended by each class that encapsulates event information.
 * <p>
 * This class can be used as is, but generally it is subclassed.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/09/21 16:00:28 $
 * 
 * @author Stephen Colebourne
 */
public class ModificationEvent extends EventObject {

    /** The source collection */
    protected final ObservableCollection collection;
    /** The handler */
    protected final ModificationHandler handler;
    /** The event code */
    protected final int type;

    // Constructor
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param obsCollection  the event source
     * @param handler  the handler
     * @param type  the event type
     */
    public ModificationEvent(
        final ObservableCollection obsCollection,
        final ModificationHandler handler,
        final int type) {

        super(obsCollection);
        this.collection = obsCollection;
        this.handler = handler;
        this.type = type;
    }

    // Basic info
    //-----------------------------------------------------------------------
    /**
     * Gets the collection the event is reporting on.
     * <p>
     * Using this collection will bypass any decorators that have been added
     * to the <code>ObservableCollection</code>. For example, if a synchronized
     * decorator was added it will not be called by changes to this collection.
     * <p>
     * For the synchronization case, you are normally OK however. If you
     * process the event in the same thread as the original change then your
     * code will be protected by the original synchronized decorator and this
     * collection may be used freely.
     * 
     * @return the collection
     */
    public ObservableCollection getObservedCollection() {
        return collection;
    }

    /**
     * Gets the base collection underlying the observable collection.
     * <p>
     * Using this collection will bypass the event sending mechanism.
     * It will also bypass any other decorators, such as synchronization.
     * Use with care.
     * 
     * @return the collection
     */
    public Collection getBaseCollection() {
        return handler.getBaseCollection();
    }

    /**
     * Gets the handler of the events.
     * 
     * @return the handler
     */
    public ModificationHandler getHandler() {
        return handler;
    }

    /**
     * Gets the event type constant.
     * <p>
     * This is one of the <i>method</i> constants from {@link ModificationEventType}.
     * 
     * @return the method event type constant
     */
    public int getType() {
        return type;
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
        buf.append(']');
        return buf.toString();
    }

}
