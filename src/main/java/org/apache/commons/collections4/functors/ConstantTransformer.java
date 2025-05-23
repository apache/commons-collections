/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.collections4.Transformer;

/**
 * Transformer implementation that returns the same constant each time.
 * <p>
 * No check is made that the object is immutable. In general, only immutable
 * objects should use the constant factory. Mutable objects should
 * use the prototype factory.
 * </p>
 *
 * @param <T> the type of the input to the function.
 * @param <R> the type of the result of the function.
 * @since 3.0
 */
public class ConstantTransformer<T, R> implements Transformer<T, R>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 6374440726369055124L;

    /** Returns null each time */
    @SuppressWarnings("rawtypes")
    public static final Transformer NULL_INSTANCE = new ConstantTransformer<>(null);

    /**
     * Transformer method that performs validation.
     *
     * @param <I>  the input type
     * @param <O>  the output type
     * @param constantToReturn  the constant object to return each time in the factory
     * @return the {@code constant} factory.
     */
    public static <I, O> Transformer<I, O> constantTransformer(final O constantToReturn) {
        if (constantToReturn == null) {
            return nullTransformer();
        }
        return new ConstantTransformer<>(constantToReturn);
    }

    /**
     * Gets a typed null instance.
     *
     * @param <I>  the input type
     * @param <O>  the output type
     * @return Transformer&lt;I, O&gt; that always returns null.
     */
    public static <I, O> Transformer<I, O> nullTransformer() {
        return NULL_INSTANCE;
    }

    /** The closures to call in turn */
    private final R iConstant;

    /**
     * Constructor that performs no validation.
     * Use {@code constantTransformer} if you want that.
     *
     * @param constantToReturn  the constant to return each time
     */
    public ConstantTransformer(final R constantToReturn) {
        iConstant = constantToReturn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConstantTransformer)) {
            return false;
        }
        final Object otherConstant = ((ConstantTransformer<?, ?>) obj).getConstant();
        return Objects.equals(otherConstant, getConstant());
    }

    /**
     * Gets the constant.
     *
     * @return the constant
     * @since 3.1
     */
    public R getConstant() {
        return iConstant;
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

    /**
     * Transforms the input by ignoring it and returning the stored constant instead.
     *
     * @param input  the input object which is ignored
     * @return the stored constant
     */
    @Override
    public R transform(final T input) {
        return iConstant;
    }
}
