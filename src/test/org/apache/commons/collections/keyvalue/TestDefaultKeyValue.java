/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/keyvalue/TestDefaultKeyValue.java,v 1.1 2003/12/05 20:23:57 scolebourne Exp $
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
package org.apache.commons.collections.keyvalue;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test the DefaultKeyValue class.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/05 20:23:57 $
 * 
 * @author Neil O'Toole
 */
public class TestDefaultKeyValue extends TestCase {
    
    private final String key = "name";
    private final String value = "duke";

    /**
     * JUnit constructor.
     * 
     * @param testName  the test name
     */
    public TestDefaultKeyValue(String testName) {
        super(testName);

    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestDefaultKeyValue.class);
    }

    public static Test suite() {
        return new TestSuite(TestDefaultKeyValue.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Make an instance of DefaultKeyValue with the default (null) key and value.
     * Subclasses should override this method to return a DefaultKeyValue
     * of the type being tested.
     */
    protected DefaultKeyValue makeDefaultKeyValue() {
        return new DefaultKeyValue(null, null);
    }

    /**
     * Make an instance of DefaultKeyValue with the specified key and value.
     * Subclasses should override this method to return a DefaultKeyValue
     * of the type being tested.
     */
    protected DefaultKeyValue makeDefaultKeyValue(Object key, Object value) {
        return new DefaultKeyValue(key, value);
    }

    //-----------------------------------------------------------------------
    public void testAccessorsAndMutators() {
        DefaultKeyValue kv = makeDefaultKeyValue();

        kv.setKey(key);
        assertTrue(kv.getKey() == key);

        kv.setValue(value);
        assertTrue(kv.getValue() == value);

        // check that null doesn't do anything funny
        kv.setKey(null);
        assertTrue(kv.getKey() == null);

        kv.setValue(null);
        assertTrue(kv.getValue() == null);

    }

    public void testSelfReferenceHandling() {
        // test that #setKey and #setValue do not permit
        //  the KVP to contain itself (and thus cause infinite recursion
        //  in #hashCode and #toString)

        DefaultKeyValue kv = makeDefaultKeyValue();

        try {
            kv.setKey(kv);
            fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected to happen...

            // check that the KVP's state has not changed
            assertTrue(kv.getKey() == null && kv.getValue() == null);
        }

        try {
            kv.setValue(kv);
            fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected to happen...

            // check that the KVP's state has not changed
            assertTrue(kv.getKey() == null && kv.getValue() == null);
        }
    }

    /**
     * Subclasses should override this method to test their own constructors.
     */
    public void testConstructors() {
        // 1. test default constructor
        DefaultKeyValue kv = new DefaultKeyValue();
        assertTrue(kv.getKey() == null && kv.getValue() == null);

        // 2. test key-value constructor
        kv = new DefaultKeyValue(key, value);
        assertTrue(kv.getKey() == key && kv.getValue() == value);

        // 3. test copy constructor
        DefaultKeyValue kv2 = new DefaultKeyValue(kv);
        assertTrue(kv2.getKey() == key && kv2.getValue() == value);

        // test that the KVPs are independent
        kv.setKey(null);
        kv.setValue(null);

        assertTrue(kv2.getKey() == key && kv2.getValue() == value);

        // 4. test Map.Entry constructor
        Map map = new HashMap();
        map.put(key, value);
        Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();

        kv = new DefaultKeyValue(entry);
        assertTrue(kv.getKey() == key && kv.getValue() == value);

        // test that the KVP is independent of the Map.Entry
        entry.setValue(null);
        assertTrue(kv.getValue() == value);

    }

    public void testEqualsAndHashCode() {
        // 1. test with object data
        DefaultKeyValue kv = makeDefaultKeyValue(key, value);
        DefaultKeyValue kv2 = makeDefaultKeyValue(key, value);

        assertTrue(kv.equals(kv));
        assertTrue(kv.equals(kv2));
        assertTrue(kv.hashCode() == kv2.hashCode());

        // 2. test with nulls
        kv = makeDefaultKeyValue(null, null);
        kv2 = makeDefaultKeyValue(null, null);

        assertTrue(kv.equals(kv));
        assertTrue(kv.equals(kv2));
        assertTrue(kv.hashCode() == kv2.hashCode());
    }

    public void testToString() {
        DefaultKeyValue kv = makeDefaultKeyValue(key, value);
        assertTrue(kv.toString().equals(kv.getKey() + "=" + kv.getValue()));

        // test with nulls
        kv = makeDefaultKeyValue(null, null);
        assertTrue(kv.toString().equals(kv.getKey() + "=" + kv.getValue()));
    }

    public void testToMapEntry() {
        DefaultKeyValue kv = makeDefaultKeyValue(key, value);

        Map map = new HashMap();
        map.put(kv.getKey(), kv.getValue());
        Map.Entry entry = (Map.Entry) map.entrySet().iterator().next();

        assertTrue(entry.equals(kv.toMapEntry()));
        assertTrue(entry.hashCode() == kv.hashCode());
    }

}
