/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestArrayUnsignedIntList.java,v 1.4 2003/10/05 20:48:59 scolebourne Exp $
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

package org.apache.commons.collections.primitives;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.BulkTest;

/**
 * @version $Revision: 1.4 $ $Date: 2003/10/05 20:48:59 $
 * @author Rodney Waldhoff
 */
public class TestArrayUnsignedIntList extends TestLongList {

    // conventional
    // ------------------------------------------------------------------------

    public TestArrayUnsignedIntList(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = BulkTest.makeSuite(TestArrayUnsignedIntList.class);
        return suite;
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    protected LongList makeEmptyLongList() {
        return new ArrayUnsignedIntList();
    }

    protected String[] ignoredTests() {
        // sublists are not serializable
        return new String[] { 
            "TestArrayUnsignedLongList.bulkTestSubList.testFullListSerialization",
            "TestArrayUnsignedLongList.bulkTestSubList.testEmptyListSerialization",
            "TestArrayUnsignedLongList.bulkTestSubList.testCanonicalEmptyCollectionExists",
            "TestArrayUnsignedLongList.bulkTestSubList.testCanonicalFullCollectionExists",
            "TestArrayUnsignedLongList.bulkTestSubList.testEmptyListCompatibility",
            "TestArrayUnsignedLongList.bulkTestSubList.testFullListCompatibility",
            "TestArrayUnsignedLongList.bulkTestSubList.testSerializeDeserializeThenCompare",
            "TestArrayUnsignedLongList.bulkTestSubList.testSimpleSerialization"
        };
    }

    protected long[] getFullLongs() {
        long[] result = new long[19];
        for(int i = 0; i < result.length; i++) {
            result[i] = ((long)Integer.MAX_VALUE - 1L - (long)i);
        }
        return result;
    }

    // tests
    // ------------------------------------------------------------------------

    // @TODO need to add serialized form to cvs
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

    public void testZeroInitialCapacityIsValid() {
        assertNotNull(new ArrayUnsignedIntList(0));
    }
    
    public void testIllegalArgumentExceptionWhenElementOutOfRange() {
        ArrayUnsignedIntList list = new ArrayUnsignedIntList();
        list.add(ArrayUnsignedIntList.MIN_VALUE);
        list.add(ArrayUnsignedIntList.MAX_VALUE);
        try {
            list.add(-1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            list.add(ArrayUnsignedIntList.MAX_VALUE+1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testNegativeInitialCapacityIsInvalid() {
        try {
            new ArrayUnsignedIntList(-1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testCopyConstructor() {
        ArrayUnsignedIntList expected = new ArrayUnsignedIntList();
        for(int i=0;i<10;i++) {
            expected.add(i);
        }
        ArrayUnsignedIntList list = new ArrayUnsignedIntList(expected);
        assertEquals(10,list.size());
        assertEquals(expected,list);
    }

    public void testCopyConstructorWithNull() {
        try {
            new ArrayUnsignedIntList(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }


    public void testTrimToSize() {
        ArrayUnsignedIntList list = new ArrayUnsignedIntList();
        for(int j=0;j<3;j++) {
            assertTrue(list.isEmpty());
    
            list.trimToSize();
    
            assertTrue(list.isEmpty());
            
            for(int i=0;i<10;i++) {
                list.add(i);
            }
            
            for(int i=0;i<10;i++) {
                assertEquals(i,list.get(i));
            }
            
            list.trimToSize();
    
            for(int i=0;i<10;i++) {
                assertEquals(i,list.get(i));
            }
    
            for(int i=0;i<10;i+=2) {
                list.removeElement(i);
            }
            
            for(int i=0;i<5;i++) {
                assertEquals((2*i)+1,list.get(i));
            }
    
            list.trimToSize();
                    
            for(int i=0;i<5;i++) {
                assertEquals((2*i)+1,list.get(i));
            }
    
            list.trimToSize();
                    
            for(int i=0;i<5;i++) {
                assertEquals((2*i)+1,list.get(i));
            }

            list.clear();
        }
    }

}
