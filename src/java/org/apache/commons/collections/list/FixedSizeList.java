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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;

/**
 * Decorates another <code>List</code> to fix the size preventing add/remove.
 * <p>
 * The add, remove, clear and retain operations are unsupported.
 * The set method is allowed (as it doesn't change the list size).
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class FixedSizeList<E>
        extends AbstractSerializableListDecorator<E>
        implements BoundedCollection<E> {

    /** Serialization version */
    private static final long serialVersionUID = -2218010673611160319L;

    /**
     * Factory method to create a fixed size list.
     * 
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    public static <E> List<E> decorate(List<E> list) {
        return new FixedSizeList<E>(list);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected FixedSizeList(List<E> list) {
        super(list);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean add(E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public void add(int index, E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public E get(int index) {
        return decorated().get(index);
    }

    @Override
    public int indexOf(Object object) {
        return decorated().indexOf(object);
    }

    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.decorate(decorated().iterator());
    }

    @Override
    public int lastIndexOf(Object object) {
        return decorated().lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new FixedSizeListIterator(decorated().listIterator(0));
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new FixedSizeListIterator(decorated().listIterator(index));
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public E set(int index, E object) {
        return decorated().set(index, object);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = decorated().subList(fromIndex, toIndex);
        return new FixedSizeList<E>(sub);
    }

    /**
     * List iterator that only permits changes via set()
     */
    private class FixedSizeListIterator extends AbstractListIteratorDecorator<E> {
        protected FixedSizeListIterator(ListIterator<E> iterator) {
            super(iterator);
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }
        @Override
        public void add(Object object) {
            throw new UnsupportedOperationException("List is fixed size");
        }
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

}
