/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestBidiMap.java,v 1.2 2003/09/26 23:28:43 matth Exp $
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

import java.util.Map;

import junit.framework.TestCase;

/**
 * JUnit tests.
 * 
 * @author Matthew Hawthorne
 * @version $Id: TestBidiMap.java,v 1.2 2003/09/26 23:28:43 matth Exp $
 * @see org.apache.commons.collections.BidiMap
 */
public abstract class TestBidiMap extends TestCase {

    // Test data.
    private static final Object KEY = "key1";
    private static final Object VALUE = "value1";

    private static final Object[][] entries =
        new Object[][] {
            new Object[] { KEY, VALUE },
            new Object[] { "key2", "value2" },
            new Object[] { "key3", "value3" }
    };

    public TestBidiMap(String testName) {
        super(testName);
    }

    /**
     * Creates an empty <code>BidiMap</code> implementation.
     * 
     * @return an empty <code>BidiMap</code> implementation.
     */
    protected abstract BidiMap createBidiMap();

    // testGetKey

    public void testGetKey() {
        testGetKey(createBidiMapWithData(), entries[0][0], entries[0][1]);
    }

    public void testGetKeyInverse() {
        testGetKey(
            createBidiMapWithData().inverseBidiMap(),
            entries[0][1],
            entries[0][0]);
    }

    private final void testGetKey(BidiMap map, Object key, Object value) {
        assertEquals("Value not found for key.", value, map.get(key));
        assertEquals("Key not found for value.", key, map.getKey(value));
    }

    // testInverse

    public void testInverse() {
        final BidiMap map = createBidiMapWithData();
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

    /**
     * Ensures that calling:
     * 
     * <pre>
     * map.add(a, c)
     * map.add(b, c)
     * </pre>
     * 
     * Removes the entry (a, c)
     */
    public void testAddDuplicateValue() {
        final BidiMap map = createBidiMap();

        final Object key1 = "key1";
        final Object key2 = "key2";
        final Object value = "value";

        map.put(key1, value);
        map.put(key2, value);

        assertTrue(
            "Key/value pair was not removed on duplicate value.",
            !map.containsKey(key1));
            
        assertEquals("Key/value mismatch", key2, map.getKey(value));
    }

    // ----------------------------------------------------------------
    // Removal tests
    // ----------------------------------------------------------------

    public void testClear() {
        BidiMap map = createBidiMapWithData();
        map.clear();
        assertTrue("Map was not cleared.", map.isEmpty());
        assertTrue(
            "Inverse map was not cleared.",
            map.inverseBidiMap().isEmpty());

        // Tests clear on inverse
        map = createBidiMapWithData().inverseBidiMap();
        map.clear();
        assertTrue("Map was not cleared.", map.isEmpty());
        assertTrue(
            "Inverse map was not cleared.",
            map.inverseBidiMap().isEmpty());

    }

    public void testRemove() {
        remove(createBidiMapWithData(), KEY);
        remove(createBidiMapWithData().inverseBidiMap(), VALUE);

        removeKey(createBidiMapWithData(), VALUE);
        removeKey(createBidiMapWithData().inverseBidiMap(), KEY);
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

    public void testRemoveByKeySet() {
        removeByKeySet(createBidiMapWithData(), KEY, VALUE);
        removeByKeySet(createBidiMapWithData().inverseBidiMap(), VALUE, KEY);
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

    public void testRemoveByEntrySet() {
        removeByEntrySet(createBidiMapWithData(), KEY, VALUE);
        removeByEntrySet(createBidiMapWithData().inverseBidiMap(), VALUE, KEY);
    }

    private final void removeByEntrySet(
        BidiMap map,
        Object key,
        Object value) {
        map.entrySet().remove(new DefaultMapEntry(key, value));

        assertTrue("Key was not removed.", !map.containsKey(key));
        assertTrue("Value was not removed.", !map.containsValue(value));

        assertTrue(
            "Key was not removed from inverse map.",
            !map.inverseBidiMap().containsValue(key));
        assertTrue(
            "Value was not removed from inverse map.",
            !map.inverseBidiMap().containsKey(value));
    }

    // ----------------------------------------------------------------
    // Data generation methods
    // ----------------------------------------------------------------

    /**
     * This classes used to extend collections.TestMap, but can't anymore since 
     * put() breaks a contract.
     */
    protected Map makeEmptyMap() {
        return createBidiMap();
    }

    protected final BidiMap createBidiMapWithData() {
        final BidiMap map = createBidiMap();
        fillMap(map);
        return map;
    }

    private static final void fillMap(BidiMap map) {
        for (int i = 0; i < entries.length; i++) {
            map.put(entries[i][0], entries[i][1]);
        }
    }

} // TestBidiMap
