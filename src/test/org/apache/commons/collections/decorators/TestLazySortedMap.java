/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestLazySortedMap.java,v 1.1 2003/09/14 03:30:23 psteitz Exp $
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

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.TreeMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.Comparator;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;

/**
 * Extension of {@link TestLazyMap} for exercising the 
 * {@link LazySortedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/09/14 03:30:23 $
 * 
 * @author Phil Steitz
 */
public class TestLazySortedMap extends TestLazyMap {
    
    public TestLazySortedMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestLazySortedMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestLazySortedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------
    
    protected SortedMap decorateMap(SortedMap map, Factory factory) {
        return LazySortedMap.decorate(map, factory);
    }
    
    public Map makeEmptyMap() {
        return decorateMap(new TreeMap(), nullFactory);
    }
    
    protected boolean useNullKey() {
        return false;
    }
    
//--------------------------------------------------------------------   
    
    protected SortedMap makeTestSortedMap(Factory factory) {
        return decorateMap(new TreeMap(), factory);
    }
    
    public void testSortOrder() {
        SortedMap map = makeTestSortedMap(oneFactory);
        map.put("A",  "a");
        map.get("B"); // Entry with value "One" created
        map.put("C", "c");
        assertEquals("First key should be A", map.firstKey(), "A");
        assertEquals("Last key should be C", map.lastKey(), "C");
        assertEquals("First key in tail map should be B", 
            map.tailMap("B").firstKey(), "B");
        assertEquals("Last key in head map should be B", 
            map.headMap("C").lastKey(), "B");
        assertEquals("Last key in submap should be B",
           map.subMap("A","C").lastKey(), "B");
        
        Comparator c = map.comparator();
        assertTrue("natural order, so comparator should be null", 
            c == null);      
    } 
    
    public void testTransformerDecorate() {
        Transformer transformer = TransformerUtils.asTransformer(oneFactory);
        SortedMap map = LazySortedMap.decorate(new TreeMap(), transformer);     
        assertTrue(map instanceof LazySortedMap);  
         try {
            map = LazySortedMap.decorate(new TreeMap(), (Transformer) null);
            fail("Expecting IllegalArgumentException for null transformer");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = LazySortedMap.decorate(null, transformer);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        } 
    }
}