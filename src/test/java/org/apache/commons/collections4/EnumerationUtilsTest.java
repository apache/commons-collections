/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.Test;

/**
 * Tests EnumerationUtils.
 *
 * @version $Id$
 */
public class EnumerationUtilsTest extends BulkTest {

    public EnumerationUtilsTest(final String name) {
        super(name);
    }

    public static final String TO_LIST_FIXTURE = "this is a test";

    /**
     * List of {@link Integer}s
     */
    private List<Integer> collectionA = null;

    @Override
    public void setUp() {
        collectionA = new ArrayList<Integer>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
    }

    public void testToListWithStringTokenizer() {
        final List<String> expectedList1 = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(TO_LIST_FIXTURE);
             while (st.hasMoreTokens()) {
                 expectedList1.add(st.nextToken());
             }
        final List<String> expectedList2 = new ArrayList<String>();
        expectedList2.add("this");
        expectedList2.add("is");
        expectedList2.add("a");
        expectedList2.add("test");
        final List<String> actualList = EnumerationUtils.toList(new StringTokenizer(TO_LIST_FIXTURE));
        assertEquals(expectedList1, expectedList2);
        assertEquals(expectedList1, actualList);
        assertEquals(expectedList2, actualList);
    }

    public void testToListWithHashtable() {
        final Hashtable<String, Integer> expected = new Hashtable<String, Integer>();
        expected.put("one", Integer.valueOf(1));
        expected.put("two", Integer.valueOf(2));
        expected.put("three", Integer.valueOf(3));
        // validate elements.
        final List<Integer> actualEltList = EnumerationUtils.toList(expected.elements());
        assertEquals(expected.size(), actualEltList.size());
        assertTrue(actualEltList.contains(Integer.valueOf(1)));
        assertTrue(actualEltList.contains(Integer.valueOf(2)));
        assertTrue(actualEltList.contains(Integer.valueOf(3)));
        final List<Integer> expectedEltList = new ArrayList<Integer>();
        expectedEltList.add(Integer.valueOf(1));
        expectedEltList.add(Integer.valueOf(2));
        expectedEltList.add(Integer.valueOf(3));
        assertTrue(actualEltList.containsAll(expectedEltList));

        // validate keys.
        final List<String> actualKeyList = EnumerationUtils.toList(expected.keys());
        assertEquals(expected.size(), actualEltList.size());
        assertTrue(actualKeyList.contains("one"));
        assertTrue(actualKeyList.contains("two"));
        assertTrue(actualKeyList.contains("three"));
        final List<String> expectedKeyList = new ArrayList<String>();
        expectedKeyList.add("one");
        expectedKeyList.add("two");
        expectedKeyList.add("three");
        assertTrue(actualKeyList.containsAll(expectedKeyList));
    }

    public void testGetEnumeration() {
        final Vector<Integer> vectorA = new Vector<Integer>(collectionA);
        final Enumeration<Integer> e = vectorA.elements();
        assertEquals(Integer.valueOf(2), EnumerationUtils.get(e, 2));
        assertTrue(e.hasMoreElements());
        assertEquals(Integer.valueOf(4), EnumerationUtils.get(e, 6));
        assertFalse(e.hasMoreElements());
    }

    public void testGetFromEnumeration() throws Exception {
        // Enumeration, entry exists
        final Vector<String> vector = new Vector<String>();
        vector.addElement("zero");
        vector.addElement("one");
        Enumeration<String> en = vector.elements();
        assertEquals("zero", EnumerationUtils.get(en, 0));
        en = vector.elements();
        assertEquals("one", EnumerationUtils.get(en, 1));

        // Enumerator, non-existent entry
        try {
            EnumerationUtils.get(en, 3);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (final IndexOutOfBoundsException e) {
            // expected
        }
        assertTrue(!en.hasMoreElements());
    }

    public static Test suite() {
        return BulkTest.makeSuite(EnumerationUtilsTest.class);
    }

}
