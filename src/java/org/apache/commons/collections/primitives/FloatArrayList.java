/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/FloatArrayList.java,v 1.1 2002/06/04 16:50:09 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/04 16:50:09 $
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
 * @version $Revision: 1.1 $ $Date: 2002/06/04 16:50:09 $
 * @author Rodney Waldhoff 
 */
public class FloatArrayList extends AbstractList implements List, Serializable {

    //------------------------------------------------------------ Constructors
    
    public FloatArrayList() {
        this(8);
    }

    public FloatArrayList(int capacity) {
        _data = new float[capacity];
    }

    //--------------------------------------------------------------- Accessors
    
    public int capacity() {
        return _data.length;
    }

    public int size() {
        return _size;
    }

    public Object get(int index) {
        checkRange(index);
        return new Float(_data[index]);
    }

    public float getFloat(int index) {
        checkRange(index);
        return _data[index];
    }

    public boolean containsFloat(float value) {
        return (-1 != indexOfFloat(value));
    }

    public int indexOfFloat(float value) {
        for(int i=0;i<_size;i++) {
            if(value == _data[i]) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOfFloat(float value) {
        for(int i=_size-1;i>=0;i--) {
            if(value == _data[i]) {
                return i;
            }
        }
        return -1;
    }

    //--------------------------------------------------------------- Modifiers
    
    public float setFloat(int index, float value) {
        checkRange(index);
        float old = _data[index];
        _data[index] = value;
        return old;
    }

    public boolean addFloat(float value) {
        ensureCapacity(_size+1);
        _data[_size++] = value;
        return true;
    }

    public void addFloat(int index, float value) {
        checkRangeIncludingEndpoint(index);
        ensureCapacity(_size+1);
        int numtomove = _size-index;
        System.arraycopy(_data,index,_data,index+1,numtomove);
        _data[index] = value;
        _size++;
    }

    public void add(int index, Object value) {
        addFloat(index,((Float)value).floatValue());
    }

    public void clear() {
        modCount++;
        _size = 0;
    }

    public float removeFloatAt(int index) {
        checkRange(index);
        modCount++;
        float oldval = _data[index];
        int numtomove = _size - index - 1;
        if(numtomove > 0) {
            System.arraycopy(_data,index+1,_data,index,numtomove);
        }
        _size--;
        return oldval;
    }

    public boolean removeFloat(float value) {
        int index = indexOfFloat(value);
        if(-1 == index) {
            return false;
        } else {
            removeFloatAt(index);
            return true;
        }
    }

    public void ensureCapacity(int mincap) {
        modCount++;
        if(mincap > _data.length) {
            int newcap = (_data.length * 3)/2 + 1;
            float[] olddata = _data;
            _data = new float[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata,0,_data,0,_size);
        }
    }

    public void trimToSize() {
        modCount++;
        if(_size < _data.length) {
            float[] olddata = _data;
            _data = new float[_size];
            System.arraycopy(olddata,0,_data,0,_size);
        }
    }

    //---------------------------------------------------------------

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeInt(_data.length);
        for(int i=0;i<_size;i++) {
            out.writeFloat(_data[i]);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _data = new float[in.readInt()];
        for(int i=0;i<_size;i++) {
            _data[i] = in.readFloat();
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

    private transient float[] _data = null;
    private int _size = 0;
}
