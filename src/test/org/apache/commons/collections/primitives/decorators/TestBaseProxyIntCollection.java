/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/decorators/Attic/TestBaseProxyIntCollection.java,v 1.1 2003/05/04 12:58:48 rwaldhoff Exp $
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

package org.apache.commons.collections.primitives.decorators;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections.primitives.IntCollection;
import org.apache.commons.collections.primitives.IntIterator;

/**
 * @version $Revision: 1.1 $ $Date: 2003/05/04 12:58:48 $
 * @author Rodney Waldhoff
 */
public class TestBaseProxyIntCollection extends TestCase {

    // conventional
    // ------------------------------------------------------------------------

    public TestBaseProxyIntCollection(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBaseProxyIntCollection.class);
    }

    // tests
    // ------------------------------------------------------------------------
    
    public void testCollectionCallsAreProxied() {
        final InvocationCounter proxied = new InvocationCounter();
        IntCollection collection = new BaseProxyIntCollection() {
            protected IntCollection getProxiedCollection() {
                return proxied;
            }
        };
        
        assertEquals(0,proxied.getAddCount());
        collection.add(1);
        assertEquals(1,proxied.getAddCount());

        assertEquals(0,proxied.getAddAllCount());
        collection.addAll(null);
        assertEquals(1,proxied.getAddAllCount());
        
        assertEquals(0,proxied.getClearCount());
        collection.clear();
        assertEquals(1,proxied.getClearCount());

        assertEquals(0,proxied.getContainsCount());
        collection.contains(1);
        assertEquals(1,proxied.getContainsCount());

        assertEquals(0,proxied.getContainsAllCount());
        collection.containsAll(null);
        assertEquals(1,proxied.getContainsAllCount());

        assertEquals(0,proxied.getIsEmptyCount());
        collection.isEmpty();
        assertEquals(1,proxied.getIsEmptyCount());

        assertEquals(0,proxied.getIteratorCount());
        collection.iterator();
        assertEquals(1,proxied.getIteratorCount());

        assertEquals(0,proxied.getRemoveAllCount());
        collection.removeAll(null);
        assertEquals(1,proxied.getRemoveAllCount());

        assertEquals(0,proxied.getRetainAllCount());
        collection.retainAll(null);
        assertEquals(1,proxied.getRetainAllCount());

        assertEquals(0,proxied.getRemoveElementCount());
        collection.removeElement(1);
        assertEquals(1,proxied.getRemoveElementCount());

        assertEquals(0,proxied.getSizeCount());
        collection.size();
        assertEquals(1,proxied.getSizeCount());

        assertEquals(0,proxied.getToArrayIntArrayCount());
        collection.toArray(new int[0]);
        assertEquals(1,proxied.getToArrayIntArrayCount());
        
        assertEquals(0,proxied.getToArrayCount());
        collection.toArray();
        assertEquals(1,proxied.getToArrayCount());
        
        assertEquals(0,proxied.getToStringCount());
        collection.toString();
        assertEquals(1,proxied.getToStringCount());
        
        assertEquals(0,proxied.getEqualsCount());
        collection.equals(null);
        assertEquals(1,proxied.getEqualsCount());
        
        assertEquals(0,proxied.getHashCodeCount());
        collection.hashCode();
        assertEquals(1,proxied.getHashCodeCount());
        
    }
    
    // inner classes
    // ------------------------------------------------------------------------

    static class InvocationCounter implements IntCollection {
        private int _toArrayIntArray;
        private int _toArray;
        private int _size;
        private int _retainAll;
        private int _removeElement;
        private int _removeAll;
        private int _iterator;
        private int _isEmpty;
        private int _containsAll;
        private int _contains;
        private int _clear;
        private int _addAll;
        private int _add;

        private int _equals;
        private int _toString;
        private int _hashCode;

        public boolean add(int element) {
            _add++;
            return false;
        }

        public boolean addAll(IntCollection c) {
            _addAll++;
            return false;
        }

        public void clear() {
            _clear++;
        }

        public boolean contains(int element) {
            _contains++;
            return false;
        }

        public boolean containsAll(IntCollection c) {
            _containsAll++;
            return false;
        }

        public boolean isEmpty() {
            _isEmpty++;
            return false;
        }

        public IntIterator iterator() {
            _iterator++;
            return null;
        }

        public boolean removeAll(IntCollection c) {
            _removeAll++;
            return false;
        }

        public boolean removeElement(int element) {
            _removeElement++;
            return false;
        }

        public boolean retainAll(IntCollection c) {
            _retainAll++;
            return false;
        }

        public int size() {
            _size++;
            return 0;
        }

        public int[] toArray() {
            _toArray++;
            return null;
        }

        public int[] toArray(int[] a) {
            _toArrayIntArray++;
            return null;
        }

        public boolean equals(Object obj) {
            _equals++;
            return false;
        }

        public int hashCode() {
            _hashCode++;
            return 0;
        }

        public String toString() {
            _toString++;
            return null;
        }


        public int getAddCount() {
            return _add;
        }

        public int getAddAllCount() {
            return _addAll;
        }

        public int getClearCount() {
            return _clear;
        }

        public int getContainsCount() {
            return _contains;
        }

        public int getContainsAllCount() {
            return _containsAll;
        }

        public int getIsEmptyCount() {
            return _isEmpty;
        }

        public int getIteratorCount() {
            return _iterator;
        }

        public int getRemoveAllCount() {
            return _removeAll;
        }

        public int getRemoveElementCount() {
            return _removeElement;
        }

        public int getRetainAllCount() {
            return _retainAll;
        }

        public int getSizeCount() {
            return _size;
        }

        public int getToArrayCount() {
            return _toArray;
        }

        public int getToArrayIntArrayCount() {
            return _toArrayIntArray;
        }

        public int getEqualsCount() {
            return _equals;
        }

        public int getHashCodeCount() {
            return _hashCode;
        }

        public int getToStringCount() {
            return _toString;
        }

    }
}