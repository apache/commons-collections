/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/UnsignedByteArrayList.java,v 1.2 2002/08/13 19:41:36 pjack Exp $
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list of unsigned 8-bit values, stored in a <Code>short</Code> array.
 * Mutators on this class will reject any <Code>short</Code> that does not
 * express an unsigned 8-bit value.
 *
 * @version $Revision: 1.2 $ $Date: 2002/08/13 19:41:36 $
 * @author Rodney Waldhoff 
 */
public class UnsignedByteArrayList extends AbstractShortArrayList implements List, Serializable {

    //------------------------------------------------------------ Constructors
    
    /**
     *  Constructs a new <Code>UnsignedByteArrayList</Code> with a 
     *  default initial capacity.
     */
    public UnsignedByteArrayList() {
        this(8);
    }

    /**
     *  Constructs a new <Code>UnsignedByteArrayList</Code> with the 
     *  specified initial capacity.
     *
     *  @param capacity  the capacity for this list
     *  @throws IllegalArgumentException if the given capacity is less than 
     *    or equal to zero
     */
    public UnsignedByteArrayList(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity=" + capacity);
        }
        _data = new byte[capacity];
    }

    //--------------------------------------------------------------- Accessors
    
    public int capacity() {
        return _data.length;
    }

    public int size() {
        return _size;
    }

    public short getShort(int index) {
        checkRange(index);
        return toShort(_data[index]);
    }

    public boolean containsShort(short value) {
        assertValidUnsignedByte(value);
        return (-1 != indexOfShort(value));
    }

    public int indexOfShort(short value) {
        assertValidUnsignedByte(value);
        int ivalue = fromShort(value);
        for(int i=0;i<_size;i++) {
            if(ivalue == _data[i]) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOfShort(short value) {
        assertValidUnsignedByte(value);
        int ivalue = fromShort(value);
        for(int i=_size-1;i>=0;i--) {
            if(ivalue == _data[i]) {
                return i;
            }
        }
        return -1;
    }

    //--------------------------------------------------------------- Modifiers
    
    public short setShort(int index, short value) {
        assertValidUnsignedByte(value);
        checkRange(index);
        short old = toShort(_data[index]);
        _data[index] = fromShort(value);
        return old;
    }

    public boolean addShort(short value) {
        assertValidUnsignedByte(value);
        ensureCapacity(_size+1);
        _data[_size++] = fromShort(value);
        return true;
    }

    public void addShort(int index, short value) {
        assertValidUnsignedByte(value);
        checkRangeIncludingEndpoint(index);
        ensureCapacity(_size+1);
        int numtomove = _size-index;
	System.arraycopy(_data,index,_data,index+1,numtomove);
	_data[index] = fromShort(value);
	_size++;
    }

    public void clear() {
	modCount++;
        _size = 0;
    }

    public short removeShortAt(int index) {
        checkRange(index);
	modCount++;
        short oldval = toShort(_data[index]);
	int numtomove = _size - index - 1;
	if(numtomove > 0) {
	    System.arraycopy(_data,index+1,_data,index,numtomove);
        }
        _size--;
	return oldval;
    }

    public boolean removeShort(short value) {
        assertValidUnsignedByte(value);
        int index = indexOfShort(value);
        if(-1 == index) {
            return false;
        } else {
            removeShortAt(index);
            return true;
        }
    }

    public void ensureCapacity(int mincap) {
	modCount++;
	if(mincap > _data.length) {
	    int newcap = (_data.length * 3)/2 + 1;
	    byte[] olddata = _data;
	    _data = new byte[newcap < mincap ? mincap : newcap];
	    System.arraycopy(olddata,0,_data,0,_size);
	}
    }

    public void trimToSize() {
	modCount++;
	if(_size < _data.length) {
	    byte[] olddata = _data;
	    _data = new byte[_size];
	    System.arraycopy(olddata,0,_data,0,_size);
	}
    }

    //---------------------------------------------------------------

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
