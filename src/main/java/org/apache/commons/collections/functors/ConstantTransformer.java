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

import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that returns the same constant each time.
 * <p>
 * No check is made that the object is immutable. In general, only immutable
 * objects should use the constant factory. Mutable objects should
 * use the prototype factory.
 *
 * @since 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class ConstantTransformer<I, O> implements Transformer<I, O>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 6374440726369055124L;
    
    /** Returns null each time */
    public static final Transformer<Object, Object> NULL_INSTANCE = new ConstantTransformer<Object, Object>(null);

    /** The closures to call in turn */
    private final O iConstant;

    /**
     * Get a typed null instance.
     * @param <I>
     * @param <O>
     * @return Transformer<I, O> that always returns null.
     */
    @SuppressWarnings("unchecked")
    public static <I, O> Transformer<I, O> nullTransformer() {
        return (Transformer<I, O>) NULL_INSTANCE;
    }

    /**
     * Transformer method that performs validation.
     *
     * @param constantToReturn  the constant object to return each time in the factory
     * @return the <code>constant</code> factory.
     */
    public static <I, O> Transformer<I, O> constantTransformer(O constantToReturn) {
        if (constantToReturn == null) {
            return nullTransformer();
        }
        return new ConstantTransformer<I, O>(constantToReturn);
    }
    
    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param constantToReturn  the constant to return each time
     */
    public ConstantTransformer(O constantToReturn) {
        super();
        iConstant = constantToReturn;
    }

    /**
     * Transforms the input by ignoring it and returning the stored constant instead.
     * 
     * @param input  the input object which is ignored
     * @return the stored constant
     */
    public O transform(I input) {
        return iConstant;
    }

    /**
     * Gets the constant.
     * 
     * @return the constant
     * @since 3.1
     */
    public O getConstant() {
        return iConstant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ConstantTransformer == false) {
            return false;
        }
        Object otherConstant = ((ConstantTransformer<?, ?>) obj).getConstant();
        return otherConstant == getConstant() || otherConstant != null && otherConstant.equals(getConstant());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = "ConstantTransformer".hashCode() << 2;
        if (getConstant() != null) {
            result |= getConstant().hashCode();
        }
        return result;
    }
}
