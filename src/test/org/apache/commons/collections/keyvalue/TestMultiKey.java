/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.keyvalue;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for {@link org.apache.commons.collections.MultiKey}.
 * 
 * @version $Revision: 1.3 $ $Date: 2004/02/18 01:20:40 $
 * 
 * @author Stephen Colebourne
 */
public class TestMultiKey extends TestCase {

    Integer ONE = new Integer(1);
    Integer TWO = new Integer(2);
    Integer THREE = new Integer(3);
    Integer FOUR = new Integer(4);
    Integer FIVE = new Integer(5);
    
    public TestMultiKey(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestMultiKey.class);
    }

    public static void main(String[] args) {
        String[] testCaseName = { TestMultiKey.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    

    public void testConstructorsAndGet() throws Exception {
        MultiKey mk = null;
        mk = new MultiKey(ONE, TWO);
        Assert.assertTrue(Arrays.equals(new Object[] {ONE, TWO}, mk.getKeys()));

        mk = new MultiKey(ONE, TWO, THREE);
        Assert.assertTrue(Arrays.equals(new Object[] {ONE, TWO, THREE}, mk.getKeys()));

        mk = new MultiKey(ONE, TWO, THREE, FOUR);
        Assert.assertTrue(Arrays.equals(new Object[] {ONE, TWO, THREE, FOUR}, mk.getKeys()));

        mk = new MultiKey(ONE, TWO, THREE, FOUR, FIVE);
        Assert.assertTrue(Arrays.equals(new Object[] {ONE, TWO, THREE, FOUR, FIVE}, mk.getKeys()));

        mk = new MultiKey(new Object[] {THREE, FOUR, ONE, TWO}, false);
        Assert.assertTrue(Arrays.equals(new Object[] {THREE, FOUR, ONE, TWO}, mk.getKeys()));

        // don't do this!
        Object[] keys = new Object[] {THREE, FOUR, ONE, TWO};
        mk = new MultiKey(keys);
        Assert.assertTrue(Arrays.equals(new Object[] {THREE, FOUR, ONE, TWO}, mk.getKeys()));
        keys[3] = FIVE;  // no effect
        Assert.assertTrue(Arrays.equals(new Object[] {THREE, FOUR, ONE, TWO}, mk.getKeys()));
    }
    
    public void testHashCode() {
        MultiKey mk1 = new MultiKey(ONE, TWO);
        MultiKey mk2 = new MultiKey(ONE, TWO);
        MultiKey mk3 = new MultiKey(ONE, "TWO");
        
        Assert.assertTrue(mk1.hashCode() == mk1.hashCode());
        Assert.assertTrue(mk1.hashCode() == mk2.hashCode());
        Assert.assertTrue(mk1.hashCode() != mk3.hashCode());
    }
    
    public void testEquals() {
        MultiKey mk1 = new MultiKey(ONE, TWO);
        MultiKey mk2 = new MultiKey(ONE, TWO);
        MultiKey mk3 = new MultiKey(ONE, "TWO");
        
        Assert.assertEquals(mk1, mk1);
        Assert.assertEquals(mk1, mk2);
        Assert.assertTrue(mk1.equals(mk3) == false);
        Assert.assertTrue(mk1.equals("") == false);
        Assert.assertTrue(mk1.equals(null) == false);
    }
    
}
