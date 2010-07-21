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
package org.apache.commons.collections.bag;

import java.util.Set;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

/**
 * Decorates another <code>Bag</code> to provide additional behaviour.
 * <p>
 * Methods are forwarded directly to the decorated bag.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public abstract class AbstractBagDecorator<E>
        extends AbstractCollectionDecorator<E> implements Bag<E> {

    /** Serialization version */
    private static final long serialVersionUID = -3768146017343785417L;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     * @since Commons Collections 3.1
     */
    protected AbstractBagDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param bag  the bag to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected AbstractBagDecorator(Bag<E> bag) {
        super(bag);
    }

    /**
     * Gets the bag being decorated.
     * 
     * @return the decorated bag
     */
    @Override
    protected Bag<E> decorated() {
        return (Bag<E>) super.decorated();
    }

    //-----------------------------------------------------------------------
    public int getCount(Object object) {
        return decorated().getCount(object);
    }

    public boolean add(E object, int count) {
        return decorated().add(object, count);
    }

    public boolean remove(Object object, int count) {
        return decorated().remove(object, count);
    }

    public Set<E> uniqueSet() {
        return decorated().uniqueSet();
    }

}
