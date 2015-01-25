/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

/**
 * Tests for IterableUtils.
 *
 * @since 4.1
 * @version $Id$
 */
public class IterableUtilsTest extends BulkTest {

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableA = null;
    
    public IterableUtilsTest(final String name){
        super(name);
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(IterableUtilsTest.class);
    }
    
    @Override
    public void setUp() {
        List<Integer> listA = new ArrayList<Integer>();
        listA.add(1);
        listA.add(2);
        listA.add(2);
        listA.add(3);
        listA.add(3);
        listA.add(3);
        listA.add(4);
        listA.add(4);
        listA.add(4);
        listA.add(4);
        iterableA = listA;
    }
    
    public void testToString(){
        String result = IterableUtils.toString(iterableA);
        assertEquals("[1,2,2,3,3,3,4,4,4,4]", result);
    }
    
    public void testToStringEmptyIterable(){
        String result = IterableUtils.toString(new ArrayList<Integer>());
        assertEquals("[]", result);
    }
    
    public void testToStringNullIterable(){
        try{
            IterableUtils.toString(null);
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringTransformer(){
        String result = IterableUtils.toString(iterableA, new Transformer<Integer, String>() {
            public String transform(Integer input) {
                return new Integer(input * 2).toString();
            }
        });
        assertEquals("[2,4,4,6,6,6,8,8,8,8]", result);
    }
    
    public void testToStringTransformerEmptyIterable(){
        String result = IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        });
        assertEquals("[]", result);
    }
    
    public void testToStringTransformerNullIterable(){
        try{
            IterableUtils.toString(null, new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            });
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiter(){
        
        Transformer<Integer, String> transformer = new Transformer<Integer, String>() {
            public String transform(Integer input) {
                return new Integer(input * 2).toString();
            }
        };
        
        String result = IterableUtils.toString(iterableA, transformer, "", "", "");
        assertEquals("2446668888", result);
        
        result = IterableUtils.toString(iterableA, transformer, ",", "", "");
        assertEquals("2,4,4,6,6,6,8,8,8,8", result);
        
        result = IterableUtils.toString(iterableA, transformer, "", "[", "]");
        assertEquals("[2446668888]", result);
        
        result = IterableUtils.toString(iterableA, transformer, ",", "[", "]");
        assertEquals("[2,4,4,6,6,6,8,8,8,8]", result);
        
        result = IterableUtils.toString(iterableA, transformer, ",", "[[", "]]");
        assertEquals("[[2,4,4,6,6,6,8,8,8,8]]", result);
        
        result = IterableUtils.toString(iterableA, transformer, ",,", "[", "]");
        assertEquals("[2,,4,,4,,6,,6,,6,,8,,8,,8,,8]", result);
        
        result = IterableUtils.toString(iterableA, transformer, ",,", "((", "))");
        assertEquals("((2,,4,,4,,6,,6,,6,,8,,8,,8,,8))", result);
        
    }
    
    public void testToStringDelimiterEmptyIterable(){
        
        Transformer<Integer, String> transformer = new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        };
        
        String result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "(", ")");
        assertEquals("()", result);
        
        result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "", "");
        assertEquals("", result);
    }
    
    public void testToStringDelimiterNullIterable(){
        try{
            IterableUtils.toString(null, new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", "(", ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullTransformer(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), null, "", "(", ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullDelimiter(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, null, "(", ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullPrefix(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", null, ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullSuffix(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", "(", null);
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
}
