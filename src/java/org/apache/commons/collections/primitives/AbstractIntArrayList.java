/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/AbstractIntArrayList.java,v 1.2 2002/08/13 19:41:36 pjack Exp $
 * $Revision: 1.2 $
 * $Date: 2002/08/13 19:41:36 $
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

package org.apache.commons.collections.primitives;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Abstract base class for lists backed by an <Code>int</Code> array.
 *
 * @version $Revision: 1.2 $ $Date: 2002/08/13 19:41:36 $
 * @author Rodney Waldhoff 
 */
public abstract class AbstractIntArrayList extends AbstractList implements List, Serializable {

    //------------------------------------------------------ Abstract Accessors

    /**
     *  Returns the maximum size the list can reach before the array 
     *  is resized.
     *
     *  @return the maximum size the list can reach before the array is resized
     */
    abstract public int capacity();


    /**
     *  Returns the number of <Code>int</Code> elements currently in this
     *  list.
     *
     *  @return the size of this list
     */
    abstract public int size();


    /**
     *  Returns the <Code>int</Code> element at the specified index in this
     *  array.
     *
     *  @param index  the index of the element to return
     *  @return  the <Code>int</Code> element at that index
     *  @throws  IndexOutOfBoundsException  if the index is negative or
     *    greater than or equal to {@link #size()}
     */
    abstract public int getInt(int index);


    /**
     *  Returns <Code>true</Code> if this list contains the given 
     *  <Code>int</Code> element.
     *
     *  @param value  the element to search for
     *  @return true if this list contains the given value, false otherwise
     */
    abstract public boolean containsInt(int value);


    /**
     *  Returns the first index of the given <Code>int</Code> element, or
     *  -1 if the value is not in this list.
     *
     *  @param value  the element to search for
     *  @return  the first index of that element, or -1 if the element is
     *    not in this list
     */
    abstract public int indexOfInt(int value);


    /**
     *  Returns the last index of the given <Code>int</Code> element, or
     *  -1 if the value is not in this list.
     *
     *  @param value  the element to search for
     *  @return  the last index of that element, or -1 if the element is
     *    not in this list
     */
    abstract public int lastIndexOfInt(int value);

    //--------------------------------------------------------------- Accessors
    
    /** 
     *  Returns <code>new Integer({@link #getInt getInt(index)})</code>. 
     *
     *  @param index  the index of the element to return
     *  @return  an {@link Integer} object wrapping the <Code>int</Code>
     *    value at that index
     *  @throws IndexOutOfBoundsException  if the index is negative or
     *    greater than or equal to {@link #size()}
     */
    public Object get(int index) {
        return new Integer(getInt(index));
    }

    /** 
     *  Returns <code>{@link #containsInt containsInt(((Integer)value).intValue())}</code>. 
     *
     *  @param value  an {@link Integer} object whose wrapped <Code>int</Code>
     *    value to search for
     *  @return true  if this list contains that <Code>int</Code> value
     *  @throws ClassCastException  if the given object is not an 
     *    {@link Integer}
     *  @throws NullPointerException  if the given object is <Code>null</Code> 
     */
    public boolean contains(Object value) {
        return containsInt(((Integer)value).intValue());
    }

    /** 
     *  Returns <code>({@link #size} == 0)</code>. 
     *
     *  @return true if this list is empty, false otherwise
     */
    public boolean isEmpty() {
        return (0 == size());
    }

    /** 
     *  Returns <code>{@link #indexOfInt indexOfInt(((Integer)value).intValue())}</code>. 
     *
     *  @param value  an {@link Integer} object whose wrapped <Code>int</Code>
     *    value to search for
     *  @return the first index of that <Code>int</Code> value, or -1 if 
     *    this list does not contain that value
     *  @throws ClassCastException  if the given object is not an 
     *    {@link Integer}
     *  @throws NullPointerException  if the given object is <Code>null</Code> 
     */
    public int indexOf(Object value) {
        return indexOfInt(((Integer)value).intValue());
    }

    /** 
     *  Returns <code>{@link #lastIndexOfInt lastIndexOfInt(((Integer)value).intValue())}</code>. 
     *
     *  @param value  an {@link Integer} object whose wrapped <Code>int</Code>
     *    value to search for
     *  @return the last index of that <Code>int</Code> value, or -1 if 
     *    this list does not contain that value
     *  @throws ClassCastException  if the given object is not an 
     *    {@link Integer}
     *  @throws NullPointerException  if the given object is <Code>null</Code> 
     */
    public int lastIndexOf(Object value) {
        return lastIndexOfInt(((Integer)value).intValue());
    }

    //------------------------------------------------------ Abstract Modifiers

