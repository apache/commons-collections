package org.apache.commons.collections.comparators;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestComparableComparator extends TestComparator {

    public TestComparableComparator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestComparableComparator.class);
    }

    public Comparator makeComparator() {
        return new ComparableComparator();
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
