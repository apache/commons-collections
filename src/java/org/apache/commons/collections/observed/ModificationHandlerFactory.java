/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/observed/Attic/ModificationHandlerFactory.java,v 1.2 2003/09/21 16:00:28 scolebourne Exp $
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
 * Defines a factory for creating ModificationHandler instances.
 * <p>
 * If an application wants to register its own event handler classes, it should
 * do so using this class. This must be done during initialization to be 
 * fully thread-safe. There are two steps:
 * <ol>
 * <li>A factory must be created that is an implementation of this class
 * <li>One of the <code>registerFactory</code> methods must be called on ObservableCollection
 * </ol>
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/09/21 16:00:28 $
 * 
 * @author Stephen Colebourne
 */
public interface ModificationHandlerFactory {
    
    /**
     * Creates a handler subclass for the specified listener.
     * <p>
     * The implementation will normally check to see if the listener
     * is of a suitable type, and then cast it. <code>null</code> is
     * returned if this factory does not handle the specified type.
     * <p>
     * The listener is defined in terms of an Object to allow for unusual
     * listeners, such as a Swing model object.
     * <p>
     * The collection the handler is for is passed in to allow for a different
     * handler to be selected for the same listener type based on the collection.
     * 
     * @param coll  the collection being decorated
     * @param listener  a listener object to create a handler for
     * @return an instantiated handler with the listener attached,
     *  or null if the listener type is unsuited to this factory
     */
    ModificationHandler createHandler(Collection coll, Object listener);
    
}
