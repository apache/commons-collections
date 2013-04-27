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
package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

/**
 * Transformer implementation that creates a new object instance by reflection.
 *
 * @since 3.0
 * @version $Id$
 */
public class InstantiateTransformer<T> implements Transformer<Class<? extends T>, T>, Serializable {

    /** The serial version */
    private static final long serialVersionUID = 3786388740793356347L;

    /** Singleton instance that uses the no arg constructor */
    public static final Transformer<Class<?>, ?> NO_ARG_INSTANCE = new InstantiateTransformer<Object>();

    /** The constructor parameter types */
    private final Class<?>[] iParamTypes;
    /** The constructor arguments */
    private final Object[] iArgs;

    /**
     * Get a typed no-arg instance.
     *
     * @param <T>  the type of the objects to be created
     * @return Transformer<Class<? extends T>, T>
     */
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer() {
        return new InstantiateTransformer<T>();
    }

    /**
     * Transformer method that performs validation.
     *
     * @param <T>  the type of the objects to be created
     * @param paramTypes  the constructor parameter types
     * @param args  the constructor arguments
     * @return an instantiate transformer
     */
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer(Class<?>[] paramTypes, Object[] args) {
        if (((paramTypes == null) && (args != null))
            || ((paramTypes != null) && (args == null))
            || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }

        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateTransformer<T>();
        }
        paramTypes = paramTypes.clone();
        args = args.clone();
        return new InstantiateTransformer<T>(paramTypes, args);
    }

    /**
     * Constructor for no arg instance.
     */
    private InstantiateTransformer() {
        super();
        iParamTypes = null;
        iArgs = null;
    }

    /**
     * Constructor that performs no validation.
     * Use <code>instantiateTransformer</code> if you want that.
     *
     * @param paramTypes  the constructor parameter types, not cloned
     * @param args  the constructor arguments, not cloned
     */
    public InstantiateTransformer(final Class<?>[] paramTypes, final Object[] args) {
        super();
        iParamTypes = paramTypes;
        iArgs = args;
    }

    /**
     * Transforms the input Class object to a result by instantiation.
     *
     * @param input  the input object to transform
     * @return the transformed result
     */
    public T transform(final Class<? extends T> input) {
        try {
            if (input == null) {
                throw new FunctorException(
                    "InstantiateTransformer: Input object was not an instanceof Class, it was a null object");
            }
            final Constructor<? extends T> con = input.getConstructor(iParamTypes);
            return con.newInstance(iArgs);
        } catch (final NoSuchMethodException ex) {
            throw new FunctorException("InstantiateTransformer: The constructor must exist and be public ");
        } catch (final InstantiationException ex) {
            throw new FunctorException("InstantiateTransformer: InstantiationException", ex);
        } catch (final IllegalAccessException ex) {
            throw new FunctorException("InstantiateTransformer: Constructor must be public", ex);
        } catch (final InvocationTargetException ex) {
            throw new FunctorException("InstantiateTransformer: Constructor threw an exception", ex);
        }
    }

}
