/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that creates a new object instance by reflection.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/02/18 00:59:20 $
 *
 * @author Stephen Colebourne
 */
public class InvokerTransformer implements Transformer, Serializable {

    /** The serial version */
    static final long serialVersionUID = -8653385846894047688L;
    
    /** The method name to call */
    private final String iMethodName;
    /** The array of reflection parameter types */
    private final Class[] iParamTypes;
    /** The array of reflection arguments */
    private final Object[] iArgs;

    /**
     * Transformer method that performs validation.
     * 
     * @param paramTypes  the constructor parameter types
     * @param args  the constructor arguments
     */
    public static Transformer getInstance(String methodName, Class[] paramTypes, Object[] args) {
        if (methodName == null) {
            throw new IllegalArgumentException("The method to invoke must not be null");
        }
        if (((paramTypes == null) && (args != null))
            || ((paramTypes != null) && (args == null))
            || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("The parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InvokerTransformer(methodName);
        } else {
            paramTypes = (Class[]) paramTypes.clone();
            args = (Object[]) args.clone();
            return new InvokerTransformer(methodName, paramTypes, args);
        }
    }

    /**
     * Constructor for no arg instance.
     * 
     * @param methodName  the method to call
     */
    private InvokerTransformer(String methodName) {
        super();
        iMethodName = methodName;
        iParamTypes = null;
        iArgs = null;
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param methodName  the method to call
     * @param paramTypes  the constructor parameter types, not cloned
     * @param args  the constructor arguments, not cloned
     */
    public InvokerTransformer(String methodName, Class[] paramTypes, Object[] args) {
        super();
        iMethodName = methodName;
        iParamTypes = paramTypes;
        iArgs = args;
    }

    /**
     * Return the result of instantiating the input Class object.
     */
    public Object transform(Object input) {
        if (input == null) {
            return null;
        }
        try {
            Class cls = input.getClass();
            Method method = cls.getMethod(iMethodName, iParamTypes);
            return method.invoke(input, iArgs);
                
        } catch (NoSuchMethodException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' does not exist");
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' cannot be accessed");
        } catch (InvocationTargetException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' threw an exception", ex);
        }
    }
    
}
