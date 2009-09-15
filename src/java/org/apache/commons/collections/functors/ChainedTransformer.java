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
import java.util.Collection;

import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that chains the specified transformers together.
 * <p>
 * The input object is passed to the first transformer. The transformed result
 * is passed to the second transformer and so on.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class ChainedTransformer<T> implements Transformer<T, T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 3514945074733160196L;

    /** The transformers to call in turn */
    private final Transformer<? super T, ? extends T>[] iTransformers;

    /**
     * Factory method that performs validation and copies the parameter array.
     * 
     * @param transformers  the transformers to chain, copied, no nulls
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if the transformers array is null
     * @throws IllegalArgumentException if any transformer in the array is null
     */
    public static <T> Transformer<T, T> getInstance(Transformer<? super T, ? extends T>[] transformers) {
        FunctorUtils.validate(transformers);
        if (transformers.length == 0) {
            return NOPTransformer.<T>getInstance();
        }
        transformers = FunctorUtils.copy(transformers);
        return new ChainedTransformer<T>(transformers);
    }
    
    /**
     * Create a new Transformer that calls each transformer in turn, passing the 
     * result into the next transformer. The ordering is that of the iterator()
     * method on the collection.
     * 
     * @param transformers  a collection of transformers to chain
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if the transformers collection is null
     * @throws IllegalArgumentException if any transformer in the collection is null
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> getInstance(Collection<? extends Transformer<T, T>> transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("Transformer collection must not be null");
        }
        if (transformers.size() == 0) {
            return NOPTransformer.<T>getInstance();
        }
        // convert to array like this to guarantee iterator() ordering
        Transformer<T, T>[] cmds = transformers.toArray(new Transformer[transformers.size()]);
        FunctorUtils.validate(cmds);
        return new ChainedTransformer<T>(cmds);
    }

    /**
     * Factory method that performs validation.
     * 
     * @param transformer1  the first transformer, not null
     * @param transformer2  the second transformer, not null
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if either transformer is null
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> getInstance(Transformer<? super T, ? extends T> transformer1, Transformer<? super T, ? extends T> transformer2) {
        if (transformer1 == null || transformer2 == null) {
            throw new IllegalArgumentException("Transformers must not be null");
        }
        Transformer<? super T, ? extends T>[] transformers = new Transformer[] { transformer1, transformer2 };
        return new ChainedTransformer<T>(transformers);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param transformers  the transformers to chain, not copied, no nulls
     */
    public ChainedTransformer(Transformer<? super T, ? extends T>[] transformers) {
        super();
        iTransformers = transformers;
    }

    /**
     * Transforms the input to result via each decorated transformer
     * 
     * @param object  the input object passed to the first transformer
     * @return the transformed result
     */
    public T transform(T object) {
        for (int i = 0; i < iTransformers.length; i++) {
            object = iTransformers[i].transform(object);
        }
        return object;
    }

    /**
     * Gets the transformers, do not modify the array.
     * @return the transformers
     * @since Commons Collections 3.1
     */
    public Transformer<? super T, ? extends T>[] getTransformers() {
        return iTransformers;
    }

}
