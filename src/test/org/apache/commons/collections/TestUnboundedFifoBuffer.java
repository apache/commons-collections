package org.apache.commons.collections;


import junit.framework.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 *  Test cases for UnboundedFifoBuffer.
 */
public class TestUnboundedFifoBuffer extends TestCollection {

    public TestUnboundedFifoBuffer(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestUnboundedFifoBuffer.class);
    }

    /**
     *  Returns an empty UnboundedFifoBuffer with a small capacity.
     *
     *  @return an empty UnboundedFifoBuffer
     */
    public Collection makeCollection() {
        return new UnboundedFifoBuffer(5);
    }


    /**
     *  Returns an empty ArrayList.
     *
     *  @return an empty ArrayList
     */
    public Collection makeConfirmedCollection() {
        return new ArrayList();
    }


    /**
     *  Returns a full ArrayList.
     *
     *  @return a full ArrayList
     */
    public Collection makeConfirmedFullCollection() {
        Collection c = makeConfirmedCollection();
        c.addAll(java.util.Arrays.asList(getFullElements()));
        return c;
    }


    /**
     *  Overridden because UnboundedFifoBuffer doesn't allow null elements.
     *
     *  @return an array of random elements without the null element
     */
    public Object[] getFullElements() {
        return getFullNonNullElements();
    }


    /**
     *  Overridden because UnboundedFifoBuffer's iterators aren't fail-fast.
     */
    public void testCollectionIteratorFailFast() {
    }


    /**
     *  Verifies that the ArrayList has the same elements in the same 
     *  sequence as the UnboundedFifoBuffer.
     */
    public void verify() {
        super.verify();
        Iterator iterator1 = collection.iterator();
        Iterator iterator2 = confirmed.iterator();
        while (iterator2.hasNext()) {
            assertTrue(iterator1.hasNext());
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            assertEquals(o1, o2);
        }
    }


    /**
     *  Tests that UnboundedFifoBuffer removes elements in the right order.
     */
    public void testUnboundedFifoBufferRemove() {
        resetFull();
        int size = confirmed.size();
        for (int i = 0; i < size; i++) {
            Object o1 = ((UnboundedFifoBuffer)collection).remove();
            Object o2 = ((ArrayList)confirmed).remove(0);
            assertEquals("Removed objects should be equal", o1, o2);
            verify();
        }
    }

}