    /**
     *  Sets the element at the given index to the given <Code>int</Code>
     *  value.
     *
     *  @param index  the index of the element to set
     *  @param value  the <Code>int</Code> value to set it to
     *  @return  the previous <Code>int</Code> value at that index
     *  @throws  IndexOutOfBoundsException  if the index is negative or 
     *     greater than or equal to {@link #size()}.
     */
    abstract public int setInt(int index, int value);

    /**
     *  Adds the given <Code>int</Code> value to the end of this list.
     *
     *  @param value  the value to add
     *  @return  true, always
     */
    abstract public boolean addInt(int value);


    /**
     *  Inserts the given <Code>int</Code> value into this list at the
     *  specified index.
     *
     *  @param index  the index for the insertion
     *  @param value  the value to insert at that index
     *  @throws IndexOutOfBoundsException if the index is negative or 
     *    greater than {@link #size()}
     */
    abstract public void addInt(int index, int value);


    /**
     *  Removes the <Code>int</Code> element at the specified index.
     *
     *  @param index  the index of the element to remove
     *  @return  the removed <Code>int</Code> value
     *  @throws IndexOutOfBoundsException if the index is negative or
     *   greater than or equal to {@link #size()}
     */
    abstract public int removeIntAt(int index);


    /**
     *  Removes the first occurrence of the given <Code>int</COde> value
     *  from this list.
     *
     *  @param value  the value to remove
     *  @return  true if this list contained that value and removed it,
     *   or false if this list didn't contain the value
     */
    abstract public boolean removeInt(int value);

    /**
     *  Removes all <Code>int</Code> values from this list.
     */
    abstract public void clear();

    /**
     *  Ensures that the length of the internal <Code>int</Code> array is
     *  at list the given value.
     *
     *  @param mincap  the minimum capcity for this list
     */
    abstract public void ensureCapacity(int mincap);

    /**
     *  Resizes the internal array such that {@link #capacity()} is equal
     *  to {@link #size()}.
     */
    abstract public void trimToSize();

    //--------------------------------------------------------------- Modifiers
    
    /** 
     * Returns <code>new Integer({@link #setInt(int,int) 
     * setInt(index,((Integer)value).intValue())})</code>. 
     *
     * @param index  the index of the element to set
     * @param value  an {@link Integer} object whose <Code>int</Code> value
     *  to set at that index
     * @return  an {@link Integer} that wraps the <Code>int</Code> value  
     *   previously at that index
     * @throws IndexOutOfBoundsException if the index is negative or greater
     *  than or equal to {@link #size()}
     * @throws ClassCastException if the given value is not an {@link Integer}
     * @throws NullPointerException if the given value is <Code>null</COde>
     */
    public Object set(int index, Object value) {
        return new Integer(setInt(index,((Integer)value).intValue()));
    }

    /** 
     * Invokes <code>{@link #addInt(int) addInt(((Integer)value).intValue())})</code>. 
     *
     * @param value  an {@link Integer} object that wraps the <Code>int</Code>
     *   value to add
     * @return true, always
     * @throws ClassCastException if the given value is not an {@link Integer}
     * @throws NullPointerException if the given value is <Code>null</COde>     
     */
    public boolean add(Object value) {
        return addInt(((Integer)value).intValue());
    }    

    /** 
     * Invokes <code>{@link #addInt(int,int) addInt(index,((Integer)value).intValue())})</code>. 
     *
     * @param index  the index of the insertion
     * @param value an {@link Integer} object that wraps the <Code>int</Code>
     *   value to insert
     * @throws IndexOutOfBoundsException if the index is negative or greater
     *   than {@link #size()}
     * @throws ClassCastException if the given value is not an {@link Integer}
     * @throws NullPointerException if the given value is <Code>null</COde>
     */
    public void add(int index, Object value) {
        addInt(index,((Integer)value).intValue());
    }

    /** 
     * Returns <code>new Integer({@link #removeIntAt(int) removeIntAt(index)})</code>. 
     *
     * @param index  the index of the element to remove
     * @return  an {@link Integer} object that wraps the value that was
     *   removed from that index
     * @throws IndexOutOfBoundsException  if the given index is negative
     *   or greater than or equal to {@link #size()}
     */
    public Object remove(int index) {
        return new Integer(removeIntAt(index));
    }

    /** 
     * Returns <code>{@link #removeInt(int) removeInt(((Integer)value).intValue())}</code>. 
     *
     * @param value  an {@link Integer} object that wraps the <Code>int</Code>
     *   value to remove
     * @return true if the first occurrence of that <Code>int</Code> value
     *   was removed from this list, or <Code>false</Code> if this list
     *   did not contain that <Code>int</Code> value
     * @throws ClassCastException if the given value is not an {@link Integer}
     * @throws NullPointerException if the given value is <Code>null</COde>
     */
    public boolean remove(Object value) {
        return removeInt(((Integer)value).intValue());
    }
}
