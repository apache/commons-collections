/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/AbstractLongArrayList.java,v 1.1 2002/06/04 16:01:27 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/04 16:01:27 $
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
 * @version $Revision: 1.1 $ $Date: 2002/06/04 16:01:27 $
 * @author Rodney Waldhoff 
 */
public abstract class AbstractLongArrayList extends AbstractList implements List, Serializable {

    //------------------------------------------------------ Abstract Accessors
    
    abstract public int capacity();
    abstract public int size();
    abstract public long getLong(int index);
    abstract public boolean containsLong(long value);
    abstract public int indexOfLong(long value);
    abstract public int lastIndexOfLong(long value);

    //--------------------------------------------------------------- Accessors
    
    /** Returns <code>new Long({@link #getLong getLong(index)})</code>. */
    public Object get(int index) {
        return new Long(getLong(index));
    }

    /** Returns <code>{@link #containsLong containsLong(((Long)value).longValue())}</code>. */
    public boolean contains(Object value) {
        return containsLong(((Long)value).longValue());
    }

    /** Returns <code>({@link #size} == 0)</code>. */
    public boolean isEmpty() {
        return (0 == size());
    }

    /** Returns <code>{@link #indexOfLong indexOfLong(((Long)value).longValue())}</code>. */
    public int indexOf(Object value) {
        return indexOfLong(((Long)value).longValue());
    }

    /** Returns <code>{@link #lastIndexOfLong lastIndexOfLong(((Long)value).longValue())}</code>. */
    public int lastIndexOf(Object value) {
        return lastIndexOfLong(((Long)value).longValue());
    }

    //------------------------------------------------------ Abstract Modifiers

    abstract public long setLong(int index, long value);
    abstract public boolean addLong(long value);
    abstract public void addLong(int index, long value);
    abstract public long removeLongAt(int index);
    abstract public boolean removeLong(long value);
    abstract public void clear();
    abstract public void ensureCapacity(int mincap);
    abstract public void trimToSize();

    //--------------------------------------------------------------- Modifiers
    
    /** Returns <code>new Long({@link #setLong(int,long) setLong(index,((Long)value).longValue())})</code>. */
    public Object set(int index, Object value) {
        return new Long(setLong(index,((Long)value).longValue()));
    }

    /** Invokes <code>{@link #addLong(long) addLong(((Long)value).longValue())})</code>. */
    public boolean add(Object value) {
        return addLong(((Long)value).longValue());
    }    

    /** Invokes <code>{@link #addLong(int,long) addLong(index,((Long)value).longValue())})</code>. */
    public void add(int index, Object value) {
        addLong(index,((Long)value).longValue());
    }

    /** Returns <code>new Long({@link #removeLongAt(int) removeLongAt(index)})</code>. */
    public Object remove(int index) {
        return new Long(removeLongAt(index));
    }

    /** Returns <code>{@link #removeLong(long) removeLong(((Long)value).longValue())}</code>. */
    public boolean remove(Object value) {
        return removeLong(((Long)value).longValue());
    }
}
