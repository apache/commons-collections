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
package org.apache.commons.collections4;

import java.util.function.Supplier;

/**
 * Defines a functor interface implemented by classes that create objects.
 * <p>
 * A {@code Factory} creates an object without using an input parameter.
 * If an input parameter is required, then {@link Transformer} is more appropriate.
 * </p>
 * <p>
 * Standard implementations of common factories are provided by
 * {@link FactoryUtils}. These include factories that return a constant,
 * a copy of a prototype or a new instance.
 * </p>
 *
 * @param <T> the type of results supplied by this supplier.
 * @since 2.1
 * This will be deprecated in 5.0 in favor of {@link Supplier}.
 */
//@Deprecated
public interface Factory<T> extends Supplier<T> {

    /**
     * Create a new object.
     *
     * @return a new object
     * @throws FunctorException (runtime) if the factory cannot create an object
     */
    T create();

    @Override
    default T get() {
        return create();
    }

}
