/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FunctorException;

/**
 * Factory implementation that creates a new object instance by reflection.
 * 
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class InstantiateFactory<T> implements Factory<T>, Serializable {

    /** The serial version */
    private static final long serialVersionUID = -7732226881069447957L;

    /** The class to create */
    private final Class<T> iClassToInstantiate;
    /** The constructor parameter types */
    private final Class<?>[] iParamTypes;
    /** The constructor arguments */
    private final Object[] iArgs;
    /** The constructor */
    private transient Constructor<T> iConstructor = null;

    /**
     * Factory method that performs validation.
     * 
     * @param classToInstantiate  the class to instantiate, not null
     * @param paramTypes  the constructor parameter types
     * @param args  the constructor arguments
     * @return a new instantiate factory
     */
    public static <T> Factory<T> getInstance(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
        if (classToInstantiate == null) {
            throw new IllegalArgumentException("Class to instantiate must not be null");
        }
        if (((paramTypes == null) && (args != null))
            || ((paramTypes != null) && (args == null))
            || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }

        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateFactory<T>(classToInstantiate);
        }
        paramTypes = paramTypes.clone();
        args = args.clone();
        return new InstantiateFactory<T>(classToInstantiate, paramTypes, args);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param classToInstantiate  the class to instantiate
     */
    public InstantiateFactory(Class<T> classToInstantiate) {
        super();
        iClassToInstantiate = classToInstantiate;
        iParamTypes = null;
        iArgs = null;
        findConstructor();
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param classToInstantiate  the class to instantiate
     * @param paramTypes  the constructor parameter types, not cloned
     * @param args  the constructor arguments, not cloned
     */
    public InstantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
        super();
        iClassToInstantiate = classToInstantiate;
        iParamTypes = paramTypes;
        iArgs = args;
        findConstructor();
    }

    /**
     * Find the Constructor for the class specified.
     */
    private void findConstructor() {
        try {
            iConstructor = iClassToInstantiate.getConstructor(iParamTypes);

        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
        }
    }

    /**
     * Creates an object using the stored constructor.
     * 
     * @return the new object
     */
    public T create() {
        // needed for post-serialization
        if (iConstructor == null) {
            findConstructor();
        }

        try {
            return iConstructor.newInstance(iArgs);
        } catch (InstantiationException ex) {
            throw new FunctorException("InstantiateFactory: InstantiationException", ex);
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
        } catch (InvocationTargetException ex) {
            throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex);
        }
    }
    
}
