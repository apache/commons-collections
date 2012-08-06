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
package org.apache.commons.collections.list;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to validate that all additions
 * match a specified predicate.
 * <p>
 * This list exists to provide validation for the decorated list.
 * It is normally created to decorate an empty list.
 * If an object cannot be added to the list, an IllegalArgumentException is thrown.
 * <p>
 * One usage would be to ensure that no null entries are added to the list.
 * <pre>List list = PredicatedList.decorate(new ArrayList(), NotNullPredicate.INSTANCE);</pre>
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since 3.0
 * @version $Id$
 */
public class PredicatedList<E> extends PredicatedCollection<E> implements List<E> {

    /** Serialization version */
    private static final long serialVersionUID = -5722039223898659102L;

    /**
     * Factory method to create a predicated (validating) list.
     * <p>
     * If there are any elements already in the list being decorated, they
     * are validated.
     * 
     * @param <T> the type of the elements in the list
     * @param list  the list to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @return a new predicated list
     * @throws IllegalArgumentException if list or predicate is null
     * @throws IllegalArgumentException if the list contains invalid elements
     */
    public static <T> PredicatedList<T> predicatedList(List<T> list, Predicate<? super T> predicate) {
        return new PredicatedList<T>(list, predicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the list being decorated, they
     * are validated.
     * 
     * @param list  the list to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if list or predicate is null
     * @throws IllegalArgumentException if the list contains invalid elements
     */
    protected PredicatedList(List<E> list, Predicate<? super E> predicate) {
        super(list, predicate);
    }

    /**
     * Gets the list being decorated.
     * 
     * @return the decorated list
     */
    @Override
    protected List<E> decorated() {
        return (List<E>) super.decorated();
    }

    //-----------------------------------------------------------------------
    
    /** {@inheritDoc} */
    public E get(int index) {
        return decorated().get(index);
    }

    /** {@inheritDoc} */
    public int indexOf(Object object) {
        return decorated().indexOf(object);
    }

    /** {@inheritDoc} */
    public int lastIndexOf(Object object) {
        return decorated().lastIndexOf(object);
    }

    /** {@inheritDoc} */
    public E remove(int index) {
        return decorated().remove(index);
    }

    //-----------------------------------------------------------------------
    
    /** {@inheritDoc} */
    public void add(int index, E object) {
        validate(object);
        decorated().add(index, object);
    }

    /** {@inheritDoc} */
    public boolean addAll(int index, Collection<? extends E> coll) {
        for (E aColl : coll) {
            validate(aColl);
        }
        return decorated().addAll(index, coll);
    }

    /** {@inheritDoc} */
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    /** {@inheritDoc} */
    public ListIterator<E> listIterator(int i) {
        return new PredicatedListIterator(decorated().listIterator(i));
    }

    /** {@inheritDoc} */
    public E set(int index, E object) {
        validate(object);
        return decorated().set(index, object);
    }

    /** {@inheritDoc} */
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = decorated().subList(fromIndex, toIndex);
        return new PredicatedList<E>(sub, predicate);
    }

    /**
     * Inner class Iterator for the PredicatedList
     */
    protected class PredicatedListIterator extends AbstractListIteratorDecorator<E> {
        
        /**
         * Create a new predicated list iterator.
         * 
         * @param iterator  the list iterator to decorate
         */
        protected PredicatedListIterator(ListIterator<E> iterator) {
            super(iterator);
        }
        
        @Override
        public void add(E object) {
            validate(object);
            iterator.add(object);
        }
        
        @Override
        public void set(E object) {
            validate(object);
            iterator.set(object);
        }
    }

}
