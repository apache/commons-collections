/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestLRUMap.java,v 1.18 2002/05/08 16:07:05 morgand Exp $
 * $Revision: 1.18 $
 * $Date: 2002/05/08 16:07:05 $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:morgand@apache.org">Morgan Delagrange</a>
 * @version $Id: TestLRUMap.java,v 1.18 2002/05/08 16:07:05 morgand Exp $
 */
public class TestLRUMap extends TestSequencedHashMap
{
    public TestLRUMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLRUMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestLRUMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        LRUMap map = new LRUMap();
        return map;
    }

    public void testRemoveLRU() {
        LRUMap map2 = new LRUMap(3);
        map2.put(new Integer(1),"foo");
        map2.put(new Integer(2),"foo");
        map2.put(new Integer(3),"foo");
        map2.put(new Integer(4),"foo"); // removes 1 since max size exceeded
        map2.removeLRU();  // should be Integer(2)

        assertTrue("Second to last value should exist",map2.get(new Integer(3)).equals("foo"));
        assertTrue("First value inserted should not exist", map2.get(new Integer(1)) == null);
    }

    public void testMultiplePuts() {
        LRUMap map2 = new LRUMap(2);
        map2.put(new Integer(1),"foo");
        map2.put(new Integer(2),"bar");
        map2.put(new Integer(3),"foo");
        map2.put(new Integer(4),"bar");

        assertTrue("last value should exist",map2.get(new Integer(4)).equals("bar"));
        assertTrue("LRU should not exist", map2.get(new Integer(1)) == null);
    }

    /**
     * Confirm that putAll(Map) does not cause the LRUMap
     * to exceed its maxiumum size.
     */
    public void testPutAll() {
        LRUMap map2 = new LRUMap(3);
        map2.put(new Integer(1),"foo");
        map2.put(new Integer(2),"foo");
        map2.put(new Integer(3),"foo");

        HashMap hashMap = new HashMap();
        hashMap.put(new Integer(4),"foo");

        map2.putAll(hashMap);

        assertTrue("max size is 3, but actual size is " + map2.size(),
                   map2.size() == 3);
        assertTrue("map should contain the Integer(4) object",
                   map2.containsKey(new Integer(4)));
    }

    /**
     * Test that the size of the map is reduced immediately
     * when setMaximumSize(int) is called
     */
    public void testSetMaximumSize() {
        LRUMap map = new LRUMap(6);
        map.put("1","1");
        map.put("2","2");
        map.put("3","3");
        map.put("4","4");
        map.put("5","5");
        map.put("6","6");
        map.setMaximumSize(3);

        assertTrue("map should have size = 3, but actually = " + map.size(), 
                   map.size() == 3);
    }

    public void testGetPromotion() {
        LRUMap map = new LRUMap(3);
        map.put("1","1");
        map.put("2","2");
        map.put("3","3");
        // LRU is now 1 (then 2 then 3)

        // promote 1 to top
        // eviction order is now 2,3,1
        map.get("1");

        // add another value, forcing a remove
        // 2 should be evicted (then 3,1,4)
        map.put("4","4");

        Iterator keyIterator = map.keySet().iterator();
        Object[] keys = new Object[3];
        for (int i = 0; keyIterator.hasNext() ; ++i) {
            keys[i] = keyIterator.next();
        }

        assertTrue("first evicted should be 3, was " + keys[0], keys[0].equals("3"));
        assertTrue("second evicted should be 1, was " + keys[1], keys[1].equals("1"));
        assertTrue("third evicted should be 4, was " + keys[2], keys[2].equals("4"));
    }

    /**
     * You should be able to subclass LRUMap and perform a 
     * custom action when items are removed automatically
     * by the LRU algorithm (the removeLRU() method).
     */
    public void testLRUSubclass() {
        LRUCounter counter = new LRUCounter(3);
        // oldest <--> newest
        // 1
        counter.put("1","foo");
        // 1 2
        counter.put("2","foo");
        // 1 2 3
        counter.put("3","foo");
        // 2 3 1
        counter.put("1","foo");
        // 3 1 4 (2 goes out)
        counter.put("4","foo");
        // 1 4 5 (3 goes out)
        counter.put("5","foo");
        // 4 5 2 (1 goes out)
        counter.put("2","foo");
        // 4 2
        counter.remove("5");

        assertTrue("size should be 2, but was " + counter.size(), counter.size() == 2);
        assertTrue("removedCount should be 3 but was " + counter.removedCount,
                   counter.removedCount == 3);

        assertTrue("first removed was '2'",counter.list.get(0).equals("2"));
        assertTrue("second removed was '3'",counter.list.get(1).equals("3"));
        assertTrue("third removed was '1'",counter.list.get(2).equals("1"));

        assertTrue("oldest key is '4'",counter.get(0).equals("4"));
        assertTrue("newest key is '2'",counter.get(1).equals("2"));
    }

    private class LRUCounter extends LRUMap {
        int removedCount = 0;
        ArrayList list = new ArrayList(3); 

        LRUCounter(int i) {
            super(i);
        }

        protected void processRemovedLRU(Object key, Object value) {
            ++removedCount;
            list.add(key);
        }
    }

}
