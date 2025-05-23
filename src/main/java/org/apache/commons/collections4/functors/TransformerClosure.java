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

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Transformer;

/**
 * Closure implementation that calls a Transformer using the input object
 * and ignore the result.
 *
 * @param <T> the type of the input to the operation.
 * @since 3.0
 */
public class TransformerClosure<T> implements Closure<T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -5194992589193388969L;

    /**
     * Factory method that performs validation.
     * <p>
     * A null transformer will return the {@code NOPClosure}.
     *
     * @param <E> the type that the closure acts on
     * @param transformer  the transformer to call, null means nop
     * @return the {@code transformer} closure
     */
    public static <E> Closure<E> transformerClosure(final Transformer<? super E, ?> transformer) {
        if (transformer == null) {
            return NOPClosure.<E>nopClosure();
        }
        return new TransformerClosure<>(transformer);
    }

    /** The transformer to wrap */
    private final Transformer<? super T, ?> iTransformer;

    /**
     * Constructor that performs no validation.
     * Use {@code transformerClosure} if you want that.
     *
     * @param transformer  the transformer to call, not null
     */
    public TransformerClosure(final Transformer<? super T, ?> transformer) {
        iTransformer = transformer;
    }

    /**
     * Executes the closure by calling the decorated transformer.
     *
     * @param input  the input object
     */
    @Override
    public void execute(final T input) {
        iTransformer.apply(input);
    }

    /**
     * Gets the transformer.
     *
     * @return the transformer
     * @since 3.1
     */
    public Transformer<? super T, ?> getTransformer() {
        return iTransformer;
    }

}
