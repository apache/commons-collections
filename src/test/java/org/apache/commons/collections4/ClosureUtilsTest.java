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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.ExceptionClosure;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.apache.commons.collections4.functors.NOPClosure;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Tests the ClosureUtils class.
 *
 * @since 3.0
 */
public class ClosureUtilsTest {

    private static final Object cString = "Hello";

    static class MockClosure<T> implements Closure<T> {
        int count = 0;

        @Override
        public void execute(final T object) {
            count++;
        }

        public void reset() {
            count = 0;
        }
    }

    static class MockTransformer<T> implements Transformer<T, T> {
        int count = 0;

        @Override
        public T transform(final T object) {
            count++;
            return object;
        }
    }

    @Test
    public void testExceptionClosure() {
        assertNotNull(ClosureUtils.exceptionClosure());
        assertSame(ClosureUtils.exceptionClosure(), ClosureUtils.exceptionClosure());
        assertAll(
                () -> assertThrows(FunctorException.class, () -> ClosureUtils.exceptionClosure().execute(null)),
                () -> assertThrows(FunctorException.class, () -> ClosureUtils.exceptionClosure().execute(cString))
        );
    }

    @Test
    public void testNopClosure() {
        final StringBuilder buf = new StringBuilder("Hello");
        ClosureUtils.nopClosure().execute(null);
        assertEquals("Hello", buf.toString());
        ClosureUtils.nopClosure().execute("Hello");
        assertEquals("Hello", buf.toString());
    }

    @Test
    public void testInvokeClosure() {
        StringBuffer buf = new StringBuffer("Hello"); // Only StringBuffer has setLength() method
        ClosureUtils.invokerClosure("reverse").execute(buf);
        assertEquals("olleH", buf.toString());
        buf = new StringBuffer("Hello");
        ClosureUtils.invokerClosure("setLength", new Class[] {Integer.TYPE}, new Object[] {Integer.valueOf(2)}).execute(buf);
        assertEquals("He", buf.toString());
    }

