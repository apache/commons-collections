/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 */
package org.apache.commons.collections;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests 
 * {@link org.apache.commons.collections.MultiKey}.
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
