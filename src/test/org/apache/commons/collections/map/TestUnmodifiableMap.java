/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/TestUnmodifiableMap.java,v 1.3 2003/11/18 22:37:17 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link AbstractTestMap} for exercising the 
 * {@link UnmodifiableMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/11/18 22:37:17 $
 * 
 * @author Phil Steitz
 */
public class TestUnmodifiableMap extends AbstractTestMap{
    
    public TestUnmodifiableMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestUnmodifiableMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestUnmodifiableMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //-------------------------------------------------------------------
    
    public Map makeEmptyMap() {
        return UnmodifiableMap.decorate(new HashMap());
    }
    
    public boolean isPutChangeSupported() {
        return false;
    }
    
    public boolean isPutAddSupported() {
        return false;
    }
    
    public boolean isRemoveSupported() {
        return false;
    }
    
    public Map makeFullMap() {
        Map m = new HashMap();
        addSampleMappings(m);
        return UnmodifiableMap.decorate(m);
    }
    
    //--------------------------------------------------------------------
    protected UnmodifiableMap map = null;
    protected ArrayList array = null;
    
    protected void setupMap() {
        map = (UnmodifiableMap) makeFullMap();
        array = new ArrayList();
        array.add("one");
    }
    
    public void testUnmodifiableBase() {
        setupMap();
        try {
            map.put("key", "value");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.putAll(new HashMap());
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.clear();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            map.remove("x");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    
    /**
     * Verifies that the keyset is not modifiable -- effectively tests
     * protection of UnmodifiableSet decorator
     */
    public void testUnmodifiableKeySet() {
        setupMap();
        Set keys = map.keySet();
        try {
            keys.add("x");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keys.addAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keys.clear();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keys.remove("one");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keys.removeAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keys.retainAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            Iterator iterator = keys.iterator();
            iterator.next();
            iterator.remove();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
        
    /**
     * Verifies that the values collection is not modifiable -- effectively tests
     * protection of UnmodifiableCollection decorator
     */
    public void testUnmodifiableValues() {
        setupMap();
        Collection values = map.values();
        try {
            values.add("x");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            values.addAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            values.clear();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            values.remove("one");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            values.removeAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            values.retainAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            Iterator iterator = values.iterator();
            iterator.next();
            iterator.remove();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    
    /**
     * Verifies that the entrySet is not modifiable -- effectively tests
     * protection of UnmodifiableEntrySet 
     */
    public void testUnmodifiableEntries() {
        setupMap();
        Set entries = map.entrySet();
        try {
            entries.add("x");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            entries.addAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            entries.clear();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            entries.remove("one");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            entries.removeAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            entries.retainAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            Iterator iterator = entries.iterator();
            iterator.next();
            iterator.remove();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        
        try {
            Iterator iterator = entries.iterator();
            Map.Entry entry = (Map.Entry) iterator.next();
            entry.setValue("x");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }                 
    }
    
    /**
     * Verifies that entries consists of Map.Entries corresponding to the parallel
     * keys and values arrays  (not necessarily in order)
     */
    protected void checkEntries(Object[] keys, Object[] values, Object[] entries,
        boolean checkLengths) {
        if (checkLengths) {
            assertEquals(keys.length, entries.length);
        }
        for (int i = 0; i < keys.length; i++) {
            Map.Entry entry = (Map.Entry) entries[i];
            boolean found = false;
            // Can't assume entries are in insertion order, so have
            // to search for the key
            for (int j = 0; j < keys.length; j++) {
                if (entry.getKey() == keys[j]) {
                    found = true;
                    assertEquals(entry.getValue(), values[j]);
                    break;
                }
            }
            assertTrue(found);
        }
    }
    
    /**
     * Tests EntrySet toArray() implementation
     */
    public void testToArray() {
        setupMap();
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Object[] entries = map.entrySet().toArray();
        assertTrue(keys.length == entries.length);
        checkEntries(keys, values, entries, true);
        entries = map.entrySet().toArray(entries);
        checkEntries(keys, values, entries, true);
        Object[] testArray = new Object[2];
        entries = map.entrySet().toArray(testArray);
        checkEntries(keys, values, entries, true);
        testArray = new Object[50];
        entries = map.entrySet().toArray(testArray);
        checkEntries(keys, values, entries, false);
        assertEquals(testArray[map.size()], null);
        testArray = new Object[0];
        Object[] resultArray = new Object[0];
        resultArray = map.entrySet().toArray(testArray);
        checkEntries(keys, values, resultArray, true);   
    }         
}