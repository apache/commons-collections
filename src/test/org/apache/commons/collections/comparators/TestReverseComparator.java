package org.apache.commons.collections.comparators;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestReverseComparator extends TestComparator {

    public TestReverseComparator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestReverseComparator.class);
    }

    /**
     * For the purposes of this test, return a 
     * ReverseComparator that wraps a ComparableComparator, 
     * which should produce a backward-ordered list
     * identical to the java.util.Collection.reverseOrder()
     * Comparator.
     * 
     * @return Comparator that reverses a "natural" order
     */
    public Comparator makeComparator() {
        return new ReverseComparator(new ComparableComparator());
    }

    public List getComparableObjectsOrdered() {
        List list = new LinkedList();
        list.add(new Integer(5));
        list.add(new Integer(4));
        list.add(new Integer(3));
        list.add(new Integer(2));
        list.add(new Integer(1));
        return list;
    }

}
