/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestTypedSortedSet.java,v 1.1 2003/10/13 02:48:16 psteitz Exp $
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

import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.AbstractTestSortedSet;
import org.apache.commons.collections.BulkTest;


/**
 * Extension of {@link AbstractTestSortedSet} for exercising the 
 * {@link TypedSortedSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/10/13 02:48:16 $
 * 
 * @author Phil Steitz
 */
public class TestTypedSortedSet extends AbstractTestSortedSet{
    
    public TestTypedSortedSet(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestTypedSortedSet.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestTypedSortedSet.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------      
    protected Class integerType = new Integer(0).getClass();
    
    public Set makeEmptySet() {
        return TypedSortedSet.decorate(new TreeSet(), integerType);
    }
    
    public Set makeFullSet() {
        TreeSet set = new TreeSet();
        set.addAll(Arrays.asList(getFullElements()));
        return TypedSortedSet.decorate(set, integerType);
    }
   
    
//--------------------------------------------------------------------            
    protected Long getNextAsLong() {
        SortedSet set = (SortedSet) makeFullSet();
        int nextValue = ((Integer)set.last()).intValue() + 1;
        return new Long(nextValue);
    }
    
    protected Integer getNextAsInt() {
        SortedSet set = (SortedSet) makeFullSet();
        int nextValue = ((Integer)set.last()).intValue() + 1;
        return new Integer(nextValue);
    }
           
    public void testIllegalAdd() {
        Set set = makeFullSet();
        try {
            set.add(getNextAsLong());
            fail("Should fail type test.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't convert long to int", 
         !set.contains(getNextAsInt()));   
    }

    public void testIllegalAddAll() {
        Set set = makeFullSet();
        Set elements = new TreeSet();
        elements.add(getNextAsLong());
        try {
            set.addAll(elements);
            fail("Should fail type test.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't convert long to int", 
         !set.contains(getNextAsInt()));  
    }       
}