/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/IntListList.java,v 1.4 2003/02/26 19:17:23 rwaldhoff Exp $
 * ====================================================================
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

package org.apache.commons.collections.primitives.adapters;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.primitives.IntList;

/**
 * Adapts an {@link IntList IntList} to the
 * {@link List List} interface.
 * <p />
 * This implementation delegates most methods
 * to the provided {@link IntList IntList} 
 * implementation in the "obvious" way.
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.4 $ $Date: 2003/02/26 19:17:23 $
 * @author Rodney Waldhoff 
 */
public class IntListList extends IntCollectionCollection implements List, Serializable {
    
    /**
     * Create a {@link List List} wrapping
     * the specified {@link IntList IntList}.  When
     * the given <i>list</i> is <code>null</code>,
     * returns <code>null</code>.
     * 
     * @param list the (possibly <code>null</code>) 
     *        {@link IntList IntList} to wrap
     * @return a {@link List List} wrapping the given 
     *         <i>list</i>, or <code>null</code> when <i>list</i> is
     *         <code>null</code>.
     */
    public static List wrap(IntList list) {
        return null == list ? null : new IntListList(list);
    }

    /**
     * No-arg constructor, for serialization purposes.
     */
    protected IntListList() {
    }
    
    /**
     * Creates a {@link List List} wrapping
     * the specified {@link IntList IntList}.
     * @see #wrap
     */
    public IntListList(IntList list) {
        super(list);        
        _list = list;
    }
    
    public void add(int index, Object element) {
        _list.add(index,((Number)element).intValue());
    }

    public boolean addAll(int index, Collection c) {
        return _list.addAll(index,CollectionIntCollection.wrap(c));
    }

    public Object get(int index) {
        return new Integer(_list.get(index));
    }

    public int indexOf(Object element) {
        return _list.indexOf(((Number)element).intValue());
    }

    public int lastIndexOf(Object element) {
        return _list.lastIndexOf(((Number)element).intValue());
    }

    /**
     * {@link IntListIteratorListIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.IntListIterator IntListIterator}
     * returned by my underlying 
     * {@link IntList IntList}, 
     * if any.
     */
    public ListIterator listIterator() {
        return IntListIteratorListIterator.wrap(_list.listIterator());
    }

    /**
     * {@link IntListIteratorListIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.IntListIterator IntListIterator}
     * returned by my underlying 
     * {@link IntList IntList}, 
     * if any.
     */
    public ListIterator listIterator(int index) {
        return IntListIteratorListIterator.wrap(_list.listIterator(index));
    }

    public Object remove(int index) {
        return new Integer(_list.removeElementAt(index));
    }

    public Object set(int index, Object element) {
        return new Integer(_list.set(index, ((Number)element).intValue() ));
    }

    public List subList(int fromIndex, int toIndex) {
        return IntListList.wrap(_list.subList(fromIndex,toIndex));
    }

    public boolean equals(Object obj) {
        if(obj instanceof List) {
            List that = (List)obj;
            if(this == that) {
                return true;
            } else if(this.size() != that.size()) {
                return false;            
            } else {
                Iterator thisiter = iterator();
                Iterator thatiter = that.iterator();
                while(thisiter.hasNext()) {
                    Object thiselt = thisiter.next();
                    Object thatelt = thatiter.next();
                    if(null == thiselt ? null != thatelt : !(thiselt.equals(thatelt))) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }
    
    private IntList _list = null;

}
