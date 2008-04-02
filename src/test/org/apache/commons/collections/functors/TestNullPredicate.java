package org.apache.commons.collections.functors;

import static org.apache.commons.collections.functors.NullPredicate.nullPredicate;
import static org.junit.Assert.assertSame;

import org.apache.commons.collections.Predicate;
import org.junit.Test;


public class TestNullPredicate extends BasicPredicateTestBase {
    @Test
    public void testNullPredicate() {
        assertSame(NullPredicate.nullPredicate(), NullPredicate.nullPredicate());
        assertTrue(nullPredicate(), null);
    }
    
    public void ensurePredicateCanBeTypedWithoutWarning() throws Exception {
        Predicate<String> predicate = NullPredicate.nullPredicate();
        assertFalse(predicate, cString);
    }

    @Override
    protected Predicate<?> generatePredicate() {
        return nullPredicate();
    }    
}
