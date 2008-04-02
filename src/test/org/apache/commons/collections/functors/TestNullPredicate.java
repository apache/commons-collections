package org.apache.commons.collections.functors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.Predicate;
import org.junit.Test;


public class TestNullPredicate extends BasicPredicateTestBase {
    @Test
    public void testNullPredicate() {
        assertNotNull(NullPredicate.nullPredicate());
        assertEquals(NullPredicate.nullPredicate(), NullPredicate.nullPredicate());
        assertTrue(NullPredicate.nullPredicate().evaluate(null));
    }
    
    public void ensurePredicateCanBeTypedWithoutWarning() throws Exception {
        Predicate<String> predicate = NullPredicate.nullPredicate();
        predicate.evaluate(null); //Just "use" the predicate for strict compiler settings
        assertFalse(predicate.evaluate(cString));
    }    
}
