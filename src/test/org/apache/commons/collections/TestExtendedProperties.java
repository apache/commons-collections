/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *   Tests some basic functions of the ExtendedProperties
 *   class
 * 
 *   @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 *   @version $Id: TestExtendedProperties.java,v 1.4.4.1 2004/05/22 12:14:05 scolebourne Exp $
 */
public class TestExtendedProperties extends TestCase
{
    protected ExtendedProperties eprop = new ExtendedProperties();

    public TestExtendedProperties(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return new TestSuite( TestExtendedProperties.class );
    }

    public static void main(String args[])
    {
        String[] testCaseName = { TestExtendedProperties.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void testRetrieve()
    {
        /*
         * should be emptry and return null
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
        assertTrue("This returns array", ( eprop.getVector("number") instanceof java.util.Vector ) );
        
        /*
         *  now test dan's new fix where we get the first scalar 
         *  when we access a vector valued
         *  property
         */
        assertTrue("This returns scalar", ( eprop.getString("number") instanceof String ) );

        /*
         * test comma separated string properties
         */
        String prop = "hey, that's a test";
        eprop.setProperty("prop.string", prop);
        assertTrue("This returns vector", ( eprop.getVector("prop.string") instanceof java.util.Vector ) );
        
        String prop2 = "hey\\, that's a test";
        eprop.remove("prop.string");
        eprop.setProperty("prop.string", prop2);
        assertTrue("This returns array", ( eprop.getString("prop.string") instanceof java.lang.String) );
        
        /*
         * test subset : we want to make sure that the EP doesn't reprocess the data 
         *  elements when generating the subset
         */

        ExtendedProperties subEprop = eprop.subset("prop");

        assertTrue("Returns the full string",  subEprop.getString("string").equals( prop ) );
        assertTrue("This returns string for subset", ( subEprop.getString("string") instanceof java.lang.String) );
        assertTrue("This returns array for subset", ( subEprop.getVector("string") instanceof java.util.Vector) );
        
    }

    public void testInterpolation()
    {
        eprop.setProperty("applicationRoot", "/home/applicationRoot");
        eprop.setProperty("db", "${applicationRoot}/db/hypersonic");
        String dbProp = "/home/applicationRoot/db/hypersonic";
        assertTrue("Checking interpolated variable", eprop.getString("db").equals(dbProp));
    }
}
