/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/ListUtils.java,v 1.6 2002/08/13 01:19:00 pjack Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/13 01:19:00 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains static utility methods and decorators for {@link List} 
 * instances.
 *
 * @since 1.0
 * @author  <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author  <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author Paul Jack
 */
public class ListUtils
{
    public static List intersection( final List list1, final List list2 )
    {
        final ArrayList result = new ArrayList();
        final Iterator iterator = list2.iterator();

        while( iterator.hasNext() )
        {
            final Object o = iterator.next();

            if ( list1.contains( o ) )
            {
                result.add( o );
            }
        }

        return result;
    }

    public static List subtract( final List list1, final List list2 )
    {
        final ArrayList result = new ArrayList( list1 );
        final Iterator iterator = list2.iterator();

        while( iterator.hasNext() )
        {
            result.remove( iterator.next() );
        }

        return result;
    }

    public static List sum( final List list1, final List list2 )
    {
        return subtract( union( list1, list2 ),
                         intersection( list1, list2 ) );
    }

    public static List union( final List list1, final List list2 )
    {
        final ArrayList result = new ArrayList( list1 );
        result.addAll( list2 );
        return result;
    }


    static class ListIteratorWrapper implements ListIterator {

        final protected ListIterator iterator;

        public ListIteratorWrapper(ListIterator iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return iterator.next();
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public Object previous() {
            return iterator.previous();
        }

        public int nextIndex() {
            return iterator.nextIndex();
        }

        public int previousIndex() {
            return iterator.previousIndex();
        }

        public void remove() {
            iterator.remove();
        }

        public void set(Object o) {
            iterator.set(o);
        }

        public void add(Object o) {
            iterator.add(o);
        }

    }


    static class PredicatedList extends CollectionUtils.PredicatedCollection
    implements List {

        public PredicatedList(List list, Predicate p) {
            super(list, p);
        }

        public boolean addAll(int i, Collection c) {
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                validate(iter.next());
            }
            return getList().addAll(i, c);
        }

        public Object get(int i) {
            return getList().get(i);
        }

        public Object set(int i, Object o) {
            validate(o);
            return getList().set(i, o);
        }

        public void add(int i, Object o) {
            validate(o);
            getList().add(i, o);
        }

        public Object remove(int i) {
            return getList().remove(i);
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return listIterator(0);
        }

        public ListIterator listIterator(int i) {
            return new ListIteratorWrapper(getList().listIterator(i)) {
                public void add(Object o) {
                    validate(o);
                    iterator.add(o);
                }

                public void set(Object o) {
                    validate(o);
                    iterator.set(o);
                }
            };
        }

        public List subList(int i1, int i2) {
            List sub = getList().subList(i1, i2);
            return new PredicatedList(sub, predicate);
        }

        private List getList() {
            return (List)collection;
        }

    }


