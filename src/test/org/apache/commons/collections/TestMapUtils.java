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
import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.PredicatedMap;
import org.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Tests for MapUtils.
 *
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Arun Mammen Thomas
 * @author Max Rydahl Andersen
 * @author Janek Bogucki
 * @author Neil O'Toole
 */
public class TestMapUtils extends BulkTest {

    public TestMapUtils(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestMapUtils.class);
    }

    public Predicate<Object> getPredicate() {
        return new Predicate<Object>() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
    }

    public void testPredicatedMap() {
        Predicate<Object> p = getPredicate();
        Map<Object, Object> map = MapUtils.predicatedMap(new HashMap<Object, Object>(), p, p);
        assertTrue("returned object should be a PredicatedMap", map instanceof PredicatedMap);
        try {
            map = MapUtils.predicatedMap(null, p, p);
            fail("Expecting IllegalArgumentException for null map.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testLazyMapFactory() {
        Factory<Integer> factory = FactoryUtils.constantFactory(new Integer(5));
        Map<Object, Object> map = MapUtils.lazyMap(new HashMap<Object, Object>(), factory);
        assertTrue(map instanceof LazyMap);
        try {
            map = MapUtils.lazyMap(new HashMap<Object, Object>(), (Factory<Object>) null);
            fail("Expecting IllegalArgumentException for null factory");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = MapUtils.lazyMap((Map<Object, Object>) null, factory);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Transformer<Object, Integer> transformer = TransformerUtils.asTransformer(factory);
        map = MapUtils.lazyMap(new HashMap<Object, Object>(), transformer);
        assertTrue(map instanceof LazyMap);
        try {
            map = MapUtils.lazyMap(new HashMap<Object, Object>(), (Transformer<Object, Object>) null);
            fail("Expecting IllegalArgumentException for null transformer");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = MapUtils.lazyMap((Map<Object, Object>) null, transformer);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testLazyMapTransformer() {
        Map<Object, Object> map = MapUtils.lazyMap(new HashMap<Object, Object>(), new Transformer<Object, Object>() {
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
        final Map<String, String> in = new HashMap<String, String>(5, 1);
        in.put("1", "A");
        in.put("2", "B");
        in.put("3", "C");
        in.put("4", "D");
        in.put("5", "E");

        final Set<String> inKeySet = new HashSet<String>(in.keySet());
        final Set<String> inValSet = new HashSet<String>(in.values());

        final Map<String, String> out =  MapUtils.invertMap(in);

        final Set<String> outKeySet = new HashSet<String>(out.keySet());
        final Set<String> outValSet = new HashSet<String>(out.values());

        assertTrue( inKeySet.equals( outValSet ));
        assertTrue( inValSet.equals( outKeySet ));

        assertEquals( "1", out.get("A"));
        assertEquals( "2", out.get("B"));
        assertEquals( "3", out.get("C"));
        assertEquals( "4", out.get("D"));
        assertEquals( "5", out.get("E"));
    }

    public void testPutAll_Map_array() {
        try {
            MapUtils.putAll(null, null);
            fail();
        } catch (NullPointerException ex) {}
        try {
            MapUtils.putAll(null, new Object[0]);
            fail();
        } catch (NullPointerException ex) {}

        Map<String, String> test = MapUtils.putAll(new HashMap<String, String>(), new String[0]);
        assertEquals(0, test.size());

        // sub array
        test = MapUtils.putAll(new HashMap<String, String>(), new String[][] {
            {"RED", "#FF0000"},
            {"GREEN", "#00FF00"},
            {"BLUE", "#0000FF"}
        });
        assertEquals(true, test.containsKey("RED"));
        assertEquals("#FF0000", test.get("RED"));
        assertEquals(true, test.containsKey("GREEN"));
        assertEquals("#00FF00", test.get("GREEN"));
        assertEquals(true, test.containsKey("BLUE"));
        assertEquals("#0000FF", test.get("BLUE"));
        assertEquals(3, test.size());

        try {
            MapUtils.putAll(new HashMap<String, String>(), new String[][] {
                {"RED", "#FF0000"},
                null,
                {"BLUE", "#0000FF"}
            });
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            MapUtils.putAll(new HashMap<String, String>(), new String[][] {
                {"RED", "#FF0000"},
                {"GREEN"},
                {"BLUE", "#0000FF"}
            });
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            MapUtils.putAll(new HashMap<String, String>(), new String[][] {
                {"RED", "#FF0000"},
                {},
                {"BLUE", "#0000FF"}
            });
            fail();
        } catch (IllegalArgumentException ex) {}

        // flat array
        test = MapUtils.putAll(new HashMap<String, String>(), new String[] {
            "RED", "#FF0000",
            "GREEN", "#00FF00",
            "BLUE", "#0000FF"
        });
        assertEquals(true, test.containsKey("RED"));
        assertEquals("#FF0000", test.get("RED"));
        assertEquals(true, test.containsKey("GREEN"));
        assertEquals("#00FF00", test.get("GREEN"));
        assertEquals(true, test.containsKey("BLUE"));
        assertEquals("#0000FF", test.get("BLUE"));
        assertEquals(3, test.size());

        test = MapUtils.putAll(new HashMap<String, String>(), new String[] {
            "RED", "#FF0000",
            "GREEN", "#00FF00",
            "BLUE", "#0000FF",
            "PURPLE" // ignored
        });
        assertEquals(true, test.containsKey("RED"));
        assertEquals("#FF0000", test.get("RED"));
        assertEquals(true, test.containsKey("GREEN"));
        assertEquals("#00FF00", test.get("GREEN"));
        assertEquals(true, test.containsKey("BLUE"));
        assertEquals("#0000FF", test.get("BLUE"));
        assertEquals(3, test.size());

        // map entry
        test = MapUtils.putAll(new HashMap<String, String>(), new Object[] {
            new DefaultMapEntry<String, String>("RED", "#FF0000"),
            new DefaultMapEntry<String, String>("GREEN", "#00FF00"),
            new DefaultMapEntry<String, String>("BLUE", "#0000FF")
        });
        assertEquals(true, test.containsKey("RED"));
        assertEquals("#FF0000", test.get("RED"));
        assertEquals(true, test.containsKey("GREEN"));
        assertEquals("#00FF00", test.get("GREEN"));
        assertEquals(true, test.containsKey("BLUE"));
        assertEquals("#0000FF", test.get("BLUE"));
        assertEquals(3, test.size());

        // key value
        test = MapUtils.putAll(new HashMap<String, String>(), new Object[] {
            new DefaultKeyValue<String, String>("RED", "#FF0000"),
            new DefaultKeyValue<String, String>("GREEN", "#00FF00"),
            new DefaultKeyValue<String, String>("BLUE", "#0000FF")
        });
        assertEquals(true, test.containsKey("RED"));
        assertEquals("#FF0000", test.get("RED"));
        assertEquals(true, test.containsKey("GREEN"));
        assertEquals("#00FF00", test.get("GREEN"));
        assertEquals(true, test.containsKey("BLUE"));
        assertEquals("#0000FF", test.get("BLUE"));
        assertEquals(3, test.size());
    }

    public void testConvertResourceBundle() {
        final Map<String, String> in = new HashMap<String, String>( 5 , 1 );
        in.put("1", "A");
        in.put("2", "B");
        in.put("3", "C");
        in.put("4", "D");
        in.put("5", "E");

        ResourceBundle b = new ListResourceBundle() {
            @Override
            public Object[][] getContents() {
                final Object[][] contents = new Object[ in.size() ][2];
                final Iterator<String> i = in.keySet().iterator();
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

        final Map<String, Object> out = MapUtils.toMap(b);

        assertTrue( in.equals(out));
    }

    public void testDebugAndVerbosePrintCasting() {
        final Map<Integer, String> inner = new HashMap<Integer, String>(2, 1);
        inner.put(2, "B");
        inner.put(3, "C");

        final Map<Integer, Object> outer = new HashMap<Integer, Object>(2, 1);
        outer.put(0, inner);
        outer.put(1, "A");

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

        final Map<Integer, String> map = new TreeMap<Integer, String>();  // treeMap guarantees order across JDKs for test
        map.put(2, "B");
        map.put(3, "C");
        map.put(4, null);

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

        final Map<Integer, String> map = new TreeMap<Integer, String>();  // treeMap guarantees order across JDKs for test
        map.put(2, "B");
        map.put(3, "C");
        map.put(4, null);

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
            MapUtils.verbosePrint(null, "Map", new HashMap<Object, Object>());
            fail("Should generate NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    public void testDebugPrintNullStream() {
        try {
            MapUtils.debugPrint(null, "Map", new HashMap<Object, Object>());
            fail("Should generate NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    public void testDebugPrintNullKey() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, String> map = new HashMap<Object, String>();
        map.put(null, "A");

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

        final Map<Object, String> map = new HashMap<Object, String>();
        map.put(null, "A");

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

        final Map<Object, Map<?, ?>> map = new HashMap<Object, Map<?, ?>>();
        map.put(null, map);

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

        final Map<Object, Map<?, ?>> map = new HashMap<Object, Map<?, ?>>();
        map.put(null, map);

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

        final Map<Object, Object> map = new HashMap<Object, Object>();
        final Map<Object, Object> map2= new HashMap<Object, Object>();
        map.put(null, map2);
        map2.put("2", "B");

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

        final Map<Object, Object> map = new HashMap<Object, Object>();
        final Map<Object, Object> map2= new HashMap<Object, Object>();
        map.put(null, map2);
        map2.put("2", "B");

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

        final Map<Integer, String> inner = new TreeMap<Integer, String>();  // treeMap guarantees order across JDKs for test
        inner.put(2, "B");
        inner.put(3, "C");

        final Map<Integer, Object> outer = new TreeMap<Integer, Object>();
        outer.put(1, inner);
        outer.put(0, "A");
        outer.put(7, outer);

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

        final Map<Integer, String> inner = new TreeMap<Integer, String>();  // treeMap guarantees order across JDKs for test
        inner.put(2, "B");
        inner.put(3, "C");

        final Map<Integer, Object> outer = new TreeMap<Integer, Object>();
        outer.put(1, inner);
        outer.put(0, "A");
        outer.put(7, outer);

        MapUtils.debugPrint(outPrint, "Print Map", outer);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    public void testVerbosePrintSelfReference() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String LABEL = "Print Map";
        final String INDENT = "    ";

        final Map<Integer, Object> grandfather = new TreeMap<Integer, Object>();// treeMap guarantees order across JDKs for test
        final Map<Integer, Object> father = new TreeMap<Integer, Object>();
        final Map<Integer, Object> son    = new TreeMap<Integer, Object>();

        grandfather.put(0, "A");
        grandfather.put(1, father);

        father.put(2, "B");
        father.put(3, grandfather);
        father.put(4, son);

        son.put(5, "C");
        son.put(6, grandfather);
        son.put(7, father);

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

        final Map<Integer, Object> grandfather = new TreeMap<Integer, Object>();// treeMap guarantees order across JDKs for test
        final Map<Integer, Object> father = new TreeMap<Integer, Object>();
        final Map<Integer, Object> son    = new TreeMap<Integer, Object>();

        grandfather.put(0, "A");
        grandfather.put(1, father);

        father.put(2, "B");
        father.put(3, grandfather);
        father.put(4, son);

        son.put(5, "C");
        son.put(6, grandfather);
        son.put(7, father);

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

    //-----------------------------------------------------------------------
    public void testIsEmptyWithEmptyMap() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        assertEquals(true, MapUtils.isEmpty(map));
    }

    public void testIsEmptyWithNonEmptyMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("item", "value");
        assertEquals(false, MapUtils.isEmpty(map));
    }

    public void testIsEmptyWithNull() {
        Map<Object, Object> map = null;
        assertEquals(true, MapUtils.isEmpty(map));
    }

    public void testIsNotEmptyWithEmptyMap() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        assertEquals(false, MapUtils.isNotEmpty(map));
    }

    public void testIsNotEmptyWithNonEmptyMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("item", "value");
        assertEquals(true, MapUtils.isNotEmpty(map));
    }

    public void testIsNotEmptyWithNull() {
        Map<Object, Object> map = null;
        assertEquals(false, MapUtils.isNotEmpty(map));
    }

    public void testPopulateMap() {
        // Setup Test Data
        List list = new ArrayList();
        list.add("1");
        list.add("3");
        list.add("5");
        list.add("7");
        list.add("2");
        list.add("4");
        list.add("6");

        // Now test key transform population
        Map map = new HashMap();
        MapUtils.populateMap(map, list, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(list.size(), map.size());

        for (int i = 0; i < list.size(); i++) {
            assertEquals(true, map.containsKey(new Integer((String) list.get(i))));
            assertEquals(false, map.containsKey(list.get(i)));
            assertEquals(true, map.containsValue(list.get(i)));
            assertEquals(list.get(i), map.get(new Integer((String) list.get(i))));
        }

        // Now test both Key-Value transform population
        map = new HashMap();
        MapUtils.populateMap(map, list, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);

        assertEquals(list.size(), map.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(true, map.containsKey(new Integer((String) list.get(i))));
            assertEquals(false, map.containsKey(list.get(i)));
            assertEquals(true, map.containsValue(new Integer((String) list.get(i))));
            assertEquals(new Integer((String) list.get(i)), map.get(new Integer((String) list.get(i))));
        }
    }

    public void testIterableMap() {
        try {
            MapUtils.iterableMap(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("foo", "foov");
        map.put("bar", "barv");
        map.put("baz", "bazv");
        IterableMap<String, String> iMap = MapUtils.iterableMap(map);
        assertEquals(map, iMap);
        assertNotSame(map, iMap);
        HashedMap<String, String> hMap = new HashedMap<String, String>(map);
        assertSame(hMap, MapUtils.iterableMap(hMap));
    }

    public void testIterableSortedMap() {
        try {
            MapUtils.iterableSortedMap(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("foo", "foov");
        map.put("bar", "barv");
        map.put("baz", "bazv");
        IterableSortedMap<String, String> iMap = MapUtils.iterableSortedMap(map);
        assertEquals(map, iMap);
        assertNotSame(map, iMap);
        assertSame(iMap, MapUtils.iterableMap(iMap));
    }

}
