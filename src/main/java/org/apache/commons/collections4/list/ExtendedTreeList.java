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
package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * This class extends {@link TreeList}. It provides some methods that are needed
 * in benchmarking the actual {@code TreeList} against the 
 * {@link IndexedLinkedList}.
 */
public class ExtendedTreeList<E> extends TreeList<E> {

    public boolean add(E element) {
        super.add(super.size(), element);
        return true;
    }
    
    public void addFirst(E element) {
        super.add(0, element);
    }
    
    public boolean addAll(int index, Collection<? extends E> coll) {
        if (coll.isEmpty()) {
            return false;
        }
        
        if (index == super.size()) {
            super.addAll(coll);
        } else {
            super.addAll(index, coll);
        }
        
        return true;
    }
    
    public E removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("removeFirst on empty list");
        }
        
        return remove(0);
    }
    
    public E removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("removeFirst on empty list");
        }
        
        return remove(size() - 1);
    }
    
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, 
                          toIndex,
                          size());
        
        return new EnhancedSubList(this,
                                   fromIndex, 
                                   toIndex);
    }
    
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        
        List<E> other = (List<E>) object;
        
        if (size() != other.size()) {
            return false;
        }
        
        Iterator<E> iter1 = iterator();
        Iterator<E> iter2 = other.iterator();
        
        while (iter1.hasNext() && iter2.hasNext()) {
            if (!Objects.equals(iter1.next(), iter2.next())) {
                return false;
            }
        }
        
        if (iter1.hasNext() || iter2.hasNext()) {
            throw new IllegalStateException("Problems with iteration");
        }
        
        return true;
    }
    
    final class EnhancedSubList implements List<E> {
        
        /**
         * The root list.
         */
        private final List<E> root;
        
        /**
         * The parent view. This view cannot be wider than its parent view.
         */
        private final EnhancedSubList parent;
        
        /**
         * The offset with regard to the parent view or the root list.
         */
        private final int offset;
        
        /**
         * The length of this view.
         */
        private int size;
        
        /**
         * The modification count.
         */
        private int modCount;
        
        EnhancedSubList(List<E> root, 
                        int fromIndex, 
                        int toIndex) {
            
            this.root     = root;
            this.parent   = null;
            this.offset   = fromIndex;
            this.size     = toIndex - fromIndex;
        }

        @Override
        public void clear() {
            for (int i = 0; i < size; ++i) {
                parent.remove(offset);
            }
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Iterator<E> iterator() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public E get(int index) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public ListIterator<E> listIterator() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
    
    static void subListRangeCheck(int fromIndex, 
                                  int toIndex, 
                                  int size) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }

        if (toIndex > size){
            throw new IndexOutOfBoundsException(
                    "toIndex(" + toIndex + ") > size(" + size + ")");
        }

        if (fromIndex > toIndex)
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndex(" 
                            + toIndex + ")");
    }
}
