/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestMapUtils.java,v 1.6 2003/08/20 21:03:16 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
package org.apache.commons.collections;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import junit.framework.Test;

/**
 * Tests for MapUtils.
 * 
 * @version $Revision: 1.6 $ $Date: 2003/08/20 21:03:16 $
 * 
 * @author Stephen Colebourne
 * @author Arun Mammen Thomas
 * @author Max Rydahl Andersen
 */
public class TestMapUtils extends BulkTest {

    public TestMapUtils(String name) {
        super(name);
    }


    public static Test suite() {
        return BulkTest.makeSuite(TestMapUtils.class);
    }

    public Predicate getPredicate() {
        return new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
    }


    public void testPredicatedMapIllegalPut() {
        Predicate p = getPredicate();
        Map map = MapUtils.predicatedMap(new HashMap(), p, p);
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
    }

    // Since a typed map is a predicated map, I copied the tests for predicated map
    public void testTypedMapIllegalPut() {
        final Map map = MapUtils.typedMap(new HashMap(), String.class, String.class);
        
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
    
    }

    public BulkTest bulkTestPredicatedMap() {
        return new TestMap("") {
            public boolean useNullKey() {
                return false;
            }

            public boolean useNullValue() {
                return false;
            }

            public Map makeEmptyMap() {
                Predicate p = getPredicate();
                return MapUtils.predicatedMap(new HashMap(), p, p);
            }
        };
    }
    
    public BulkTest bulkTestTypedMap() {
        return new TestMap("") {
            public boolean useNullKey() {
                return false;
            }

            public boolean useNullValue() {
                return false;
            }

            public Map makeEmptyMap() {
                return MapUtils.typedMap(new HashMap(), String.class, String.class);
            }
        };
    }

    public void testLazyMapFactory() {
        Map map = MapUtils.lazyMap(new HashMap(), new Factory() {
            public Object create() {
                return new Integer(5);
            }
        });

        assertEquals(0, map.size());
        Integer i1 = (Integer) map.get("Five");
        assertEquals(new Integer(5), i1);
        assertEquals(1, map.size());
        Integer i2 = (Integer) map.get(new String(new char[] {'F','i','v','e'}));
        assertEquals(new Integer(5), i2);
        assertEquals(1, map.size());
        assertSame(i1, i2);
    }

    public void testLazyMapTransformer() {
        Map map = MapUtils.lazyMap(new HashMap(), new Transformer() {
            public Object transform(Object mapKey) {
                if (mapKey instanceof String) {
                    return new Integer((String) mapKey);
                }
                return null;
            }
        });

        assertEquals(0, map.size());
        Integer i1 = (Integer) map.get("5");
        assertEquals(new Integer(5), i1);
        assertEquals(1, map.size());
        Integer i2 = (Integer) map.get(new String(new char[] {'5'}));
        assertEquals(new Integer(5), i2);
        assertEquals(1, map.size());
        assertSame(i1, i2);
    }

    public void testInvertMap() {
        final Map in = new HashMap( 5 , 1 );
        in.put( "1" , "A" );
        in.put( "2" , "B" );
        in.put( "3" , "C" );
        in.put( "4" , "D" );
        in.put( "5" , "E" );
    
        final Set inKeySet = new HashSet( in.keySet() );
        final Set inValSet = new HashSet( in.values() );
        
        final Map out =  MapUtils.invertMap(in);

        final Set outKeySet = new HashSet( out.keySet() );
        final Set outValSet = new HashSet( out.values() );
        
        assertTrue( inKeySet.equals( outValSet ));
        assertTrue( inValSet.equals( outKeySet ));
        
        assertEquals( out.get("A"), "1" );
        assertEquals( out.get("B"), "2" );
        assertEquals( out.get("C"), "3" );
        assertEquals( out.get("D"), "4" );
        assertEquals( out.get("E"), "5" );
    }
                
    public void testConvertResourceBundle() {
        final Map in = new HashMap( 5 , 1 );
        in.put( "1" , "A" );
        in.put( "2" , "B" );
        in.put( "3" , "C" );
        in.put( "4" , "D" );
        in.put( "5" , "E" );
    
        ResourceBundle b = new ListResourceBundle() {
            public Object[][] getContents() {
                final Object[][] contents = new Object[ in.size() ][2];
                final Iterator i = in.keySet().iterator();
                int n = 0;
                while ( i.hasNext() ) {
                    final Object key = i.next();
                    final Object val = in.get( key );
                    contents[ n ][ 0 ] = key;
                    contents[ n ][ 1 ] = val;
                    ++n;
                }
                return contents;
            }
        };
        
        final Map out = MapUtils.toMap(b); 

        assertTrue( in.equals(out));
    }

    public void testDebugAndVerbosePrintCasting() {
        final Map inner = new HashMap(2, 1);
        inner.put( new Integer(2) , "B" );
        inner.put( new Integer(3) , "C" );

        final Map outer = new HashMap(2, 1);
        outer.put( new Integer(0) , inner );
        outer.put( new Integer(1) , "A");

 
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);
        
        try {
            MapUtils.debugPrint(outPrint, "Print Map", outer);
        } catch (final ClassCastException e) {
            fail("No Casting should be occurring!");
        }
    }
}
