package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestComparatorChain extends TestComparator {

    public TestComparatorChain(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestComparatorChain.class);
    }

    /**
     * 
     * @return 
     */
    public Comparator makeComparator() {
        ComparatorChain chain = new ComparatorChain(new ColumnComparator(0));
        chain.addComparator(new ColumnComparator(1),true); // reverse the second column
        chain.addComparator(new ColumnComparator(2),false);
        return chain;
    }

    public void testNoopComparatorChain() {
        ComparatorChain chain = new ComparatorChain();
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);
        chain.addComparator(new ComparableComparator());

        int correctValue = i1.compareTo(i2);
        assertTrue("Comparison returns the right order",chain.compare(i1,i2) == correctValue);
    }

    public void testBadNoopComparatorChain() {
        ComparatorChain chain = new ComparatorChain();
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);
        try {
            chain.compare(i1,i2);
            fail("An exception should be thrown when a chain contains zero comparators.");
        } catch (UnsupportedOperationException e) {

        }
    }

    public void testListComparatorChain() {
        List list = new LinkedList();
        list.add(new ComparableComparator());
        ComparatorChain chain = new ComparatorChain(list);
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);

        int correctValue = i1.compareTo(i2);
        assertTrue("Comparison returns the right order",chain.compare(i1,i2) == correctValue);
    }

    public void testBadListComparatorChain() {
        List list = new LinkedList();
        ComparatorChain chain = new ComparatorChain(list);
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);
        try {
            chain.compare(i1,i2);
            fail("An exception should be thrown when a chain contains zero comparators.");
        } catch (UnsupportedOperationException e) {

        }
    }

    public List getComparableObjectsOrdered() {
        List list = new LinkedList();
        // this is the correct order assuming a
        // "0th forward, 1st reverse, 2nd forward" sort
        list.add(new PseudoRow(1,2,3));
        list.add(new PseudoRow(2,3,5));
        list.add(new PseudoRow(2,2,4));
        list.add(new PseudoRow(2,2,8));
        list.add(new PseudoRow(3,1,0));
        list.add(new PseudoRow(4,4,4));
        list.add(new PseudoRow(4,4,7));
        return list;
    }

    public static class PseudoRow implements Serializable {

        public int cols[] = new int[3];

        public PseudoRow(int col1, int col2, int col3) {
            cols[0] = col1;
            cols[1] = col2;
            cols[2] = col3;
        }

        public int getColumn(int colIndex) {
            return cols[colIndex];
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("[");
            buf.append(cols[0]);
            buf.append(",");
            buf.append(cols[1]);
            buf.append(",");
            buf.append(cols[2]);
            buf.append("]");
            return buf.toString();
        }

        public boolean equals(Object o) {
            if (!(o instanceof PseudoRow)) {
                return false;
            }

            PseudoRow row = (PseudoRow) o;
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

    public static class ColumnComparator implements Comparator,Serializable {

        protected int colIndex = 0;

        public ColumnComparator(int colIndex) {
            this.colIndex = colIndex;
        }

        public int compare(Object o1, Object o2) {

            int col1 = ( (PseudoRow) o1).getColumn(colIndex);
            int col2 = ( (PseudoRow) o2).getColumn(colIndex);

            if (col1 > col2) {
                return 1;
            } else if (col1 < col2) {
                return -1;
            }

            return 0;
        }
    }
}
