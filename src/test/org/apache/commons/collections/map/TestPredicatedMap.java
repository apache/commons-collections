/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/TestPredicatedMap.java,v 1.3 2003/11/18 22:37:17 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

/**
 * Extension of {@link TestMap} for exercising the 
 * {@link PredicatedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/11/18 22:37:17 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedMap extends AbstractTestMap{
    
    public TestPredicatedMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestPredicatedMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------
    
    protected Predicate truePredicate = PredicateUtils.truePredicate();
    
    protected Map decorateMap(Map map, Predicate keyPredicate, 
        Predicate valuePredicate) {
        return PredicatedMap.decorate(map, keyPredicate, valuePredicate);
    }
    
    public Map makeEmptyMap() {
        return decorateMap(new HashMap(), truePredicate, truePredicate);
    }
    
//--------------------------------------------------------------------   
    
     protected Predicate testPredicate =  
        new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };      
    
    public Map makeTestMap() {
        return decorateMap(new HashMap(), testPredicate, testPredicate);
    }
    
    public void testEntrySet() {
        Map map = makeTestMap();
        assertTrue("returned entryset should not be null",
            map.entrySet() != null);
        map = decorateMap(new HashMap(), null, null);
        map.put("oneKey", "oneValue");
        assertTrue("returned entryset should contain one entry",
            map.entrySet().size() == 1); 
        map = decorateMap(map, null, null);
    }
    
    public void testPut() {
        Map map = makeTestMap();
        try {
            map.put("Hi", new Integer(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            map.put(new Integer(3), "Hi");
            fail("Illegal key should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        assertTrue(!map.containsKey(new Integer(3)));
        assertTrue(!map.containsValue(new Integer(3)));

        Map map2 = new HashMap();
        map2.put("A", "a");
        map2.put("B", "b");
        map2.put("C", "c");
        map2.put("c", new Integer(3));

        try {
            map.putAll(map2);
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        map.put("E", "e");
        Iterator iterator = map.entrySet().iterator();
        try {
            Map.Entry entry = (Map.Entry)iterator.next();
            entry.setValue(new Integer(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        map.put("F", "f");
        iterator = map.entrySet().iterator();
        Map.Entry entry = (Map.Entry)iterator.next();
        entry.setValue("x");
        
    }
        
}