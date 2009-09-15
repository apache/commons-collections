package org.apache.commons.collections.functors;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Entry point for all Functors package tests.
 *
 * @version $Revision: 471163 $ $Date: 2006-11-04 02:56:39 -0800 (Sat, 04 Nov 2006) $
 *
 * @author Edwin Tellman
 */
@RunWith(Suite.class)
@SuiteClasses({TestAllPredicate.class,
    TestEqualPredicate.class,
    TestNullPredicate.class})
public class TestAll extends TestCase {
}
