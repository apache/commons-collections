/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/AbstractTestOrderedMapIterator.java,v 1.3 2003/12/01 22:48:58 scolebourne Exp $
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.OrderedMapIterator;

/**
 * Abstract class for testing the OrderedMapIterator interface.
 * <p>
 * This class provides a framework for testing an implementation of MapIterator.
 * Concrete subclasses must provide the list iterator to be tested.
 * They must also specify certain details of how the list iterator operates by
 * overriding the supportsXxx() methods if necessary.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/12/01 22:48:58 $
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractTestOrderedMapIterator extends AbstractTestMapIterator {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestOrderedMapIterator(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    public final OrderedMapIterator makeEmptyOrderedMapIterator() {
        return (OrderedMapIterator) makeEmptyMapIterator();
    }

    public final OrderedMapIterator makeFullOrderedMapIterator() {
        return (OrderedMapIterator) makeFullMapIterator();
    }
    
    //-----------------------------------------------------------------------
    /**
     * Test that the empty list iterator contract is correct.
     */
    public void testEmptyMapIterator() {
        if (supportsEmptyIterator() == false) {
            return;
        }

        super.testEmptyMapIterator();
        
        OrderedMapIterator it = makeEmptyOrderedMapIterator();
        Map map = getMap();
        assertEquals(false, it.hasPrevious());
        try {
            it.previous();
            fail();
        } catch (NoSuchElementException ex) {}
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the full list iterator contract is correct.
     */
    public void testFullMapIterator() {
        if (supportsFullIterator() == false) {
            return;
        }

        super.testFullMapIterator();
        
        OrderedMapIterator it = makeFullOrderedMapIterator();
        Map map = getMap();
        
        assertEquals(true, it.hasNext());
        assertEquals(false, it.hasPrevious());
        Set set = new HashSet();
        while (it.hasNext()) {
            // getKey
            Object key = it.next();
            assertSame("it.next() should equals getKey()", key, it.getKey());
            assertTrue("Key must be in map",  map.containsKey(key));
            assertTrue("Key must be unique", set.add(key));
            
            // getValue
            Object value = it.getValue();
            assertSame("Value must be mapped to key", map.get(key), value);
            assertTrue("Value must be in map",  map.containsValue(value));
            assertSame("Value must be mapped to key", map.get(key), value);

            assertEquals(true, it.hasPrevious());
            
            verify();
        }
        while (it.hasPrevious()) {
            // getKey
            Object key = it.previous();
            assertSame("it.previous() should equals getKey()", key, it.getKey());
            assertTrue("Key must be in map",  map.containsKey(key));
            assertTrue("Key must be unique", set.remove(key));
            
            // getValue
            Object value = it.getValue();
            assertSame("Value must be mapped to key", map.get(key), value);
            assertTrue("Value must be in map",  map.containsValue(value));
            assertSame("Value must be mapped to key", map.get(key), value);

            assertEquals(true, it.hasNext());
            
            verify();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Test that the iterator order matches the keySet order.
     */
    public void testMapIteratorOrder() {
        if (supportsFullIterator() == false) {
            return;
        }

        OrderedMapIterator it = makeFullOrderedMapIterator();
        Map map = getMap();
        
        assertEquals("keySet() not consistent", new ArrayList(map.keySet()), new ArrayList(map.keySet()));
        
        Iterator it2 = map.keySet().iterator();
        assertEquals(true, it.hasNext());
        assertEquals(true, it2.hasNext());
        List list = new ArrayList();
        while (it.hasNext()) {
            Object key = it.next();
            assertEquals(it2.next(), key);
            list.add(key);
        }
        while (it.hasPrevious()) {
            Object key = it.previous();
            assertEquals(list.get(list.size() - 1), key);
            list.remove(list.size() - 1);
        }
    }
    
}
