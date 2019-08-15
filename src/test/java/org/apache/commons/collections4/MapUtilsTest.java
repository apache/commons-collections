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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.apache.commons.collections4.junit.AbstractAvailableLocalesTest;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.collections4.map.PredicatedMap;
import org.junit.Test;

/**
 * Tests for MapUtils.
 *
 */
@SuppressWarnings("boxing")
public class MapUtilsTest extends AbstractAvailableLocalesTest {

    public MapUtilsTest(final Locale locale) {
        super(locale);
    }

    public Predicate<Object> getPredicate() {
        return new Predicate<Object>() {
            @Override
            public boolean evaluate(final Object o) {
                return o instanceof String;
            }
        };
    }

    @Test
    public void testPredicatedMap() {
        final Predicate<Object> p = getPredicate();
        final Map<Object, Object> map = MapUtils.predicatedMap(new HashMap<>(), p, p);
        assertTrue("returned object should be a PredicatedMap", map instanceof PredicatedMap);
        try {
            MapUtils.predicatedMap(null, p, p);
            fail("Expecting NullPointerException for null map.");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testLazyMapFactory() {
        final Factory<Integer> factory = FactoryUtils.constantFactory(Integer.valueOf(5));
        Map<Object, Object> map = MapUtils.lazyMap(new HashMap<>(), factory);
        assertTrue(map instanceof LazyMap);
        try {
            map = MapUtils.lazyMap(new HashMap<>(), (Factory<Object>) null);
            fail("Expecting NullPointerException for null factory");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            map = MapUtils.lazyMap((Map<Object, Object>) null, factory);
            fail("Expecting NullPointerException for null map");
        } catch (final NullPointerException e) {
            // expected
        }
        final Transformer<Object, Integer> transformer = TransformerUtils.asTransformer(factory);
        map = MapUtils.lazyMap(new HashMap<>(), transformer);
        assertTrue(map instanceof LazyMap);
        try {
            map = MapUtils.lazyMap(new HashMap<>(), (Transformer<Object, Object>) null);
            fail("Expecting NullPointerException for null transformer");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            map = MapUtils.lazyMap((Map<Object, Object>) null, transformer);
            fail("Expecting NullPointerException for null map");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testLazyMapTransformer() {
        final Map<Object, Object> map = MapUtils.lazyMap(new HashMap<>(), new Transformer<Object, Object>() {
            @Override
            public Object transform(final Object mapKey) {
                if (mapKey instanceof String) {
                    return Integer.valueOf((String) mapKey);
                }
                return null;
            }
        });

        assertEquals(0, map.size());
        final Integer i1 = (Integer) map.get("5");
        assertEquals(Integer.valueOf(5), i1);
        assertEquals(1, map.size());
        final Integer i2 = (Integer) map.get(new String(new char[] {'5'}));
        assertEquals(Integer.valueOf(5), i2);
        assertEquals(1, map.size());
        assertSame(i1, i2);
    }

    @Test
    public void testInvertMap() {
        final Map<String, String> in = new HashMap<>(5, 1);
        in.put("1", "A");
        in.put("2", "B");
        in.put("3", "C");
        in.put("4", "D");
        in.put("5", "E");

        final Set<String> inKeySet = new HashSet<>(in.keySet());
        final Set<String> inValSet = new HashSet<>(in.values());

        final Map<String, String> out =  MapUtils.invertMap(in);

        final Set<String> outKeySet = new HashSet<>(out.keySet());
        final Set<String> outValSet = new HashSet<>(out.values());

        assertTrue( inKeySet.equals( outValSet ));
        assertTrue( inValSet.equals( outKeySet ));

        assertEquals( "1", out.get("A"));
        assertEquals( "2", out.get("B"));
        assertEquals( "3", out.get("C"));
        assertEquals( "4", out.get("D"));
        assertEquals( "5", out.get("E"));
    }

    @Test
    public void testPutAll_Map_array() {
        try {
            MapUtils.putAll(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            MapUtils.putAll(null, new Object[0]);
            fail();
        } catch (final NullPointerException ex) {}

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
        } catch (final IllegalArgumentException ex) {}

        try {
            MapUtils.putAll(new HashMap<String, String>(), new String[][] {
                    {"RED", "#FF0000"},
                    {"GREEN"},
                    {"BLUE", "#0000FF"}
            });
            fail();
        } catch (final IllegalArgumentException ex) {}

        try {
            MapUtils.putAll(new HashMap<String, String>(), new String[][] {
                    {"RED", "#FF0000"},
                    {},
                    {"BLUE", "#0000FF"}
            });
            fail();
        } catch (final IllegalArgumentException ex) {}

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

        test = MapUtils.putAll(new HashMap<String, String>(), null);
        assertEquals(0, test.size());

        // map entry
        test = MapUtils.putAll(new HashMap<String, String>(), new Object[] {
                new DefaultMapEntry<>("RED", "#FF0000"),
                new DefaultMapEntry<>("GREEN", "#00FF00"),
                new DefaultMapEntry<>("BLUE", "#0000FF")
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
                new DefaultKeyValue<>("RED", "#FF0000"),
                new DefaultKeyValue<>("GREEN", "#00FF00"),
                new DefaultKeyValue<>("BLUE", "#0000FF")
        });
        assertEquals(true, test.containsKey("RED"));
        assertEquals("#FF0000", test.get("RED"));
        assertEquals(true, test.containsKey("GREEN"));
        assertEquals("#00FF00", test.get("GREEN"));
        assertEquals(true, test.containsKey("BLUE"));
        assertEquals("#0000FF", test.get("BLUE"));
        assertEquals(3, test.size());
    }

    @Test
    public void testConvertResourceBundle() {
        final Map<String, String> in = new HashMap<>( 5 , 1 );
        in.put("1", "A");
        in.put("2", "B");
        in.put("3", "C");
        in.put("4", "D");
        in.put("5", "E");

        final ResourceBundle b = new ListResourceBundle() {
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

    @Test
    public void testDebugAndVerbosePrintCasting() {
        final Map<Integer, String> inner = new HashMap<>(2, 1);
        inner.put(2, "B");
        inner.put(3, "C");

        final Map<Integer, Object> outer = new HashMap<>(2, 1);
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

    @Test
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

    @Test
    public void testVerbosePrintNullLabel() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Integer, String> map = new TreeMap<>();  // treeMap guarantees order across JDKs for test
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

    @Test
    public void testDebugPrintNullLabel() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Integer, String> map = new TreeMap<>();  // treeMap guarantees order across JDKs for test
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

    @Test
    public void testVerbosePrintNullLabelAndMap() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        outPrint.println("null");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.verbosePrint(outPrint, null, null);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testDebugPrintNullLabelAndMap() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        outPrint.println("null");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.debugPrint(outPrint, null, null);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testVerbosePrintNullStream() {
        try {
            MapUtils.verbosePrint(null, "Map", new HashMap<>());
            fail("Should generate NullPointerException");
        } catch (final NullPointerException expected) {
        }
    }

    @Test
    public void testDebugPrintNullStream() {
        try {
            MapUtils.debugPrint(null, "Map", new HashMap<>());
            fail("Should generate NullPointerException");
        } catch (final NullPointerException expected) {
        }
    }

    @Test
    public void testDebugPrintNullKey() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, String> map = new HashMap<>();
        map.put(null, "A");

        outPrint.println("{");
        outPrint.println(INDENT + "null = A " + String.class.getName());
        outPrint.println("} " + HashMap.class.getName());
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.debugPrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testVerbosePrintNullKey() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, String> map = new HashMap<>();
        map.put(null, "A");

        outPrint.println("{");
        outPrint.println(INDENT + "null = A");
        outPrint.println("}");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.verbosePrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testDebugPrintNullKeyToMap1() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, Map<?, ?>> map = new HashMap<>();
        map.put(null, map);

        outPrint.println("{");
        outPrint.println(INDENT + "null = (this Map) " + HashMap.class.getName());
        outPrint.println("} " + HashMap.class.getName());
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.debugPrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testVerbosePrintNullKeyToMap1() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, Map<?, ?>> map = new HashMap<>();
        map.put(null, map);

        outPrint.println("{");
        outPrint.println(INDENT + "null = (this Map)");
        outPrint.println("}");
        final String EXPECTED_OUT = out.toString();
        out.reset();

        MapUtils.verbosePrint(outPrint, null, map);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testDebugPrintNullKeyToMap2() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, Object> map = new HashMap<>();
        final Map<Object, Object> map2= new HashMap<>();
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

    @Test
    public void testVerbosePrintNullKeyToMap2() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String INDENT = "    ";

        final Map<Object, Object> map = new HashMap<>();
        final Map<Object, Object> map2= new HashMap<>();
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

    @Test
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

        final Map<Integer, String> inner = new TreeMap<>();  // treeMap guarantees order across JDKs for test
        inner.put(2, "B");
        inner.put(3, "C");

        final Map<Integer, Object> outer = new TreeMap<>();
        outer.put(1, inner);
        outer.put(0, "A");
        outer.put(7, outer);

        MapUtils.verbosePrint(outPrint, "Print Map", outer);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
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

        final Map<Integer, String> inner = new TreeMap<>();  // treeMap guarantees order across JDKs for test
        inner.put(2, "B");
        inner.put(3, "C");

        final Map<Integer, Object> outer = new TreeMap<>();
        outer.put(1, inner);
        outer.put(0, "A");
        outer.put(7, outer);

        MapUtils.debugPrint(outPrint, "Print Map", outer);
        assertEquals(EXPECTED_OUT, out.toString());
    }

    @Test
    public void testVerbosePrintSelfReference() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String LABEL = "Print Map";
        final String INDENT = "    ";

        final Map<Integer, Object> grandfather = new TreeMap<>();// treeMap guarantees order across JDKs for test
        final Map<Integer, Object> father = new TreeMap<>();
        final Map<Integer, Object> son    = new TreeMap<>();

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

    @Test
    public void testDebugPrintSelfReference() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream outPrint = new PrintStream(out);

        final String LABEL = "Print Map";
        final String INDENT = "    ";

        final Map<Integer, Object> grandfather = new TreeMap<>();// treeMap guarantees order across JDKs for test
        final Map<Integer, Object> father = new TreeMap<>();
        final Map<Integer, Object> son    = new TreeMap<>();

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

    @Test
    public void testEmptyIfNull() {
        assertTrue(MapUtils.emptyIfNull(null).isEmpty());

        final Map<Long, Long> map = new HashMap<>();
        assertSame(map, MapUtils.emptyIfNull(map));
    }

    @Test
    public void testIsEmptyWithEmptyMap() {
        final Map<Object, Object> map = new HashMap<>();
        assertEquals(true, MapUtils.isEmpty(map));
    }

    @Test
    public void testIsEmptyWithNonEmptyMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("item", "value");
        assertEquals(false, MapUtils.isEmpty(map));
    }

    @Test
    public void testIsEmptyWithNull() {
        final Map<Object, Object> map = null;
        assertEquals(true, MapUtils.isEmpty(map));
    }

    @Test
    public void testIsNotEmptyWithEmptyMap() {
        final Map<Object, Object> map = new HashMap<>();
        assertEquals(false, MapUtils.isNotEmpty(map));
    }

    @Test
    public void testIsNotEmptyWithNonEmptyMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("item", "value");
        assertEquals(true, MapUtils.isNotEmpty(map));
    }

    @Test
    public void testIsNotEmptyWithNull() {
        final Map<Object, Object> map = null;
        assertEquals(false, MapUtils.isNotEmpty(map));
    }

    @Test
    public void testPopulateMap() {
        // Setup Test Data
        final List<String> list = new ArrayList<>();
        list.add("1");
        list.add("3");
        list.add("5");
        list.add("7");
        list.add("2");
        list.add("4");
        list.add("6");

        // Now test key transform population
        Map<Object, Object> map = new HashMap<>();
        MapUtils.populateMap(map, list, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(list.size(), map.size());

        for (int i = 0; i < list.size(); i++) {
            assertEquals(true, map.containsKey(Integer.valueOf(list.get(i))));
            assertEquals(false, map.containsKey(list.get(i)));
            assertEquals(true, map.containsValue(list.get(i)));
            assertEquals(list.get(i), map.get(Integer.valueOf(list.get(i))));
        }

        // Now test both Key-Value transform population
        map = new HashMap<>();
        MapUtils.populateMap(map, list, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);

        assertEquals(list.size(), map.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(true, map.containsKey(Integer.valueOf(list.get(i))));
            assertEquals(false, map.containsKey(list.get(i)));
            assertEquals(true, map.containsValue(Integer.valueOf(list.get(i))));
            assertEquals(Integer.valueOf(list.get(i)), map.get(Integer.valueOf(list.get(i))));
        }
    }

    /**
     * Test class for populateMap(MultiMap).
     */
    public static class X implements Comparable<X> {
        int key;
        String name;

        public X(final int key, final String name) {
            this.key = key;
            this.name = name;
        }

        @Override
        public int compareTo(final X o) {
            return key - o.key | name.compareTo(o.name);
        }

    }

    @Test
    public void testPopulateMultiMap() {
        // Setup Test Data
        final List<X> list = new ArrayList<>();
        list.add(new X(1, "x1"));
        list.add(new X(2, "x2"));
        list.add(new X(2, "x3"));
        list.add(new X(5, "x4"));
        list.add(new X(5, "x5"));

        // Now test key transform population
        final MultiValueMap<Integer, X> map = MultiValueMap.multiValueMap(new TreeMap<Integer, Collection<X>>());
        MapUtils.populateMap(map, list, new Transformer<X, Integer>() {
            @Override
            public Integer transform(final X input) {
                return input.key;
            }
        }, TransformerUtils.<X> nopTransformer());
        assertEquals(list.size(), map.totalSize());

        for (int i = 0; i < list.size(); i++) {
            assertEquals(true, map.containsKey(list.get(i).key));
            assertEquals(true, map.containsValue(list.get(i)));
        }
    }

    @Test
    public void testIterableMap() {
        try {
            MapUtils.iterableMap(null);
            fail("Should throw NullPointerException");
        } catch (final NullPointerException e) {
        }
        final HashMap<String, String> map = new HashMap<>();
        map.put("foo", "foov");
        map.put("bar", "barv");
        map.put("baz", "bazv");
        final IterableMap<String, String> iMap = MapUtils.iterableMap(map);
        assertEquals(map, iMap);
        assertNotSame(map, iMap);
        final HashedMap<String, String> hMap = new HashedMap<>(map);
        assertSame(hMap, MapUtils.iterableMap(hMap));
    }

    @Test
    public void testIterableSortedMap() {
        try {
            MapUtils.iterableSortedMap(null);
            fail("Should throw NullPointerException");
        } catch (final NullPointerException e) {
        }
        final TreeMap<String, String> map = new TreeMap<>();
        map.put("foo", "foov");
        map.put("bar", "barv");
        map.put("baz", "bazv");
        final IterableSortedMap<String, String> iMap = MapUtils.iterableSortedMap(map);
        assertEquals(map, iMap);
        assertNotSame(map, iMap);
        assertSame(iMap, MapUtils.iterableMap(iMap));
    }

    @Test
    public void testSize0() {
        assertEquals(0, MapUtils.size(new HashMap<>()));
    }

    @Test
    public void testSizeNull() {
        assertEquals(0, MapUtils.size(null));
    }

    @Test
    public void testSize() {
        final HashMap<Object, Object> map = new HashMap<>();
        map.put("A", "1");
        map.put("B", "2");
        assertEquals(2, MapUtils.size(map));
    }

    @Test
    public void testToProperties() {
        final Map<String, String> in = new HashMap<>();
        in.put("key1", "A");
        in.put("key2", "B");
        in.put("key3", "C");

        final Properties out =  MapUtils.toProperties(in);

        assertEquals(in.get("key1"), out.get("key1"));
        assertEquals(in.get("key2"), out.get("key2"));
        assertEquals(in.get("key3"), out.get("key3"));
    }

    @Test
    public void testToPropertiesEmpty() {
        final Map<String, String> in = null;
        final Properties out =  MapUtils.toProperties(in);

        assertEquals(out.size(), 0);
    }

    @Test
    public void testgetDoubleValue() {
        final Map<String, Double> in = new HashMap<>();
        in.put("key", 2.0);

        assertEquals(2.0, MapUtils.getDoubleValue(in,"key", 0.0), 0);
        assertEquals(2.0, MapUtils.getDoubleValue(in,"key"), 0);
        assertEquals(1.0, MapUtils.getDoubleValue(in,"noKey", 1.0), 0);
        assertEquals(5.0, MapUtils.getDoubleValue(in,"noKey", (key)->{
            //sometimes the default value need to be calculated,such as System.currentTimeMillis()
            return 5.0D;
        }),0);

        assertEquals(0, MapUtils.getDoubleValue(in,"noKey"), 0);
        assertEquals(2.0, MapUtils.getDouble(in,"key", 0.0), 0);
        assertEquals(1.0, MapUtils.getDouble(in,"noKey", 1.0), 0);
        assertEquals(1.0, MapUtils.getDouble(in,"noKey", (key)->{
            return 1.0;
        }), 0);


        final Map<String, String> inStr = new HashMap<>();
        final char decimalSeparator = getDecimalSeparator();
        inStr.put("str1", "2" + decimalSeparator + "0");

        assertEquals(MapUtils.getDoubleValue(inStr,"str1", 0.0), 2.0, 0);
    }

    @Test
    public void testgetFloatValue() {
        final Map<String, Float> in = new HashMap<>();
        in.put("key", 2.0f);

        assertEquals(2.0, MapUtils.getFloatValue(in,"key", 0.0f), 0);
        assertEquals(2.0, MapUtils.getFloatValue(in,"key"), 0);
        assertEquals(1.0, MapUtils.getFloatValue(in,"noKey", 1.0f), 0);
        assertEquals(1.0, MapUtils.getFloatValue(in,"noKey", (key)->{
            return 1.0F;
        }), 0);
        assertEquals(0, MapUtils.getFloatValue(in,"noKey"), 0);
        assertEquals(2.0, MapUtils.getFloat(in,"key", 0.0f), 0);
        assertEquals(1.0, MapUtils.getFloat(in,"noKey", 1.0f), 0);
        assertEquals(1.0, MapUtils.getFloat(in,"noKey", (key)->{
            return 1.0F;
        }), 0);

        final Map<String, String> inStr = new HashMap<>();
        final char decimalSeparator = getDecimalSeparator();
        inStr.put("str1", "2" + decimalSeparator + "0");

        assertEquals(MapUtils.getFloatValue(inStr,"str1", 0.0f), 2.0, 0);
    }

    @Test
    public void testgetLongValue() {
        final Map<String, Long> in = new HashMap<>();
        in.put("key", 2L);

        assertEquals(2.0, MapUtils.getLongValue(in,"key", 0L), 0);
        assertEquals(2.0, MapUtils.getLongValue(in,"key"), 0);
        assertEquals(1, MapUtils.getLongValue(in,"noKey", 1L), 0);
        assertEquals(1, MapUtils.getLongValue(in,"noKey", (key)->{
            return 1L;
        }), 0);
        assertEquals(0, MapUtils.getLongValue(in,"noKey"), 0);
        assertEquals(2.0, MapUtils.getLong(in,"key", 0L), 0);
        assertEquals(1, MapUtils.getLong(in,"noKey", 1L), 0);
        assertEquals(1, MapUtils.getLong(in,"noKey", (key)->{
            return 1L;
        }), 0);

        final Map<String, String> inStr = new HashMap<>();
        inStr.put("str1", "2");

        assertEquals(MapUtils.getLongValue(inStr,"str1", 0L), 2, 0);
        assertEquals(MapUtils.getLong(inStr, "str1", 1L), 2, 0);

    }

    @Test
    public void testgetIntValue() {
        final Map<String, Integer> in = new HashMap<>();
        in.put("key", 2);

        assertEquals(2, MapUtils.getIntValue(in,"key", 0), 0);
        assertEquals(2, MapUtils.getIntValue(in,"key"), 0);
        assertEquals(0, MapUtils.getIntValue(in,"noKey", 0), 0);
        assertEquals(0, MapUtils.getIntValue(in,"noKey", (key)->{
            return 0;
        }), 0);
        assertEquals(0, MapUtils.getIntValue(in,"noKey"), 0);
        assertEquals(2, MapUtils.getInteger(in,"key", 0), 0);
        assertEquals(0, MapUtils.getInteger(in,"noKey", 0), 0);
        assertEquals(0, MapUtils.getInteger(in,"noKey", (key)->{
            return 0;
        }), 0);

        final Map<String, String> inStr = new HashMap<>();
        inStr.put("str1", "2");

        assertEquals(MapUtils.getIntValue(inStr,"str1", 0), 2, 0);
    }

    @Test
    public void testgetShortValue() {
        final Map<String, Short> in = new HashMap<>();
        final short val = 10;
        in.put("key", val);

        assertEquals(val, MapUtils.getShortValue(in,"key", val), 0);
        assertEquals(val, MapUtils.getShortValue(in,"key"), 0);
        assertEquals(val, MapUtils.getShortValue(in,"noKey", val), 0);
        assertEquals(val, MapUtils.getShortValue(in,"noKey", (key)->{
            return val;
        }), 0);
        assertEquals(0, MapUtils.getShortValue(in,"noKey"), 0);
        assertEquals(val, MapUtils.getShort(in,"key", val), 0);
        assertEquals(val,MapUtils.getShort(in,"noKey", val), 0);
        assertEquals(val,MapUtils.getShort(in,"noKey", (key)->{
            return val;
        }), 0);

        final Map<String, String> inStr = new HashMap<>();
        inStr.put("str1", "10");

        assertEquals(MapUtils.getShortValue(inStr,"str1", val), val, 0);
    }

    @Test
    public void testgetByteValue() {
        final Map<String, Byte> in = new HashMap<>();
        final byte val = 100;
        in.put("key", val);

        assertEquals(val, MapUtils.getByteValue(in,"key", val), 0);
        assertEquals(val, MapUtils.getByteValue(in,"key"), 0);
        assertEquals(val, MapUtils.getByteValue(in,"noKey", val), 0);
        assertEquals(val, MapUtils.getByteValue(in,"noKey", (key)->{
            return (byte)100;
        }), 0);
        assertEquals(0, MapUtils.getByteValue(in,"noKey"), 0);
        assertEquals(val, MapUtils.getByte(in,"key", val), 0);
        assertEquals(val, MapUtils.getByte(in,"noKey", val), 0);
        assertEquals(val, MapUtils.getByte(in,"noKey", (key)->{
            return val;
        }), 0);


        final Map<String, String> inStr = new HashMap<>();
        inStr.put("str1", "100");

        assertEquals(MapUtils.getByteValue(inStr,"str1", val), val, 0);
    }

    @Test
    public void testgetNumber() {
        final Map<String, Number> in = new HashMap<>();
        final Number val = 1000;
        in.put("key", val);

        assertEquals(val.intValue(), MapUtils.getNumber(in,"key", val).intValue(), 0);
        assertEquals(val.intValue(), MapUtils.getNumber(in,"noKey", val).intValue(), 0);
        assertEquals(val.intValue(), MapUtils.getNumber(in,"noKey", (key)->{
            if (true) {
                return val;
            } else {
                return null;
            }
        }).intValue(), 0);

    }

    @Test
    public void testgetString() {
        final Map<String, String> in = new HashMap<>();
        in.put("key", "str");

        assertEquals("str", MapUtils.getString(in,"key", "defualt"));
        assertEquals("str", MapUtils.getString(in,"key"));
        assertEquals(null, MapUtils.getString(null,"key"));
        assertEquals("default", MapUtils.getString(in,"noKey", "default"));
        assertEquals("default", MapUtils.getString(in,"noKey", (key)->{
            if ("noKey".equals(key)) {
                return "default";
            } else {
                return "";
            }
        }));
        assertEquals("default", MapUtils.getString(null,"noKey", "default"));

    }

    @Test
    public void testgetObject() {
        final Map<String, Object> in = new HashMap<>();
        in.put("key", "str");

        assertEquals("str", MapUtils.getObject(in,"key", "defualt"));
        assertEquals("str", MapUtils.getObject(in,"key"));
        assertEquals(null, MapUtils.getObject(null,"key"));
        assertEquals("default", MapUtils.getObject(in,"noKey", "default"));
        assertEquals("default", MapUtils.getObject(null,"noKey", "default"));
    }

    @Test
    public void testgetBooleanValue() {
        final Map<String, Object> in = new HashMap<>();
        in.put("key", true);
        in.put("keyNumberTrue", 1);
        in.put("keyNumberFalse", 0);
        in.put("keyUnmapped", new Object());

        assertTrue(MapUtils.getBooleanValue(in,"key", true));
        assertTrue(MapUtils.getBooleanValue(in,"key"));
        assertTrue(MapUtils.getBooleanValue(in,"noKey", true));
        assertTrue(MapUtils.getBooleanValue(in,"noKey", (key)->{
            return true;
        }));
        assertTrue(!MapUtils.getBooleanValue(in,"noKey"));
        assertTrue(MapUtils.getBoolean(in,"key", true));
        assertTrue(MapUtils.getBoolean(in,"noKey", true));
        assertTrue(MapUtils.getBoolean(in,"noKey", (key)->{
            if (System.currentTimeMillis() > 0) {
                return true;
            }
            return false;
        }));
        assertEquals(null, MapUtils.getBoolean(null,"noKey"));
        // Values are Numbers
        assertFalse(MapUtils.getBoolean(in,"keyNumberFalse"));
        assertTrue(MapUtils.getBoolean(in,"keyNumberTrue"));
        assertNull(MapUtils.getBoolean(in,"keyString"));

        final Map<String, String> inStr = new HashMap<>();
        inStr.put("str1", "true");

        assertTrue(MapUtils.getBooleanValue(inStr,"str1", true));
        assertTrue(MapUtils.getBoolean(inStr,"str1", true));
    }

    @Test
    public void testgetMap() {
        final Map<String, Map<String,String>> in = new HashMap<>();
        final Map<String, String> valMap = new HashMap<>();
        valMap.put("key1", "value1");
        in.put("key1", valMap);
        final Map<?, ?> outValue =  MapUtils.getMap(in,"key1", (Map<?, ?>) null);

        assertEquals("value1", outValue.get("key1"));
        assertEquals(null, outValue.get("key2"));
        assertEquals(null, MapUtils.getMap(in, "key2", (Map<?, ?>) null));
        assertEquals(null, MapUtils.getMap(null, "key2", (Map<?, ?>) null));
    }

    @Test
    public void testSafeAddToMap() {

        final Map<String, Object> inMap = new HashMap<>();

        MapUtils.safeAddToMap(inMap,"key1", "value1");
        MapUtils.safeAddToMap(inMap,"key2", null);
        assertEquals("value1", inMap.get("key1"));
        assertEquals("", inMap.get("key2"));
    }

    @Test
    public void testOrderedMap() {
        final Map<String, String> inMap = new HashMap<>();
        inMap.put("key1", "value1");
        inMap.put("key2", "value2");
        final Map<String, String> map = MapUtils.orderedMap(inMap);
        assertTrue("returned object should be a OrderedMap", map instanceof OrderedMap);
    }

    private char getDecimalSeparator() {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (numberFormat instanceof DecimalFormat) {
            return ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
        }
        return '.';
    }


}
