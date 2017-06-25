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

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Tests for ComparatorChain.
 *
 * @version $Id$
 */
public class ComparatorChainTest extends AbstractComparatorTest<ComparatorChainTest.PseudoRow> {

    public ComparatorChainTest(final String testName) {
        super(testName);
    }

    @Override
    public Comparator<PseudoRow> makeObject() {
        final ComparatorChain<PseudoRow> chain = new ComparatorChain<PseudoRow>(new ColumnComparator(0));
        chain.addComparator(new ColumnComparator(1), true); // reverse the second column
        chain.addComparator(new ColumnComparator(2), false);
        return chain;
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/ComparatorChain.version4.obj");
//    }

    @Test
    public void testNoopComparatorChain() {
        final ComparatorChain<Integer> chain = new ComparatorChain<Integer>();
        final Integer i1 = Integer.valueOf(4);
        final Integer i2 = Integer.valueOf(6);
        chain.addComparator(new ComparableComparator<Integer>());

        final int correctValue = i1.compareTo(i2);
        assertTrue("Comparison returns the right order", chain.compare(i1, i2) == correctValue);
    }

    @Test
    public void testBadNoopComparatorChain() {
        final ComparatorChain<Integer> chain = new ComparatorChain<Integer>();
        final Integer i1 = Integer.valueOf(4);
        final Integer i2 = Integer.valueOf(6);
        try {
            chain.compare(i1,i2);
            fail("An exception should be thrown when a chain contains zero comparators.");
        } catch (final UnsupportedOperationException e) {
        }
    }

    @Test
    public void testListComparatorChain() {
        final List<Comparator<Integer>> list = new LinkedList<Comparator<Integer>>();
        list.add(new ComparableComparator<Integer>());
        final ComparatorChain<Integer> chain = new ComparatorChain<Integer>(list);
        final Integer i1 = Integer.valueOf(4);
        final Integer i2 = Integer.valueOf(6);

        final int correctValue = i1.compareTo(i2);
        assertTrue("Comparison returns the right order", chain.compare(i1, i2) == correctValue);
    }

    @Test
    public void testBadListComparatorChain() {
        final List<Comparator<Integer>> list = new LinkedList<Comparator<Integer>>();
        final ComparatorChain<Integer> chain = new ComparatorChain<Integer>(list);
        final Integer i1 = Integer.valueOf(4);
        final Integer i2 = Integer.valueOf(6);
        try {
            chain.compare(i1, i2);
            fail("An exception should be thrown when a chain contains zero comparators.");
        } catch (final UnsupportedOperationException e) {
        }
    }

    @Test
    public void testComparatorChainOnMinvaluedCompatator() {
        // -1 * Integer.MIN_VALUE is less than 0,
        // test that ComparatorChain handles this edge case correctly
        final ComparatorChain<Integer> chain = new ComparatorChain<Integer>();
        chain.addComparator(new Comparator<Integer>() {
            @Override
            public int compare(final Integer a, final Integer b) {
                final int result = a.compareTo(b);
                if (result < 0) {
                    return Integer.MIN_VALUE;
                }
                if (result > 0) {
                    return Integer.MAX_VALUE;
                }
                return 0;
            }
        }, true);

        assertTrue(chain.compare(Integer.valueOf(4), Integer.valueOf(5)) > 0);
        assertTrue(chain.compare(Integer.valueOf(5), Integer.valueOf(4)) < 0);
        assertTrue(chain.compare(Integer.valueOf(4), Integer.valueOf(4)) == 0);
    }

    @Test
    public void testEqualsReturnsFalseWithNonNullParameter() throws Exception {

        final ComparatorChain<?> compChain = new ComparatorChain<Object>();

        assertFalse( compChain.equals(new String("a")) );

    }

    @Override
    public List<PseudoRow> getComparableObjectsOrdered() {
        final List<PseudoRow> list = new LinkedList<PseudoRow>();
        // this is the correct order assuming a
        // "0th forward, 1st reverse, 2nd forward" sort
        list.add(new PseudoRow(1, 2, 3));
        list.add(new PseudoRow(2, 3, 5));
        list.add(new PseudoRow(2, 2, 4));
        list.add(new PseudoRow(2, 2, 8));
        list.add(new PseudoRow(3, 1, 0));
        list.add(new PseudoRow(4, 4, 4));
        list.add(new PseudoRow(4, 4, 7));
        return list;
    }

    public static class PseudoRow implements Serializable {

        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = 8085570439751032499L;
        public int cols[] = new int[3];

        public PseudoRow(final int col1, final int col2, final int col3) {
            cols[0] = col1;
            cols[1] = col2;
            cols[2] = col3;
        }

        public int getColumn(final int colIndex) {
            return cols[colIndex];
        }

        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("[");
            buf.append(cols[0]);
            buf.append(",");
            buf.append(cols[1]);
            buf.append(",");
            buf.append(cols[2]);
            buf.append("]");
            return buf.toString();
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof PseudoRow)) {
                return false;
            }

            final PseudoRow row = (PseudoRow) o;
            if (getColumn(0) != row.getColumn(0)) {
                return false;
            }

            if (getColumn(1) != row.getColumn(1)) {
                return false;
            }

            if (getColumn(2) != row.getColumn(2)) {
                return false;
            }

            return true;
        }

    }

    public static class ColumnComparator implements Comparator<PseudoRow>, Serializable {
        private static final long serialVersionUID = -2284880866328872105L;

        protected int colIndex = 0;

        public ColumnComparator(final int colIndex) {
            this.colIndex = colIndex;
        }

        @Override
        public int compare(final PseudoRow o1, final PseudoRow o2) {

            final int col1 = o1.getColumn(colIndex);
            final int col2 = o2.getColumn(colIndex);

            if (col1 > col2) {
                return 1;
            }
            if (col1 < col2) {
                return -1;
            }
            return 0;
        }

        @Override
        public int hashCode() {
            return colIndex;
        }

        @Override
        public boolean equals(final Object that) {
            return that instanceof ColumnComparator && colIndex == ((ColumnComparator) that).colIndex;
        }
    }
}
