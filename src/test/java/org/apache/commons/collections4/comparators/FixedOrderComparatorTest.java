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
package org.apache.commons.collections4.comparators;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

/**
 * Test class for FixedOrderComparator.
 */
public class FixedOrderComparatorTest extends AbstractComparatorTest<String> {

    /**
     * Top cities of the world, by population including metro areas.
     */
    private static final String topCities[] = new String[] {
        "Tokyo",
        "Mexico City",
        "Mumbai",
        "Sao Paulo",
        "New York",
        "Shanghai",
        "Lagos",
        "Los Angeles",
        "Calcutta",
        "Buenos Aires"
    };

    //
    // Initialization and busywork
    //

    public FixedOrderComparatorTest(final String name) {
        super(name);
    }

    //
    // Set up and tear down
    //

    @Override
    public Comparator<String> makeObject() {
        return new FixedOrderComparator<>(topCities);
    }

    @Override
    public List<String> getComparableObjectsOrdered() {
        return Arrays.asList(topCities);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/FixedOrderComparator.version4.obj");
//    }

    //
    // The tests
    //

    /**
     * Tests that the constructor plus add method compares items properly.
     */
    @Test
    public void testConstructorPlusAdd() {
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>();
        for (final String topCity : topCities) {
            comparator.add(topCity);
        }
        final String[] keys = topCities.clone();
        assertComparatorYieldsOrder(keys, comparator);
    }

    /**
     * Tests that the array constructor compares items properly.
     */
    @Test
    public void testArrayConstructor() {
        final String[] keys = topCities.clone();
        final String[] topCitiesForTest = topCities.clone();
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCitiesForTest);
        assertComparatorYieldsOrder(keys, comparator);
        // test that changing input after constructor has no effect
        topCitiesForTest[0] = "Brighton";
        assertComparatorYieldsOrder(keys, comparator);
    }

    /**
     * Tests the list constructor.
     */
    @Test
    public void testListConstructor() {
        final String[] keys = topCities.clone();
        final List<String> topCitiesForTest = new LinkedList<>(Arrays.asList(topCities));
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCitiesForTest);
        assertComparatorYieldsOrder(keys, comparator);
        // test that changing input after constructor has no effect
        topCitiesForTest.set(0, "Brighton");
        assertComparatorYieldsOrder(keys, comparator);
    }

    /**
     * Tests addAsEqual method.
     */
    @Test
    public void testAddAsEqual() {
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCities);
        comparator.addAsEqual("New York", "Minneapolis");
        assertEquals(0, comparator.compare("New York", "Minneapolis"));
        assertEquals(-1, comparator.compare("Tokyo", "Minneapolis"));
        assertEquals(1, comparator.compare("Shanghai", "Minneapolis"));
    }

    /**
     * Tests whether or not updates are disabled after a comparison is made.
     */
    @Test
    public void testLock() {
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCities);
        assertFalse(comparator.isLocked());
        comparator.compare("New York", "Tokyo");
        assertTrue(comparator.isLocked());

        assertThrows(UnsupportedOperationException.class, () -> comparator.add("Minneapolis"),
                "Should have thrown an UnsupportedOperationException");

        assertThrows(UnsupportedOperationException.class, () -> comparator.addAsEqual("New York", "Minneapolis"),
                "Should have thrown an UnsupportedOperationException");
    }

    @Test
    public void testUnknownObjectBehavior() {
        FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCities);

        FixedOrderComparator<String> finalComparator = comparator;
        assertThrows(IllegalArgumentException.class, () -> finalComparator.compare("New York", "Minneapolis"),
                "Should have thrown a IllegalArgumentException");

        assertThrows(IllegalArgumentException.class, () -> finalComparator.compare("Minneapolis", "New York"),
                "Should have thrown a IllegalArgumentException");

        assertEquals(FixedOrderComparator.UnknownObjectBehavior.EXCEPTION, comparator.getUnknownObjectBehavior());

        comparator = new FixedOrderComparator<>(topCities);
        comparator.setUnknownObjectBehavior(FixedOrderComparator.UnknownObjectBehavior.BEFORE);
        assertEquals(FixedOrderComparator.UnknownObjectBehavior.BEFORE, comparator.getUnknownObjectBehavior());
        LinkedList<String> keys = new LinkedList<>(Arrays.asList(topCities));
        keys.addFirst("Minneapolis");
        assertComparatorYieldsOrder(keys.toArray(new String[0]), comparator);

        assertEquals(-1, comparator.compare("Minneapolis", "New York"));
        assertEquals( 1, comparator.compare("New York", "Minneapolis"));
        assertEquals( 0, comparator.compare("Minneapolis", "St Paul"));

        comparator = new FixedOrderComparator<>(topCities);
        comparator.setUnknownObjectBehavior(FixedOrderComparator.UnknownObjectBehavior.AFTER);
        keys = new LinkedList<>(Arrays.asList(topCities));
        keys.add("Minneapolis");
        assertComparatorYieldsOrder(keys.toArray(new String[0]), comparator);

        assertEquals( 1, comparator.compare("Minneapolis", "New York"));
        assertEquals(-1, comparator.compare("New York", "Minneapolis"));
        assertEquals( 0, comparator.compare("Minneapolis", "St Paul"));
    }

    //
    // Helper methods
    //

    /** Shuffles the keys and asserts that the comparator sorts them back to
     * their original order.
     */
    private void assertComparatorYieldsOrder(final String[] orderedObjects,
                                             final Comparator<String> comparator) {
        final String[] keys = orderedObjects.clone();

        // shuffle until the order changes.  It's extremely rare that
        // this requires more than one shuffle.

        boolean isInNewOrder = false;
        final Random rand = new Random();
        while (keys.length > 1 && !isInNewOrder) {
            // shuffle:
            for (int i = keys.length-1; i > 0; i--) {
                final String swap = keys[i];
                final int j = rand.nextInt(i+1);
                keys[i] = keys[j];
                keys[j] = swap;
            }

            // testShuffle
            for (int i = 0; i < keys.length; i++) {
                if (!orderedObjects[i].equals(keys[i])) {
                    isInNewOrder = true;
                    break;
                }
            }
        }

        // The real test:  sort and make sure they come out right.

        Arrays.sort(keys, comparator);

        for (int i = 0; i < orderedObjects.length; i++) {
            assertEquals(orderedObjects[i], keys[i]);
        }
    }

}