    static class FixedSizeList extends CollectionUtils.UnmodifiableCollection
    implements List {

        public FixedSizeList(List list) {
            super(list);
        }

        public boolean addAll(int i, Collection c) {
            throw new UnsupportedOperationException();
        }

        public Object get(int i) {
            return getList().get(i);
        }

        public Object set(int i, Object o) {
            return getList().set(i, o);
        }

        public void add(int i, Object o) {
            throw new UnsupportedOperationException();
        }

        public Object remove(int i) {
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return listIterator(0);
        }

        public ListIterator listIterator(int i) {
            return new ListIteratorWrapper(getList().listIterator(i)) {
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException();
                }

                public void remove(Object o) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public List subList(int i1, int i2) {
            List sub = getList().subList(i1, i2);
            return new FixedSizeList(sub);
        }

        private List getList() {
            return (List)collection;
        }

    }


    static class BoundedList extends CollectionUtils.CollectionWrapper 
    implements List {

        final protected int maxSize;

        public BoundedList(List list, int maxSize) {
            super(list);
            this.maxSize = maxSize;
        }

        public boolean addAll(Collection c) {
            validate(c.size());
            return collection.addAll(c);
        }

        public boolean add(Object o) {
            validate(1);
            return collection.add(o);
        }

        public boolean addAll(int i, Collection c) {
            validate(c.size());
            return getList().addAll(i, c);
        }

        public void add(int i, Object o) {
            validate(1);
            getList().add(i, o);
        }

        public Object get(int i) {
            return getList().get(i);
        }

        public Object set(int i, Object o) {
            return getList().set(i, o);
        }

        public Object remove(int i) {
            return getList().remove(i);
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return listIterator(0);
        }

        public ListIterator listIterator(int i) {
            return new ListIteratorWrapper(getList().listIterator(i)) {
                public void add(Object o) {
                    validate(1);
                    iterator.add(o);
                }
            };
        }

        public List subList(int i1, int i2) {
            return getList().subList(i1, i2);
        }

        private List getList() {
            return (List)collection;
        }

        private void validate(int delta) {
            if (delta + size() > maxSize) {
                throw new IllegalStateException("Maximum size reached.");
            }
        }

    }


    static class LazyList extends CollectionUtils.CollectionWrapper 
    implements List {

        final protected Factory factory;

        public LazyList(List list, Factory factory) {
            super(list);
            this.factory = factory;
        }

        
        /* Proxy method to the impl's get method. With the exception that if it's out
         * of bounds, then the collection will grow, leaving place-holders in its
         * wake, so that an item can be set at any given index. Later the
         * place-holders are removed to return to a pure collection.
         *
         * If there's a place-holder at the index, then it's replaced with a proper
         * object to be used.
         */
        public Object get(int index) {
            Object obj;
            if (index < (getList().size())) {
            /* within bounds, get the object */
                obj = getList().get(index);
                if (obj == null) {
                    /* item is a place holder, create new one, set and return */
                    obj = this.factory.createObject();
                    this.getList().set(index, obj);
                    return obj;
                } else {
                    /* good and ready to go */
                    return obj;
                }
            } else {
                /* we have to grow the list */
                for (int i = getList().size(); i < index; i++) {
                    getList().add(null);
                }
                /* create our last object, set and return */
                obj = this.factory.createObject();
                getList().add(obj);
                return obj;
            }
        }


        /* proxy the call to the provided list implementation. */
        public List subList(int fromIndex, int toIndex) {
            /* wrap the returned sublist so it can continue the functionality */
            return new LazyList(getList().subList(fromIndex, toIndex), factory);
        }

        public boolean addAll(int i, Collection c) {
            return getList().addAll(i, c);
        }

        public Object set(int i, Object o) {
            return getList().set(i, o);
        }

        public void add(int i, Object o) {
            getList().add(i, o);
        }

        public Object remove(int i) {
            return getList().remove(i);
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return getList().listIterator();
        }

        public ListIterator listIterator(int i) {
            return getList().listIterator(i);
        }

        private List getList() {
            return (List)collection;
        }

    }


    /**
     *  Returns a predicated list backed by the given list.  Only objects
     *  that pass the test in the given predicate can be added to the list.
     *  It is important not to use the original list after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param list  the list to predicate
     *  @param p  the predicate for the list
     *  @return  a predicated list backed by the given list
     */
    public static List predicatedList(List list, Predicate p) {
        return new PredicatedList(list, p);
    }


    /**
     *  Returns a "lazy" list whose elements will be created on demand.<P>
     *  <P>
     *  When the index passed to the returned list's {@link List#get(int) get}
     *  method is greater than the list's size, then the factory will be used
     *  to create a new object and that object will be inserted at that index.
     *  <P>
     *  For instance:
     *
     *  <Pre>
     *  Factory factory = new Factory() {
     *      public Object createObject() {
     *          return new Date();
     *      }
     *  }
     *  List lazy = ListUtils.lazyList(new ArrayList(), factory);
     *  Object obj = lazy.get(3);
     *  </Pre>
     *
     *  After the above code is executed, <Code>obj</Code> will contain
     *  a new <Code>Date</Code> instance.  Furthermore, that <Code>Date</Code>
     *  instance is the fourth element in the list.  The first, second, 
     *  and third element are all set to <Code>null</Code>.<P>
     *
     *  @param list  the list to make lazy
     *  @param factory  the factory for creating new objects
     *  @return a lazy list backed by the given list
     */
    public static List lazyList(List list, Factory factory) {
        return new LazyList(list, factory);
    }


    /**
     *  Returns a fixed-sized list backed by the given list.
     *  Elements may not be added or removed from the returned list, but 
     *  existing elements can be changed (for instance, via the 
     *  {@link List#set(int,Object)} method).
     *
     *  @param list  the list whose size to fix
     *  @return  a fixed-size list backed by that list
     */
    public static List fixedSizeList(List list) {
        return new FixedSizeList(list);
    }


    /**
     *  Returns a bounded list backed by the given list.
     *  New elements may only be added to the returned list if its 
     *  size is less than the specified maximum; otherwise, an
     *  {@link IllegalStateException} will be thrown.
     *
     *  @param list  the list whose size to bind
     *  @param maxSize  the maximum size of the returned list
     *  @return  a bounded list 
     */
    public static List boundedList(List list, int maxSize) {
        return new BoundedList(list, maxSize);
    }


}
