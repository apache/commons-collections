/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/set/TestPredicatedSortedSet.java,v 1.2 2003/11/16 22:15:10 scolebourne Exp $
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
package org.apache.commons.collections.set;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.map.TestPredicatedSortedMap;

/**
 * Extension of {@link AbstractTestSortedSet} for exercising the 
 * {@link PredicatedSortedSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/11/16 22:15:10 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedSortedSet extends AbstractTestSortedSet{
    
    public TestPredicatedSortedSet(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestPredicatedSortedSet.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedSortedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------    
    
    protected Predicate truePredicate = PredicateUtils.truePredicate();
    
    public Set makeEmptySet() {
        return PredicatedSortedSet.decorate(new TreeSet(), truePredicate);
    }
    
    public Set makeFullSet() {
        TreeSet set = new TreeSet();
        set.addAll(Arrays.asList(getFullElements()));
        return PredicatedSortedSet.decorate(set, truePredicate);
    }
   
    
//--------------------------------------------------------------------   
    protected Predicate testPredicate =  
        new Predicate() {
            public boolean evaluate(Object o) {
                return (o instanceof String) && (((String) o).startsWith("A"));
            }
        };      
     
    
    protected SortedSet makeTestSet() {
        return PredicatedSortedSet.decorate(new TreeSet(), testPredicate);
    }
    
    public void testGetSet() {
        SortedSet set = makeTestSet();
        assertTrue("returned set should not be null",
            ((PredicatedSortedSet) set).getSet() != null);
    }
    
    public void testIllegalAdd() {
        SortedSet set = makeTestSet();
        String testString = "B";
        try {
            set.add(testString);
            fail("Should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !set.contains(testString));   
    }

    public void testIllegalAddAll() {
        SortedSet set = makeTestSet();
        Set elements = new TreeSet();
        elements.add("Aone");
        elements.add("Atwo");
        elements.add("Bthree");
        elements.add("Afour");
        try {
            set.addAll(elements);
            fail("Should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Set shouldn't contain illegal element", 
         !set.contains("Aone"));   
        assertTrue("Set shouldn't contain illegal element", 
         !set.contains("Atwo"));   
        assertTrue("Set shouldn't contain illegal element", 
         !set.contains("Bthree"));   
        assertTrue("Set shouldn't contain illegal element", 
         !set.contains("Afour"));   
    }
    
    public void testComparator() {
        SortedSet set = makeTestSet();
        Comparator c = set.comparator();
        assertTrue("natural order, so comparator should be null", c == null);
    }
        
}