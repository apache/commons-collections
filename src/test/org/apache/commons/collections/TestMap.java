/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestMap.java,v 1.4 2002/02/20 22:38:46 morgand Exp $
 * $Revision: 1.4 $
 * $Date: 2002/02/20 22:38:46 $
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

import junit.framework.*;
import java.util.Map;
import java.util.Collection;

/**
 * Tests base {@link java.util.Map} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeMap} method.
 * <p>
 * If your {@link Map} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Map} fails.
 *
 * @author Rodney Waldhoff
 * @version $Id: TestMap.java,v 1.4 2002/02/20 22:38:46 morgand Exp $
 */
public abstract class TestMap extends TestObject {
    public TestMap(String testName) {
        super(testName);
    }

    /**
     * Return a new, empty {@link Map} to used for testing.
     */
    public abstract Map makeMap();

    public Object makeObject() {
        return makeMap();
    }

        /**
     * Try to put the given pair into the given Collection.
     *
     * Fails any Throwable except UnsupportedOperationException,
     * ClassCastException, or IllegalArgumentException
     * or NullPointerException is thrown.
     */
    protected Object tryToPut(Map map, Object key, Object val) {
        try {
            return map.put(key,val);
        } catch(UnsupportedOperationException e) {
            return null;
        } catch(ClassCastException e) {
            return null;
        } catch(IllegalArgumentException e) {
            return null;
        } catch(NullPointerException e) {
            return null;
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Map.put should only throw UnsupportedOperationException, ClassCastException, IllegalArgumentException or NullPointerException. Found " + t.toString());
            return null; // never get here, since fail throws exception
        }
    }

    /*

    public void testMapContainsKey() {
        // XXX finish me
    }

    public void testMapContainsValue() {
        // XXX finish me
    }

    public void testMapEntrySet() {
        // XXX finish me
    }

    public void testMapEquals() {
        // XXX finish me
    }

    public void testMapGet() {
        // XXX finish me
    }

    public void testMapHashCode() {
        // XXX finish me
    }

    public void testMapIsEmpty() {
        // XXX finish me
    }

    public void testMapKeySet() {
        // XXX finish me
    }
    
    */
    
    //-------TEST AGAINST OPTIONAL OPERATIONS, ENABLE IN TEST SUBCLASSES

    public void testMapSupportsNullValues() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeMap();
        map.put(new Integer(1),"foo");
        
        assertTrue("no null values in Map",map.containsValue(null) == false);

        map.put(new Integer(2),null);

        assertTrue("null value in Map",map.containsValue(null));
        assertTrue("key to a null value",map.containsKey(new Integer(2)));
    }

    public void testMultiplePuts() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeMap();
        map.put(new Integer(4),"foo");
        map.put(new Integer(4),"bar");
        map.put(new Integer(4),"foo");
        map.put(new Integer(4),"bar");

        assertTrue("same key different value",map.get(new Integer(4)).equals("bar"));
    }


    public void testCapacity() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeMap();
        map.put(new Integer(1),"foo");
        map.put(new Integer(2),"foo");
        map.put(new Integer(3),"foo");
        map.put(new Integer(1),"foo");
        
        assertTrue("size of Map should be 3, but was " + map.size(), map.size() == 3);
    }

    /*
        // optional operation
public void testMapClear() {
    // XXX finish me
}

    // optional operation
    public void testMapPut() {
        // XXX finish me
    }

    // optional operation
    public void testMapPutAll() {
        // XXX finish me
    }

    // optional operation
    public void testMapRemove() {
        // XXX finish me
    }

    public void testMapSize() {
        // XXX finish me
    }

    public void testMapValues() {
        // XXX finish me
    }

    */

    /**
     * Marker interface, indicating that a TestMap subclass
     * can test put(Object,Object) operations.
     */
    public interface SupportsPut {

    }

}
