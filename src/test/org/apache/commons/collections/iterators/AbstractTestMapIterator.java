/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/AbstractTestMapIterator.java,v 1.9 2003/12/07 01:21:51 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.MapIterator;

/**
 * Abstract class for testing the MapIterator interface.
 * <p>
 * This class provides a framework for testing an implementation of MapIterator.
 * Concrete subclasses must provide the list iterator to be tested.
 * They must also specify certain details of how the list iterator operates by
 * overriding the supportsXxx() methods if necessary.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.9 $ $Date: 2003/12/07 01:21:51 $
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractTestMapIterator extends AbstractTestIterator {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestMapIterator(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implement this method to return a map iterator over an empty map.
     * 
     * @return an empty iterator
     */
    public abstract MapIterator makeEmptyMapIterator();

    /**
     * Implement this method to return a map iterator over a map with elements.
     * 
     * @return a full iterator
     */
    public abstract MapIterator makeFullMapIterator();

    /**
     * Implement this method to return the map which contains the same data as the
     * iterator.
     * 
     * @return a full map which can be updated
     */
    public abstract Map getMap();
    
    /**
     * Implement this method to return the confirmed map which contains the same
     * data as the iterator.
     * 
     * @return a full map which can be updated
     */
    public abstract Map getConfirmedMap();
    
    /**
     * Implements the abstract superclass method to return the list iterator.
     * 
     * @return an empty iterator
     */
    public final Iterator makeEmptyIterator() {
        return makeEmptyMapIterator();
    }

    /**
     * Implements the abstract superclass method to return the list iterator.
     * 
     * @return a full iterator
     */
    public final Iterator makeFullIterator() {
        return makeFullMapIterator();
    }

    /**
     * Whether or not we are testing an iterator that supports setValue().
     * Default is true.
     * 
     * @return true if Iterator supports set
     */
    public boolean supportsSetValue() {
        return true;
    }

    /**
     * Whether the get operation on the map structurally modifies the map,
     * such as with LRUMap. Default is false.
     * 
     * @return true if the get method structurally modifies the map
     */
    public boolean isGetStructuralModify() {
        return false;
    }
    
    /**
     * The values to be used in the add and set tests.
     * Default is two strings.
     */
    public Object[] addSetValues() {
        return new Object[] {"A", "B"};
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the empty list iterator contract is correct.
     */
    public void testEmptyMapIterator() {
        if (supportsEmptyIterator() == false) {
            return;
        }

        MapIterator it = makeEmptyMapIterator();
        Map map = getMap();
        assertEquals(false, it.hasNext());
        
        // next() should throw a NoSuchElementException
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {}
        
        // getKey() should throw an IllegalStateException
        try {
            it.getKey();
            fail();
        } catch (IllegalStateException ex) {}
        
        // getValue() should throw an IllegalStateException
        try {
            it.getValue();
            fail();
        } catch (IllegalStateException ex) {}
        
        if (supportsSetValue() == false) {
            // setValue() should throw an UnsupportedOperationException/IllegalStateException
            try {
                it.setValue(addSetValues()[0]);
                fail();
            } catch (UnsupportedOperationException ex) {
            } catch (IllegalStateException ex) {}
        } else {
            // setValue() should throw an IllegalStateException
            try {
                it.setValue(addSetValues()[0]);
                fail();
            } catch (IllegalStateException ex) {}
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the full list iterator contract is correct.
     */
    public void testFullMapIterator() {
        if (supportsFullIterator() == false) {
            return;
        }

        MapIterator it = makeFullMapIterator();
        Map map = getMap();
        assertEquals(true, it.hasNext());
        
        assertEquals(true, it.hasNext());
        Set set = new HashSet();
        while (it.hasNext()) {
            // getKey
            Object key = it.next();
            assertSame("it.next() should equals getKey()", key, it.getKey());
            assertTrue("Key must be in map",  map.containsKey(key));
            assertTrue("Key must be unique", set.add(key));
            
            // getValue
            Object value = it.getValue();
            if (isGetStructuralModify() == false) {
                assertSame("Value must be mapped to key", map.get(key), value);
            }
            assertTrue("Value must be in map",  map.containsValue(value));

            verify();
        }
    }
    
    //-----------------------------------------------------------------------
    public void testMapIteratorSet() {
        if (supportsFullIterator() == false) {
            return;
        }

        Object newValue = addSetValues()[0];
        Object newValue2 = addSetValues()[1];
        MapIterator it = makeFullMapIterator();
        Map map = getMap();
        Map confirmed = getConfirmedMap();
        assertEquals(true, it.hasNext());
        Object key = it.next();
        Object value = it.getValue();
        
        if (supportsSetValue() == false) {
            try {
                it.setValue(newValue);
                fail();
            } catch (UnsupportedOperationException ex) {}
            return;
        }
        Object old = it.setValue(newValue);
        confirmed.put(key, newValue);
        assertSame("Key must not change after setValue", key, it.getKey());
        assertSame("Value must be changed after setValue", newValue, it.getValue());
        assertSame("setValue must return old value", value, old);
        assertEquals("Map must contain key", true, map.containsKey(key));
        // test against confirmed, as map may contain value twice
        assertEquals("Map must not contain old value", 
            confirmed.containsValue(old), map.containsValue(old));
        assertEquals("Map must contain new value", true, map.containsValue(newValue));
        verify();
        
        it.setValue(newValue);  // same value - should be OK
        confirmed.put(key, newValue);
        assertSame("Key must not change after setValue", key, it.getKey());
        assertSame("Value must be changed after setValue", newValue, it.getValue());
        verify();
        
        it.setValue(newValue2);  // new value
        confirmed.put(key, newValue2);
        assertSame("Key must not change after setValue", key, it.getKey());
        assertSame("Value must be changed after setValue", newValue2, it.getValue());
        verify();
    }

    //-----------------------------------------------------------------------
    public void testRemove() { // override
        MapIterator it = makeFullMapIterator();
        Map map = getMap();
        Map confirmed = getConfirmedMap();
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        if (supportsRemove() == false) {
            try {
                it.remove();
                fail();
            } catch (UnsupportedOperationException ex) {
            }
            return;
        }
        
        it.remove();
        confirmed.remove(key);
        assertEquals(false, map.containsKey(key));
        verify();
        
        try {
            it.remove();  // second remove fails
        } catch (IllegalStateException ex) {
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorSetRemoveSet() {
        if (supportsSetValue() == false || supportsRemove() == false) {
            return;
        }
        Object newValue = addSetValues()[0];
        MapIterator it = makeFullMapIterator();
        Map map = getMap();
        Map confirmed = getConfirmedMap();
        
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        it.setValue(newValue);
        it.remove();
        confirmed.remove(key);
        verify();
        
        try {
            it.setValue(newValue);
            fail();
        } catch (IllegalStateException ex) {}
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorRemoveGetKey() {
        if (supportsRemove() == false) {
            return;
        }
        MapIterator it = makeFullMapIterator();
        Map map = getMap();
        Map confirmed = getConfirmedMap();
        
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        it.remove();
        confirmed.remove(key);
        verify();
        
        try {
            it.getKey();
            fail();
        } catch (IllegalStateException ex) {}
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorRemoveGetValue() {
        if (supportsRemove() == false) {
            return;
        }
        MapIterator it = makeFullMapIterator();
        Map map = getMap();
        Map confirmed = getConfirmedMap();
        
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        it.remove();
        confirmed.remove(key);
        verify();
        
        try {
            it.getValue();
            fail();
        } catch (IllegalStateException ex) {}
        verify();
    }

}
