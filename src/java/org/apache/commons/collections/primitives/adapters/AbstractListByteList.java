/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/AbstractListByteList.java,v 1.1 2003/04/15 01:55:22 rwaldhoff Exp $
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

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.primitives.ByteCollection;
import org.apache.commons.collections.primitives.ByteIterator;
import org.apache.commons.collections.primitives.ByteList;
import org.apache.commons.collections.primitives.ByteListIterator;

/**
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.1 $ $Date: 2003/04/15 01:55:22 $
 * @author Rodney Waldhoff 
 */
abstract class AbstractListByteList extends AbstractCollectionByteCollection implements ByteList {

    public void add(int index, byte element) {
        getList().add(index,new Byte(element));
    }

    public boolean addAll(int index, ByteCollection collection) {
        return getList().addAll(index,ByteCollectionCollection.wrap(collection));
    }

    public byte get(int index) {
        return ((Number)getList().get(index)).byteValue();
    }

    public int indexOf(byte element) {
        return getList().indexOf(new Byte(element));
    }

    public int lastIndexOf(byte element) {
        return getList().lastIndexOf(new Byte(element));
    }

    /**
     * {@link ListIteratorByteListIterator#wrap wraps} the 
     * {@link ByteList ByteList} 
     * returned by my underlying 
     * {@link ByteListIterator ByteListIterator},
     * if any.
     */
    public ByteListIterator listIterator() {
        return ListIteratorByteListIterator.wrap(getList().listIterator());
    }

    /**
     * {@link ListIteratorByteListIterator#wrap wraps} the 
     * {@link ByteList ByteList} 
     * returned by my underlying 
     * {@link ByteListIterator ByteListIterator},
     * if any.
     */
    public ByteListIterator listIterator(int index) {
        return ListIteratorByteListIterator.wrap(getList().listIterator(index));
    }

    public byte removeElementAt(int index) {
        return ((Number)getList().remove(index)).byteValue();
    }

    public byte set(int index, byte element) {
        return ((Number)getList().set(index,new Byte(element))).byteValue();
    }

    public ByteList subList(int fromIndex, int toIndex) {
        return ListByteList.wrap(getList().subList(fromIndex,toIndex));
    }

    public boolean equals(Object obj) {
        if(obj instanceof ByteList) {
            ByteList that = (ByteList)obj;
            if(this == that) {
                return true;
            } else if(this.size() != that.size()) {
                return false;            
            } else {
                ByteIterator thisiter = iterator();
                ByteIterator thatiter = that.iterator();
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
        
    public int hashCode() {
        return getList().hashCode();
    }
    
    final protected Collection getCollection() {
        return getList();
    }
    
    abstract protected List getList();
}
