package org.apache.commons.collections.comparators;

import java.util.Collections;
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
     * ReverseComparator that wraps the java.util.Collections.reverseOrder()
     * Comparator.  The resulting comparator shouls
     * sort according to natural Order.  (Note: we wrap
     * a Comparator taken from the JDK so that we can
     * save a "canonical" form in CVS.
     * 
     * @return Comparator that returns "natural" order
     */
    public Comparator makeComparator() {
        return new ReverseComparator(Collections.reverseOrder());
    }

    public List getComparableObjectsOrdered() {
        List list = new LinkedList();
        list.add(new Integer(1));
        list.add(new Integer(2));
        list.add(new Integer(3));
        list.add(new Integer(4));
        list.add(new Integer(5));
        return list;
    }

}
