/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/TestLRUMap.java,v 1.2 2003/12/07 23:59:12 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.ResettableIterator;

/**
 * JUnit tests.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/12/07 23:59:12 $
 * 
 * @author Stephen Colebourne
 */
public class TestLRUMap extends AbstractTestOrderedMap {

    public TestLRUMap(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestLRUMap.class);
    }

    public Map makeEmptyMap() {
        return new LRUMap();
    }

    public boolean isGetStructuralModify() {
        return true;
    }
    
    public String getCompatibilityVersion() {
        return "3";
    }

    //-----------------------------------------------------------------------
    public void testLRU() {
        if (isPutAddSupported() == false || isPutChangeSupported() == false) return;
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Iterator it = null;
        
        LRUMap map = new LRUMap(2);
        assertEquals(0, map.size());
        assertEquals(false, map.isFull());
        assertEquals(2, map.maxSize());
        
        map.put(keys[0], values[0]);
        assertEquals(1, map.size());
        assertEquals(false, map.isFull());
        assertEquals(2, map.maxSize());
        
        map.put(keys[1], values[1]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[1], it.next());
        assertSame(values[0], it.next());

        map.put(keys[2], values[2]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[2], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[2], it.next());
        assertSame(values[1], it.next());

        map.put(keys[2], values[0]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[2], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        map.put(keys[1], values[3]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[2], it.next());
        it = map.values().iterator();
        assertSame(values[3], it.next());
        assertSame(values[0], it.next());
    }
    
    //-----------------------------------------------------------------------    
    public void testReset() {
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        ((ResettableIterator) ordered.mapIterator()).reset();
        
        resetFull();
        ordered = (OrderedMap) map;
        List list = new ArrayList(ordered.keySet());
        ResettableIterator it = (ResettableIterator) ordered.mapIterator();
        assertSame(list.get(0), it.next());
        assertSame(list.get(1), it.next());
        it.reset();
        assertSame(list.get(0), it.next());
    }
    
    //-----------------------------------------------------------------------
    public void testAccessOrder() {
        if (isPutAddSupported() == false || isPutChangeSupported() == false) return;
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Iterator it = null;
        
        resetEmpty();
        map.put(keys[1], values[1]);
        map.put(keys[0], values[0]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        // change to order
        map.put(keys[1], values[1]);
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[1], it.next());
        assertSame(values[0], it.next());

        // no change to order
        map.put(keys[1], values[2]);
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[2], it.next());
        assertSame(values[0], it.next());

        // change to order
        map.put(keys[0], values[3]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[3], it.next());
        assertSame(values[2], it.next());

        // change to order
        map.get(keys[1]);
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[2], it.next());
        assertSame(values[3], it.next());

        // change to order
        map.get(keys[0]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[3], it.next());
        assertSame(values[2], it.next());

        // no change to order
        map.get(keys[0]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[3], it.next());
        assertSame(values[2], it.next());
    }
    
    //-----------------------------------------------------------------------
    public void testFirstKey() {  // override
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        try {
            ordered.firstKey();
            fail();
        } catch (NoSuchElementException ex) {}
        
        resetFull();
        ordered = (OrderedMap) map;
        Object confirmedFirst = confirmed.keySet().iterator().next();
        ordered.get(confirmedFirst);
        assertEquals(confirmedFirst, ordered.firstKey());
    }
    
    public void testLastKey() {  // override
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        try {
            ordered.lastKey();
            fail();
        } catch (NoSuchElementException ex) {}
        
        resetFull();
        ordered = (OrderedMap) map;
        Object confirmedFirst = confirmed.keySet().iterator().next();
        // access order, thus first in is now in last place
        assertEquals(confirmedFirst, ordered.lastKey());
    }

    //-----------------------------------------------------------------------    
    public void testNextKey() {  // override
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        assertEquals(null, ordered.nextKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, ordered.nextKey(null)); // this is allowed too
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.nextKey(null));
        }
        
        resetFull();
        ordered = (OrderedMap) map;
        List list = new ArrayList(confirmed.keySet());
        Collections.reverse(list);  // first into map is eldest
        Iterator it = list.iterator();
        Object confirmedLast = it.next();
        while (it.hasNext()) {
            Object confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.nextKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, ordered.nextKey(confirmedLast));
    }
    
    public void testPreviousKey() {  // override
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        assertEquals(null, ordered.previousKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, ordered.previousKey(null)); // this is allowed too
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.previousKey(null));
        }
        
        resetFull();
        ordered = (OrderedMap) map;
        List list = new ArrayList(confirmed.keySet());
        Iterator it = list.iterator();
        Object confirmedLast = it.next();
        while (it.hasNext()) {
            Object confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.previousKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, ordered.previousKey(confirmedLast));
    }
    
//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LRUMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LRUMap.fullCollection.version3.obj");
//    }
}
