/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/AbstractRandomAccessIntList.java,v 1.1 2003/01/06 03:59:12 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/06 03:59:12 $
 *
 * ====================================================================
 *
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

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class AbstractRandomAccessIntList extends AbstractIntCollection implements IntList {

    // constructors
    //-------------------------------------------------------------------------

    protected AbstractRandomAccessIntList() { }    

    // fully abstract methods
    //-------------------------------------------------------------------------
    
    public abstract int get(int index);
    public abstract int size();

    // unsupported in base
    //-------------------------------------------------------------------------
    
    public int removeElementAt(int index) {
        throw new UnsupportedOperationException();
    }
    
    public int set(int index, int element) {
        throw new UnsupportedOperationException();
    }
        
    public void add(int index, int element) {
        throw new UnsupportedOperationException();
    }

    //-------------------------------------------------------------------------
    
    public boolean add(int element) {
        add(size(),element);
        return true;
    }

    public boolean addAll(int index, IntCollection collection) {
        boolean modified = false;
        for(IntIterator iter = collection.iterator(); iter.hasNext(); ) {
            add(index++,iter.next());
            modified = true;
        }
        return modified;
    }

    public int indexOf(int element) {
        int i = 0;
        for(IntIterator iter = iterator(); iter.hasNext(); ) {
            if(iter.next() == element) { 
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    public int lastIndexOf(int element) {
        for(IntListIterator iter = listIterator(size()); iter.hasPrevious(); ) {
            if(iter.previous() == element) {
                return iter.nextIndex();
            }
        }
        return -1;
    }

    public IntIterator iterator() {
        return listIterator();
    }

    public IntListIterator listIterator() {
        return listIterator(0);
    }

    public IntListIterator listIterator(int index) {
        return new RandomAccessIntListIterator(this,index);            
    }

    public IntList subList(int fromIndex, int toIndex) {
        return new RandomAccessIntSubList(this,fromIndex,toIndex);
    }

    public boolean equals(Object that) {
        // handle an easy and somewhat frequent case via a shortcut
        if(this == that) { return true; } 

        // otherwise, try to get an IntIterator from that object
        IntIterator thatIter = null;        
        if(that instanceof IntList) {
            if(size() != ((IntList)that).size()) { return false; } // another quick check
            thatIter = ((IntList)that).iterator();
        } else if(that instanceof List) {
            if(size() != ((List)that).size()) { return false; } // another quick check
            thatIter = IteratorIntIterator.wrap(((List)that).iterator());
        }
        
        if(null == thatIter) { return false; }
        
        // walk thru thisIter and thatIter, checking for any differences
        for(IntIterator thisIter = iterator(); thisIter.hasNext();) {
            if(!thatIter.hasNext()) { return false; } // thatIter has a different length
            if(thisIter.next() != thatIter.next()) { 
                return false; 
            }
        }
        
        if(thatIter.hasNext()) { return false; } // thatIter has a different length
        
        return true;
    }
    
    public int hashCode() {
        int hash = 1;
        for(IntIterator iter = iterator(); iter.hasNext(); ) {
            hash = 31*hash + iter.next();
        }
        return hash;
    }
    
    // protected utilities
    //-------------------------------------------------------------------------
    
    protected int getModCount() {
        return _modCount;
    }

    protected void incrModCount() {
        _modCount++;
    }

    // attributes
    //-------------------------------------------------------------------------
    
    private int _modCount = 0;

    // inner classes
    //-------------------------------------------------------------------------
    
    protected static class ComodChecker {
        ComodChecker(AbstractRandomAccessIntList source) {
            _source = source;  
            resyncModCount();             
        }
        
        protected AbstractRandomAccessIntList getList() {
            return _source;
        }
        
        protected void assertNotComodified() throws ConcurrentModificationException {
            if(_expectedModCount != getList().getModCount()) {
                throw new ConcurrentModificationException();
            }
        }
            
        protected void resyncModCount() {
            _expectedModCount = getList().getModCount();
        }
        
        private AbstractRandomAccessIntList _source = null;
        private int _expectedModCount = -1;
    }
    
    protected static class RandomAccessIntListIterator extends ComodChecker implements IntListIterator {
        RandomAccessIntListIterator(AbstractRandomAccessIntList list) {
            this(list,0);
        }
            
        RandomAccessIntListIterator(AbstractRandomAccessIntList list, int index) {
            super(list);
            if(index < 0 || index > getList().size()) {
                throw new IllegalArgumentException("Index " + index + " not in [0," + getList().size() + ")");
            } else {
                _nextIndex = index;
                resyncModCount();
            }
        }
            
        public boolean hasNext() {
            assertNotComodified();
            return _nextIndex < getList().size();
        }
        
        public boolean hasPrevious() {
            assertNotComodified();
            return _nextIndex > 0;
        }
        
        public int nextIndex() {
            assertNotComodified();
            return _nextIndex;
        }
        
        public int previousIndex() {
            assertNotComodified();
            return _nextIndex - 1;
        }
        
        public int next() {
            assertNotComodified();
            if(!hasNext()) {
                throw new NoSuchElementException();
            } else {
                int val = getList().get(_nextIndex);
                _lastReturnedIndex = _nextIndex;
                _nextIndex++;
                return val;
            }
        }
        
        public int previous() {
            assertNotComodified();
            if(!hasPrevious()) {
                throw new NoSuchElementException();
            } else {
                int val = getList().get(_nextIndex-1);
                _lastReturnedIndex = _nextIndex-1;
                _nextIndex--;
                return val;
            }
        }
        
        public void add(int value) {
            assertNotComodified();
            getList().add(_nextIndex,value);
            _nextIndex++;
            _lastReturnedIndex = -1;
            resyncModCount();
        }
    
        public void remove() {
            assertNotComodified();
            if(-1 == _lastReturnedIndex) {
                throw new IllegalStateException();
            } else {
                getList().removeElementAt(_lastReturnedIndex);
                _lastReturnedIndex = -1;
                _nextIndex--;
                resyncModCount();
            }
        }
        
        public void set(int value) {
            assertNotComodified();
            if(-1 == _lastReturnedIndex) {
                throw new IllegalStateException();
            } else {
                getList().set(_lastReturnedIndex,value);
                resyncModCount();
            }
        }
        
        private int _nextIndex = 0;
        private int _lastReturnedIndex = -1;        
    }   

    protected static class RandomAccessIntSubList extends AbstractRandomAccessIntList implements IntList {
        RandomAccessIntSubList(AbstractRandomAccessIntList list, int fromIndex, int toIndex) {
            if(fromIndex < 0 || toIndex > list.size() || fromIndex > toIndex) {
                throw new IllegalArgumentException();
            } else {
                _list = list;
                _offset = fromIndex;
                _limit = toIndex - fromIndex;
                _comod = new ComodChecker(list);
                _comod.resyncModCount();
            }            
        }
    
        public int get(int index) {
            checkRange(index);
            _comod.assertNotComodified();
            return _list.get(toUnderlyingIndex(index));
        }
    
        public int removeElementAt(int index) {
            checkRange(index);
            _comod.assertNotComodified();
            int val = _list.removeElementAt(toUnderlyingIndex(index));
            _limit--;
            _comod.resyncModCount();
            incrModCount();
            return val;
        }
    
        public int set(int index, int element) {
            checkRange(index);
            _comod.assertNotComodified();
            int val = _list.set(toUnderlyingIndex(index),element);
            incrModCount();
            _comod.resyncModCount();
            return val;
        }
    
        public void add(int index, int element) {
            checkRangeIncludingEndpoint(index);
            _comod.assertNotComodified();
             _list.add(toUnderlyingIndex(index),element);
            _limit++;
            _comod.resyncModCount();
            incrModCount();
        }
    
        public int size() {
            _comod.assertNotComodified();
            return _limit;
        }
    
        private void checkRange(int index) {
            if(index < 0 || index >= size()) {
                throw new IllegalArgumentException("index " + index + " not in [0," + size() + ")");
            }
        }
          
        private void checkRangeIncludingEndpoint(int index) {
            if(index < 0 || index > size()) {
                throw new IllegalArgumentException("index " + index + " not in [0," + size() + "]");
            }
        }
          
        private int toUnderlyingIndex(int index) {
            return (index + _offset);
        }
        
        private int _offset = 0;        
        private int _limit = 0; 
        private AbstractRandomAccessIntList _list = null;
        private ComodChecker _comod = null;
    
    }
}

