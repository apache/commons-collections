/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestLRUMap.java,v 1.4 2002/02/13 22:32:37 morgand Exp $
 * $Revision: 1.4 $
 * $Date: 2002/02/13 22:32:37 $
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
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Id: TestLRUMap.java,v 1.4 2002/02/13 22:32:37 morgand Exp $
 */
public class TestLRUMap extends TestHashMap
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

    public Map makeMap() {
        LRUMap map = new LRUMap(10);
        return map;
    }

    public void setUp() {
        map = (HashMap) makeMap();
    }

    public void testRemoveLRU() {
        LRUMap map2 = new LRUMap(4);
        map2.put(new Integer(1),"foo");
        map2.put(new Integer(2),"foo");
        map2.put(new Integer(3),"foo");
        map2.put(new Integer(4),"foo");
        map2.removeLRU();  // should be Integer(4)

        assertTrue("Second to last value should exist",map2.get(new Integer(3)).equals("foo"));
        assertTrue("Last value inserted should not exist", map2.get(new Integer(4)) == null);
    }

    public void testMultiplePuts() {
        LRUMap map2 = new LRUMap(2);
        map2.put(new Integer(4),"foo");
        map2.put(new Integer(4),"bar");
        map2.put(new Integer(4),"foo");
        map2.put(new Integer(4),"bar");

        assertTrue("same key different value",map2.get(new Integer(4)).equals("bar"));
    }

    public void testCapacity() {
        LRUMap map2 = new LRUMap(3);
        map2.put(new Integer(1),"foo");
        map2.put(new Integer(2),"foo");
        map2.put(new Integer(3),"foo");
        map2.put(new Integer(1),"foo");
        
        assertTrue("size of Map should be 3, but was " + map2.size(), map2.size() == 3);
    }

}
