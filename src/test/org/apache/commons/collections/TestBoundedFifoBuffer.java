package org.apache.commons.collections;


import junit.framework.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 *  Test cases for BoundedFifoBuffer.
 */
public class TestBoundedFifoBuffer extends TestCollection {

    public TestBoundedFifoBuffer(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestBoundedFifoBuffer.class);
    }

    /**
     *  Returns an empty BoundedFifoBuffer that won't overflow.  
     *  
     *  @return an empty BoundedFifoBuffer
     */
    public Collection makeCollection() {
        return new BoundedFifoBuffer(100);
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
     *  Overridden because BoundedFifoBuffer doesn't support null elements.
     *
     *  @return an array of random objects without a null element
     */
    public Object[] getFullElements() {
        return getFullNonNullElements();
    }


    /**
     *  Overridden, because BoundedFifoBuffer's iterators aren't fail-fast.
     */
    public void testCollectionIteratorFailFast() {
    }


    /**
     *  Runs through the regular verifications, but also verifies that 
     *  the buffer contains the same elements in the same sequence as the
     *  list.
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
     * Tests that the removal operation actually removes the first element.
     */
    public void testBoundedFifoBufferRemove() {
        resetFull();
        int size = confirmed.size();
        for (int i = 0; i < size; i++) {
            Object o1 = ((BoundedFifoBuffer)collection).remove();
            Object o2 = ((ArrayList)confirmed).remove(0);
            assertEquals("Removed objects should be equal", o1, o2);
            verify();
        }

        try {
            ((BoundedFifoBuffer)collection).remove();
            fail("Empty buffer should raise Underflow.");
        } catch (BufferUnderflowException e) {
            // expected
        }
    }

}
