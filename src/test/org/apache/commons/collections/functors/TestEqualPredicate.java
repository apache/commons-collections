package org.apache.commons.collections.functors;

import static org.apache.commons.collections.functors.EqualPredicate.equalPredicate;
import static org.apache.commons.collections.functors.NullPredicate.nullPredicate;
import static org.junit.Assert.assertSame;

import org.apache.commons.collections.Predicate;
import org.junit.Test;


public class TestEqualPredicate extends BasicPredicateTestBase {
    private static final EqualsTestObject FALSE_OBJECT = new EqualsTestObject(false);
    private static final EqualsTestObject TRUE_OBJECT = new EqualsTestObject(true);

    @Override
    protected Predicate<Object> generatePredicate() {
       return equalPredicate(null);
    }
    
    @Test
    public void testNullArgumentEqualsNullPredicate() throws Exception {
        assertSame(nullPredicate(), equalPredicate(null));
    }
    
    @Test
    public void objectFactoryUsesEqualsForTest() throws Exception {
        Predicate<EqualsTestObject> predicate = equalPredicate(FALSE_OBJECT);
        assertFalse(predicate, FALSE_OBJECT);
        assertTrue(equalPredicate(TRUE_OBJECT), TRUE_OBJECT);
    }
    
    @Test
    public void testPredicateTypeCanBeSuperClassOfObject() throws Exception {
        Predicate<Number> predicate = equalPredicate((Number) 4);
        assertTrue(predicate, 4);
    }

    public static class EqualsTestObject {
        private final boolean b;

        public EqualsTestObject(boolean b) {
            this.b = b;
        }
        
        @Override
        public boolean equals(Object obj) {
            return b;
        }
    }
}
