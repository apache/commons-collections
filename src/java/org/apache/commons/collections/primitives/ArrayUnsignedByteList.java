/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/ArrayUnsignedByteList.java,v 1.4 2003/08/31 17:21:15 scolebourne Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections.primitives;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A {@link ShortList} backed by an array of unsigned
 * <code>byte</code> values.
 * This list stores <code>short</code> values
 * in the range [{@link #MIN_VALUE},
 * {@link #MAX_VALUE}] in 8-bits 
 * per element.  Attempts to use elements outside this 
 * range may cause an 
 * {@link IllegalArgumentException IllegalArgumentException} 
 * to be thrown.
 * <p />
 * This implementation supports all optional methods.
 * 
 * @since Commons Collections 2.2
 * @version $Revision: 1.4 $ $Date: 2003/08/31 17:21:15 $
 * 
 * @author Rodney Waldhoff 
 */
public class ArrayUnsignedByteList extends RandomAccessShortList implements ShortList, Serializable {

    // constructors
    //-------------------------------------------------------------------------

    /** 
     * Construct an empty list with the default
     * initial capacity.
     */
    public ArrayUnsignedByteList() {
        this(8);
    }    

    /**
     * Construct an empty list with the given
     * initial capacity.
     * @throws IllegalArgumentException when <i>initialCapacity</i> is negative
     */
    public ArrayUnsignedByteList(int initialCapacity) {
        if(initialCapacity < 0) {
            throw new IllegalArgumentException("capacity " + initialCapacity);
        }
        _data = new byte[initialCapacity];
        _size = 0;
    }    

    /** 
     * Constructs a list containing the elements of the given collection, 
     * in the order they are returned by that collection's iterator.
     * 
     * @see ShortList#addAll(org.apache.commons.collections.primitives.ShortCollection)
     * @param that the non-<code>null</code> collection of <code>int</code>s 
     *        to add
     * @throws NullPointerException if <i>that</i> is <code>null</code>
     */
    public ArrayUnsignedByteList(ShortCollection that) { 
        this(that.size());
        addAll(that);
    }    

    // ShortList methods
    //-------------------------------------------------------------------------

    /** 
     * Returns the element at the specified position within 
     * me. 
     * By construction, the returned value will be 
     * between {@link #MIN_VALUE} and {@link #MAX_VALUE}, inclusive.
     * 
     * @param index the index of the element to return
     * @return the value of the element at the specified position
     * @throws IndexOutOfBoundsException if the specified index is out of range
     */
    public short get(int index) {
        checkRange(index);
        return toShort(_data[index]);
    }
    
    public int size() {
        return _size;
    }
    
    /** 
     * Removes the element at the specified position in 
     * (optional operation).  Any subsequent elements 
     * are shifted to the left, subtracting one from their 
     * indices.  Returns the element that was removed.
     * By construction, the returned value will be 
     * between {@link #MIN_VALUE} and {@link #MAX_VALUE}, inclusive.
     * 
     * @param index the index of the element to remove
     * @return the value of the element that was removed
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     * @throws IndexOutOfBoundsException if the specified index is out of range
     */
    public short removeElementAt(int index) {
        checkRange(index);
        incrModCount();
        short oldval = toShort(_data[index]);
        int numtomove = _size - index - 1;
        if(numtomove > 0) {
            System.arraycopy(_data,index+1,_data,index,numtomove);
        }
        _size--;
        return oldval;
    }
    
    /** 
     * Replaces the element at the specified 
     * position in me with the specified element
     * (optional operation). 
     * Throws {@link IllegalArgumentException} if <i>element</i>
     * is less than {@link #MIN_VALUE} or greater than {@link #MAX_VALUE}.
     * 
     * @param index the index of the element to change
     * @param element the value to be stored at the specified position
     * @return the value previously stored at the specified position
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     * @throws IndexOutOfBoundsException if the specified index is out of range
     */
    public short set(int index, short element) {
        assertValidUnsignedByte(element);
        checkRange(index);
        incrModCount();
        short oldval = toShort(_data[index]);
        _data[index] = fromShort(element);
        return oldval;
    }
        
    /** 
     * Inserts the specified element at the specified position 
     * (optional operation). Shifts the element currently 
     * at that position (if any) and any subsequent elements to the 
     * right, increasing their indices.
     * Throws {@link IllegalArgumentException} if <i>element</i>
     * is less than {@link #MIN_VALUE} or greater than {@link #MAX_VALUE}.
     * 
     * @param index the index at which to insert the element
     * @param element the value to insert
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     * @throws IllegalArgumentException if some aspect of the specified element 
     *         prevents it from being added to me
     * @throws IndexOutOfBoundsException if the specified index is out of range
     */
    public void add(int index, short element) {
        assertValidUnsignedByte(element);
        checkRangeIncludingEndpoint(index);
        incrModCount();
        ensureCapacity(_size+1);
        int numtomove = _size-index;
        System.arraycopy(_data,index,_data,index+1,numtomove);
        _data[index] = fromShort(element);
        _size++;
    }

    // capacity methods
    //-------------------------------------------------------------------------

    /** 
     * Increases my capacity, if necessary, to ensure that I can hold at 
     * least the number of elements specified by the minimum capacity 
     * argument without growing.
     */
    public void ensureCapacity(int mincap) {
        incrModCount();
        if(mincap > _data.length) {
            int newcap = (_data.length * 3)/2 + 1;
            byte[] olddata = _data;
            _data = new byte[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata,0,_data,0,_size);
        }
    }

    /** 
     * Reduce my capacity, if necessary, to match my
     * current {@link #size size}.
     */
    public void trimToSize() {
        incrModCount();
        if(_size < _data.length) {
            byte[] olddata = _data;
            _data = new byte[_size];
            System.arraycopy(olddata,0,_data,0,_size);
        }
    }

    // private methods
    //-------------------------------------------------------------------------

    private final short toShort(byte value) { 
        return (short)(((short)value)&MAX_VALUE);
    }

    private final byte fromShort(short value) {
        return (byte)(value&MAX_VALUE);
    }

    private final void assertValidUnsignedByte(short value) throws IllegalArgumentException {
        if(value > MAX_VALUE) {
            throw new IllegalArgumentException(value + " > " + MAX_VALUE);
        }
        if(value < MIN_VALUE) {
            throw new IllegalArgumentException(value + " < " + MIN_VALUE);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeInt(_data.length);
        for(int i=0;i<_size;i++) {
            out.writeByte(_data[i]);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _data = new byte[in.readInt()];
        for(int i=0;i<_size;i++) {
            _data[i] = in.readByte();
        }
    }
    
    private final void checkRange(int index) {
        if(index < 0 || index >= _size) {
            throw new IndexOutOfBoundsException("Should be at least 0 and less than " + _size + ", found " + index);
        }
    }

    private final void checkRangeIncludingEndpoint(int index) {
        if(index < 0 || index > _size) {
            throw new IndexOutOfBoundsException("Should be at least 0 and at most " + _size + ", found " + index);
        }
    }

    private transient byte[] _data = null;
    private int _size = 0;

    /**
     *  The maximum possible unsigned 8-bit value.
     */
    public static final short MAX_VALUE = 0xFF;

    /**
     *  The minimum possible unsigned 8-bit value.
     */
    public static final short MIN_VALUE = 0;

}
