/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections;

import org.apache.commons.collections.functors.ConstantFactory;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.functors.ExceptionFactory;
import org.apache.commons.collections.functors.PrototypeFactory;

/**
 * <code>FactoryUtils</code> provides reference implementations and utilities
 * for the Factory functor interface. The supplied factories are:
 * <ul>
 * <li>Prototype - clones a specified object
 * <li>Reflection - creates objects using reflection
 * <li>Constant - always returns the same object
 * <li>Null - always returns null
 * <li>Exception - always throws an exception
 * </ul>
 * All the supplied factories are Serializable.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.12 $ $Date: 2004/01/14 21:43:04 $
 *
 * @author Stephen Colebourne
 */
public class FactoryUtils {

    /**
     * This class is not normally instantiated.
     */
    public FactoryUtils() {
        super();
    }

    /**
     * Gets a Factory that always throws an exception.
     * This could be useful during testing as a placeholder.
     *
     * @return the factory
     */
    public static Factory exceptionFactory() {
        return ExceptionFactory.INSTANCE;
    }

    /**
     * Gets a Factory that will return null each time the factory is used.
     * This could be useful during testing as a placeholder.
     *
     * @return the factory
     */
    public static Factory nullFactory() {
        return ConstantFactory.NULL_INSTANCE;
    }

    /**
     * Creates a Factory that will return the same object each time the factory
     * is used. No check is made that the object is immutable. In general, only
     * immutable objects should use the constant factory. Mutable objects should
     * use the prototype factory.
     *
     * @param constantToReturn  the constant object to return each time in the factory
     * @return the <code>constant</code> factory.
     */
    public static Factory constantFactory(Object constantToReturn) {
        return ConstantFactory.getInstance(constantToReturn);
    }

    /**
     * Creates a Factory that will return a clone of the same prototype object
     * each time the factory is used. The prototype will be cloned using one of these
     * techniques (in order):
     * <ul>
     * <li>public clone method
     * <li>public copy constructor
     * <li>serialization clone
     * <ul>
     *
     * @param prototype  the object to clone each time in the factory
     * @return the <code>prototype</code> factory
     * @throws IllegalArgumentException if the prototype is null
     * @throws IllegalArgumentException if the prototype cannot be cloned
     */
    public static Factory prototypeFactory(Object prototype) {
        return PrototypeFactory.getInstance(prototype);
    }

    /**
     * Creates a Factory that can create objects of a specific type using
     * a no-args constructor.
     *
     * @param classToInstantiate  the Class to instantiate each time in the factory
     * @return the <code>reflection</code> factory
     * @throws IllegalArgumentException if the classToInstantiate is null
     */
    public static Factory instantiateFactory(Class classToInstantiate) {
        return InstantiateFactory.getInstance(classToInstantiate, null, null);
    }

    /**
     * Creates a Factory that can create objects of a specific type using
     * the arguments specified to this method.
     *
     * @param classToInstantiate  the Class to instantiate each time in the factory
     * @param paramTypes  parameter types for the constructor, can be null
     * @param args  the arguments to pass to the constructor, can be null
     * @return the <code>reflection</code> factory
     * @throws IllegalArgumentException if the classToInstantiate is null
     * @throws IllegalArgumentException if the paramTypes and args don't match
     * @throws IllegalArgumentException if the constructor doesn't exist
     */
    public static Factory instantiateFactory(Class classToInstantiate, Class[] paramTypes, Object[] args) {
        return InstantiateFactory.getInstance(classToInstantiate, paramTypes, args);
    }

}
