/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestBidiMap.java,v 1.7 2003/10/09 20:21:32 scolebourne Exp $
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
package org.apache.commons.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * JUnit tests.
 * 
 * @version $Revision: 1.7 $ $Date: 2003/10/09 20:21:32 $
 * 
 * @author Matthew Hawthorne
 */
public abstract class TestBidiMap extends AbstractTestMap {

    // Test data.
    private static final Object KEY = "key1";
    private static final Object VALUE = "value1";

    private static final Object[][] entriesKV =
        new Object[][] {
            new Object[] { KEY, VALUE },
            new Object[] { "key2", "value2" },
            new Object[] { "key3", "value3" }
    };
    private static final Object[][] entriesVK =
        new Object[][] {
            new Object[] { VALUE, KEY },
            new Object[] { "value2", "key2" },
            new Object[] { "value3", "key3" }
    };
    private final Object[][] entries;

    public TestBidiMap(String testName) {
        super(testName);
        entries = entriesKV;
    }

    public TestBidiMap() {
        super("Inverse");
        entries = entriesVK;
    }

    //-----------------------------------------------------------------------
    /**
     * Implement to create an empty <code>BidiMap</code>.
     * 
     * @return an empty <code>BidiMap</code> implementation.
     */
    protected abstract BidiMap makeEmptyBidiMap();

    /**
     * Override to create a full <code>BidiMap</code> other than the default.
     * 
     * @return a full <code>BidiMap</code> implementation.
     */
    protected BidiMap makeFullBidiMap() {
        final BidiMap map = makeEmptyBidiMap();
        for (int i = 0; i < entries.length; i++) {
            map.put(entries[i][0], entries[i][1]);
        }
        return map;
    }

    /**
     * Override to return the empty BidiMap.
     */
    protected final  Map makeEmptyMap() {
        return makeEmptyBidiMap();
    }

    /**
     * Override to indicate to AbstractTestMap this is a BidiMap.
     */
    protected boolean isAllowDuplicateValues() {
        return false;
    }
    
    /**
     * Override as DualHashBidiMap didn't exist until version 3.
     */
    protected String getCompatibilityVersion() {
        return "3";
    }

