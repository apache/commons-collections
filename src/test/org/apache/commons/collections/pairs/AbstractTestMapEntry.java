/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/pairs/Attic/AbstractTestMapEntry.java,v 1.3 2003/11/18 22:37:18 scolebourne Exp $
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
package org.apache.commons.collections.pairs;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Abstract tests that can be extended to test any Map.Entry implementation.
 * Subclasses must implement {@link #makeMapEntry(Object, Object)} to return
 * a new Map.Entry of the type being tested. Subclasses must also implement
 * {@link #testConstructors()} to test the constructors of the Map.Entry
 * type being tested.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/11/18 22:37:18 $
 * 
 * @author Neil O'Toole
 */
public abstract class AbstractTestMapEntry extends TestCase {
    
    protected final String key = "name";
    protected final String value = "duke";

    /**
     * JUnit constructor.
     * 
     * @param testName  the test name
     */
    public AbstractTestMapEntry(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Make an instance of Map.Entry with the default (null) key and value.
     * This implementation simply calls {@link #makeMapEntry(Object, Object)}
     * with null for key and value. Subclasses can override this method if desired.
     */
    public Map.Entry makeMapEntry() {
        return makeMapEntry(null, null);
    }

    /**
     * Make an instance of Map.Entry with the specified key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    public abstract Map.Entry makeMapEntry(Object key, Object value);

    /**
     * Makes a Map.Entry of a type that's known to work correctly.
     */
    public Map.Entry makeKnownMapEntry() {
        return makeKnownMapEntry(null, null);
    }

    /**
     * Makes a Map.Entry of a type that's known to work correctly.
     */
    public Map.Entry makeKnownMapEntry(Object key, Object value) {
        Map map = new HashMap(1);
        map.put(key, value);
        Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();
        return entry;
    }

    //-----------------------------------------------------------------------
    public void testAccessorsAndMutators() {
        Map.Entry entry = makeMapEntry(key, value);

        assertTrue(entry.getKey() == key);

        entry.setValue(value);
        assertTrue(entry.getValue() == value);

        // check that null doesn't do anything funny
        entry = makeMapEntry(null, null);
        assertTrue(entry.getKey() == null);

        entry.setValue(null);
        assertTrue(entry.getValue() == null);
    }

    /**
     * Subclasses should override this method to test the
     * desired behaviour of the class with respect to
     * handling of self-references.
     *
     */

    public void testSelfReferenceHandling() {
        // test that #setValue does not permit
        //  the MapEntry to contain itself (and thus cause infinite recursion
        //  in #hashCode and #toString)

        Map.Entry entry = makeMapEntry();

        try {
            entry.setValue(entry);
            fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected to happen...

            // check that the KVP's state has not changed
            assertTrue(entry.getKey() == null && entry.getValue() == null);
        }
    }

    /**
     * Subclasses should provide tests for their constructors.
     *
     */
    public abstract void testConstructors();

    public void testEqualsAndHashCode() {
        // 1. test with object data
        Map.Entry e1 = makeMapEntry(key, value);
        Map.Entry e2 = makeKnownMapEntry(key, value);

        assertTrue(e1.equals(e1));
        assertTrue(e2.equals(e1));
        assertTrue(e1.equals(e2));
        assertTrue(e1.hashCode() == e2.hashCode());

        // 2. test with nulls
        e1 = makeMapEntry();
        e2 = makeKnownMapEntry();

        assertTrue(e1.equals(e1));
        assertTrue(e2.equals(e1));
        assertTrue(e1.equals(e2));
        assertTrue(e1.hashCode() == e2.hashCode());
    }

    public void testToString() {
        Map.Entry entry = makeMapEntry(key, value);
        assertTrue(entry.toString().equals(entry.getKey() + "=" + entry.getValue()));

        // test with nulls
        entry = makeMapEntry();
        assertTrue(entry.toString().equals(entry.getKey() + "=" + entry.getValue()));
    }

}
