/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/ListIntList.java,v 1.5 2003/02/26 19:17:23 rwaldhoff Exp $
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
import java.util.List;

import org.apache.commons.collections.primitives.IntCollection;
import org.apache.commons.collections.primitives.IntIterator;
import org.apache.commons.collections.primitives.IntList;
import org.apache.commons.collections.primitives.IntListIterator;

/**
 * Adapts a {@link Number}-valued {@link List List} 
 * to the {@link IntList IntList} interface.
 * <p />
 * This implementation delegates most methods
 * to the provided {@link List List} 
 * implementation in the "obvious" way.
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.5 $ $Date: 2003/02/26 19:17:23 $
 * @author Rodney Waldhoff 
 */
public class ListIntList extends CollectionIntCollection implements IntList, Serializable {
    
    /**
     * Create an {@link IntList IntList} wrapping
     * the specified {@link List List}.  When
     * the given <i>list</i> is <code>null</code>,
     * returns <code>null</code>.
     * 
     * @param list the (possibly <code>null</code>) 
     *        {@link List List} to wrap
     * @return a {@link IntList IntList} wrapping the given 
     *         <i>list</i>, or <code>null</code> when <i>list</i> is
     *         <code>null</code>.
     */
    public static IntList wrap(List list) {
        return null == list ? null : new ListIntList(list);
    }

    /**
     * No-arg constructor, for serialization purposes.
     */
    protected ListIntList() {
    }

    /**
     * Creates an {@link IntList IntList} wrapping
     * the specified {@link List List}.
     * @see #wrap
     */
    public ListIntList(List list) {
        super(list);        
        _list = list;
    }
    
    public void add(int index, int element) {
        _list.add(index,new Integer(element));
    }

    public boolean addAll(int index, IntCollection collection) {
        return _list.addAll(index,IntCollectionCollection.wrap(collection));
    }

    public int get(int index) {
        return ((Number)_list.get(index)).intValue();
    }

    public int indexOf(int element) {
        return _list.indexOf(new Integer(element));
    }

    public int lastIndexOf(int element) {
        return _list.lastIndexOf(new Integer(element));
    }

    /**
     * {@link ListIteratorIntListIterator#wrap wraps} the 
     * {@link IntList IntList} 
     * returned by my underlying 
     * {@link IntListIterator IntListIterator},
     * if any.
     */
    public IntListIterator listIterator() {
        return ListIteratorIntListIterator.wrap(_list.listIterator());
    }

    /**
     * {@link ListIteratorIntListIterator#wrap wraps} the 
     * {@link IntList IntList} 
     * returned by my underlying 
     * {@link IntListIterator IntListIterator},
     * if any.
     */
    public IntListIterator listIterator(int index) {
        return ListIteratorIntListIterator.wrap(_list.listIterator(index));
    }

    public int removeElementAt(int index) {
        return ((Number)_list.remove(index)).intValue();
    }

    public int set(int index, int element) {
        return ((Number)_list.set(index,new Integer(element))).intValue();
    }

    public IntList subList(int fromIndex, int toIndex) {
        return ListIntList.wrap(_list.subList(fromIndex,toIndex));
    }

    public boolean equals(Object obj) {
        if(obj instanceof IntList) {
            IntList that = (IntList)obj;
            if(this == that) {
                return true;
            } else if(this.size() != that.size()) {
                return false;            
            } else {
                IntIterator thisiter = iterator();
                IntIterator thatiter = that.iterator();
                while(thisiter.hasNext()) {
                    if(thisiter.next() != thatiter.next()) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }
        
    private List _list = null;

}
