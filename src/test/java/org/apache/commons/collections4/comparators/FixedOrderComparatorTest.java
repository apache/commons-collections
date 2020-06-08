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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Test class for FixedOrderComparator.
 *
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
    public void testConstructorPlusAdd() {
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>();
        for (final String topCitie : topCities) {
            comparator.add(topCitie);
        }
        final String[] keys = topCities.clone();
        assertComparatorYieldsOrder(keys, comparator);
    }

    /**
     * Tests that the array constructor compares items properly.
     */
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
    public void testLock() {
        final FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCities);
        assertFalse(comparator.isLocked());
        comparator.compare("New York", "Tokyo");
        assertTrue(comparator.isLocked());
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            comparator.add("Minneapolis");
        });
        assertTrue(exception.getMessage().contains("Cannot modify a FixedOrderComparator after a comparison"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            comparator.addAsEqual("New York", "Minneapolis");
        });
        assertTrue(exception.getMessage().contains("Cannot modify a FixedOrderComparator after a comparison"));
    }

    public void testUnknownObjectBehavior() {
        final FixedOrderComparator<String> fixedOrderComparator = new FixedOrderComparator<>(topCities);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fixedOrderComparator.compare("New York", "Minneapolis");
        });
        assertTrue(exception.getMessage().contains("Attempting to compare unknown object Minneapolis"));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            fixedOrderComparator.compare("Minneapolis", "New York");
        });
        assertTrue(exception.getMessage().contains("Attempting to compare unknown object Minneapolis"));

        assertEquals(FixedOrderComparator.UnknownObjectBehavior.EXCEPTION, fixedOrderComparator.getUnknownObjectBehavior());

        FixedOrderComparator<String> comparator = new FixedOrderComparator<>(topCities);
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
