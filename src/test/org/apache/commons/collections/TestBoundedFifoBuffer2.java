package org.apache.commons.collections;


import junit.framework.Test;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


/**
 * Runs tests against a full BoundedFifoBuffer, since many of the algorithms
 * differ depending on whether the fifo is full or not.
 */
public class TestBoundedFifoBuffer2 extends TestBoundedFifoBuffer {

    public TestBoundedFifoBuffer2(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestBoundedFifoBuffer2.class);
    }

    /**
     *  Returns a BoundedFifoBuffer that's filled to capacity.
     *  Any attempt to add to the returned buffer will result in a 
     *  BufferOverflowException.
     *
     *  @return a full BoundedFifoBuffer
     */
    public Collection makeFullCollection() {
        return new BoundedFifoBuffer(Arrays.asList(getFullElements()));
    }


    /**
     *  Overridden to skip the add tests.  All of them would fail with a 
     *  BufferOverflowException.
     *
     *  @return false
     */
    public boolean isAddSupported() {
        return false;
    }


    /**
     *  Overridden because the add operations raise BufferOverflowException
     *  instead of UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
    }


    /**
     *  Tests to make sure the add operations raise BufferOverflowException.
     */
    public void testBufferOverflow() {
        resetFull();
        try {
            collection.add(getOtherElements()[0]);
            fail("add should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();

        try {
            collection.addAll(Arrays.asList(getOtherElements()));
            fail("addAll should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();
    }

}

