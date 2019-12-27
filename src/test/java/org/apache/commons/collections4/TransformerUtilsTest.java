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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.ExceptionTransformer;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.apache.commons.collections4.functors.NOPTransformer;
import org.apache.commons.collections4.functors.StringValueTransformer;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.Test;

/**
 * Tests the TransformerUtils class.
 *
 * @since 3.0
 */
public class TransformerUtilsTest {

    private static final Object cObject = new Object();
    private static final Object cString = "Hello";
    private static final Object cInteger = Integer.valueOf(6);

    // exceptionTransformer
    //------------------------------------------------------------------

    @Test
    public void testExceptionTransformer() {
        assertNotNull(TransformerUtils.exceptionTransformer());
        assertSame(TransformerUtils.exceptionTransformer(), TransformerUtils.exceptionTransformer());
        try {
            TransformerUtils.exceptionTransformer().transform(null);
        } catch (final FunctorException ex) {
            try {
                TransformerUtils.exceptionTransformer().transform(cString);
            } catch (final FunctorException ex2) {
                return;
            }
        }
        fail();
    }

    // nullTransformer
    //------------------------------------------------------------------

    @Test
    public void testNullTransformer() {
        assertNotNull(TransformerUtils.nullTransformer());
        assertSame(TransformerUtils.nullTransformer(), TransformerUtils.nullTransformer());
        assertEquals(null, TransformerUtils.nullTransformer().transform(null));
        assertEquals(null, TransformerUtils.nullTransformer().transform(cObject));
        assertEquals(null, TransformerUtils.nullTransformer().transform(cString));
        assertEquals(null, TransformerUtils.nullTransformer().transform(cInteger));
    }

    // nopTransformer
    //------------------------------------------------------------------

    @Test
    public void testNopTransformer() {
        assertNotNull(TransformerUtils.nullTransformer());
        assertSame(TransformerUtils.nullTransformer(), TransformerUtils.nullTransformer());
        assertEquals(null, TransformerUtils.nopTransformer().transform(null));
        assertEquals(cObject, TransformerUtils.nopTransformer().transform(cObject));
        assertEquals(cString, TransformerUtils.nopTransformer().transform(cString));
        assertEquals(cInteger, TransformerUtils.nopTransformer().transform(cInteger));
    }

    // constantTransformer
    //------------------------------------------------------------------

