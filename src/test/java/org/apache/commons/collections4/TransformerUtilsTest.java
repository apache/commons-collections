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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.junit.jupiter.api.Test;

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
        assertAll(
                () -> assertNotNull(TransformerUtils.exceptionTransformer()),

                () -> assertSame(TransformerUtils.exceptionTransformer(), TransformerUtils.exceptionTransformer()),

                () -> assertThrows(FunctorException.class, () -> TransformerUtils.exceptionTransformer().transform(null)),

                () -> assertThrows(FunctorException.class, () -> TransformerUtils.exceptionTransformer().transform(cString))
        );
    }

    // nullTransformer
    //------------------------------------------------------------------

    @Test
    public void testNullTransformer() {
        assertNotNull(TransformerUtils.nullTransformer());
        assertSame(TransformerUtils.nullTransformer(), TransformerUtils.nullTransformer());
        assertNull(TransformerUtils.nullTransformer().transform(null));
        assertNull(TransformerUtils.nullTransformer().transform(cObject));
        assertNull(TransformerUtils.nullTransformer().transform(cString));
        assertNull(TransformerUtils.nullTransformer().transform(cInteger));
    }

    // nopTransformer
    //------------------------------------------------------------------

    @Test
    public void testNopTransformer() {
        assertNotNull(TransformerUtils.nullTransformer());
        assertSame(TransformerUtils.nullTransformer(), TransformerUtils.nullTransformer());
        assertNull(TransformerUtils.nopTransformer().transform(null));
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
        assertNull(TransformerUtils.cloneTransformer().transform(null));
        assertEquals(cString, TransformerUtils.cloneTransformer().transform(cString));
        assertEquals(cInteger, TransformerUtils.cloneTransformer().transform(cInteger));

        assertThrows(IllegalArgumentException.class, () -> assertEquals(cObject, TransformerUtils.cloneTransformer().transform(cObject)));
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
        assertNull(TransformerUtils.mapTransformer(map).transform(cInteger));
        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.mapTransformer(null));
    }

    // commandTransformer
    //------------------------------------------------------------------

    @Test
    public void testExecutorTransformer() {
        assertNull(TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(null));
        assertEquals(cObject, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(cObject));
        assertEquals(cString, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(cString));
        assertEquals(cInteger, TransformerUtils.asTransformer(ClosureUtils.nopClosure()).transform(cInteger));

        assertThrows(NullPointerException.class, () -> TransformerUtils.asTransformer((Closure<Object>) null));
    }

    // predicateTransformer
    //------------------------------------------------------------------

    @Test
    public void testPredicateTransformer() {
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(null));
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(cObject));
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(cString));
        assertEquals(Boolean.TRUE, TransformerUtils.asTransformer(TruePredicate.truePredicate()).transform(cInteger));

        assertThrows(IllegalArgumentException.class, () -> TransformerUtils.asTransformer((Predicate<Object>) null));
    }

    // factoryTransformer
    //------------------------------------------------------------------

    @Test
    public void testFactoryTransformer() {
        assertNull(TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(null));
        assertNull(TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(cObject));
        assertNull(TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(cString));
        assertNull(TransformerUtils.asTransformer(FactoryUtils.nullFactory()).transform(cInteger));

        assertThrows(NullPointerException.class, () -> TransformerUtils.asTransformer((Factory<Object>) null));
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

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.chainedTransformer(null, null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.chainedTransformer((Transformer[]) null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.chainedTransformer((Collection<Transformer<Object, Object>>) null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.chainedTransformer(null, null)),

                () -> assertThrows(NullPointerException.class, () -> {
                    Collection<Transformer<Object, Object>> coll1 = new ArrayList<>();
                    coll1.add(null);
                    coll1.add(null);
                    TransformerUtils.chainedTransformer(coll1);
                })
        );
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
        assertEquals("A", TransformerUtils.ifTransformer(lessThanFivePredicate, a, b).transform(1));
        assertEquals("B", TransformerUtils.ifTransformer(lessThanFivePredicate, a, b).transform(5));

        // if tests
        final Predicate<String> equalsAPredicate = EqualPredicate.equalPredicate("A");
        assertEquals("C", TransformerUtils.ifTransformer(equalsAPredicate, c).transform("A"));
        assertEquals("B", TransformerUtils.ifTransformer(equalsAPredicate, c).transform("B"));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.ifTransformer(null, null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.ifTransformer(TruePredicate.truePredicate(), null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.ifTransformer(null, ConstantTransformer.constantTransformer("A"))),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.ifTransformer(null, null, null))
        );
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

        assertNull(TransformerUtils.<Object, String>switchTransformer(
                new Predicate[]{EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE")},
                new Transformer[]{a, b}).transform("WELL"));
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
        assertNull(TransformerUtils.switchTransformer(map).transform("WELL"));
        assertEquals("A", TransformerUtils.switchTransformer(map).transform("HELLO"));
        assertEquals("B", TransformerUtils.switchTransformer(map).transform("THERE"));
        map.put(null, c);
        assertEquals("C", TransformerUtils.switchTransformer(map).transform("WELL"));

        assertEquals(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchTransformer(new Predicate[0], new Transformer[0]));
        assertEquals(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchTransformer(new HashMap<Predicate<Object>, Transformer<Object, Object>>()));
        map = new HashMap<>();
        map.put(null, null);
        assertEquals(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchTransformer(map));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.switchTransformer(null, null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.switchTransformer(null, (Transformer[]) null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.switchTransformer(null)),

                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.switchTransformer(new Predicate[2], new Transformer[2])),

                () -> assertThrows(IllegalArgumentException.class, () -> TransformerUtils.switchTransformer(
                        new Predicate[]{TruePredicate.truePredicate()},
                        new Transformer[]{a, b}))
        );
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
        assertNull(TransformerUtils.switchMapTransformer(map).transform("WELL"));
        assertEquals("A", TransformerUtils.switchMapTransformer(map).transform("HELLO"));
        assertEquals("B", TransformerUtils.switchMapTransformer(map).transform("THERE"));
        map.put(null, c);
        assertEquals("C", TransformerUtils.switchMapTransformer(map).transform("WELL"));

        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchMapTransformer(new HashMap<Object, Transformer<Object, Object>>()));
        map = new HashMap<>();
        map.put(null, null);
        assertSame(ConstantTransformer.NULL_INSTANCE, TransformerUtils.switchMapTransformer(map));

        assertThrows(NullPointerException.class, () -> TransformerUtils.switchMapTransformer(null));
    }

    // invokerTransformer
    //------------------------------------------------------------------

    @Test
    public void testInvokerTransformer() {
        final List<Object> list = new ArrayList<>();
        assertEquals(0, TransformerUtils.invokerTransformer("size").transform(list));
        list.add(new Object());
        assertEquals(1, TransformerUtils.invokerTransformer("size").transform(list));
        assertNull(TransformerUtils.invokerTransformer("size").transform(null));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.invokerTransformer(null)),

                () -> assertThrows(FunctorException.class, () -> TransformerUtils.invokerTransformer("noSuchMethod").transform(new Object()))
        );
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
        assertNull(TransformerUtils.invokerTransformer("contains",
                new Class[]{Object.class}, new Object[]{cString}).transform(null));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> TransformerUtils.invokerTransformer(null, null, null)),

                () -> assertThrows(FunctorException.class, () -> TransformerUtils.invokerTransformer("noSuchMethod", new Class[]{Object.class},
                        new Object[]{cString}).transform(new Object())),

                () -> assertThrows(IllegalArgumentException.class, () -> TransformerUtils.invokerTransformer("badArgs", null, new Object[]{cString})),

                () -> assertThrows(IllegalArgumentException.class, () -> TransformerUtils.invokerTransformer("badArgs", new Class[]{Object.class}, null)),

                () -> assertThrows(IllegalArgumentException.class, () -> TransformerUtils.invokerTransformer("badArgs", new Class[]{}, new Object[]{cString}))
        );
    }

    // stringValueTransformer
    //------------------------------------------------------------------

    @Test
    public void testStringValueTransformer() {
        assertNotNull( "StringValueTransformer should NEVER return a null value.",
            TransformerUtils.stringValueTransformer().transform(null));
        assertEquals( "null",
            TransformerUtils.stringValueTransformer().transform(null), "StringValueTransformer should return \"null\" when given a null argument.");
        assertEquals( "6",
            TransformerUtils.stringValueTransformer().transform(6), "StringValueTransformer should return toString value");
    }

    // instantiateFactory
    //------------------------------------------------------------------

    @Test
    public void testInstantiateTransformerNull() {

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> TransformerUtils.instantiateTransformer(null, new Object[]{"str"})),

                () -> assertThrows(IllegalArgumentException.class, () -> TransformerUtils.instantiateTransformer(new Class[]{}, new Object[]{"str"}))
        );

        Transformer<Class<?>, Object> trans = TransformerUtils.instantiateTransformer(new Class[] { Long.class }, new Object[] { null });

        Transformer<Class<?>, Object> finalTrans = trans;
        assertThrows(FunctorException.class, () -> finalTrans.transform(String.class));

        trans = TransformerUtils.instantiateTransformer();
        assertEquals("", trans.transform(String.class));

        trans = TransformerUtils.instantiateTransformer(new Class[] { Long.TYPE }, new Object[] {1000L});
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
        final Object[] singletons = new Object[] {
            ExceptionTransformer.INSTANCE,
            NOPTransformer.INSTANCE,
            StringValueTransformer.stringValueTransformer(),
        };

        for (final Object original : singletons) {
            TestUtils.assertSameAfterSerialization("Singleton pattern broken for " + original.getClass(), original);
        }
    }

}
