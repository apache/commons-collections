/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestTypedSortedBag.java,v 1.3 2003/10/02 22:35:31 scolebourne Exp $
 * ====================================================================
 *
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
package org.apache.commons.collections.decorators;

import java.util.Comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.AbstractTestSortedBag;
import org.apache.commons.collections.TreeBag;

/**
 * Extension of {@link TestBag} for exercising the {@link TypedSortedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/10/02 22:35:31 $
 * 
 * @author Phil Steitz
 */
public class TestTypedSortedBag extends AbstractTestSortedBag {
       
    public TestTypedSortedBag(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestTypedSortedBag.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestTypedSortedBag.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //--------------------------------------------------------------------------
    
    protected Class stringClass = this.getName().getClass();
    private Object obj = new Object();
    protected Class objectClass = obj.getClass();
    protected SortedBag emptyBag = new TreeBag();
    protected SortedBag nullBag = null;
    
    protected SortedBag decorateBag(SortedBag bag, Class claz) {
        return TypedSortedBag.decorate(bag, claz);
    }

    protected Bag makeBag() {
        return decorateBag(emptyBag, objectClass);
    }
    
    protected Bag makeTestBag() {
        return decorateBag(emptyBag, stringClass);
    }
    
    //--------------------------------------------------------------------------
    
    public void testDecorate() {
        SortedBag bag = decorateBag(emptyBag, stringClass);
        try {
            SortedBag bag3 = decorateBag(emptyBag, null);
            fail("Expecting IllegalArgumentException for null predicate");
        } catch (IllegalArgumentException e) {}
        try {
            SortedBag bag4 = decorateBag(nullBag, stringClass);
            fail("Expecting IllegalArgumentException for null bag");
        } catch (IllegalArgumentException e) {}
    }
    
    public void testSortOrder() {
        SortedBag bag = decorateBag(emptyBag, stringClass);
        String one = "one";
        String two = "two";
        String three = "three";
        bag.add(one);
        bag.add(two);
        bag.add(three);
        assertEquals("first element", bag.first(), one);
        assertEquals("last element", bag.last(), two); 
        Comparator c = bag.comparator();
        assertTrue("natural order, so comparator should be null", 
            c == null);
    }
}