    // BidiPut
    //-----------------------------------------------------------------------
    public void testBidiPut() {
        BidiMap map = makeEmptyBidiMap();
        BidiMap inverse = map.inverseBidiMap();
        assertEquals(0, map.size());
        assertEquals(map.size(), inverse.size());
        
        map.put("A", "B");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("B", map.get("A"));
        assertEquals("A", inverse.get("B"));
        
        map.put("A", "C");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("C", map.get("A"));
        assertEquals("A", inverse.get("C"));
        
        map.put("B", "C");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("C", map.get("B"));
        assertEquals("B", inverse.get("C"));
        
        map.put("E", "F");
        assertEquals(2, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("F", map.get("E"));
        assertEquals("E", inverse.get("F"));
    }

    /**
     * Verifies that {@link #map} is still equal to {@link #confirmed}.
     * <p>
     * This implementation checks the inverse map as well.
     */
    protected void verify() {
        // verify inverse
        assertEquals(map.size(), ((BidiMap) map).inverseBidiMap().size());
        
        // verify fully
        super.verify();
    }
    
    // testGetKey
    //-----------------------------------------------------------------------
    public void testBidiGetKey() {
        doTestGetKey(makeFullBidiMap(), entries[0][0], entries[0][1]);
    }

    public void testBidiGetKeyInverse() {
        doTestGetKey(
            makeFullBidiMap().inverseBidiMap(),
            entries[0][1],
            entries[0][0]);
    }

    private final void doTestGetKey(BidiMap map, Object key, Object value) {
        assertEquals("Value not found for key.", value, map.get(key));
        assertEquals("Key not found for value.", key, map.getKey(value));
    }

    // testInverse
    //-----------------------------------------------------------------------
    public void testBidiInverse() {
        final BidiMap map = makeFullBidiMap();
        final BidiMap inverseMap = map.inverseBidiMap();

        assertSame(
            "Inverse of inverse is not equal to original.",
            map,
            inverseMap.inverseBidiMap());

        assertEquals(
            "Value not found for key.",
            entries[0][0],
            inverseMap.get(entries[0][1]));

        assertEquals(
            "Key not found for value.",
            entries[0][1],
            inverseMap.getKey(entries[0][0]));
    }

    //-----------------------------------------------------------------------
    public void testBidiModifyEntrySet() {
        modifyEntrySet(makeFullBidiMap());
        modifyEntrySet(makeFullBidiMap().inverseBidiMap());
    }

    private final void modifyEntrySet(BidiMap map) {
        // Gets first entry
        final Map.Entry entry = (Map.Entry)map.entrySet().iterator().next();

        // Gets key and value
        final Object key = entry.getKey();
        final Object oldValue = entry.getValue();

        // Sets new value
        final Object newValue = "newValue";
        entry.setValue(newValue);

        assertEquals(
            "Modifying entrySet did not affect underlying Map.",
            newValue,
            map.get(key));

        assertNull(
            "Modifying entrySet did not affect inverse Map.",
            map.getKey(oldValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiClear() {
        BidiMap map = makeFullBidiMap();
        map.clear();
        assertTrue("Map was not cleared.", map.isEmpty());
        assertTrue("Inverse map was not cleared.", map.inverseBidiMap().isEmpty());

        // Tests clear on inverse
        map = makeFullBidiMap().inverseBidiMap();
        map.clear();
        assertTrue("Map was not cleared.", map.isEmpty());
        assertTrue("Inverse map was not cleared.", map.inverseBidiMap().isEmpty());

    }

    //-----------------------------------------------------------------------
    public void testBidiRemove() {
        remove(makeFullBidiMap(), KEY);
        remove(makeFullBidiMap().inverseBidiMap(), VALUE);

        removeKey(makeFullBidiMap(), VALUE);
        removeKey(makeFullBidiMap().inverseBidiMap(), KEY);
    }

    private final void remove(BidiMap map, Object key) {
        final Object value = map.remove(key);
        assertTrue("Key was not removed.", !map.containsKey(key));
        assertNull("Value was not removed.", map.getKey(value));
    }

    private final void removeKey(BidiMap map, Object value) {
        final Object key = map.removeKey(value);
        assertTrue("Key was not removed.", !map.containsKey(key));
        assertNull("Value was not removed.", map.getKey(value));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByKeySet() {
        removeByKeySet(makeFullBidiMap(), KEY, VALUE);
        removeByKeySet(makeFullBidiMap().inverseBidiMap(), VALUE, KEY);
    }

    private final void removeByKeySet(BidiMap map, Object key, Object value) {
        map.keySet().remove(key);

        assertTrue("Key was not removed.", !map.containsKey(key));
        assertTrue("Value was not removed.", !map.containsValue(value));

        assertTrue(
            "Key was not removed from inverse map.",
            !map.inverseBidiMap().containsValue(key));
        assertTrue(
            "Value was not removed from inverse map.",
            !map.inverseBidiMap().containsKey(value));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByEntrySet() {
        removeByEntrySet(makeFullBidiMap(), KEY, VALUE);
        removeByEntrySet(makeFullBidiMap().inverseBidiMap(), VALUE, KEY);
    }

    private final void removeByEntrySet(BidiMap map, Object key, Object value) {
        Map temp = new HashMap();
        temp.put(key, value);
        map.entrySet().remove(temp.entrySet().iterator().next());

        assertTrue("Key was not removed.", !map.containsKey(key));
        assertTrue("Value was not removed.", !map.containsValue(value));

        assertTrue(
            "Key was not removed from inverse map.",
            !map.inverseBidiMap().containsValue(key));
        assertTrue(
            "Value was not removed from inverse map.",
            !map.inverseBidiMap().containsKey(value));
    }

    public BulkTest bulkTestInverseMap() {
        return new TestInverseBidiMap(this);
    }

    class TestInverseBidiMap extends TestBidiMap {
        final TestBidiMap main;
        
        public TestInverseBidiMap(TestBidiMap main) {
            super();
            this.main = main;
        }
        protected BidiMap makeEmptyBidiMap() {
            return main.makeEmptyBidiMap().inverseBidiMap();
        }
        protected BidiMap makeFullBidiMap() {
            return main.makeFullBidiMap().inverseBidiMap();
        }
        
        protected String getCompatibilityVersion() {
            return main.getCompatibilityVersion();
        }
        protected boolean isAllowNullKey() {
            return main.isAllowNullKey();
        }
        protected boolean isAllowNullValue() {
            return main.isAllowNullValue();
        }
        protected boolean isPutAddSupported() {
            return main.isPutAddSupported();
        }
        protected boolean isPutChangeSupported() {
            return main.isPutChangeSupported();
        }
        protected boolean isRemoveSupported() {
            return main.isRemoveSupported();
        }

    }
    
}
