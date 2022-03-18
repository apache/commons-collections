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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.AbstractObjectTest;
import org.junit.Test;

/**
 * Abstract test class for testing the Comparator interface.
 * <p>
 * Concrete subclasses declare the comparator to be tested.
 * They also declare certain aspects of the tests.
 */
public abstract class AbstractComparatorTest<T> extends AbstractObjectTest {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractComparatorTest(final String testName) {
        super(testName);
    }


    /**
     * Implement this method to return a list of sorted objects.
     *
     * @return sorted objects
     */
    public abstract List<T> getComparableObjectsOrdered();

    /**
     * Implements the abstract superclass method to return the comparator.
     *
     * @return a full iterator
     */
    @Override
    public abstract Comparator<T> makeObject();

    /**
     * Overrides superclass to block tests.
     */
    @Override
    public boolean supportsEmptyCollections() {
        return false;
    }

    /**
     * Overrides superclass to block tests.
     */
    @Override
    public boolean supportsFullCollections() {
        return false;
    }

    /**
     * Reverse the list.
     */
    protected void reverseObjects(final List<?> list) {
        Collections.reverse(list);
    }

    /**
     * Randomize the list.
     */
    protected void randomizeObjects(final List<?> list) {
        Collections.shuffle(list);
    }

    /**
     * Sort the list.
     */
    protected void sortObjects(final List<T> list, final Comparator<? super T> comparator) {
        list.sort(comparator);
    }

    /**
     * Test sorting an empty list
     */
    @Test
    public void testEmptyListSort() {
        final List<T> list = new LinkedList<>();
        sortObjects(list, makeObject());

        final List<T> list2 = new LinkedList<>();

        assertEquals("Comparator cannot sort empty lists", list2, list);
    }

    /**
     * Test sorting a reversed list.
     */
    @Test
    public void testReverseListSort() {
        final Comparator<T> comparator = makeObject();

        final List<T> randomList = getComparableObjectsOrdered();
        reverseObjects(randomList);
        sortObjects(randomList, comparator);

        final List<T> orderedList = getComparableObjectsOrdered();

        assertEquals("Comparator did not reorder the List correctly", orderedList, randomList);
    }

    /**
     * Test sorting a random list.
     */
    @Test
    public void testRandomListSort() {
        final Comparator<T> comparator = makeObject();

        final List<T> randomList = getComparableObjectsOrdered();
        randomizeObjects(randomList);
        sortObjects(randomList, comparator);

        final List<T> orderedList = getComparableObjectsOrdered();

        /* debug
        Iterator i = randomList.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
        */

        assertEquals("Comparator did not reorder the List correctly", orderedList, randomList);
    }

    /**
     * Nearly all Comparators should be Serializable.
     */
    @Test
    public void testComparatorIsSerializable() {
        final Comparator<T> comparator = makeObject();
        assertTrue("This comparator should be Serializable.",
                   comparator instanceof Serializable);
    }

    public String getCanonicalComparatorName(final Object object) {
        final StringBuilder retval = new StringBuilder();
        retval.append(TEST_DATA_PATH);
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1);
        retval.append(colName);
        retval.append(".version");
        retval.append(getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    /**
     * Compare the current serialized form of the Comparator
     * against the canonical version in SCM.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testComparatorCompatibility() throws IOException, ClassNotFoundException {
        if (!skipSerializedCanonicalTests()) {
            Comparator<T> comparator = null;

            // test to make sure the canonical form has been preserved
            try {
                comparator = (Comparator<T>) readExternalFormFromDisk(getCanonicalComparatorName(makeObject()));
            } catch (final FileNotFoundException exception) {

                final boolean autoCreateSerialized = false;

                if (autoCreateSerialized) {
                    comparator = makeObject();
                    final String fileName = getCanonicalComparatorName(comparator);
                    writeExternalFormToDisk((Serializable) comparator, fileName);
                    fail("Serialized form could not be found.  A serialized version "
                            + "has now been written (and should be added to CVS): " + fileName);
                } else {
                    fail("The Serialized form could be located to test serialization "
                            + "compatibility: " + exception.getMessage());
                }
            }


            // make sure the canonical form produces the ordering we currently
            // expect
            final List<T> randomList = getComparableObjectsOrdered();
            reverseObjects(randomList);
            sortObjects(randomList, comparator);

            final List<T> orderedList = getComparableObjectsOrdered();

            assertEquals("Comparator did not reorder the List correctly", orderedList, randomList);
        }
    }
}
