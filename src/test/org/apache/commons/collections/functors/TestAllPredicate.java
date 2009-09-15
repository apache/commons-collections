package org.apache.commons.collections.functors;

import junit.framework.JUnit4TestAdapter;
import org.apache.commons.collections.Predicate;

import static org.apache.commons.collections.functors.AllPredicate.allPredicate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

/**
 * Tests the org.apache.commons.collections.functors.AllPredicate class.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 468603 $ $Date: 2006-10-27 17:52:37 -0700 (Fri, 27 Oct 2006) $
 *
 * @author Edwin Tellman
 */
public class TestAllPredicate extends TestAnyAllOnePredicate<Integer> {

    /**
     * Creates a JUnit3 test suite.
     *
     * @return a JUnit3 test suite
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestAllPredicate.class);
    }
    
    /**
     * Creates a new <code>TestAllPredicate</code>.
     */
    public TestAllPredicate() {
        super(42);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Predicate<Integer> getPredicateInstance(final Predicate<? super Integer> ... predicates) {
        return AllPredicate.allPredicate(predicates);
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    protected final Predicate<Integer> getPredicateInstance(final Collection<Predicate<Integer>> predicates) {
        return AllPredicate.allPredicate(predicates);
    }

    /**
     * Verifies that providing an empty predicate array evaluates to true.
     */
    @SuppressWarnings({"unchecked"})
    @Test
    public void emptyArrayToGetInstance() {
        assertTrue("empty array not true", getPredicateInstance(new Predicate[] {}).evaluate(null));
    }

    /**
     * Verifies that providing an empty predicate collection evaluates to true.
     */
    @Test
    public void emptyCollectionToGetInstance() {
        final Predicate<Integer> allPredicate = getPredicateInstance(
                Collections.<Predicate<Integer>>emptyList());
        assertTrue("empty collection not true", allPredicate.evaluate(getTestValue()));
    }

    /**
     * Tests whether a single true predicate evaluates to true.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void oneTruePredicate() {
        // use the constructor directly, as getInstance() returns the original predicate when passed
        // an array of size one.
        final Predicate<Integer> predicate = createMockPredicate(true);
        
        assertTrue("single true predicate evaluated to false",
                allPredicate(predicate).evaluate(getTestValue()));
    }

    /**
     * Tests whether a single false predicate evaluates to true.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void oneFalsePredicate() {
        // use the constructor directly, as getInstance() returns the original predicate when passed
        // an array of size one.
        final Predicate<Integer> predicate = createMockPredicate(false);
        assertFalse("single false predicate evaluated to true",
                allPredicate(predicate).evaluate(getTestValue()));
    }

    /**
     * Tests whether multiple true predicates evaluates to true.
     */
    @Test
    public void allTrue() {
        assertTrue("multiple true predicates evaluated to false",
                getPredicateInstance(true, true).evaluate(getTestValue()));
        assertTrue("multiple true predicates evaluated to false",
                getPredicateInstance(true, true, true).evaluate(getTestValue()));
    }

    /**
     * Tests whether combining some true and one false evalutes to false.  Also verifies that only the first
     * false predicate is actually evaluated
     */
    @Test
    public void trueAndFalseCombined() {
        assertFalse("false predicate evaluated to true",
                getPredicateInstance(false, null).evaluate(getTestValue()));
        assertFalse("false predicate evaluated to true",
                getPredicateInstance(false, null, null).evaluate(getTestValue()));
        assertFalse("false predicate evaluated to true",
                getPredicateInstance(true, false, null).evaluate(getTestValue()));
        assertFalse("false predicate evaluated to true",
                getPredicateInstance(true, true, false).evaluate(getTestValue()));
        assertFalse("false predicate evaluated to true",
                getPredicateInstance(true, true, false, null).evaluate(getTestValue()));
    }
}
