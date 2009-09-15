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

import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that creates a new object instance by reflection.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
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
     * @param <T>
     * @return Transformer<Class<? extends T>, T>
     */
    public static <T> Transformer<Class<? extends T>, T> getInstance() {
        return new InstantiateTransformer<T>();
    }

    /**
     * Transformer method that performs validation.
     *
     * @param paramTypes  the constructor parameter types
     * @param args  the constructor arguments
     * @return an instantiate transformer
     */
    public static <T> Transformer<Class<? extends T>, T> getInstance(Class<?>[] paramTypes, Object[] args) {
        if (((paramTypes == null) && (args != null))
            || ((paramTypes != null) && (args == null))
            || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }

        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateTransformer<T>();
        }
        paramTypes = (Class[]) paramTypes.clone();
        args = (Object[]) args.clone();
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
     * Use <code>getInstance</code> if you want that.
     *
     * @param paramTypes  the constructor parameter types, not cloned
     * @param args  the constructor arguments, not cloned
     */
    public InstantiateTransformer(Class<?>[] paramTypes, Object[] args) {
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
    public T transform(Class<? extends T> input) {
        try {
            if (input instanceof Class == false) {
                throw new FunctorException(
                    "InstantiateTransformer: Input object was not an instanceof Class, it was a "
                        + (input == null ? "null object" : input.getClass().getName()));
            }
            Constructor<? extends T> con = input.getConstructor(iParamTypes);
            return con.newInstance(iArgs);
        } catch (NoSuchMethodException ex) {
            throw new FunctorException("InstantiateTransformer: The constructor must exist and be public ");
        } catch (InstantiationException ex) {
            throw new FunctorException("InstantiateTransformer: InstantiationException", ex);
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InstantiateTransformer: Constructor must be public", ex);
        } catch (InvocationTargetException ex) {
            throw new FunctorException("InstantiateTransformer: Constructor threw an exception", ex);
        }
    }

}
