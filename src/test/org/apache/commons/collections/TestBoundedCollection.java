package org.apache.commons.collections;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class TestBoundedCollection extends BulkTest {

    public TestBoundedCollection(String name) {
        super(name);
    }


    public abstract Collection boundedCollection();


    public void testIllegalAdd() {
        Collection c = boundedCollection();
        Integer i = new Integer(3);
        try {
            c.add(i);
            fail("Collection should be full.");
        } catch (IllegalStateException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains(i));
    }


    public void testIllegalAddAll() {
        Collection c = boundedCollection();
        List elements = new ArrayList();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            c.addAll(elements);
            fail("Collection should be full.");
        } catch (IllegalStateException e) {
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
