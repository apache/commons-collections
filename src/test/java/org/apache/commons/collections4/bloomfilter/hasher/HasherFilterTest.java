package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class HasherFilterTest {

    @Test
    public void testBasicFiltering() {
        Hasher.Filter filter = new Hasher.Filter(10);

        for (int i=0;i<10;i++) {
            assertTrue( filter.test(i));
        }

        for (int i=0;i<10;i++) {
            assertFalse( filter.test(i));
        }

        try {
            filter.test(10);
            fail( "Should have thrown IndexOutOfBounds exception");
        }
        catch (IndexOutOfBoundsException expected) {
            // do nothing.
        }
    }

}
