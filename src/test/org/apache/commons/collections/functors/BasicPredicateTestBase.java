package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class BasicPredicateTestBase {
    protected Object cObject;
    protected String cString;
    protected Integer cInteger;

    @Before
    public void initialiseTestObjects() throws Exception {
        cObject = new Object();
        cString = "Hello";
        cInteger = new Integer(6);
    }
    
    @Test
    public void predicateSanityTests() throws Exception {
        Predicate<?> predicate = generatePredicate();
        Assert.assertNotNull(predicate);
    }

    /**
     * @return a predicate for general sanity tests.
     */
    protected abstract Predicate<?> generatePredicate();

    protected <T> void assertFalse(Predicate<T> predicate, T testObject) {
        Assert.assertFalse(predicate.evaluate(testObject));
    }

    protected <T> void assertTrue(Predicate<T> predicate, T testObject) {
        Assert.assertTrue(predicate.evaluate(testObject));
    }
}
