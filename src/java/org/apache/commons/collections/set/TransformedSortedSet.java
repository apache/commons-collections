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
package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.SortedSet;

import org.apache.commons.collections.Transformer;

/**
 * Decorates another <code>SortedSet</code> to transform objects that are added.
 * <p>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * 
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 * 
 * @author Stephen Colebourne
 */
public class TransformedSortedSet extends TransformedSet implements SortedSet {

    /** Serialization version */
    private static final long serialVersionUID = -1675486811351124386L;

    /**
     * Factory method to create a transforming sorted set.
     * <p>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     * Constrast this with {@link #decorateTransform}.
     * 
     * @param set  the set to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    public static SortedSet decorate(SortedSet set, Transformer transformer) {
        return new TransformedSortedSet(set, transformer);
    }
    
    /**
     * Factory method to create a transforming sorted set that will transform
     * existing contents of the specified sorted set.
     * <p>
     * If there are any elements already in the set being decorated, they
     * will be transformed by this method.
     * Constrast this with {@link #decorate}.
     * 
     * @param set  the set to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed SortedSet
     * @throws IllegalArgumentException if set or transformer is null
     * @since Commons Collections 3.3
     */
    public static SortedSet decorateTransform(SortedSet set, Transformer transformer) {
        TransformedSortedSet decorated = new TransformedSortedSet(set, transformer);
        if (transformer != null && set != null && set.size() > 0) {
            Object[] values = set.toArray();
            set.clear();
            for(int i=0; i<values.length; i++) {
                decorated.getCollection().add(transformer.transform(values[i]));
            }
        }
        return decorated;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     * 
     * @param set  the set to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    protected TransformedSortedSet(SortedSet set, Transformer transformer) {
        super(set, transformer);
    }

    /**
     * Gets the decorated set.
     * 
     * @return the decorated set
     */
    protected SortedSet getSortedSet() {
        return (SortedSet) collection;
    }

    //-----------------------------------------------------------------------
    public Object first() {
        return getSortedSet().first();
    }

    public Object last() {
        return getSortedSet().last();
    }

    public Comparator comparator() {
        return getSortedSet().comparator();
    }

    //-----------------------------------------------------------------------
    public SortedSet subSet(Object fromElement, Object toElement) {
        SortedSet set = getSortedSet().subSet(fromElement, toElement);
        return new TransformedSortedSet(set, transformer);
    }

    public SortedSet headSet(Object toElement) {
        SortedSet set = getSortedSet().headSet(toElement);
        return new TransformedSortedSet(set, transformer);
    }

    public SortedSet tailSet(Object fromElement) {
        SortedSet set = getSortedSet().tailSet(fromElement);
        return new TransformedSortedSet(set, transformer);
    }

}
