/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestOrderedMap.java,v 1.4 2003/11/08 18:46:57 scolebourne Exp $
 * ====================================================================
 *
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
package org.apache.commons.collections.decorators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections.AbstractTestMap;
import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.iterators.AbstractTestMapIterator;
import org.apache.commons.collections.iterators.MapIterator;

/**
 * Extension of {@link TestMap} for exercising the {@link OrderedMap}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/11/08 18:46:57 $
 * 
 * @author Henri Yandell
 * @author Stephen Colebourne
 */
public class TestOrderedMap extends AbstractTestMap {

    public TestOrderedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestOrderedMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestOrderedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        return OrderedMap.decorate(new HashMap());
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestMapIterator() {
        return new TestOrderedMapIterator();
    }
    
    public class TestOrderedMapIterator extends AbstractTestMapIterator {
        public TestOrderedMapIterator() {
            super("TestOrderedMapIterator");
        }
        
        protected Object addSetValue() {
            return TestOrderedMap.this.getNewSampleValues()[0];
        }
        
        protected boolean supportsRemove() {
            return TestOrderedMap.this.isRemoveSupported();
        }

        protected boolean supportsSetValue() {
            return TestOrderedMap.this.isSetValueSupported();
        }

        protected MapIterator makeEmptyMapIterator() {
            resetEmpty();
            return ((OrderedMap) TestOrderedMap.this.map).mapIterator();
        }

        protected MapIterator makeFullMapIterator() {
            resetFull();
            return ((OrderedMap) TestOrderedMap.this.map).mapIterator();
        }
        
        protected Map getMap() {
            // assumes makeFullMapIterator() called first
            return TestOrderedMap.this.map;
        }
        
        protected Map getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return TestOrderedMap.this.confirmed;
        }
        
        protected void verify() {
            super.verify();
            TestOrderedMap.this.verify();
        }
    }
    
    //-----------------------------------------------------------------------
    public void testMapIteratorRemove() {
        resetFull();
        OrderedMap testMap = (OrderedMap) map;
        MapIterator it = testMap.mapIterator();
        assertEquals(true, it.hasNext());
        Object key = it.next();
        
        if (isRemoveSupported() == false) {
            try {
                it.remove();
                fail();
            } catch (UnsupportedOperationException ex) {
            }
            return;
        }
        
        it.remove();
        confirmed.remove(key);
        assertEquals(false, testMap.containsKey(key));
        verify();
        
        try {
            it.remove();  // second remove fails
        } catch (IllegalStateException ex) {
        }
        verify();
    }

    //-----------------------------------------------------------------------
    public void testMapIteratorSet() {
        Object newValue1 = getOtherValues()[0];
        Object newValue2 = getOtherValues()[1];
        
        resetFull();
        OrderedMap testMap = (OrderedMap) map;
        MapIterator it = testMap.mapIterator();
        assertEquals(true, it.hasNext());
        Object key1 = it.next();
        
        if (isSetValueSupported() == false) {
            try {
                it.setValue(newValue1);
                fail();
            } catch (UnsupportedOperationException ex) {
            }
            return;
        }
        
        it.setValue(newValue1);
        confirmed.put(key1, newValue1);
        assertSame(key1, it.getKey());
        assertSame(newValue1, it.getValue());
        assertEquals(true, testMap.containsKey(key1));
        assertEquals(true, testMap.containsValue(newValue1));
        assertEquals(newValue1, testMap.get(key1));
        verify();
        
        it.setValue(newValue1);  // same value - should be OK
        confirmed.put(key1, newValue1);
        assertSame(key1, it.getKey());
        assertSame(newValue1, it.getValue());
        assertEquals(true, testMap.containsKey(key1));
        assertEquals(true, testMap.containsValue(newValue1));
        assertEquals(newValue1, testMap.get(key1));
        verify();
        
        Object key2 = it.next();
        it.setValue(newValue2);
        confirmed.put(key2, newValue2);
        assertSame(key2, it.getKey());
        assertSame(newValue2, it.getValue());
        assertEquals(true, testMap.containsKey(key2));
        assertEquals(true, testMap.containsValue(newValue2));
        assertEquals(newValue2, testMap.get(key2));
        verify();
    }

    //-----------------------------------------------------------------------
    // Creates a known series of Objects, puts them in 
    // an OrderedMap and ensures that all three Collection 
    // methods return in the correct order.
    public void testInsertionOrder() {
        int size = 10; // number to try
        ArrayList list = new ArrayList(size);
        for( int i=0; i<size; i++ ) {
            list.add( new Object() );
        }

        Map map = makeEmptyMap();
        for( Iterator itr = list.iterator(); itr.hasNext(); ) {
            Object obj = itr.next();
            map.put( obj, obj );
        }

        assertSameContents(map.values(), list);
        assertSameContents(map.keySet(), list);

        // check entrySet
        Set entries = map.entrySet();
        assertEquals( entries.size(), list.size() );
        Iterator i1 = entries.iterator();
        Iterator i2 = list.iterator();
        while( i1.hasNext() && i2.hasNext() ) {
            Map.Entry entry = (Map.Entry) i1.next();
            Object obj = i2.next();
            assertSame( entry.getKey(), obj );
            assertSame( entry.getValue(), obj );
        }
        assertTrue( !(i1.hasNext() && i2.hasNext()) );

    }

    private void assertSameContents(Collection c1, Collection c2) {
        assertNotNull(c1);
        assertNotNull(c2);
        assertEquals( c1.size(), c2.size() );
        Iterator i1 = c1.iterator();
        Iterator i2 = c2.iterator();
        while( i1.hasNext() && i2.hasNext() ) {
            assertSame( i1.next(), i2.next() );
        }
        // ensure they've both ended
        assertTrue( !(i1.hasNext() && i2.hasNext()) );
    }

}
