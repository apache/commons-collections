package org.apache.commons.collections;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class TestPredicatedCollection extends BulkTest {

    public TestPredicatedCollection(String name) {
        super(name);
    }


    protected abstract Collection predicatedCollection();

    protected Predicate getPredicate() {
        return new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
    }


    public void testIllegalAdd() {
        Collection c = predicatedCollection();
        Integer i = new Integer(3);
        try {
            c.add(i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains(i));   
    }


    public void testIllegalAddAll() {
        Collection c = predicatedCollection();
        List elements = new ArrayList();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            c.addAll(elements);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains("one"));   
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains("two"));   
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains(new Integer(3)));   
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains("four"));   
    }

}
