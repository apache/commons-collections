/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/AbstractShortList.java,v 1.3 2002/08/22 01:50:54 pjack Exp $
 * $Revision: 1.3 $
 * $Date: 2002/08/22 01:50:54 $
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
 * Abstract base class for lists of primitive <Code>short</Code> elements.<P>
 *
 * The {@link List} methods are all implemented, but they forward to 
 * abstract methods that operate on <Code>short</Code> elements.  For 
 * instance, the {@link #get(int)} method simply forwards to 
 * {@link #getShort(int)}.  The primitive <Code>short</Code> that is 
 * returned from {@link #getShort(int)} is wrapped in a {@link java.lang.Short}
 * and returned from {@link #get(int)}.<p>
 *
 * Concrete implementations offer substantial memory savings by not storing
 * primitives as wrapped objects.  If you excuslively use the primitive 
 * signatures, there can also be substantial performance gains, since 
 * temporary wrapper objects do not need to be created.<p>
 *
 * To implement a read-only list of <Code>short</Code> elements, you need
 * only implement the {@link #getShort(int)} and {@link #size()} methods.
 * To implement a modifiable list, you will also need to implement the
 * {@link #setShort(int,short)}, {@link #addShort(int,short)}, 
 * {@link #removeShortAt(int)} and {@link #clear()} methods.  You may want 
 * to override the other methods to increase performance.<P>
 *
 * @version $Revision: 1.3 $ $Date: 2002/08/22 01:50:54 $
 * @author Rodney Waldhoff 
 */
public abstract class AbstractShortList extends AbstractList {

    //------------------------------------------------------ Abstract Accessors
    
    /**
     *  Returns the number of <Code>short</Code> elements currently in this
     *  list.
     *
     *  @return the size of this list
     */
    abstract public int size();

    /**
     *  Returns the <Code>short</Code> element at the specified index in this
     *  array.
     *
     *  @param index  the index of the element to return
     *  @return  the <Code>short</Code> element at that index
     *  @throws  IndexOutOfBoundsException  if the index is negative or
     *    greater than or equal to {@link #size()}
     */
    abstract public short getShort(int index);

    //--------------------------------------------------------------- Accessors
    
    /**
     *  Returns <Code>true</Code> if this list contains the given 
     *  <Code>short</Code> element.  The default implementation uses 
     *  {@link #indexOfShort(short)} to determine if the given value is
     *  in this list.
     *
     *  @param value  the element to search for
     *  @return true if this list contains the given value, false otherwise
     */
    public boolean containsShort(short value) {
        return indexOfShort(value) >= 0;
    }

    /**
     *  Returns the first index of the given <Code>short</Code> element, or
     *  -1 if the value is not in this list.  The default implementation is:
     *
     *  <pre>
     *   for (int i = 0; i < size(); i++) {
     *       if (getShort(i) == value) return i;
     *   }
     *   return -1;
     *  </pre>
     *
     *  @param value  the element to search for
     *  @return  the first index of that element, or -1 if the element is
     *    not in this list
     */
    public int indexOfShort(short value) {
        for (int i = 0; i < size(); i++) {
            if (getShort(i) == value) return i;
        }
        return -1;
    }

    /**
     *  Returns the last index of the given <Code>short</Code> element, or
     *  -1 if the value is not in this list.  The default implementation is:
     *
     *  <pre>
     *   for (int i = size() - 1; i >= 0; i--) {
     *       if (getShort(i) == value) return i;
     *   }
     *   return -1;
     *  </pre>
     *
     *  @param value  the element to search for
     *  @return  the last index of that element, or -1 if the element is
     *    not in this list
     */
    public int lastIndexOfShort(short value) {
        for (int i = size() - 1; i >= 0; i--) {
            if (getShort(i) == value) return i;
        }
        return -1;
    }

    /** 
     *  Returns <code>new Short({@link #getShort getShort(index)})</code>. 
     *
     *  @param index  the index of the element to return
     *  @return  an {@link Short} object wrapping the <Code>short</Code>
     *    value at that index
     *  @throws IndexOutOfBoundsException  if the index is negative or
     *    greater than or equal to {@link #size()}
     */
    public Object get(int index) {
        return new Short(getShort(index));
    }

    /** 
     *  Returns <code>{@link #containsShort containsShort(((Short)value.shortValue())}</code>. 
     *
     *  @param value  an {@link Short} object whose wrapped <Code>short</Code>
     *    value to search for
     *  @return true  if this list contains that <Code>short</Code> value
     *  @throws ClassCastException  if the given object is not an 
     *    {@link Short}
     *  @throws NullPointerException  if the given object is <Code>null</Code> 
     */
    public boolean contains(Object value) {
        return containsShort(((Short)value).shortValue());
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
     *  Returns <code>{@link #indexOfShort indexOfShort(((Short)value.shortValue())}</code>. 
     *
     *  @param value  an {@link Short} object whose wrapped <Code>short</Code>
     *    value to search for
     *  @return the first index of that <Code>short</Code> value, or -1 if 
     *    this list does not contain that value
     *  @throws ClassCastException  if the given object is not an 
     *    {@link Short}
     *  @throws NullPointerException  if the given object is <Code>null</Code> 
     */
    public int indexOf(Object value) {
        return indexOfShort(((Short)value).shortValue());
    }

    /** 
     *  Returns <code>{@link #lastIndexOfShort lastIndexOfShort(((Short)value.shortValue())}</code>. 
     *
     *  @param value  an {@link Short} object whose wrapped <Code>short</Code>
     *    value to search for
     *  @return the last index of that <Code>short</Code> value, or -1 if 
     *    this list does not contain that value
     *  @throws ClassCastException  if the given object is not an 
     *    {@link Short}
     *  @throws NullPointerException  if the given object is <Code>null</Code> 
     */
    public int lastIndexOf(Object value) {
        return lastIndexOfShort(((Short)value).shortValue());
    }

    //------------------------------------------------------ Abstract Modifiers

    /**
     *  Sets the element at the given index to the given <Code>short</Code>
     *  value.
     *
     *  @param index  the index of the element to set
     *  @param value  the <Code>short</Code> value to set it to
     *  @return  the previous <Code>short</Code> value at that index
     *  @throws  IndexOutOfBoundsException  if the index is negative or 
     *     greater than or equal to {@link #size()}.
     */
    abstract public short setShort(int index, short value);

    /**
     *  Inserts the given <Code>short</Code> value into this list at the
     *  specified index.
     *
     *  @param index  the index for the insertion
     *  @param value  the value to insert at that index
     *  @throws IndexOutOfBoundsException if the index is negative or 
     *    greater than {@link #size()}
     */
    abstract public void addShort(int index, short value);

    /**
     *  Removes the <Code>short</Code> element at the specified index.
     *
     *  @param index  the index of the element to remove
     *  @return  the removed <Code>short</Code> value
     *  @throws IndexOutOfBoundsException if the index is negative or
     *   greater than or equal to {@link #size()}
     */
    abstract public short removeShortAt(int index);

    /**
     *  Removes all <Code>short</Code> values from this list.
     */
    abstract public void clear();

    //--------------------------------------------------------------- Modifiers
    
    /**
     *  Adds the given <Code>short</Code> value to the end of this list.
     *  The default implementation invokes {@link #addShort(int,short)
     *  addShort(size(), value)}.
     *
     *  @param value  the value to add
     *  @return  true, always
     */
    public boolean addShort(short value) {
        addShort(size(), value);
        return true;
    }

    /**
     *  Removes the first occurrence of the given <Code>short</Code> value
     *  from this list.  The default implementation is:
     *
     *  <pre>
     *   int i = indexOfShort(value);
     *   if (i < 0) return false;
     *   removeShortAt(i);
     *   return true;
     *  </pre>
     *
     *  @param value  the value to remove
     *  @return  true if this list contained that value and removed it,
     *   or false if this list didn't contain the value
     */
    public boolean removeShort(short value) {
        int i = indexOfShort(value);
        if (i < 0) return false;
        removeShortAt(i);
        return true;
    }

    /** 
     * Returns <code>new Short({@link #setShort(int,short) 
     * setShort(index,((Short)value.shortValue())})</code>. 
     *
     * @param index  the index of the element to set
     * @param value  an {@link Short} object whose <Code>short</Code> value
     *  to set at that index
     * @return  an {@link Short} that wraps the <Code>short</Code> value  
     *   previously at that index
     * @throws IndexOutOfBoundsException if the index is negative or greater
     *  than or equal to {@link #size()}
     * @throws ClassCastException if the given value is not an {@link Short}
     * @throws NullPointerException if the given value is <Code>null</COde>
     */
    public Object set(int index, Object value) {
        return new Short(setShort(index,((Short)value).shortValue()));
    }

    /** 
     * Invokes <code>{@link #addShort(short) addShort(((Short)value.shortValue())})</code>. 
     *
     * @param value  an {@link Short} object that wraps the <Code>short</Code>
     *   value to add
     * @return true, always
     * @throws ClassCastException if the given value is not an {@link Short}
     * @throws NullPointerException if the given value is <Code>null</COde>     
     */
    public boolean add(Object value) {
        return addShort(((Short)value).shortValue());
    }    

    /** 
     * Invokes <code>{@link #addShort(int,short) addShort(index,((Short)value.shortValue())})</code>. 
     *
     * @param index  the index of the insertion
     * @param value an {@link Short} object that wraps the <Code>short</Code>
     *   value to insert
     * @throws IndexOutOfBoundsException if the index is negative or greater
     *   than {@link #size()}
     * @throws ClassCastException if the given value is not an {@link Short}
     * @throws NullPointerException if the given value is <Code>null</COde>
     */
    public void add(int index, Object value) {
        addShort(index,((Short)value).shortValue());
    }

    /** 
     * Returns <code>new Short({@link #removeShortAt(int) removeIntAt(index)})</code>. 
     *
     * @param index  the index of the element to remove
     * @return  an {@link Short} object that wraps the value that was
     *   removed from that index
     * @throws IndexOutOfBoundsException  if the given index is negative
     *   or greater than or equal to {@link #size()}
     */
    public Object remove(int index) {
        return new Short(removeShortAt(index));
    }

    /** 
     * Returns <code>{@link #removeShort(short) removeShort(((Short)value.shortValue())}</code>. 
     *
     * @param value  an {@link Short} object that wraps the <Code>short</Code>
     *   value to remove
     * @return true if the first occurrence of that <Code>short</Code> value
     *   was removed from this list, or <Code>false</Code> if this list
     *   did not contain that <Code>short</Code> value
     * @throws ClassCastException if the given value is not an {@link Short}
     * @throws NullPointerException if the given value is <Code>null</COde>
     */
    public boolean remove(Object value) {
        return removeShort(((Short)value).shortValue());
    }

}



    //--------------------------------------------------------------- Accessors
    


    
