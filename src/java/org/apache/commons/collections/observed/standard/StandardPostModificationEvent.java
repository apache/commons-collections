/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/standard/Attic/StandardPostModificationEvent.java,v 1.6 2003/11/27 22:55:16 scolebourne Exp $
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

import org.apache.commons.collections.observed.ModificationHandler;
import org.apache.commons.collections.observed.ObservableCollection;

/**
 * Event class that encapsulates all the event information for a
 * standard collection event.
 * <p>
 * The information stored in this event is all that is available as
 * parameters or return values.
 * In addition, the <code>size</code> method is used on the collection.
 * All objects used are the real objects from the method calls, not clones.
 *
 * @deprecated TO BE REMOVED BEFORE v3.0
 * @since Commons Collections 3.0
 * @version $Revision: 1.6 $ $Date: 2003/11/27 22:55:16 $
 * 
 * @author Stephen Colebourne
 */
public class StandardPostModificationEvent extends StandardModificationEvent {

    /** The size after the event */
    protected final int postSize;

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
    public StandardPostModificationEvent(
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

        super(obsCollection, handler, type, preSize, index,
            object, repeat, previous, view, viewOffset);
        postSize = collection.size();
    }

    // Size info
    //-----------------------------------------------------------------------
    /**
     * Gets the size after the change.
     * 
     * @return the size after the change
     */
    public int getPostSize() {
        return postSize;
    }

    /**
     * Gets the size change, negative for remove/clear.
     * 
     * @return the size before the change
     */
    public int getSizeChange() {
        return postSize - preSize;
    }

    /**
     * Returns true if the size of the collection changed.
     * 
     * @return true is the size changed
     */
    public boolean isSizeChanged() {
        return (preSize != postSize);
    }

}
