/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import static org.apache.commons.collections.functors.NullPredicate.nullPredicate;
import static org.apache.commons.collections.functors.TruePredicate.truePredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.functors.AbstractPredicateTest;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.ExceptionPredicate;
import org.apache.commons.collections.functors.FalsePredicate;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.collections.functors.NullPredicate;
import org.apache.commons.collections.functors.TruePredicate;
import org.junit.Test;

/**
 * Tests the org.apache.commons.collections.PredicateUtils class.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 * @author Matt Benson
 */
@SuppressWarnings("boxing")
public class TestPredicateUtils extends AbstractPredicateTest {
    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {
    }

    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
    }

    // exceptionPredicate
    //------------------------------------------------------------------

    @Test public void testExceptionPredicate() {
        assertNotNull(PredicateUtils.exceptionPredicate());
        assertSame(PredicateUtils.exceptionPredicate(), PredicateUtils.exceptionPredicate());
        try {
            PredicateUtils.exceptionPredicate().evaluate(null);
        } catch (FunctorException ex) {
            try {
                PredicateUtils.exceptionPredicate().evaluate(cString);
            } catch (FunctorException ex2) {
                return;
            }
        }
        fail();
    }

    // notNullPredicate
    //------------------------------------------------------------------

    @Test public void testIsNotNullPredicate() {
        assertNotNull(PredicateUtils.notNullPredicate());
        assertSame(PredicateUtils.notNullPredicate(), PredicateUtils.notNullPredicate());
        assertEquals(false, PredicateUtils.notNullPredicate().evaluate(null));
        assertEquals(true, PredicateUtils.notNullPredicate().evaluate(cObject));
        assertEquals(true, PredicateUtils.notNullPredicate().evaluate(cString));
        assertEquals(true, PredicateUtils.notNullPredicate().evaluate(cInteger));
    }

    // identityPredicate
    //------------------------------------------------------------------

    @Test public void testIdentityPredicate() {
        assertSame(nullPredicate(), PredicateUtils.identityPredicate(null));
        assertNotNull(PredicateUtils.identityPredicate(new Integer(6)));
        assertEquals(false, PredicateUtils.identityPredicate(new Integer(6)).evaluate(null));
        assertEquals(false, PredicateUtils.<Object>identityPredicate(new Integer(6)).evaluate(cObject));
        assertEquals(false, PredicateUtils.<Object>identityPredicate(new Integer(6)).evaluate(cString));
        assertEquals(false, PredicateUtils.identityPredicate(new Integer(6)).evaluate(cInteger));
        assertEquals(true, PredicateUtils.identityPredicate(cInteger).evaluate(cInteger));
    }

    // truePredicate
    //------------------------------------------------------------------

    @Test public void testTruePredicate() {
        assertNotNull(TruePredicate.truePredicate());
        assertSame(TruePredicate.truePredicate(), TruePredicate.truePredicate());
        assertEquals(true, TruePredicate.truePredicate().evaluate(null));
        assertEquals(true, TruePredicate.truePredicate().evaluate(cObject));
        assertEquals(true, TruePredicate.truePredicate().evaluate(cString));
        assertEquals(true, TruePredicate.truePredicate().evaluate(cInteger));
    }

    // falsePredicate
    //------------------------------------------------------------------

    @Test public void testFalsePredicate() {
        assertNotNull(FalsePredicate.falsePredicate());
        assertSame(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate());
        assertEquals(false, FalsePredicate.falsePredicate().evaluate(null));
        assertEquals(false, FalsePredicate.falsePredicate().evaluate(cObject));
        assertEquals(false, FalsePredicate.falsePredicate().evaluate(cString));
        assertEquals(false, FalsePredicate.falsePredicate().evaluate(cInteger));
    }

    // notPredicate
    //------------------------------------------------------------------

    @Test public void testNotPredicate() {
        assertNotNull(PredicateUtils.notPredicate(TruePredicate.truePredicate()));
        assertEquals(false, PredicateUtils.notPredicate(TruePredicate.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.notPredicate(TruePredicate.truePredicate()).evaluate(cObject));
        assertEquals(false, PredicateUtils.notPredicate(TruePredicate.truePredicate()).evaluate(cString));
        assertEquals(false, PredicateUtils.notPredicate(TruePredicate.truePredicate()).evaluate(cInteger));
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testNotPredicateEx() {
        PredicateUtils.notPredicate(null);
    }

    // andPredicate
    //------------------------------------------------------------------

    @Test public void testAndPredicate() {
        assertEquals(true, PredicateUtils.andPredicate(TruePredicate.truePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.andPredicate(TruePredicate.truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.andPredicate(FalsePredicate.falsePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.andPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAndPredicateEx() {
        PredicateUtils.andPredicate(null, null);
    }

    // allPredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test public void testAllPredicate() {
        assertTrue(AllPredicate.allPredicate(new Predicate[] {}), null);
        assertEquals(true, AllPredicate.allPredicate(new Predicate[] {
                TruePredicate.truePredicate(), TruePredicate.truePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(false, AllPredicate.allPredicate(new Predicate[] {
                TruePredicate.truePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(false, AllPredicate.allPredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(false, AllPredicate.allPredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()}).evaluate(null));
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(true, AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertEquals(false, AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertFalse(AllPredicate.allPredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertTrue(AllPredicate.allPredicate(coll), null);
        coll.clear();
        assertTrue(AllPredicate.allPredicate(coll), null);
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAllPredicateEx1() {
        AllPredicate.allPredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class) 
    public void testAllPredicateEx2() {
        AllPredicate.<Object>allPredicate(new Predicate[] { null });
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class) 
    public void testAllPredicateEx3() {
        AllPredicate.allPredicate(new Predicate[] { null, null });
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAllPredicateEx4() {
        AllPredicate.allPredicate((Collection<Predicate<Object>>) null);
    }

    @Test public void testAllPredicateEx5() {
        AllPredicate.allPredicate(Collections.<Predicate<Object>>emptyList());
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAllPredicateEx6() {
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(null);
        coll.add(null);
        AllPredicate.allPredicate(coll);
    }

    // orPredicate
    //------------------------------------------------------------------

    @Test public void testOrPredicate() {
        assertEquals(true, PredicateUtils.orPredicate(TruePredicate.truePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.orPredicate(TruePredicate.truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.orPredicate(FalsePredicate.falsePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.orPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testOrPredicateEx() {
        PredicateUtils.orPredicate(null, null);
    }

    // anyPredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test public void testAnyPredicate() {
        assertFalse(PredicateUtils.anyPredicate(new Predicate[] {}), null);

        assertEquals(true, PredicateUtils.anyPredicate(new Predicate[] {
                TruePredicate.truePredicate(), TruePredicate.truePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.anyPredicate(new Predicate[] {
                TruePredicate.truePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.anyPredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.anyPredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()}).evaluate(null));
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(true, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(true, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(true, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertEquals(false, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertFalse(PredicateUtils.anyPredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertTrue(PredicateUtils.anyPredicate(coll), null);
        coll.clear();
        assertFalse(PredicateUtils.anyPredicate(coll), null);
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAnyPredicateEx1() {
        PredicateUtils.anyPredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class) 
    public void testAnyPredicateEx2() {
        PredicateUtils.anyPredicate(new Predicate[] {null});
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class) 
    public void testAnyPredicateEx3() {
        PredicateUtils.anyPredicate(new Predicate[] {null, null});
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAnyPredicateEx4() {
        PredicateUtils.anyPredicate((Collection<Predicate<Object>>) null);
    }

    @Test public void testAnyPredicateEx5() {
        PredicateUtils.anyPredicate(Collections.<Predicate<Object>>emptyList());
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testAnyPredicateEx6() {
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(null);
        coll.add(null);
        PredicateUtils.anyPredicate(coll);
    }

    // eitherPredicate
    //------------------------------------------------------------------

    @Test public void testEitherPredicate() {
        assertEquals(false, PredicateUtils.eitherPredicate(TruePredicate.truePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.eitherPredicate(TruePredicate.truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.eitherPredicate(FalsePredicate.falsePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.eitherPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testEitherPredicateEx() {
        PredicateUtils.eitherPredicate(null, null);
    }

    // onePredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test public void testOnePredicate() {
        assertFalse(PredicateUtils.onePredicate((Predicate<Object>[]) new Predicate[] {}), null);
        assertEquals(false, PredicateUtils.onePredicate(new Predicate[] {
            TruePredicate.truePredicate(), TruePredicate.truePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.onePredicate(new Predicate[] {
                TruePredicate.truePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.onePredicate(new Predicate[] {
                TruePredicate.truePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.onePredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), TruePredicate.truePredicate(), FalsePredicate.falsePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.onePredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.onePredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()}).evaluate(null));
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(true, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertEquals(false, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertFalse(PredicateUtils.onePredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertTrue(PredicateUtils.onePredicate(coll), null);
        coll.clear();
        assertFalse(PredicateUtils.onePredicate(coll), null);
    }

    @Test(expected=IllegalArgumentException.class) 
    public void testOnePredicateEx1() {
        PredicateUtils.onePredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class)
    public void testOnePredicateEx2() {
        PredicateUtils.onePredicate(new Predicate[] {null});
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class)
    public void testOnePredicateEx3() {
        PredicateUtils.onePredicate(new Predicate[] {null, null});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOnePredicateEx4() {
        PredicateUtils.onePredicate((Collection<Predicate<Object>>) null);
    }

    @SuppressWarnings("unchecked")
    @Test public void testOnePredicateEx5() {
        PredicateUtils.onePredicate(Collections.EMPTY_LIST);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOnePredicateEx6() {
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(null);
        coll.add(null);
        PredicateUtils.onePredicate(coll);
    }

    // neitherPredicate
    //------------------------------------------------------------------

    @Test public void testNeitherPredicate() {
        assertEquals(false, PredicateUtils.neitherPredicate(TruePredicate.truePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.neitherPredicate(TruePredicate.truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.neitherPredicate(FalsePredicate.falsePredicate(), TruePredicate.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.neitherPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNeitherPredicateEx() {
        PredicateUtils.neitherPredicate(null, null);
    }

    // nonePredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test public void testNonePredicate() {
        assertTrue(PredicateUtils.nonePredicate(new Predicate[] {}), null);
        assertEquals(false, PredicateUtils.nonePredicate(new Predicate[] {
                TruePredicate.truePredicate(), TruePredicate.truePredicate(), TruePredicate.truePredicate() }).evaluate(null));
        assertEquals(false, PredicateUtils.nonePredicate(new Predicate[] {
                TruePredicate.truePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate() }).evaluate(null));
        assertEquals(false, PredicateUtils.nonePredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), TruePredicate.truePredicate() }).evaluate(null));
        assertEquals(true, PredicateUtils.nonePredicate(new Predicate[] {
                FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate() }).evaluate(null));
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertEquals(false, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertEquals(true, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertTrue(PredicateUtils.nonePredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertFalse(PredicateUtils.nonePredicate(coll), null);
        coll.clear();
        assertTrue(PredicateUtils.nonePredicate(coll), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonePredicateEx1() {
        PredicateUtils.nonePredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class)
    public void testNonePredicateEx2() {
        PredicateUtils.nonePredicate(new Predicate[] {null});
    }

    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class)
    public void testNonePredicateEx3() {
        PredicateUtils.nonePredicate(new Predicate[] {null, null});
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonePredicateEx4() {
        PredicateUtils.nonePredicate((Collection<Predicate<Object>>) null);
    }

    @Test public void testNonePredicateEx5() {
        PredicateUtils.nonePredicate(Collections.<Predicate<Object>>emptyList());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNonePredicateEx6() {
        Collection<Predicate<Object>> coll = new ArrayList<Predicate<Object>>();
        coll.add(null);
        coll.add(null);
        PredicateUtils.nonePredicate(coll);
    }

    // instanceofPredicate
    //------------------------------------------------------------------

    @Test public void testInstanceOfPredicate() {
        assertNotNull(PredicateUtils.instanceofPredicate(String.class));
        assertEquals(false, PredicateUtils.instanceofPredicate(String.class).evaluate(null));
        assertEquals(false, PredicateUtils.instanceofPredicate(String.class).evaluate(cObject));
        assertEquals(true, PredicateUtils.instanceofPredicate(String.class).evaluate(cString));
        assertEquals(false, PredicateUtils.instanceofPredicate(String.class).evaluate(cInteger));
    }

    // uniquePredicate
    //------------------------------------------------------------------

    @Test public void testUniquePredicate() {
        Predicate<Object> p = PredicateUtils.uniquePredicate();
        assertEquals(true, p.evaluate(new Object()));
        assertEquals(true, p.evaluate(new Object()));
        assertEquals(true, p.evaluate(new Object()));
        assertEquals(true, p.evaluate(cString));
        assertEquals(false, p.evaluate(cString));
        assertEquals(false, p.evaluate(cString));
    }

    // asPredicate(Transformer)
    //------------------------------------------------------------------

    @Test public void testAsPredicateTransformer() {
        assertEquals(false, PredicateUtils.asPredicate(TransformerUtils.<Boolean>nopTransformer()).evaluate(false));
        assertEquals(true, PredicateUtils.asPredicate(TransformerUtils.<Boolean>nopTransformer()).evaluate(true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAsPredicateTransformerEx1() {
        PredicateUtils.asPredicate(null);
    }

    @Test(expected=FunctorException.class)
    public void testAsPredicateTransformerEx2() {
        PredicateUtils.asPredicate(TransformerUtils.<Boolean>nopTransformer()).evaluate(null);
    }

    // invokerPredicate
    //------------------------------------------------------------------

    @Test public void testInvokerPredicate() {
        List<Object> list = new ArrayList<Object>();
        assertEquals(true, PredicateUtils.invokerPredicate("isEmpty").evaluate(list));
        list.add(new Object());
        assertEquals(false, PredicateUtils.invokerPredicate("isEmpty").evaluate(list));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvokerPredicateEx1() {
        PredicateUtils.invokerPredicate(null);
    }

    @Test(expected=FunctorException.class)
    public void testInvokerPredicateEx2() {
        PredicateUtils.invokerPredicate("isEmpty").evaluate(null);
    }

    @Test(expected=FunctorException.class)
    public void testInvokerPredicateEx3() {
        PredicateUtils.invokerPredicate("noSuchMethod").evaluate(new Object());
    }

    // invokerPredicate2
    //------------------------------------------------------------------

    @Test public void testInvokerPredicate2() {
        List<String> list = new ArrayList<String>();
        assertEquals(false, PredicateUtils.invokerPredicate(
            "contains", new Class[] {Object.class}, new Object[] {cString}).evaluate(list));
        list.add(cString);
        assertEquals(true, PredicateUtils.invokerPredicate(
            "contains", new Class[] {Object.class}, new Object[] {cString}).evaluate(list));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvokerPredicate2Ex1() {
        PredicateUtils.invokerPredicate(null, null, null);
    }

    @Test(expected=FunctorException.class)
    public void testInvokerPredicate2Ex2() {
        PredicateUtils.invokerPredicate("contains", new Class[] {Object.class}, new Object[] {cString}).evaluate(null);
    }

    @Test(expected=FunctorException.class)
    public void testInvokerPredicate2Ex3() {
        PredicateUtils.invokerPredicate(
                "noSuchMethod", new Class[] {Object.class}, new Object[] {cString}).evaluate(new Object());
    }

    // nullIsException
    //------------------------------------------------------------------

    @Test(expected=FunctorException.class)
    public void testNullIsExceptionPredicate() {
        assertEquals(true, PredicateUtils.nullIsExceptionPredicate(TruePredicate.truePredicate()).evaluate(new Object()));
        PredicateUtils.nullIsExceptionPredicate(TruePredicate.truePredicate()).evaluate(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullIsExceptionPredicateEx1() {
        PredicateUtils.nullIsExceptionPredicate(null);
    }

    // nullIsTrue
    //------------------------------------------------------------------

    @Test public void testNullIsTruePredicate() {
        assertEquals(true, PredicateUtils.nullIsTruePredicate(TruePredicate.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.nullIsTruePredicate(TruePredicate.truePredicate()).evaluate(new Object()));
        assertEquals(false, PredicateUtils.nullIsTruePredicate(FalsePredicate.falsePredicate()).evaluate(new Object()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullIsTruePredicateEx1() {
        PredicateUtils.nullIsTruePredicate(null);
    }

    // nullIsFalse
    //------------------------------------------------------------------

    @Test public void testNullIsFalsePredicate() {
        assertEquals(false, PredicateUtils.nullIsFalsePredicate(TruePredicate.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.nullIsFalsePredicate(TruePredicate.truePredicate()).evaluate(new Object()));
        assertEquals(false, PredicateUtils.nullIsFalsePredicate(FalsePredicate.falsePredicate()).evaluate(new Object()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullIsFalsePredicateEx1() {
        PredicateUtils.nullIsFalsePredicate(null);
    }

    // transformed
    //------------------------------------------------------------------

    @Test public void testTransformedPredicate() {
        assertEquals(true, PredicateUtils.transformedPredicate(
                TransformerUtils.nopTransformer(),
                TruePredicate.truePredicate()).evaluate(new Object()));

        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(Boolean.TRUE, "Hello");
        Transformer<Object, Object> t = TransformerUtils.mapTransformer(map);
        Predicate<Object> p = EqualPredicate.<Object>equalPredicate("Hello");
        assertEquals(false, PredicateUtils.transformedPredicate(t, p).evaluate(null));
        assertEquals(true, PredicateUtils.transformedPredicate(t, p).evaluate(Boolean.TRUE));
        try {
            PredicateUtils.transformedPredicate(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

    // misc tests
    //------------------------------------------------------------------

    /**
     * Test that all Predicate singletones hold singleton pattern in
     * serialization/deserialization process.
     */
    @Test public void testSingletonPatternInSerialization() {
        final Object[] singletones = new Object[] {
                ExceptionPredicate.INSTANCE,
                FalsePredicate.INSTANCE,
                NotNullPredicate.INSTANCE,
                NullPredicate.INSTANCE,
                TruePredicate.INSTANCE
        };

        for (final Object original : singletones) {
            TestUtils.assertSameAfterSerialization(
                    "Singletone patern broken for " + original.getClass(),
                    original
            );
        }
    }

    @Override
    protected Predicate<?> generatePredicate() {
        return truePredicate();  //Just return something to satisfy super class.
    }

}
