/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/list/TestPredicatedList.java,v 1.1 2003/11/16 00:05:44 scolebourne Exp $
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
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.AbstractTestList;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

/**
 * Extension of {@link TestList} for exercising the 
 * {@link PredicatedList} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/16 00:05:44 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedList extends AbstractTestList{
    
    public TestPredicatedList(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestPredicatedList.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------
    
    protected Predicate truePredicate = PredicateUtils.truePredicate();
    
    protected List decorateList(List list, Predicate predicate) {
        return PredicatedList.decorate(list, predicate);
    }
    
    public List makeEmptyList() {
        return decorateList(new ArrayList(), truePredicate);
    }
    
    protected Object[] getFullElements() {
        return new Object[] {"1", "3", "5", "7", "2", "4", "6"};
    }
    
//--------------------------------------------------------------------   
    
     protected Predicate testPredicate =  
        new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };      
    
    public List makeTestList() {
        return decorateList(new ArrayList(), testPredicate);
    }
    
    public void testIllegalAdd() {
        List list = makeTestList();
        Integer i = new Integer(3);
        try {
            list.add(i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !list.contains(i));   
    }

    public void testIllegalAddAll() {
        List list = makeTestList();
        List elements = new ArrayList();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            list.addAll(0,elements);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("List shouldn't contain illegal element", 
         !list.contains("one"));   
        assertTrue("List shouldn't contain illegal element", 
         !list.contains("two"));   
        assertTrue("List shouldn't contain illegal element", 
         !list.contains(new Integer(3)));   
        assertTrue("List shouldn't contain illegal element", 
         !list.contains("four"));   
    }
    
    public void testIllegalSet() {
        List list = makeTestList();
        try {
            list.set(0,new Integer(3));
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testLegalAddAll() {
        List list = makeTestList();
        list.add("zero");
        List elements = new ArrayList();
        elements.add("one");
        elements.add("two");
        elements.add("three");
        list.addAll(1,elements);
        assertTrue("List should contain legal element", 
         list.contains("zero"));   
        assertTrue("List should contain legal element", 
         list.contains("one"));   
        assertTrue("List should contain legal element", 
         list.contains("two"));   
        assertTrue("List should contain legal element", 
         list.contains("three"));   
    }       
        
}