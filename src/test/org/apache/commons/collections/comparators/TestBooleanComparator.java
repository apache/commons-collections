/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/comparators/TestBooleanComparator.java,v 1.5 2003/10/05 23:21:07 scolebourne Exp $
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
package org.apache.commons.collections.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for {@link BooleanComparator}.
 * 
 * @version $Revision: 1.5 $ $Date: 2003/10/05 23:21:07 $
 * 
 * @author Rodney Waldhoff
 */
public class TestBooleanComparator extends AbstractTestComparator {

    // conventional
    // ------------------------------------------------------------------------

    public TestBooleanComparator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBooleanComparator.class);
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    protected Comparator makeComparator() {
        return new BooleanComparator();
    }

    protected List getComparableObjectsOrdered() {
        List list = new ArrayList();
        list.add(new Boolean(false));
        list.add(Boolean.FALSE);
        list.add(new Boolean(false));
        list.add(Boolean.TRUE);
        list.add(new Boolean(true));
        list.add(Boolean.TRUE);
        return list;
    }
    
    public String getCompatibilityVersion() {
        return "3.0";
    }

    // tests
    // ------------------------------------------------------------------------

    public void testConstructors() {
        allTests(false,new BooleanComparator());
        allTests(false,new BooleanComparator(false));
        allTests(true,new BooleanComparator(true));        
    }
    
    public void testStaticFactoryMethods() {
        allTests(false,BooleanComparator.getFalseFirstComparator());
        allTests(false,BooleanComparator.getBooleanComparator(false));
        allTests(true,BooleanComparator.getTrueFirstComparator());
        allTests(true,BooleanComparator.getBooleanComparator(true));
    }
    
    public void testEqualsCompatibleInstance() {
        assertEquals(new BooleanComparator(),new BooleanComparator(false));
        assertEquals(new BooleanComparator(false),new BooleanComparator(false));
        assertEquals(new BooleanComparator(false),BooleanComparator.getFalseFirstComparator());
        assertSame(BooleanComparator.getFalseFirstComparator(),BooleanComparator.getBooleanComparator(false));

        assertEquals(new BooleanComparator(true),new BooleanComparator(true));
        assertEquals(new BooleanComparator(true),BooleanComparator.getTrueFirstComparator());
        assertSame(BooleanComparator.getTrueFirstComparator(),BooleanComparator.getBooleanComparator(true));

        assertTrue(!(new BooleanComparator().equals(new BooleanComparator(true))));
        assertTrue(!(new BooleanComparator(true).equals(new BooleanComparator(false))));
    }
    
    // utilities
    // ------------------------------------------------------------------------

    protected void allTests(boolean trueFirst, BooleanComparator comp) {
        orderIndependentTests(comp);
        if(trueFirst) {
            trueFirstTests(comp);
        } else {
            falseFirstTests(comp);
        }
    }

    protected void trueFirstTests(BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0,comp.compare(Boolean.TRUE,Boolean.TRUE));
        assertEquals(0,comp.compare(Boolean.FALSE,Boolean.FALSE));
        assertTrue(comp.compare(Boolean.FALSE,Boolean.TRUE) > 0);
        assertTrue(comp.compare(Boolean.TRUE,Boolean.FALSE) < 0);

        assertEquals(0,comp.compare((Object)(Boolean.TRUE),(Object)(Boolean.TRUE)));
        assertEquals(0,comp.compare((Object)(Boolean.FALSE),(Object)(Boolean.FALSE)));
        assertTrue(comp.compare((Object)(Boolean.FALSE),(Object)(Boolean.TRUE)) > 0);
        assertTrue(comp.compare((Object)(Boolean.TRUE),(Object)(Boolean.FALSE)) < 0);
    }

    protected void falseFirstTests(BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0,comp.compare(Boolean.TRUE,Boolean.TRUE));
        assertEquals(0,comp.compare(Boolean.FALSE,Boolean.FALSE));
        assertTrue(comp.compare(Boolean.FALSE,Boolean.TRUE) < 0);
        assertTrue(comp.compare(Boolean.TRUE,Boolean.FALSE) > 0);

        assertEquals(0,comp.compare((Object)(Boolean.TRUE),(Object)(Boolean.TRUE)));
        assertEquals(0,comp.compare((Object)(Boolean.FALSE),(Object)(Boolean.FALSE)));
        assertTrue(comp.compare((Object)(Boolean.FALSE),(Object)(Boolean.TRUE)) < 0);
        assertTrue(comp.compare((Object)(Boolean.TRUE),(Object)(Boolean.FALSE)) > 0);
    }

    protected void orderIndependentTests(BooleanComparator comp) {
        nullArgumentTests(comp);
        nonBooleanArgumentTests(comp);
        nullAndNonBooleanArgumentsTests(comp);
    }
    
    protected void nullArgumentTests(BooleanComparator comp) {
        assertNotNull(comp);
        try {
            comp.compare(null,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(Boolean.TRUE,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(Boolean.FALSE,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(null,Boolean.TRUE);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(null,Boolean.FALSE);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    protected void nonBooleanArgumentTests(BooleanComparator comp) {
        assertNotNull(comp);
        try {
            comp.compare("string","string");
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
        try {
            comp.compare(Boolean.TRUE,"string");
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
        try {
            comp.compare("string",Boolean.TRUE);
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
        try {
            comp.compare("string",new Integer(3));
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
        try {
            comp.compare(new Integer(3),"string");
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
    }
    
    protected void nullAndNonBooleanArgumentsTests(BooleanComparator comp) {
        assertNotNull(comp);
        try {
            comp.compare(null,"string");
            fail("Expected ClassCast or NullPointer Exception");
        } catch(ClassCastException e) {
            // expected
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare("string",null);
            fail("Expected ClassCast or NullPointer Exception");
        } catch(ClassCastException e) {
            // expected
        } catch(NullPointerException e) {
            // expected
        }
    }

}
