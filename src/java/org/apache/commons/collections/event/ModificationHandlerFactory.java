/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/event/Attic/ModificationHandlerFactory.java,v 1.1 2003/09/03 00:11:28 scolebourne Exp $
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
 * Defines a factory for creating ModificationHandler instances and utilities
 * for using the factories.
 * <p>
 * If an application wants to register its own event handler classes, it should
 * do so using this class. This must be done during initialization to be 
 * fully thread-safe. There are two steps:
 * <ol>
 * <li>A factory must be created that is a subclass of this class
 * <li>One of the <code>addFactory</code> methods must be called
 * </ol>
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/09/03 00:11:28 $
 * 
 * @author Stephen Colebourne
 */
public abstract class ModificationHandlerFactory {
    
    /** The list of factories, cannot pre-populate as factories are subclasses */
    private static ModificationHandlerFactory[] factories = new ModificationHandlerFactory[0];
    
    // Static access to factories
    //-----------------------------------------------------------------------
    /**
     * Creates a handler subclass based on the specified listener.
     * <p>
     * The method is defined in terms of an Object to allow for unusual
     * listeners, such as a Swing model object.
     * 
     * @param listener  a listener object to create a handler for
     * @return an instantiated handler with the listener attached
     * @throws IllegalArgumentException if no suitable handler
     */
    public static final ModificationHandler createHandler(final Collection coll, final Object listener) {
        for (int i = 0; i < factories.length; i++) {
            ModificationHandler handler = factories[i].create(coll, listener);
            if (handler != null) {
                return handler;
            }
        }
        throw new IllegalArgumentException("Unrecognised listener type: " +
            (listener == null ? "null" : listener.getClass().getName()));
    }

    /**
     * Adds a handler factory to the list available for use.
     * This factory will be checked after the others in the list.
     * <p>
     * This method is used to add your own event handler to the supplied ones.
     * Registering the factory will enable the standard <code>decorate</code>
     * method on <code>ObservedColection</code> to create your handler.
     * <p>
     * This method is NOT threadsafe! It should only be called during initialization.
     * 
     * @param factory  the factory to add, may be null
     */
    public static void addFactory(final ModificationHandlerFactory factory) {
        addFactory(factory, false);
    }

    /**
     * Adds a handler factory to the list available for use selecting whether
     * to override existing factories or not.
     * <p>
     * This method is used to add your own event handler to the supplied ones.
     * Registering the factory will enable the standard <code>decorate</code>
     * method on <code>ObservedColection</code> to create your handler.
     * <p>
     * It is also possible to replace the Jakarta handlers using this method.
     * Obviously this should be done with care in a shared web environment!
     * <p>
     * This method is NOT threadsafe! It should only be called during initialization.
     * 
     * @param factory  the factory to add, may be null
     */
    public static void addFactory(final ModificationHandlerFactory factory, final boolean override) {
        if (factory != null) {
            ModificationHandlerFactory[] array = new ModificationHandlerFactory[factories.length + 1];
            if (override) {
                System.arraycopy(factories, 0, array, 1, factories.length);
                array[0] = factory;
            } else {
                System.arraycopy(factories, 0, array, 0, factories.length);
                array[factories.length] = factory;
            }
            factories = array;
        }
    }

    // Initialize the collection-handler pair
    //-----------------------------------------------------------------------
    /**
     * Initializes handler specified with the collection.
     * <p>
     * The method avoids exposing an implementation detail on ModificationHandler.
     * 
     * @param handler  the handler to initialize
     * @param coll  the collection to store
     * @return an instantiated handler with the listener attached
     */
    public static void initHandler(final ModificationHandler handler, final ObservedCollection coll) {
        handler.init(coll);
    }
    
    // Constructor
    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    protected ModificationHandlerFactory() {
        super();
    }

    // Abstract factory
    //-----------------------------------------------------------------------
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
    protected abstract ModificationHandler create(Collection coll, Object listener);
    
}
