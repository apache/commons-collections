/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/SetList.java,v 1.1 2003/10/02 22:34:44 matth Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.decorators;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * SetList combines the <code>List</code> and <code>Set</code> interfaces
 * in one implementation.
 * <p>
 * The <code>List</code> interface makes certain assumptions/requirements.
 * This implementation breaks these in certain ways, but this is merely the
 * result of rejecting duplicates.
 * Each violation is explained in the method, but it should not affect you.
 * <p>
 * The {@link org.apache.commons.collections.decorators.OrderedSet OrderedSet}
 * class provides an alternative approach, by wrapping an existing Set and
 * retaining insertion order in the iterator. This class offers the <code>List</code>
 * interface implementation as well.
 * <p>
 * If the <code>Set</code> aspects are important to you (fast add/contains/remove)
 * then <code>OrderedSet</code> is a better choice.
 * This implementation is based solely on <code>ArrayList</code>, and so has
 * slow add/contains/remove operations for all except small lists.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/10/02 22:34:44 $
 * 
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public class SetList extends AbstractList {

    /**
     * Delegate list.
     */
    private final List delegate = new ArrayList();

    /**
     * Helps to maintain uniqueness.
     */
    private final Set set = new HashSet();

    /**
     * Factory method to create a SetList.
     * @param list the list to decorate
     */
    public static SetList decorate(List list) {
        return new SetList(list);
    }

    /**
     * Contructs an new list copying the specified elements.
     * @param coll  a collection to copy
     */
    protected SetList(List list) {
        addAll(list);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds an element to the list if it is not already present.
     * <p>
     * <i>(Violation)</i>
     * The <code>List</code> interface requires that this method returns
     * <code>true</code> always. However this class may return <code>false</code>
     * because of the <code>Set</code> behaviour.
     * 
     * @param object the object to add
     * @return true if object was added
     */
    public boolean add(Object object) {
        // gets initial size
        final int sizeBefore = size();

        // adds element if unique
        add(size(), object);

        // compares sizes to detect if collection changed
        return (sizeBefore != size());
    }

    /**
     * Adds an element to a specific index in the list if it is not already present.
     * <p>
     * <i>(Violation)</i>
     * The <code>List</code> interface makes the assumption that the element is
     * always inserted. This may not happen with this implementation.
     * 
     * @param index  the index to insert at
     * @param object  the object to add
     */
    public void add(int index, Object object) {
        // Adds element if it is not contained already
        if (!set.contains(object)) {
            delegate.add(index, object);
            set.add(object);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a collection of objects to the end of the list avoiding duplicates.
     * <p>
     * Only elements that are not already in this list will be added, and
     * duplicates from the specified collection will be ignored.
     * <p>
     * <i>(Violation)</i>
     * The <code>List</code> interface makes the assumption that the elements
     * are always inserted. This may not happen with this implementation.
     * 
     * @param index  the index to insert at
     * @param coll  the collection to add in iterator order
     * @return true if this collection changed
     */
    public boolean addAll(int index, Collection coll) {
        // gets initial size
        final int sizeBefore = size();

        // adds all elements
        for (final Iterator it = coll.iterator(); it.hasNext();) {
            add(it.next());
        }

        // compares sizes to detect if collection changed
        return sizeBefore != size();
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the value at the specified index avoiding duplicates.
     * <p>
     * The object is set into the specified index.
     * Afterwards, any previous duplicate is removed
     * If the object is not already in the list then a normal set occurs.
     * If it is present, then the old version is removed and re-added at this index
     * 
     * @param index  the index to insert at
     * @param object  the object to set
     * @return the previous object
     */
    public Object set(int index, Object object) {
        int pos = indexOf(object);
        Object result = delegate.set(index, object);
        if (pos == -1 || pos == index) {
            return result;
        }
        return remove(pos);
    }

    public Object get(int index) {
        return delegate.get(index);
    }

    public int size() {
        return delegate.size();
    }

    public Object remove(int index) {
        return delegate.remove(index);
    }

    public void clear() {
        delegate.clear();
        set.clear();
    }

}
