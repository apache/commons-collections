/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/Attic/AbstractIntListList.java,v 1.3 2003/11/07 20:09:15 rwaldhoff Exp $
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

package org.apache.commons.collections.primitives.adapters;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.primitives.IntCollection;
import org.apache.commons.collections.primitives.IntList;

/**
 * 
 * @deprecated This code has been moved to Jakarta Commons Primitives (http://jakarta.apache.org/commons/primitives/)
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.3 $ $Date: 2003/11/07 20:09:15 $
 * @author Rodney Waldhoff 
 */
abstract class AbstractIntListList extends AbstractIntCollectionCollection implements List {
    
    public void add(int index, Object element) {
        getIntList().add(index,((Number)element).intValue());
    }

    public boolean addAll(int index, Collection c) {
        return getIntList().addAll(index,CollectionIntCollection.wrap(c));
    }

    public Object get(int index) {
        return new Integer(getIntList().get(index));
    }

    public int indexOf(Object element) {
        return getIntList().indexOf(((Number)element).intValue());
    }

    public int lastIndexOf(Object element) {
        return getIntList().lastIndexOf(((Number)element).intValue());
    }

    /**
     * {@link IntListIteratorListIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.IntListIterator IntListIterator}
     * returned by my underlying 
     * {@link IntList IntList}, 
     * if any.
     */
    public ListIterator listIterator() {
        return IntListIteratorListIterator.wrap(getIntList().listIterator());
    }

    /**
     * {@link IntListIteratorListIterator#wrap wraps} the 
     * {@link org.apache.commons.collections.primitives.IntListIterator IntListIterator}
     * returned by my underlying 
     * {@link IntList IntList}, 
     * if any.
     */
    public ListIterator listIterator(int index) {
        return IntListIteratorListIterator.wrap(getIntList().listIterator(index));
    }

    public Object remove(int index) {
        return new Integer(getIntList().removeElementAt(index));
    }

    public Object set(int index, Object element) {
        return new Integer(getIntList().set(index, ((Number)element).intValue() ));
    }

    public List subList(int fromIndex, int toIndex) {
        return IntListList.wrap(getIntList().subList(fromIndex,toIndex));
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

    public int hashCode() {
        return getIntList().hashCode();
    }
    
    protected final IntCollection getIntCollection() {
        return getIntList();
    }
    
    protected abstract IntList getIntList();
        

}
