/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestMapUtils.java,v 1.15 2003/09/20 17:02:03 scolebourne Exp $
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
import java.util.TreeMap;

import junit.framework.Test;

import org.apache.commons.collections.decorators.LazyMap;
import org.apache.commons.collections.decorators.PredicatedMap;

/**
 * Tests for MapUtils.
 * 
 * @version $Revision: 1.15 $ $Date: 2003/09/20 17:02:03 $
 * 
 * @author Stephen Colebourne
 * @author Arun Mammen Thomas
 * @author Max Rydahl Andersen
 * @author Janek Bogucki
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

    public void testPredicatedMap() {
        Predicate p = getPredicate();
        Map map = MapUtils.predicatedMap(new HashMap(), p, p);
        assertTrue("returned object should be a PredicatedMap",
            map instanceof PredicatedMap);
        try {
            map = MapUtils.predicatedMap(null, p, p);
            fail("Expecting IllegalArgumentException for null map.");
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
        Factory factory = FactoryUtils.constantFactory(new Integer(5));
        Map map = MapUtils.lazyMap(new HashMap(), factory);       
        assertTrue(map instanceof LazyMap);        
        try {
            map = MapUtils.lazyMap(new HashMap(), (Factory) null);
            fail("Expecting IllegalArgumentException for null factory");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = MapUtils.lazyMap(null, factory);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Transformer transformer = TransformerUtils.asTransformer(factory);
        map = MapUtils.lazyMap(new HashMap(), transformer);       
        assertTrue(map instanceof LazyMap);  
         try {
            map = MapUtils.lazyMap(new HashMap(), (Transformer) null);
            fail("Expecting IllegalArgumentException for null transformer");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = MapUtils.lazyMap(null, transformer);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        }              
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

    public void testDebugAndVerbosePrintNullMap() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String LABEL = "Print Map";
        outPrint.println(LABEL + " = " + String.valueOf((Object) null));
        final String EXPECTED_OUT = out.toString();

        out.reset();            

        MapUtils.debugPrint(outPrint, LABEL, null);
        assertEquals(EXPECTED_OUT, out.toString());
        
        out.reset();
        
        MapUtils.verbosePrint(outPrint, LABEL, null);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintNullLabel() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new TreeMap();  // treeMap guarantees order across JDKs for test
        map.put( new Integer(2) , "B" );
        map.put( new Integer(3) , "C" );
        map.put( new Integer(4) , null );
        
        outPrint.println("{");
        outPrint.println(INDENT + "2 = B");
        outPrint.println(INDENT + "3 = C");
        outPrint.println(INDENT + "4 = null");
        outPrint.println("}");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.verbosePrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }
            
    public void testDebugPrintNullLabel() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new TreeMap();  // treeMap guarantees order across JDKs for test
        map.put( new Integer(2) , "B" );
        map.put( new Integer(3) , "C" );
        map.put( new Integer(4) , null );
    
        outPrint.println("{");
        outPrint.println(INDENT + "2 = B " + String.class.getName());
        outPrint.println(INDENT + "3 = C " + String.class.getName());
        outPrint.println(INDENT + "4 = null");
        outPrint.println("} " + TreeMap.class.getName());
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.debugPrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintNullLabelAndMap() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);
        
        outPrint.println("null");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.verbosePrint(outPrint, null, null);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testDebugPrintNullLabelAndMap() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        outPrint.println("null");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.debugPrint(outPrint, null, null);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintNullStream() {
        try {
            MapUtils.verbosePrint(null, "Map", new HashMap());
            fail("Should generate NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
    
    public void testDebugPrintNullStream() {
        try {
            MapUtils.debugPrint(null, "Map", new HashMap());
            fail("Should generate NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
    
    public void testDebugPrintNullKey() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new HashMap();
        map.put( null , "A" );
    
        outPrint.println("{");
        outPrint.println(INDENT + "null = A " + String.class.getName());
        outPrint.println("} " + HashMap.class.getName());
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.debugPrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintNullKey() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new HashMap();
        map.put( null , "A" );
    
        outPrint.println("{");
        outPrint.println(INDENT + "null = A");
        outPrint.println("}");
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.verbosePrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testDebugPrintNullKeyToMap1() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new HashMap();
        map.put( null , map );
    
        outPrint.println("{");
        outPrint.println(INDENT + "null = (this Map) " + HashMap.class.getName());
        outPrint.println("} " + HashMap.class.getName());
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.debugPrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintNullKeyToMap1() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new HashMap();
        map.put( null , map );
    
        outPrint.println("{");
        outPrint.println(INDENT + "null = (this Map)");
        outPrint.println("}");
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.verbosePrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }    

    public void testDebugPrintNullKeyToMap2() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new HashMap();
        final Map map2= new HashMap();
        map.put( null , map2 );
        map2.put( "2", "B" );
    
        outPrint.println("{");
        outPrint.println(INDENT + "null = ");
        outPrint.println(INDENT + "{");
        outPrint.println(INDENT + INDENT + "2 = B " + String.class.getName());
        outPrint.println(INDENT + "} " + HashMap.class.getName());
        outPrint.println("} " + HashMap.class.getName());
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.debugPrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintNullKeyToMap2() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map map = new HashMap();
        final Map map2= new HashMap();
        map.put( null , map2 );
        map2.put( "2", "B" );
    
        outPrint.println("{");
        outPrint.println(INDENT + "null = ");
        outPrint.println(INDENT + "{");
        outPrint.println(INDENT + INDENT + "2 = B");
        outPrint.println(INDENT + "}");
        outPrint.println("}");
        final String EXPECTED_OUT = out.toString();
        out.reset();
        
        MapUtils.verbosePrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }    

    public void testVerbosePrint() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);
        
        final String LABEL = "Print Map";
        final String INDENT = "    ";
        
        outPrint.println(LABEL + " = ");
        outPrint.println("{");
        outPrint.println(INDENT + "0 = A");
        outPrint.println(INDENT + "1 = ");
        outPrint.println(INDENT + "{");
        outPrint.println(INDENT + INDENT + "2 = B");
        outPrint.println(INDENT + INDENT + "3 = C");
        outPrint.println(INDENT + "}");
        outPrint.println(INDENT + "7 = (this Map)");
        outPrint.println("}");
        
        final String EXPECTED_OUT = out.toString();

        out.reset();            

        final Map inner = new TreeMap();  // treeMap guarantees order across JDKs for test
        inner.put( new Integer(2) , "B" );
        inner.put( new Integer(3) , "C" );

        final Map outer = new TreeMap();
        outer.put( new Integer(1) , inner );
        outer.put( new Integer(0) , "A");
        outer.put( new Integer(7) , outer);

        MapUtils.verbosePrint(outPrint, "Print Map", outer);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testDebugPrint() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);
        
        final String LABEL = "Print Map";
        final String INDENT = "    ";
        
        outPrint.println(LABEL + " = ");
        outPrint.println("{");
        outPrint.println(INDENT + "0 = A " + String.class.getName());
        outPrint.println(INDENT + "1 = ");
        outPrint.println(INDENT + "{");
        outPrint.println(INDENT + INDENT + "2 = B " + String.class.getName());
        outPrint.println(INDENT + INDENT + "3 = C " + String.class.getName());
        outPrint.println(INDENT + "} " + TreeMap.class.getName());
        outPrint.println(INDENT + "7 = (this Map) " + TreeMap.class.getName());
        outPrint.println("} " + TreeMap.class.getName());
        
        final String EXPECTED_OUT = out.toString();

        out.reset();            

        final Map inner = new TreeMap();  // treeMap guarantees order across JDKs for test
        inner.put( new Integer(2) , "B" );
        inner.put( new Integer(3) , "C" );

        final Map outer = new TreeMap();
        outer.put( new Integer(1) , inner );
        outer.put( new Integer(0) , "A");
        outer.put( new Integer(7) , outer);

        MapUtils.debugPrint(outPrint, "Print Map", outer);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintSelfReference() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String LABEL = "Print Map";
        final String INDENT = "    ";
        

        final Map grandfather = new TreeMap();// treeMap guarantees order across JDKs for test
        final Map father = new TreeMap();
        final Map son    = new TreeMap();
        
        grandfather.put( new Integer(0), "A" );
        grandfather.put( new Integer(1), father );
        
        father.put( new Integer(2), "B" );
        father.put( new Integer(3), grandfather);
        father.put( new Integer(4), son);
        
        son.put( new Integer(5), "C");
        son.put( new Integer(6), grandfather);
        son.put( new Integer(7), father);
        
        outPrint.println(LABEL + " = ");
        outPrint.println("{");
        outPrint.println(INDENT + "0 = A");
        outPrint.println(INDENT + "1 = ");
        outPrint.println(INDENT + "{");
        outPrint.println(INDENT + INDENT + "2 = B");
        outPrint.println(INDENT + INDENT + "3 = (ancestor[0] Map)");
        outPrint.println(INDENT + INDENT + "4 = ");
        outPrint.println(INDENT + INDENT + "{");
        outPrint.println(INDENT + INDENT + INDENT + "5 = C");
        outPrint.println(INDENT + INDENT + INDENT + "6 = (ancestor[1] Map)");
        outPrint.println(INDENT + INDENT + INDENT + "7 = (ancestor[0] Map)");
        outPrint.println(INDENT + INDENT + "}");
        outPrint.println(INDENT + "}");
        outPrint.println("}");

        final String EXPECTED_OUT = out.toString();

        out.reset();
        MapUtils.verbosePrint(outPrint, "Print Map", grandfather);

        assertEquals(EXPECTED_OUT, out.toString());
    }
    
    public void testDebugPrintSelfReference() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);
    
        final String LABEL = "Print Map";
        final String INDENT = "    ";
        
    
        final Map grandfather = new TreeMap();// treeMap guarantees order across JDKs for test
        final Map father = new TreeMap();
        final Map son    = new TreeMap();
        
        grandfather.put( new Integer(0), "A" );
        grandfather.put( new Integer(1), father );
        
        father.put( new Integer(2), "B" );
        father.put( new Integer(3), grandfather);
        father.put( new Integer(4), son);
        
        son.put( new Integer(5), "C");
        son.put( new Integer(6), grandfather);
        son.put( new Integer(7), father);
        
        outPrint.println(LABEL + " = ");
        outPrint.println("{");
        outPrint.println(INDENT + "0 = A " + String.class.getName());
        outPrint.println(INDENT + "1 = ");
        outPrint.println(INDENT + "{");
        outPrint.println(INDENT + INDENT + "2 = B " + String.class.getName());
        outPrint.println(INDENT + INDENT + "3 = (ancestor[0] Map) " + TreeMap.class.getName());
        outPrint.println(INDENT + INDENT + "4 = ");
        outPrint.println(INDENT + INDENT + "{");
        outPrint.println(INDENT + INDENT + INDENT + "5 = C " + String.class.getName());
        outPrint.println(INDENT + INDENT + INDENT + "6 = (ancestor[1] Map) " + TreeMap.class.getName());
        outPrint.println(INDENT + INDENT + INDENT + "7 = (ancestor[0] Map) " + TreeMap.class.getName());
        outPrint.println(INDENT + INDENT + "} " + TreeMap.class.getName());
        outPrint.println(INDENT + "} " + TreeMap.class.getName());
        outPrint.println("} " + TreeMap.class.getName());
    
        final String EXPECTED_OUT = out.toString();
    
        out.reset();
        MapUtils.debugPrint(outPrint, "Print Map", grandfather);
    
        assertEquals(EXPECTED_OUT, out.toString());
    }
    
}