    @Test
    public void testConstantTransformer() {
        assertEquals(cObject, TransformerUtils.constantTransformer(cObject).transform(null));
        assertEquals(cObject, TransformerUtils.constantTransformer(cObject).transform(cObject));
        assertEquals(cObject, TransformerUtils.constantTransformer(cObject).transform(cString));
        assertEquals(cObject, TransformerUtils.constantTransformer(cObject).transform(cInteger));
        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.constantTransformer(null));
    }

    // cloneTransformer
    //------------------------------------------------------------------

    @Test
    public void testCloneTransformer() {
        assertEquals(null, TransformerUtils.cloneTransformer().transform(null));
        assertEquals(cString, TransformerUtils.cloneTransformer().transform(cString));
        assertEquals(cInteger, TransformerUtils.cloneTransformer().transform(cInteger));
        try {
            assertEquals(cObject, TransformerUtils.cloneTransformer().transform(cObject));
        } catch (final IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    // mapTransformer
    //------------------------------------------------------------------

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testMapTransformer() {
        final Map<Object, Integer> map = new HashMap<>();
        map.put(null, 0);
        map.put(cObject, 1);
        map.put(cString, 2);
        assertEquals(Integer.valueOf(0), TransformerUtils.mapTransformer(map).transform(null));
        assertEquals(Integer.valueOf(1), TransformerUtils.mapTransformer(map).transform(cObject));
        assertEquals(Integer.valueOf(2), TransformerUtils.mapTransformer(map).transform(cString));
        assertEquals(null, TransformerUtils.mapTransformer(map).transform(cInteger));
        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.mapTransformer(null));
    }

    // commandTransformer
    //------------------------------------------------------------------

    @Test
    public void testExecutorTransformer() {
        assertEquals(null, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(null));
        assertEquals(cObject, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(cObject));
        assertEquals(cString, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(cString));
        assertEquals(cInteger, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(cInteger));
        try {
            TransformerUtils.asTransformer((Closure<Object>) null);
        } catch (final NullPointerException ex) {
            return;
        }
        fail();
    }

    // predicateTransformer
    //------------------------------------------------------------------

    @Test
    public void testPredicateTransformer() {
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(null));
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(cObject));
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(cString));
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(cInteger));
        try {
            TransformerUtils.asTransformer((Predicate<Object>) null);
        } catch (final IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    // factoryTransformer
    //------------------------------------------------------------------

    @Test
    public void testFactoryTransformer() {
        assertEquals(null, TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(null));
        assertEquals(null, TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(cObject));
        assertEquals(null, TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(cString));
        assertEquals(null, TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(cInteger));
        try {
            TransformerUtils.asTransformer((Factory<Object>) null);
        } catch (final NullPointerException ex) {
            return;
        }
        fail();
    }

    // chainedTransformer
    //------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testChainedTransformer() {
        final Transformer<Object, Object> a = TransformerUtils.<Object, Object>constantTransformer("A");
        final Transformer<Object, Object> b = TransformerUtils.constantTransformer((Object) "B");

        assertEquals("A", TransformerUtils.chainedTransformer(b, a).transform(null));
        assertEquals("B", TransformerUtils.chainedTransformer(a, b).transform(null));
        assertEquals("A", TransformerUtils.chainedTransformer(b, a).transform(null));
        Collection<Transformer<Object, Object>> coll = new ArrayList<>();
        coll.add(b);
        coll.add(a);
        assertEquals("A", TransformerUtils.chainedTransformer(coll).transform(null));

        assertSame(NOPTransformer.INSTANCE, TransformerUtils.chainedTransformer());
        assertSame(NOPTransformer.INSTANCE, TransformerUtils.chainedTransformer(Collections.<Transformer<Object, Object>>emptyList()));

        try {
            TransformerUtils.chainedTransformer(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.chainedTransformer((Transformer[]) null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.chainedTransformer((Collection<Transformer<Object, Object>>) null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.chainedTransformer(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            coll = new ArrayList<>();
            coll.add(null);
            coll.add(null);
            TransformerUtils.chainedTransformer(coll);
            fail();
        } catch (final NullPointerException ex) {}
    }

    // ifTransformer
    //------------------------------------------------------------------

    @Test
    public void testIfTransformer() {
        final Transformer<Object, String> a = TransformerUtils.constantTransformer("A");
        final Transformer<Object, String> b = TransformerUtils.constantTransformer("B");
        final Transformer<Object, String> c = TransformerUtils.constantTransformer("C");

        assertEquals("A", TransformerUtils.ifTransformer(TruePredicate.truePredicate(), a, b).transform(null));
        assertEquals("B", TransformerUtils.ifTransformer(FalsePredicate.falsePredicate(), a, b).transform(null));

        final Predicate<Integer> lessThanFivePredicate = value -> value < 5;
        // if/else tests
        assertEquals("A", TransformerUtils.<Integer, String>ifTransformer(lessThanFivePredicate, a, b).transform(1));
        assertEquals("B", TransformerUtils.<Integer, String>ifTransformer(lessThanFivePredicate, a, b).transform(5));

        // if tests
        final Predicate<String> equalsAPredicate = EqualPredicate.equalPredicate("A");
        assertEquals("C", TransformerUtils.<String>ifTransformer(equalsAPredicate, c).transform("A"));
        assertEquals("B", TransformerUtils.<String>ifTransformer(equalsAPredicate, c).transform("B"));

        try {
            TransformerUtils.ifTransformer(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.ifTransformer(TruePredicate.truePredicate(), null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.ifTransformer(null, ConstantTransformer.constantTransformer("A"));
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.ifTransformer(null, null, null);
            fail();
        } catch (final NullPointerException ex) {}
    }

    // switchTransformer
    //------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testSwitchTransformer() {
        final Transformer<String, String> a = TransformerUtils.constantTransformer("A");
        final Transformer<String, String> b = TransformerUtils.constantTransformer("B");
        final Transformer<String, String> c = TransformerUtils.constantTransformer("C");

        assertEquals("A", TransformerUtils.switchTransformer(TruePredicate.truePredicate(), a, b).transform(null));
        assertEquals("B", TransformerUtils.switchTransformer(FalsePredicate.falsePredicate(), a, b).transform(null));

        assertEquals(null, TransformerUtils.<Object, String>switchTransformer(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Transformer[] { a, b }).transform("WELL"));
        assertEquals("A", TransformerUtils.switchTransformer(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Transformer[] { a, b }).transform("HELLO"));
        assertEquals("B", TransformerUtils.switchTransformer(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Transformer[] { a, b }).transform("THERE"));

        assertEquals("C", TransformerUtils.switchTransformer(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Transformer[] { a, b }, c).transform("WELL"));

        Map<Predicate<String>, Transformer<String, String>> map = new HashMap<>();
        map.put(EqualPredicate.equalPredicate("HELLO"), a);
        map.put(EqualPredicate.equalPredicate("THERE"), b);
        assertEquals(null, TransformerUtils.switchTransformer(map).transform("WELL"));
        assertEquals("A", TransformerUtils.switchTransformer(map).transform("HELLO"));
        assertEquals("B", TransformerUtils.switchTransformer(map).transform("THERE"));
        map.put(null, c);
        assertEquals("C", TransformerUtils.switchTransformer(map).transform("WELL"));

        assertEquals(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchTransformer(new Predicate[0], new Transformer[0]));
        assertEquals(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchTransformer(new HashMap<Predicate<Object>, Transformer<Object, Object>>()));
        map = new HashMap<>();
        map.put(null, null);
        assertEquals(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchTransformer(map));

        try {
            TransformerUtils.switchTransformer(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.switchTransformer((Predicate[]) null, (Transformer[]) null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.switchTransformer((Map<Predicate<Object>, Transformer<Object, Object>>) null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.switchTransformer(new Predicate[2], new Transformer[2]);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.switchTransformer(
                    new Predicate[] { TruePredicate.truePredicate() },
                    new Transformer[] { a, b });
            fail();
        } catch (final IllegalArgumentException ex) {}
    }

    // switchMapTransformer
    //------------------------------------------------------------------

    @Test
    public void testSwitchMapTransformer() {
        final Transformer<String, String> a = TransformerUtils.constantTransformer("A");
        final Transformer<String, String> b = TransformerUtils.constantTransformer("B");
        final Transformer<String, String> c = TransformerUtils.constantTransformer("C");

        Map<String, Transformer<String, String>> map = new HashMap<>();
        map.put("HELLO", a);
        map.put("THERE", b);
        assertEquals(null, TransformerUtils.switchMapTransformer(map).transform("WELL"));
        assertEquals("A", TransformerUtils.switchMapTransformer(map).transform("HELLO"));
        assertEquals("B", TransformerUtils.switchMapTransformer(map).transform("THERE"));
        map.put(null, c);
        assertEquals("C", TransformerUtils.switchMapTransformer(map).transform("WELL"));

        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchMapTransformer(new HashMap<Object, Transformer<Object, Object>>()));
        map = new HashMap<>();
        map.put(null, null);
        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchMapTransformer(map));

        try {
            TransformerUtils.switchMapTransformer(null);
            fail();
        } catch (final NullPointerException ex) {}
    }

    // invokerTransformer
    //------------------------------------------------------------------

    @Test
    public void testInvokerTransformer() {
        final List<Object> list = new ArrayList<>();
        assertEquals(Integer.valueOf(0), TransformerUtils.invokerTransformer("size").transform(list));
        list.add(new Object());
        assertEquals(Integer.valueOf(1), TransformerUtils.invokerTransformer("size").transform(list));
        assertEquals(null, TransformerUtils.invokerTransformer("size").transform(null));

        try {
            TransformerUtils.invokerTransformer(null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.invokerTransformer("noSuchMethod").transform(new Object());
            fail();
        } catch (final FunctorException ex) {}
    }

    // invokerTransformer2
    //------------------------------------------------------------------

    @Test
    public void testInvokerTransformer2() {
        final List<Object> list = new ArrayList<>();
        assertEquals(Boolean.FALSE, TransformerUtils.invokerTransformer("contains",
                new Class[] { Object.class }, new Object[] { cString }).transform(list));
        list.add(cString);
        assertEquals(Boolean.TRUE, TransformerUtils.invokerTransformer("contains",
                new Class[] { Object.class }, new Object[] { cString }).transform(list));
        assertEquals(null, TransformerUtils.invokerTransformer("contains",
                new Class[] { Object.class }, new Object[] { cString }).transform(null));

        try {
            TransformerUtils.invokerTransformer(null, null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            TransformerUtils.invokerTransformer("noSuchMethod", new Class[] { Object.class },
                    new Object[] { cString }).transform(new Object());
            fail();
        } catch (final FunctorException ex) {}
        try {
            TransformerUtils.invokerTransformer("badArgs", null, new Object[] { cString });
            fail();
        } catch (final IllegalArgumentException ex) {}
        try {
            TransformerUtils.invokerTransformer("badArgs", new Class[] { Object.class }, null);
            fail();
        } catch (final IllegalArgumentException ex) {}
        try {
            TransformerUtils.invokerTransformer("badArgs", new Class[] {}, new Object[] { cString });
            fail();
        } catch (final IllegalArgumentException ex) {}
    }

    // stringValueTransformer
    //------------------------------------------------------------------

    @Test
    public void testStringValueTransformer() {
        assertNotNull( "StringValueTransformer should NEVER return a null value.",
           TransformerUtils.stringValueTransformer().transform(null));
        assertEquals( "StringValueTransformer should return \"null\" when given a null argument.", "null",
            TransformerUtils.stringValueTransformer().transform(null));
        assertEquals( "StringValueTransformer should return toString value", "6",
            TransformerUtils.stringValueTransformer().transform(Integer.valueOf(6)));
    }

    // instantiateFactory
    //------------------------------------------------------------------

    @Test
    public void testInstantiateTransformerNull() {
        try {
            TransformerUtils.instantiateTransformer(null, new Object[] { "str" });
            fail();
        } catch (final IllegalArgumentException ex) {}
        try {
            TransformerUtils.instantiateTransformer(new Class[] {}, new Object[] { "str" });
            fail();
        } catch (final IllegalArgumentException ex) {}

        Transformer<Class<?>, Object> trans = TransformerUtils.instantiateTransformer(new Class[] { Long.class }, new Object[] { null });
        try {
            trans.transform(String.class);
            fail();
        } catch (final FunctorException ex) {}

        trans = TransformerUtils.instantiateTransformer();
        assertEquals("", trans.transform(String.class));

        trans = TransformerUtils.instantiateTransformer(new Class[] { Long.TYPE }, new Object[] { new Long(1000L) });
        assertEquals(new Date(1000L), trans.transform(Date.class));
    }

    // misc tests
    //------------------------------------------------------------------

    /**
     * Test that all Transformer singletons hold singleton pattern in
     * serialization/deserialization process.
     */
    @Test
    public void testSingletonPatternInSerialization() {
        final Object[] singletones = new Object[] {
                ExceptionTransformer.INSTANCE,
                NOPTransformer.INSTANCE,
                StringValueTransformer.stringValueTransformer(),
        };

        for (final Object original : singletones) {
            TestUtils.assertSameAfterSerialization("Singleton pattern broken for " + original.getClass(), original);
        }
    }

}
