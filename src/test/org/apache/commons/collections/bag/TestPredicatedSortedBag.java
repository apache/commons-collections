/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/bag/TestPredicatedSortedBag.java,v 1.4 2003/12/05 20:22:12 scolebourne Exp $
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
package org.apache.commons.collections.bag;

import java.util.Comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.SortedBag;

/**
 * Extension of {@link TestBag} for exercising the {@link PredicatedSortedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/12/05 20:22:12 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedSortedBag extends AbstractTestSortedBag {
    
    private SortedBag emptyBag = new TreeBag();
    private SortedBag nullBag = null;
    
    public TestPredicatedSortedBag(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestPredicatedSortedBag.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedSortedBag.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //--------------------------------------------------------------------------
    
    protected Predicate stringPredicate() {
        return new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
    }   
    
    protected Predicate truePredicate = PredicateUtils.truePredicate();
    
    protected SortedBag decorateBag(SortedBag bag, Predicate predicate) {
        return PredicatedSortedBag.decorate(bag, predicate);
    }
    
    public Bag makeBag() {
        return decorateBag(emptyBag, truePredicate);
    }
    
    protected Bag makeTestBag() {
        return decorateBag(emptyBag, stringPredicate());
    }
    
    //--------------------------------------------------------------------------
    
    public void testDecorate() {
        SortedBag bag = decorateBag(emptyBag, stringPredicate());
        SortedBag bag2 = ((PredicatedSortedBag) bag).getSortedBag();
        try {
            SortedBag bag3 = decorateBag(emptyBag, null);
            fail("Expecting IllegalArgumentException for null predicate");
        } catch (IllegalArgumentException e) {}
        try {
            SortedBag bag4 = decorateBag(nullBag, stringPredicate());
            fail("Expecting IllegalArgumentException for null bag");
        } catch (IllegalArgumentException e) {}
    }
    
    public void testSortOrder() {
        SortedBag bag = decorateBag(emptyBag, stringPredicate());
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
