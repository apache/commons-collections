/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests some basic functions of the ExtendedProperties class.
 * 
 * @version $Revision$ $Date$
 * 
 * @author Geir Magnusson Jr.
 * @author Mohan Kishore
 * @author Stephen Colebourne
 * @author Shinobu Kawai
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
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
        assertNull("This returns null", eprop.getProperty("foo"));

        /*
         *  add a real value, and get it two different ways
         */
        eprop.setProperty("number", "1");
        assertEquals("This returns '1'", "1", eprop.getProperty("number"));
        assertEquals("This returns '1'", "1", eprop.getString("number"));

        /*
         * now add another and get a Vector/list
         */
        eprop.addProperty("number", "2");
        assertTrue("This returns array", (eprop.getVector("number") instanceof java.util.Vector));
        assertTrue("This returns array", (eprop.getList("number") instanceof java.util.List));

        /*
         *  now test dan's new fix where we get the first scalar 
         *  when we access a vector/list valued
         *  property
         */
        assertTrue("This returns scalar", (eprop.getString("number") instanceof String));

        /*
         * test comma separated string properties
         */
        String prop = "hey, that's a test";
        eprop.setProperty("prop.string", prop);
        assertTrue("This returns vector", (eprop.getVector("prop.string") instanceof java.util.Vector));
        assertTrue("This returns list", (eprop.getList("prop.string") instanceof java.util.List));

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
        assertTrue("This returns array for subset", (subEprop.getList("string") instanceof java.util.List));

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

            assertEquals("Commas not interpreted properly", 
                    2, ep1.getList("three").size());
            assertEquals("Commas not interpreted properly", 
                    "Hello", ep1.getList("three").get(0));
            assertEquals("Commas not interpreted properly", 
                    "World", ep1.getList("three").get(1));
                    
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
    
    public void testMultipleSameKey1() throws Exception {
        ExtendedProperties ep1 = new ExtendedProperties();

        /*
        initialize using:
        one=a
        one=b,c
        */
        String s1 = "one=a\none=b,c\n";
        byte[] bytes = s1.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ep1.load(bais);
        assertEquals(1, ep1.size());

        assertEquals(3, ep1.getVector("one").size());
        assertEquals("a", ep1.getVector("one").get(0));
        assertEquals("b", ep1.getVector("one").get(1));
        assertEquals("c", ep1.getVector("one").get(2));

        assertEquals(3, ep1.getList("one").size());
        assertEquals("a", ep1.getList("one").get(0));
        assertEquals("b", ep1.getList("one").get(1));
        assertEquals("c", ep1.getList("one").get(2));
    }
    
    public void testMultipleSameKey2() throws Exception {
        ExtendedProperties ep1 = new ExtendedProperties();

        /*
        initialize using:
        one=a,b
        one=c,d
        */
        String s1 = "one=a,b\none=c,d\n";
        byte[] bytes = s1.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ep1.load(bais);
        assertEquals(1, ep1.size());

        assertEquals(4, ep1.getVector("one").size());
        assertEquals("a", ep1.getVector("one").get(0));
        assertEquals("b", ep1.getVector("one").get(1));
        assertEquals("c", ep1.getVector("one").get(2));
        assertEquals("d", ep1.getVector("one").get(3));

        assertEquals(4, ep1.getList("one").size());
        assertEquals("a", ep1.getList("one").get(0));
        assertEquals("b", ep1.getList("one").get(1));
        assertEquals("c", ep1.getList("one").get(2));
        assertEquals("d", ep1.getList("one").get(3));
    }
    
    public void testMultipleSameKey3() throws Exception {
        ExtendedProperties ep1 = new ExtendedProperties();

        /*
        initialize using:
        one=a,b
        one=c
        */
        String s1 = "one=a,b\none=c\n";
        byte[] bytes = s1.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ep1.load(bais);
        assertEquals(1, ep1.size());

        assertEquals(3, ep1.getVector("one").size());
        assertEquals("a", ep1.getVector("one").get(0));
        assertEquals("b", ep1.getVector("one").get(1));
        assertEquals("c", ep1.getVector("one").get(2));

        assertEquals(3, ep1.getList("one").size());
        assertEquals("a", ep1.getList("one").get(0));
        assertEquals("b", ep1.getList("one").get(1));
        assertEquals("c", ep1.getList("one").get(2));
    }
    
    public void testMultipleSameKeyByCode() throws Exception {
        ExtendedProperties ep1 = new ExtendedProperties();

        ep1.addProperty("one", "a");
        assertEquals(1, ep1.size());

        assertEquals(1, ep1.getVector("one").size());
        assertEquals("a", ep1.getVector("one").get(0));

        assertEquals(1, ep1.getList("one").size());
        assertEquals("a", ep1.getList("one").get(0));
        
        ep1.addProperty("one", Boolean.TRUE);
        assertEquals(1, ep1.size());

        assertEquals(2, ep1.getVector("one").size());
        assertEquals("a", ep1.getVector("one").get(0));
        assertEquals(Boolean.TRUE, ep1.getVector("one").get(1));

        assertEquals(2, ep1.getList("one").size());
        assertEquals("a", ep1.getList("one").get(0));
        assertEquals(Boolean.TRUE, ep1.getList("one").get(1));
        
        ep1.addProperty("one", "c,d");
        assertEquals(1, ep1.size());

        assertEquals(4, ep1.getVector("one").size());
        assertEquals("a", ep1.getVector("one").get(0));
        assertEquals(Boolean.TRUE, ep1.getVector("one").get(1));
        assertEquals("c", ep1.getVector("one").get(2));
        assertEquals("d", ep1.getVector("one").get(3));

        assertEquals(4, ep1.getList("one").size());
        assertEquals("a", ep1.getList("one").get(0));
        assertEquals(Boolean.TRUE, ep1.getList("one").get(1));
        assertEquals("c", ep1.getList("one").get(2));
        assertEquals("d", ep1.getList("one").get(3));
    }

    public void testInheritDefaultProperties() {
        Properties defaults = new Properties();
        defaults.setProperty("resource.loader", "class");

        Properties properties = new Properties(defaults);
        properties.setProperty("test", "foo");

        ExtendedProperties extended = ExtendedProperties.convertProperties(properties);

        assertEquals("foo", extended.getString("test"));
        assertEquals("class", extended.getString("resource.loader"));
    }

    public void testInclude() {
        ExtendedProperties a = new ExtendedProperties();
        ExtendedProperties b = new ExtendedProperties();
        
        assertEquals("include", a.getInclude());
        assertEquals("include", b.getInclude());
        
        a.setInclude("import");
        assertEquals("import", a.getInclude());
        assertEquals("include", b.getInclude());
        
        a.setInclude("");
        assertEquals(null, a.getInclude());
        assertEquals("include", b.getInclude());
        
        a.setInclude("hi");
        assertEquals("hi", a.getInclude());
        assertEquals("include", b.getInclude());
        
        a.setInclude(null);
        assertEquals(null, a.getInclude());
        assertEquals("include", b.getInclude());
    }

    public void testKeySet1() {
            ExtendedProperties p = new ExtendedProperties();
            p.addProperty("a", "foo");
            p.addProperty("b", "bar");
            p.addProperty("c", "bar");

            Iterator it = p.getKeys();
            assertEquals("a", (String) it.next());
            assertEquals("b", (String) it.next());
            assertEquals("c", (String) it.next());
            assertFalse(it.hasNext());
    }

    public void testKeySet2() {
        ExtendedProperties p = new ExtendedProperties();
        p.put("a", "foo");
        p.put("b", "bar");
        p.put("c", "bar");

        Iterator it = p.getKeys();
        assertEquals("a", (String) it.next());
        assertEquals("b", (String) it.next());
        assertEquals("c", (String) it.next());
        assertFalse(it.hasNext());
    }


    public void testKeySet3() {
        ExtendedProperties q = new ExtendedProperties();
        q.addProperty("a", "foo");
        q.addProperty("b", "bar");
        q.addProperty("c", "bar");

        ExtendedProperties p = new ExtendedProperties();
        p.putAll(q);

        Iterator it = p.getKeys();
        assertEquals("a", (String) it.next());
        assertEquals("b", (String) it.next());
        assertEquals("c", (String) it.next());
        assertFalse(it.hasNext());
    }

    public void testKeySet4() {
        ExtendedProperties q = new ExtendedProperties();
        q.addProperty("a", "foo");
        q.addProperty("b", "bar");
        q.addProperty("c", "bar");

        q.remove("b");

        Iterator it = q.getKeys();
        assertEquals("a", (String) it.next());
        assertEquals("c", (String) it.next());
        assertFalse(it.hasNext());
    }

    public void testCollections271() {
        ExtendedProperties props = new ExtendedProperties();
        props.setProperty("test", "\\\\\\\\192.168.1.91\\\\test");
        props.getProperty("test");
        assertEquals( "\\\\192.168.1.91\\test", props.getProperty("test") );

        ExtendedProperties props2 = new ExtendedProperties();
        props2.combine(props);
        assertEquals( "\\\\192.168.1.91\\test", props2.getProperty("test") );

        ExtendedProperties props3 = new ExtendedProperties();
        props3.setProperty("sub.test", "foo");
        props2.combine(props3);
        assertEquals("foo", props2.getProperty("sub.test"));

        ExtendedProperties subs = props2.subset("sub");
        assertNotNull(subs);
        assertEquals("foo", subs.getProperty("test"));
    }

    public void testCollections238() throws IOException {
        ExtendedProperties props = new ExtendedProperties();
        String txt = "x=1\ny=\nz=3";
        byte[] bytes = txt.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        props.load(in);
        assertEquals("1", props.getProperty("x"));
        assertEquals("3", props.getProperty("z"));
        assertEquals("", props.getProperty("y"));
        assertEquals(3, props.size());
    }

    public void testCollections299() {
        Properties defaults = new Properties();
        defaults.put("objectTrue", Boolean.TRUE);

        Properties properties = new Properties(defaults);
        properties.put("objectFalse", Boolean.FALSE);

        ExtendedProperties extended = ExtendedProperties.convertProperties(properties);

        assertNull(extended.getString("objectTrue"));
        assertNull(extended.getString("objectFalse"));

        assertNull(extended.get("objectTrue"));
        assertNull(extended.get("objectFalse"));
    }

}