    @Test
    public void testForClosure() {
        final MockClosure<Object> cmd = new MockClosure<>();
        ClosureUtils.forClosure(5, cmd).execute(null);
        assertEquals(5, cmd.count);
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(0, new MockClosure<>()));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(-1, new MockClosure<>()));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(1, null));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(3, null));
        assertSame(cmd, ClosureUtils.forClosure(1, cmd));
    }

    @Test
    public void testWhileClosure() {
        MockClosure<Object> cmd = new MockClosure<>();
        ClosureUtils.whileClosure(FalsePredicate.falsePredicate(), cmd).execute(null);
        assertEquals(0, cmd.count);

        cmd = new MockClosure<>();
        ClosureUtils.whileClosure(PredicateUtils.uniquePredicate(), cmd).execute(null);
        assertEquals(1, cmd.count);
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.whileClosure(null, ClosureUtils.nopClosure())),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.whileClosure(FalsePredicate.falsePredicate(), null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.whileClosure(null, null))
        );
    }

    @Test
    public void testDoWhileClosure() {
        MockClosure<Object> cmd = new MockClosure<>();
        ClosureUtils.doWhileClosure(cmd, FalsePredicate.falsePredicate()).execute(null);
        assertEquals(1, cmd.count);

        cmd = new MockClosure<>();
        ClosureUtils.doWhileClosure(cmd, PredicateUtils.uniquePredicate()).execute(null);
        assertEquals(2, cmd.count);

        assertThrows(NullPointerException.class, () -> ClosureUtils.doWhileClosure(null, null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testChainedClosure() {
        MockClosure<Object> a = new MockClosure<>();
        MockClosure<Object> b = new MockClosure<>();
        ClosureUtils.chainedClosure(a, b).execute(null);
        assertEquals(1, a.count);
        assertEquals(1, b.count);

        a = new MockClosure<>();
        b = new MockClosure<>();
        ClosureUtils.<Object>chainedClosure(a, b, a).execute(null);
        assertEquals(2, a.count);
        assertEquals(1, b.count);

        a = new MockClosure<>();
        b = new MockClosure<>();
        Collection<Closure<Object>> coll = new ArrayList<>();
        coll.add(b);
        coll.add(a);
        coll.add(b);
        ClosureUtils.<Object>chainedClosure(coll).execute(null);
        assertEquals(1, a.count);
        assertEquals(2, b.count);

        assertSame(NOPClosure.INSTANCE, ClosureUtils.<Object>chainedClosure());
        assertSame(NOPClosure.INSTANCE, ClosureUtils.<Object>chainedClosure(Collections.<Closure<Object>>emptyList()));
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.chainedClosure(null, null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.<Object>chainedClosure((Closure[]) null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.<Object>chainedClosure((Collection<Closure<Object>>) null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.<Object>chainedClosure(null, null)),
                () -> {
                    Collection<Closure<Object>> finalColl = new ArrayList<>();
                    finalColl.add(null);
                    finalColl.add(null);
                    assertThrows(NullPointerException.class, () -> ClosureUtils.chainedClosure(finalColl));
                }
        );
    }

    @Test
    public void testIfClosure() {
        MockClosure<Object> a = new MockClosure<>();
        MockClosure<Object> b;
        ClosureUtils.ifClosure(TruePredicate.truePredicate(), a).execute(null);
        assertEquals(1, a.count);

        a = new MockClosure<>();
        ClosureUtils.ifClosure(FalsePredicate.<Object>falsePredicate(), a).execute(null);
        assertEquals(0, a.count);

        a = new MockClosure<>();
        b = new MockClosure<>();
        ClosureUtils.ifClosure(TruePredicate.<Object>truePredicate(), a, b).execute(null);
        assertEquals(1, a.count);
        assertEquals(0, b.count);

        a = new MockClosure<>();
        b = new MockClosure<>();
        ClosureUtils.ifClosure(FalsePredicate.<Object>falsePredicate(), a, b).execute(null);
        assertEquals(0, a.count);
        assertEquals(1, b.count);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSwitchClosure() {
        final MockClosure<String> a = new MockClosure<>();
        final MockClosure<String> b = new MockClosure<>();
        ClosureUtils.<String>switchClosure(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Closure[] { a, b }).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);

        a.reset();
        b.reset();
        ClosureUtils.<String>switchClosure(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Closure[] { a, b }).execute("HELLO");
        assertEquals(1, a.count);
        assertEquals(0, b.count);

        a.reset();
        b.reset();
        final MockClosure<String> c = new MockClosure<>();
        ClosureUtils.<String>switchClosure(
            new Predicate[] { EqualPredicate.equalPredicate("HELLO"), EqualPredicate.equalPredicate("THERE") },
            new Closure[] { a, b }, c).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        assertEquals(1, c.count);

        a.reset();
        b.reset();
        final Map<Predicate<String>, Closure<String>> map = new HashMap<>();
        map.put(EqualPredicate.equalPredicate("HELLO"), a);
        map.put(EqualPredicate.equalPredicate("THERE"), b);
        ClosureUtils.<String>switchClosure(map).execute(null);
        assertEquals(0, a.count);
        assertEquals(0, b.count);

        a.reset();
        b.reset();
        map.clear();
        map.put(EqualPredicate.equalPredicate("HELLO"), a);
        map.put(EqualPredicate.equalPredicate("THERE"), b);
        ClosureUtils.switchClosure(map).execute("THERE");
        assertEquals(0, a.count);
        assertEquals(1, b.count);

        a.reset();
        b.reset();
        c.reset();
        map.clear();
        map.put(EqualPredicate.equalPredicate("HELLO"), a);
        map.put(EqualPredicate.equalPredicate("THERE"), b);
        map.put(null, c);
        ClosureUtils.switchClosure(map).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        assertEquals(1, c.count);

        assertEquals(NOPClosure.INSTANCE, ClosureUtils.<String>switchClosure(new Predicate[0], new Closure[0]));
        assertEquals(NOPClosure.INSTANCE, ClosureUtils.<String>switchClosure(new HashMap<Predicate<String>, Closure<String>>()));
        map.clear();
        map.put(null, null);
        assertEquals(NOPClosure.INSTANCE, ClosureUtils.switchClosure(map));
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.switchClosure(null, null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.<String>switchClosure((Predicate<String>[]) null, (Closure<String>[]) null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.<String>switchClosure((Map<Predicate<String>, Closure<String>>) null)),
                () -> assertThrows(NullPointerException.class, () -> ClosureUtils.<String>switchClosure(new Predicate[2], new Closure[2])),
                () -> assertThrows(IllegalArgumentException.class, () -> ClosureUtils.<String>switchClosure(
                        new Predicate[]{TruePredicate.<String>truePredicate()},
                        new Closure[]{a, b}))
        );
    }

    @Test
    public void testSwitchMapClosure() {
        final MockClosure<String> a = new MockClosure<>();
        final MockClosure<String> b = new MockClosure<>();
        final Map<String, Closure<String>> map = new HashMap<>();
        map.put("HELLO", a);
        map.put("THERE", b);
        ClosureUtils.switchMapClosure(map).execute(null);
        assertEquals(0, a.count);
        assertEquals(0, b.count);

        a.reset();
        b.reset();
        map.clear();
        map.put("HELLO", a);
        map.put("THERE", b);
        ClosureUtils.switchMapClosure(map).execute("THERE");
        assertEquals(0, a.count);
        assertEquals(1, b.count);

        a.reset();
        b.reset();
        map.clear();
        final MockClosure<String> c = new MockClosure<>();
        map.put("HELLO", a);
        map.put("THERE", b);
        map.put(null, c);
        ClosureUtils.switchMapClosure(map).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        assertEquals(1, c.count);

        assertEquals(NOPClosure.INSTANCE, ClosureUtils.switchMapClosure(new HashMap<String, Closure<String>>()));

        assertThrows(NullPointerException.class, () -> ClosureUtils.switchMapClosure(null));
    }

    @Test
    public void testTransformerClosure() {
        final MockTransformer<Object> mock = new MockTransformer<>();
        final Closure<Object> closure = ClosureUtils.asClosure(mock);
        closure.execute(null);
        assertEquals(1, mock.count);
        closure.execute(null);
        assertEquals(2, mock.count);

        assertEquals(ClosureUtils.nopClosure(), ClosureUtils.asClosure(null));
    }

    /**
     * Test that all Closure singletons hold singleton pattern in
     * serialization/deserialization process.
     */
    @Test
    public void testSingletonPatternInSerialization() {
        final Object[] singletons = {
            ExceptionClosure.INSTANCE,
            NOPClosure.INSTANCE,
        };

        for (final Object original : singletons) {
            TestUtils.assertSameAfterSerialization(
                    "Singleton pattern broken for " + original.getClass(),
                    original
            );
        }
    }

}
