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
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections4.EnumerationUtils;

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
        expected.put("one", new Integer(1));
        expected.put("two", new Integer(2));
        expected.put("three", new Integer(3));
        // validate elements.
        final List<Integer> actualEltList = EnumerationUtils.toList(expected.elements());
        assertEquals(expected.size(), actualEltList.size());
        assertTrue(actualEltList.contains(new Integer(1)));
        assertTrue(actualEltList.contains(new Integer(2)));
        assertTrue(actualEltList.contains(new Integer(3)));
        final List<Integer> expectedEltList = new ArrayList<Integer>();
        expectedEltList.add(new Integer(1));
        expectedEltList.add(new Integer(2));
        expectedEltList.add(new Integer(3));
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

    public static Test suite() {
        return BulkTest.makeSuite(EnumerationUtilsTest.class);
    }

}
