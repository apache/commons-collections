/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestArrayCharList.java,v 1.2 2003/05/05 23:25:21 rwaldhoff Exp $
 * ====================================================================
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

package org.apache.commons.collections.primitives;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.BulkTest;

/**
 * @version $Revision: 1.2 $ $Date: 2003/05/05 23:25:21 $
 * @author Rodney Waldhoff
 */
public class TestArrayCharList extends TestCharList {

    // conventional
    // ------------------------------------------------------------------------

    public TestArrayCharList(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = BulkTest.makeSuite(TestArrayCharList.class);
        return suite;
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    protected CharList makeEmptyCharList() {
        return new ArrayCharList();
    }

    public String[] ignoredSimpleTests() {
        // sublists are not serializable
        return new String[] { 
            "TestArrayCharList.bulkTestSubList.testFullListSerialization",
            "TestArrayCharList.bulkTestSubList.testEmptyListSerialization",
            "TestArrayCharList.bulkTestSubList.testCanonicalEmptyCollectionExists",
            "TestArrayCharList.bulkTestSubList.testCanonicalFullCollectionExists",
            "TestArrayCharList.bulkTestSubList.testEmptyListCompatibility",
            "TestArrayCharList.bulkTestSubList.testFullListCompatibility",
            "TestArrayCharList.bulkTestSubList.testSerializeDeserializeThenCompare",
            "TestArrayCharList.bulkTestSubList.testSimpleSerialization"
        };
    }

    // tests
    // ------------------------------------------------------------------------

    /** @TODO need to add serialized form to cvs */
    public void testCanonicalEmptyCollectionExists() {
        // XXX FIX ME XXX
        // need to add a serialized form to cvs
    }

    public void testCanonicalFullCollectionExists() {
        // XXX FIX ME XXX
        // need to add a serialized form to cvs
    }

    public void testEmptyListCompatibility() {
        // XXX FIX ME XXX
        // need to add a serialized form to cvs
    }

    public void testFullListCompatibility() {
        // XXX FIX ME XXX
        // need to add a serialized form to cvs
    }

    public void testAddGetLargeValues() {
        CharList list = new ArrayCharList();
        for (int i = 0; i < 1000; i++) {
            char value = ((char) (Character.MAX_VALUE));
            value -= i;
            list.add(value);
        }
        for (int i = 0; i < 1000; i++) {
            char value = ((char) (Character.MAX_VALUE));
            value -= i;
            assertEquals(value, list.get(i));
        }
    }

    public void testZeroInitialCapacityIsValid() {
        assertNotNull(new ArrayCharList(0));
    }

    public void testNegativeInitialCapacityIsInvalid() {
        try {
            new ArrayCharList(-1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testCopyConstructor() {
        ArrayCharList expected = new ArrayCharList();
        for(int i=0;i<10;i++) {
            expected.add((char)i);
        }
        ArrayCharList list = new ArrayCharList(expected);
        assertEquals(10,list.size());
        assertEquals(expected,list);
    }

    public void testCopyConstructorWithNull() {
        try {
            new ArrayCharList(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }


    public void testTrimToSize() {
        ArrayCharList list = new ArrayCharList();
        for(int j=0;j<3;j++) {
            assertTrue(list.isEmpty());
    
            list.trimToSize();
    
            assertTrue(list.isEmpty());
            
            for(int i=0;i<10;i++) {
                list.add((char)i);
            }
            
            for(int i=0;i<10;i++) {
                assertEquals((char)i,list.get(i), 0f);
            }
            
            list.trimToSize();
    
            for(int i=0;i<10;i++) {
                assertEquals((char)i,list.get(i), 0f);
            }
    
            for(int i=0;i<10;i+=2) {
                list.removeElement((char)i);
            }
            
            for(int i=0;i<5;i++) {
                assertEquals((char)(2*i)+1,list.get(i), 0f);
            }
    
            list.trimToSize();
                    
            for(int i=0;i<5;i++) {
                assertEquals((char)(2*i)+1,list.get(i), 0f);
            }

            list.trimToSize();
                    
            for(int i=0;i<5;i++) {
                assertEquals((char)(2*i)+1,list.get(i), 0f);
            }
    
            list.clear();
        }
    }

}
