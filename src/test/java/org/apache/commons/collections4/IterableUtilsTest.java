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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.bag.HashBag;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for IterableUtils.
 *
 * @since 4.1
 * @version $Id$
 */
public class IterableUtilsTest {

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableA = null;

    /**
     * Iterable of {@link Long}s
     */
    private Iterable<Long> iterableB = null;

    /**
     * An empty Iterable.
     */
    private Iterable<Integer> emptyIterable = null;

    @Before
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

        Collection<Long> collectionB = new LinkedList<Long>();
        collectionB.add(5L);
        collectionB.add(4L);
        collectionB.add(4L);
        collectionB.add(3L);
        collectionB.add(3L);
        collectionB.add(3L);
        collectionB.add(2L);
        collectionB.add(2L);
        collectionB.add(2L);
        collectionB.add(2L);
        iterableB = collectionB;

        emptyIterable = Collections.emptyList();
    }

    private static Predicate<Number> EQUALS_TWO = new Predicate<Number>() {
        public boolean evaluate(final Number input) {
            return input.intValue() == 2;
        }
    };

    // -----------------------------------------------------------------------
    @Test
    public void frequency() {
        assertEquals(4, IterableUtils.frequency(iterableB, EQUALS_TWO));
        assertEquals(0, IterableUtils.frequency(null, EQUALS_TWO));

        try {
            assertEquals(0, IterableUtils.frequency(iterableA, null));
            fail("predicate must not be null");
        } catch (NullPointerException ex) {
            // expected
        }

        try {
            assertEquals(0, IterableUtils.frequency(null, null));
            fail("predicate must not be null");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void matchesAny() {
        final List<Integer> list = new ArrayList<Integer>();
        
        try {
            assertFalse(IterableUtils.matchesAny(null, null));
            fail("predicate must not be null");
        } catch (NullPointerException ex) {
            // expected
        }

        try {
            assertFalse(IterableUtils.matchesAny(list, null));
            fail("predicate must not be null");
        } catch (NullPointerException ex) {
            // expected
        }

        assertFalse(IterableUtils.matchesAny(null, EQUALS_TWO));
        assertFalse(IterableUtils.matchesAny(list, EQUALS_TWO));
        list.add(1);
        list.add(3);
        list.add(4);
        assertFalse(IterableUtils.matchesAny(list, EQUALS_TWO));

        list.add(2);
        assertEquals(true, IterableUtils.matchesAny(list, EQUALS_TWO));
    }

    @Test
    public void matchesAll() {
        try {
            assertFalse(IterableUtils.matchesAll(null, null));
            fail("predicate must not be null");
        } catch (NullPointerException ex) {
            // expected
        }

        try {
            assertFalse(IterableUtils.matchesAll(iterableA, null));
            fail("predicate must not be null");
        } catch (NullPointerException ex) {
            // expected
        }

        Predicate<Integer> lessThanFive = new Predicate<Integer>() {
            public boolean evaluate(Integer object) {
                return object < 5;
            }
        };
        assertTrue(IterableUtils.matchesAll(iterableA, lessThanFive));

        Predicate<Integer> lessThanFour = new Predicate<Integer>() {
            public boolean evaluate(Integer object) {
                return object < 4;
            }
        };
        assertFalse(IterableUtils.matchesAll(iterableA, lessThanFour));

        assertTrue(IterableUtils.matchesAll(null, lessThanFour));
        assertTrue(IterableUtils.matchesAll(emptyIterable, lessThanFour));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<String>();
        bag.add("element", 1);
        assertEquals("element", IterableUtils.get(bag, 0));

        // Collection, non-existent entry
        IterableUtils.get(bag, 1);
    }

    @Test
    public void testToString() {
        String result = IterableUtils.toString(iterableA);
        assertEquals("[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]", result);
        
        result = IterableUtils.toString(new ArrayList<Integer>());
        assertEquals("[]", result);

        result = IterableUtils.toString(null);
        assertEquals("[]", result);

        result = IterableUtils.toString(iterableA, new Transformer<Integer, String>() {
            public String transform(Integer input) {
                return new Integer(input * 2).toString();
            }
        });
        assertEquals("[2, 4, 4, 6, 6, 6, 8, 8, 8, 8]", result);

        result = IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        });
        assertEquals("[]", result);

        result = IterableUtils.toString(null, new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        });
        assertEquals("[]", result);
    }

    @Test
    public void testToStringDelimiter() {
        
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

        result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "(", ")");
        assertEquals("()", result);
        
        result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "", "");
        assertEquals("", result);
    }

    @Test
    public void testToStringWithNullArguments() {
        String result = IterableUtils.toString(null, new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        }, "", "(", ")");
        assertEquals("()", result);

        try {
            IterableUtils.toString(new ArrayList<Integer>(), null, "", "(", ")");
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, null, "(", ")");
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", null, ")");
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", "(", null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }
    }
}
