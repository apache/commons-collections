/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/TestLinkedMap.java,v 1.4 2003/12/28 22:45:47 scolebourne Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.list.AbstractTestList;

/**
 * JUnit tests.
 * 
 * @version $Revision: 1.4 $ $Date: 2003/12/28 22:45:47 $
 * 
 * @author Stephen Colebourne
 */
public class TestLinkedMap extends AbstractTestOrderedMap {

    public TestLinkedMap(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestLinkedMap.class);
    }

    public Map makeEmptyMap() {
        return new LinkedMap();
    }

    public String getCompatibilityVersion() {
        return "3";
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
    public void testInsertionOrder() {
        if (isPutAddSupported() == false || isPutChangeSupported() == false) return;
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Iterator it = null;
        
        resetEmpty();
        map.put(keys[0], values[0]);
        map.put(keys[1], values[1]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        // no change to order
        map.put(keys[1], values[1]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        // no change to order
        map.put(keys[1], values[2]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[2], it.next());

        // no change to order
        map.put(keys[0], values[3]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[3], it.next());
        assertSame(values[2], it.next());
    }
    
    //-----------------------------------------------------------------------
    public void testGetByIndex() {
        resetEmpty();
        LinkedMap lm = (LinkedMap) map;
        try {
            lm.get(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.get(-1);
        } catch (IndexOutOfBoundsException ex) {}
        
        resetFull();
        lm = (LinkedMap) map;
        try {
            lm.get(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.get(lm.size());
        } catch (IndexOutOfBoundsException ex) {}
        
        int i = 0;
        for (MapIterator it = lm.mapIterator(); it.hasNext(); i++) {
            assertSame(it.next(), lm.get(i));
        }
    }

    public void testGetValueByIndex() {
        resetEmpty();
        LinkedMap lm = (LinkedMap) map;
        try {
            lm.getValue(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.getValue(-1);
        } catch (IndexOutOfBoundsException ex) {}
        
        resetFull();
        lm = (LinkedMap) map;
        try {
            lm.getValue(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.getValue(lm.size());
        } catch (IndexOutOfBoundsException ex) {}
        
        int i = 0;
        for (MapIterator it = lm.mapIterator(); it.hasNext(); i++) {
            it.next();
            assertSame(it.getValue(), lm.getValue(i));
        }
    }

    public void testIndexOf() {
        resetEmpty();
        LinkedMap lm = (LinkedMap) map;
        assertEquals(-1, lm.indexOf(getOtherKeys()));
        
        resetFull();
        lm = (LinkedMap) map;
        List list = new ArrayList();
        for (MapIterator it = lm.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i, lm.indexOf(list.get(i)));
        }
    }

    public void testRemoveByIndex() {
        resetEmpty();
        LinkedMap lm = (LinkedMap) map;
        try {
            lm.remove(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.remove(-1);
        } catch (IndexOutOfBoundsException ex) {}
        
        resetFull();
        lm = (LinkedMap) map;
        try {
            lm.remove(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.remove(lm.size());
        } catch (IndexOutOfBoundsException ex) {}
        
        List list = new ArrayList();
        for (MapIterator it = lm.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            Object key = list.get(i);
            Object value = lm.get(key);
            assertEquals(value, lm.remove(i));
            list.remove(i);
            assertEquals(false, lm.containsKey(key));
        }
    }
    
    public BulkTest bulkTestListView() {
        return new TestListView();
    }
    
    public class TestListView extends AbstractTestList {
        
        TestListView() {
            super("TestListView");
        }

        public List makeEmptyList() {
            return ((LinkedMap) TestLinkedMap.this.makeEmptyMap()).asList();
        }
        
        public List makeFullList() {
            return ((LinkedMap) TestLinkedMap.this.makeFullMap()).asList();
        }
        
        public Object[] getFullElements() {
            return TestLinkedMap.this.getSampleKeys();
        }
        public boolean isAddSupported() {
            return false;
        }
        public boolean isRemoveSupported() {
            return false;
        }
        public boolean isSetSupported() {
            return false;
        }
        public boolean isNullSupported() {
            return TestLinkedMap.this.isAllowNullKey();
        }

    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LinkedMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LinkedMap.fullCollection.version3.obj");
//    }
}
