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

import static org.apache.commons.collections4.functors.EqualPredicate.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.bag.HashBag;
import org.junit.Assert;
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
        Collection<Integer> collectionA = new ArrayList<Integer>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        iterableA = collectionA;

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

    private static Predicate<Number> EVEN = new Predicate<Number>() {
        public boolean evaluate(final Number input) {
            return input.intValue() % 2 == 0;
        }
    };

    // -----------------------------------------------------------------------
    @Test
    public void apply() {
        final List<Integer> listA = new ArrayList<Integer>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<Integer>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<List<Integer>>();
        col.add(listA);
        col.add(listB);
        IterableUtils.apply(col, testClosure);
        assertTrue(listA.isEmpty() && listB.isEmpty());
        try {
            IterableUtils.apply(col, null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }

        IterableUtils.apply(null, testClosure);

        // null should be OK
        col.add(null);
        IterableUtils.apply(col, testClosure);
    }

    @Test(expected = FunctorException.class)
    public void applyFailure() {
        final Closure<String> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<String> col = new ArrayList<String>();
        col.add("x");
        IterableUtils.apply(col, testClosure);
    }

    @Test
    public void containsWithEquator() {
        final List<String> base = new ArrayList<String>();
        base.add("AC");
        base.add("BB");
        base.add("CA");

        final Equator<String> secondLetterEquator = new Equator<String>() {

            public boolean equate(String o1, String o2) {
                return o1.charAt(1) == o2.charAt(1);
            }

            public int hash(String o) {
                return o.charAt(1);
            }

        };

        assertFalse(base.contains("CC"));
        assertTrue(IterableUtils.contains(base, "AC", secondLetterEquator));
        assertTrue(IterableUtils.contains(base, "CC", secondLetterEquator));
        assertFalse(IterableUtils.contains(base, "CX", secondLetterEquator));
        assertFalse(IterableUtils.contains(null, null, secondLetterEquator));

        try {
            IterableUtils.contains(base, "AC", null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
        } // this is what we want
    }

    @Test
    public void find() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        Integer test = IterableUtils.find(iterableA, testPredicate);
        assertTrue(test.equals(4));
        testPredicate = equalPredicate((Number) 45);
        test = IterableUtils.find(iterableA, testPredicate);
        assertTrue(test == null);
        assertNull(IterableUtils.find(null,testPredicate));
        try {
            assertNull(IterableUtils.find(iterableA, null));
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
            // expected
        }
    }

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

    @SuppressWarnings("unchecked")
    @Test
    public void partition() {
        List<Integer> input = new ArrayList<Integer>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);
        List<List<Integer>> partitions = IterableUtils.partition(input, EQUALS_TWO);
        assertEquals(2, partitions.size());
        
        // first partition contains 2
        Collection<Integer> partition = partitions.get(0);
        assertEquals(1, partition.size());
        assertEquals(2, CollectionUtils.extractSingleton(partition).intValue());
        
        // second partition contains 1, 3, and 4
        Integer[] expected = {1, 3, 4};
        partition = partitions.get(1);
        Assert.assertArrayEquals(expected, partition.toArray());
        
        partitions = IterableUtils.partition((List<Integer>) null, EQUALS_TWO);
        assertEquals(2, partitions.size());
        assertTrue(partitions.get(0).isEmpty());
        assertTrue(partitions.get(1).isEmpty());

        partitions = IterableUtils.partition(input);
        assertEquals(1, partitions.size());
        assertEquals(input, partitions.get(0));

        try {
            IterableUtils.partition(input, (Predicate<Integer>) null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void partitionMultiplePredicates() {
        List<Integer> input = new ArrayList<Integer>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);
        List<List<Integer>> partitions = IterableUtils.partition(input, EQUALS_TWO, EVEN);

        // first partition contains 2
        Collection<Integer> partition = partitions.get(0);
        assertEquals(1, partition.size());
        assertEquals(2, partition.iterator().next().intValue());
        
        // second partition contains 4
        partition = partitions.get(1);
        assertEquals(1, partition.size());
        assertEquals(4, partition.iterator().next().intValue());
        
        // third partition contains 1 and 3
        Integer[] expected = {1, 3};
        partition = partitions.get(2);
        Assert.assertArrayEquals(expected, partition.toArray());

        try {
            IterableUtils.partition(input, EQUALS_TWO, null);
        } catch (NullPointerException npe) {
            // expected
        }
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
