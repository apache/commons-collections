/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestExtendedProperties.java,v 1.7 2003/08/31 13:09:53 scolebourne Exp $
 * $Revision: 1.7 $
 * $Date: 2003/08/31 13:09:53 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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

import java.io.*;

/**
 * Tests some basic functions of the ExtendedProperties
 * class
 * 
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author Mohan Kishore
 * @author Stephen Colebourne
 * @version $Id: TestExtendedProperties.java,v 1.7 2003/08/31 13:09:53 scolebourne Exp $
 */
public class TestExtendedProperties extends TestCase {
    protected ExtendedProperties eprop = new ExtendedProperties();

    public TestExtendedProperties(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestExtendedProperties.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestExtendedProperties.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public void testRetrieve() {
        /*
         * should be empty and return null
         */
        assertEquals("This returns null", eprop.getProperty("foo"), null);

        /*
         *  add a real value, and get it two different ways
         */
        eprop.setProperty("number", "1");
        assertEquals("This returns '1'", eprop.getProperty("number"), "1");
        assertEquals("This returns '1'", eprop.getString("number"), "1");

        /*
         * now add another and get a Vector
         */
        eprop.addProperty("number", "2");
        assertTrue("This returns array", (eprop.getVector("number") instanceof java.util.Vector));

        /*
         *  now test dan's new fix where we get the first scalar 
         *  when we access a vector valued
         *  property
         */
        assertTrue("This returns scalar", (eprop.getString("number") instanceof String));

        /*
         * test comma separated string properties
         */
        String prop = "hey, that's a test";
        eprop.setProperty("prop.string", prop);
        assertTrue("This returns vector", (eprop.getVector("prop.string") instanceof java.util.Vector));

        String prop2 = "hey\\, that's a test";
        eprop.remove("prop.string");
        eprop.setProperty("prop.string", prop2);
        assertTrue("This returns array", (eprop.getString("prop.string") instanceof java.lang.String));

        /*
         * test subset : we want to make sure that the EP doesn't reprocess the data 
         *  elements when generating the subset
         */

        ExtendedProperties subEprop = eprop.subset("prop");

        assertTrue("Returns the full string", subEprop.getString("string").equals(prop));
        assertTrue("This returns string for subset", (subEprop.getString("string") instanceof java.lang.String));
        assertTrue("This returns array for subset", (subEprop.getVector("string") instanceof java.util.Vector));

    }

    public void testInterpolation() {
        eprop.setProperty("applicationRoot", "/home/applicationRoot");
        eprop.setProperty("db", "${applicationRoot}/db/hypersonic");
        String dbProp = "/home/applicationRoot/db/hypersonic";
        assertTrue("Checking interpolated variable", eprop.getString("db").equals(dbProp));
    }

    public void testSaveAndLoad() {
        ExtendedProperties ep1 = new ExtendedProperties();
        ExtendedProperties ep2 = new ExtendedProperties();

        try {
            /* initialize value:
            one=Hello\World
            two=Hello\,World
            three=Hello,World
            */
            String s1 = "one=Hello\\World\ntwo=Hello\\,World\nthree=Hello,World";
            byte[] bytes = s1.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ep1.load(bais);
            assertEquals("Back-slashes not interpreted properly", 
                    "Hello\\World", ep1.getString("one"));
            assertEquals("Escaped commas not interpreted properly", 
                    "Hello,World", ep1.getString("two"));
            assertEquals("Commas not interpreted properly", 
                    2, ep1.getVector("three").size());
            assertEquals("Commas not interpreted properly", 
                    "Hello", ep1.getVector("three").get(0));
            assertEquals("Commas not interpreted properly", 
                    "World", ep1.getVector("three").get(1));
                    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ep1.save(baos, null);
            bytes = baos.toByteArray();
            bais = new ByteArrayInputStream(bytes);
            ep2.load(bais);
            assertEquals("Back-slash not same after being saved and loaded",
                    ep1.getString("one"), ep2.getString("one"));
            assertEquals("Escaped comma not same after being saved and loaded",
                    ep1.getString("two"), ep2.getString("two"));
            assertEquals("Comma not same after being saved and loaded",
                    ep1.getString("three"), ep2.getString("three"));
        } catch (IOException ioe) {
            fail("There was an exception saving and loading the EP");
        }
    }

    public void testTrailingBackSlash() {
        ExtendedProperties ep1 = new ExtendedProperties();

        try {
            /*
            initialize using:
            one=ONE
            two=TWO \\
            three=THREE
            */
            String s1 = "one=ONE\ntwo=TWO \\\\\nthree=THREE";
            byte[] bytes = s1.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ep1.load(bais);
            assertEquals("Trailing back-slashes not interpreted properly", 
                    3, ep1.size());
            assertEquals("Back-slash not escaped properly", 
                    "TWO \\", ep1.getString("two"));
        } catch (IOException ioe) {
            fail("There was an exception loading the EP");
        }
    }
    
}
