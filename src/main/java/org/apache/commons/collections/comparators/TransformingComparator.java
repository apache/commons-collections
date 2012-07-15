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
package org.apache.commons.collections.comparators;

import java.util.Comparator;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.Transformer;

/**
 * Decorates another Comparator with transformation behavior. That is, the
 * return value from the transform operation will be passed to the decorated
 * {@link Comparator#compare(Object,Object) compare} method.
 *
 * @since 2.1
 * @version $Id$
 *
 * @see org.apache.commons.collections.Transformer
 * @see org.apache.commons.collections.comparators.ComparableComparator
 */
public class TransformingComparator<E> implements Comparator<E> {
    
    /** The decorated comparator. */
    protected final Comparator<E> decorated;
    /** The transformer being used. */    
    protected final Transformer<? super E, ? extends E> transformer;

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the given Transformer and a 
     * {@link ComparableComparator ComparableComparator}.
     * 
     * @param transformer what will transform the arguments to <code>compare</code>
     */
    @SuppressWarnings("unchecked")
    public TransformingComparator(Transformer<? super E, ? extends E> transformer) {
        this(transformer, ComparatorUtils.NATURAL_COMPARATOR);
    }

    /**
     * Constructs an instance with the given Transformer and Comparator.
     * 
     * @param transformer  what will transform the arguments to <code>compare</code>
     * @param decorated  the decorated Comparator
     */
    public TransformingComparator(Transformer<? super E, ? extends E> transformer, Comparator<E> decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the result of comparing the values from the transform operation.
     * 
     * @param obj1  the first object to transform then compare
     * @param obj2  the second object to transform then compare
     * @return negative if obj1 is less, positive if greater, zero if equal
     */
    public int compare(E obj1, E obj2) {
        E value1 = this.transformer.transform(obj1);
        E value2 = this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }

}

