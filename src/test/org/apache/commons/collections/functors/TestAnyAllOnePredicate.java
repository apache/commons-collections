package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Collections;

/**
 * Base class for tests of AnyPredicate, AllPredicate, and OnePredicate.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 468603 $ $Date: 2006-10-27 17:52:37 -0700 (Fri, 27 Oct 2006) $
 *
 * @author Edwin Tellman
 */
public abstract class TestAnyAllOnePredicate<T> extends TestCompositePredicate<T> {

    /**
     * Creates a new <code>TestCompositePredicate</code>.
     *
     * @param testValue the value which the mock predicates should expect to see (may be null).
     */
    protected TestAnyAllOnePredicate(final T testValue) {
        super(testValue);
    }


    /**
     * Tests whether <code>getInstance</code> with a one element array returns the first element in the array.
     */
    @Test
    public final void singleElementArrayToGetInstance()
    {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(predicate);
        assertSame("expected argument to be returned by getInstance()", predicate, allPredicate);
    }

    /**
     * Tests that passing a singleton collection to <code>getInstance</code> returns the single element in the
     * collection.
     */
    @Test
    public final void singletonCollectionToGetInstance()
    {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(
                Collections.<Predicate<? super T>>singleton(predicate));
        assertSame("expected singleton collection member to be returned by getInstance()",
                predicate, allPredicate);
    }

    /**
     * Tests creating composite predicate instances with single predicates and verifies that the composite returns
     * the same value as the single predicate does. 
     */
    public final void singleValues()
    {
        assertTrue(getPredicateInstance(true).evaluate(null));
        assertFalse(getPredicateInstance(false).evaluate(null));
    }

}
