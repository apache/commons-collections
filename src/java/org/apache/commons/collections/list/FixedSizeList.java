/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/list/FixedSizeList.java,v 1.4 2003/12/25 01:25:06 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/12/25 01:25:06 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class FixedSizeList extends AbstractListDecorator implements BoundedCollection {

    /**
     * Factory method to create a fixed size list.
     * 
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    public static List decorate(List list) {
        return new FixedSizeList(list);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected FixedSizeList(List list) {
        super(list);
    }

    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void add(int index, Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean addAll(Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean addAll(int index, Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public Object get(int index) {
        return getList().get(index);
    }

    public int indexOf(Object object) {
        return getList().indexOf(object);
    }

    public Iterator iterator() {
        return UnmodifiableIterator.decorate(getCollection().iterator());
    }

    public int lastIndexOf(Object object) {
        return getList().lastIndexOf(object);
    }

    public ListIterator listIterator() {
        return new FixedSizeListIterator(getList().listIterator(0));
    }

    public ListIterator listIterator(int index) {
        return new FixedSizeListIterator(getList().listIterator(index));
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public Object set(int index, Object object) {
        return getList().set(index, object);
    }

    public List subList(int fromIndex, int toIndex) {
        List sub = getList().subList(fromIndex, toIndex);
        return new FixedSizeList(sub);
    }

    /**
     * List iterator that only permits changes via set()
     */
    static class FixedSizeListIterator extends AbstractListIteratorDecorator {
        protected FixedSizeListIterator(ListIterator iterator) {
            super(iterator);
        }
        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }
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
