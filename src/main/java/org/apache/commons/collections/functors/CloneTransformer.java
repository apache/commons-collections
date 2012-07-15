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
 * Transformer implementation that returns a clone of the input object.
 * <p>
 * Clone is performed using <code>PrototypeFactory.getInstance(input).create()</code>.
 *
 * @since 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class CloneTransformer<T> implements Transformer<T, T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -8188742709499652567L;

    /** Singleton predicate instance */
    public static final Transformer<Object, Object> INSTANCE = new CloneTransformer<Object>();

    /**
     * Factory returning the singleton instance.
     * 
     * @return the singleton instance
     * @since 3.1
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> cloneTransformer() {
        return (Transformer<T, T>) INSTANCE;
    }

    /**
     * Constructor
     */
    private CloneTransformer() {
        super();
    }

    /**
     * Transforms the input to result by cloning it.
     * 
     * @param input  the input object to transform
     * @return the transformed result
     */
    public T transform(T input) {
        if (input == null) {
            return null;
        }
        return PrototypeFactory.prototypeFactory(input).create();
    }

    private Object readResolve() {
        return INSTANCE;
    }

}
