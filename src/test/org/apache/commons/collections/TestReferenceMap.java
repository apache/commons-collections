/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestReferenceMap.java,v 1.4 2003/01/07 15:18:15 rwaldhoff Exp $
 * $Revision: 1.4 $
 * $Date: 2003/01/07 15:18:15 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
import java.util.Random;

import junit.framework.Test;


/**
 * Tests for ReferenceMap. 
 *
 * @author Paul Jack
 * @version $Id: TestReferenceMap.java,v 1.4 2003/01/07 15:18:15 rwaldhoff Exp $
 */
public class TestReferenceMap extends TestMap {

    public TestReferenceMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestReferenceMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestReferenceMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        return map;
    }

    public boolean useNullKey() {
        return false;
    }

    public boolean useNullValue() {
        return false;
    }


/*
   // Unfortunately, these tests all rely on System.gc(), which is
   // not reliable across platforms.  Not sure how to code the tests
   // without using System.gc() though...
   // They all passed on my platform though. :)

    public void testPurge() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < hard.length; i++) {
            hard[i] = new Object();
            map.put(hard[i], new Object());
        }
        System.gc();
        assertTrue("map should be empty after purge of weak values", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
        }
        System.gc();
        assertTrue("map should be empty after purge of weak keys", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
            map.put(hard[i], new Object());
        }

        System.gc();
        assertTrue("map should be empty after purge of weak keys and values", map.isEmpty());
    }


    public void testGetAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        for (int i = 0; i < 10; i++) {
            map.put(new Integer(i), new Integer(i));
        }

        System.gc();
        for (int i = 0; i < 10; i++) {
            Integer I = new Integer(i);
            assertTrue("map.containsKey should return false for GC'd element", !map.containsKey(I));
            assertTrue("map.get should return null for GC'd element", map.get(I) == null);
        }
    }


    public void testEntrySetIteratorAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = new Integer(10 + i);
            map.put(new Integer(i), new Integer(i));
            map.put(hard[i], hard[i]);
        }

        System.gc();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer key = (Integer)entry.getKey();
            Integer value = (Integer)entry.getValue();
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }
*/


/*
    // Uncomment to create test files in /data/test
    public void testCreateTestFiles() throws Exception {
        ReferenceMap m = (ReferenceMap)makeEmptyMap();
        writeExternalFormToDisk(m, getCanonicalEmptyCollectionName(m));
        m = (ReferenceMap)makeFullMap();
        writeExternalFormToDisk(m, getCanonicalFullCollectionName(m));
    }
*/


    public String getCompatibilityVersion() {
        return "2.1";
    }


}
