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
package org.apache.commons.collections4;

import static org.apache.commons.collections4.functors.NullPredicate.*;
import static org.apache.commons.collections4.functors.TruePredicate.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.functors.AbstractPredicateTest;
import org.apache.commons.collections4.functors.AllPredicate;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.ExceptionPredicate;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.functors.NullPredicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.Test;

/**
 * Tests the PredicateUtils class.
 *
 * @since 3.0
 */
@SuppressWarnings("boxing")
public class PredicateUtilsTest extends AbstractPredicateTest {

    // exceptionPredicate
    //------------------------------------------------------------------

    @Test
    public void testExceptionPredicate() {
        assertNotNull(PredicateUtils.exceptionPredicate());
        assertSame(PredicateUtils.exceptionPredicate(), PredicateUtils.exceptionPredicate());
        try {
            PredicateUtils.exceptionPredicate().evaluate(null);
        } catch (final FunctorException ex) {
            Exception exception = assertThrows(FunctorException.class, () -> {
                PredicateUtils.exceptionPredicate().evaluate(cString);
            });
            assertTrue(exception.getMessage().contains("ExceptionPredicate invoked"));
        }
    }

    // notNullPredicate
    //------------------------------------------------------------------

    @Test
    public void testIsNotNullPredicate() {
        assertNotNull(PredicateUtils.notNullPredicate());
        assertSame(PredicateUtils.notNullPredicate(), PredicateUtils.notNullPredicate());
        assertFalse(PredicateUtils.notNullPredicate().evaluate(null));
        assertTrue(PredicateUtils.notNullPredicate().evaluate(cObject));
        assertTrue(PredicateUtils.notNullPredicate().evaluate(cString));
        assertTrue(PredicateUtils.notNullPredicate().evaluate(cInteger));
    }

    // identityPredicate
    //------------------------------------------------------------------

    @Test
    public void testIdentityPredicate() {
        assertSame(nullPredicate(), PredicateUtils.identityPredicate(null));
        assertNotNull(PredicateUtils.identityPredicate(6));
        assertFalse(PredicateUtils.identityPredicate(6).evaluate(null));
        assertFalse(PredicateUtils.<Object>identityPredicate(6).evaluate(cObject));
        assertFalse(PredicateUtils.<Object>identityPredicate(6).evaluate(cString));
        assertTrue(PredicateUtils.identityPredicate(6).evaluate(cInteger)); // Cannot use valueOf here
        assertTrue(PredicateUtils.identityPredicate(cInteger).evaluate(cInteger));
    }

    // truePredicate
    //------------------------------------------------------------------

    @Test
    public void testTruePredicate() {
        assertNotNull(TruePredicate.truePredicate());
        assertSame(TruePredicate.truePredicate(), TruePredicate.truePredicate());
        assertTrue(truePredicate().evaluate(null));
        assertTrue(truePredicate().evaluate(cObject));
        assertTrue(truePredicate().evaluate(cString));
        assertTrue(truePredicate().evaluate(cInteger));
    }

    // falsePredicate
    //------------------------------------------------------------------

    @Test
    public void testFalsePredicate() {
        assertNotNull(FalsePredicate.falsePredicate());
        assertSame(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate());
        assertFalse(FalsePredicate.falsePredicate().evaluate(null));
        assertFalse(FalsePredicate.falsePredicate().evaluate(cObject));
        assertFalse(FalsePredicate.falsePredicate().evaluate(cString));
        assertFalse(FalsePredicate.falsePredicate().evaluate(cInteger));
    }

    // notPredicate
    //------------------------------------------------------------------

    @Test
    public void testNotPredicate() {
        assertNotNull(PredicateUtils.notPredicate(TruePredicate.truePredicate()));
        assertFalse(PredicateUtils.notPredicate(truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.notPredicate(truePredicate()).evaluate(cObject));
        assertFalse(PredicateUtils.notPredicate(truePredicate()).evaluate(cString));
        assertFalse(PredicateUtils.notPredicate(truePredicate()).evaluate(cInteger));
    }

    @Test(expected=NullPointerException.class)
    public void testNotPredicateEx() {
        PredicateUtils.notPredicate(null);
    }

    // andPredicate
    //------------------------------------------------------------------

    @Test
    public void testAndPredicate() {
        assertTrue(PredicateUtils.andPredicate(truePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.andPredicate(truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertFalse(PredicateUtils.andPredicate(FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.andPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=NullPointerException.class)
    public void testAndPredicateEx() {
        PredicateUtils.andPredicate(null, null);
    }

    // allPredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    public void testAllPredicate() {
        assertPredicateTrue(AllPredicate.allPredicate(), null);
        assertTrue(AllPredicate.allPredicate(truePredicate(), truePredicate(), truePredicate()).evaluate(null));
        assertFalse(AllPredicate.allPredicate(truePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(AllPredicate.allPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(AllPredicate.allPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertTrue(AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertFalse(AllPredicate.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertPredicateFalse(AllPredicate.allPredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertPredicateTrue(AllPredicate.allPredicate(coll), null);
        coll.clear();
        assertPredicateTrue(AllPredicate.allPredicate(coll), null);
    }

    @Test(expected=NullPointerException.class)
    public void testAllPredicateEx1() {
        AllPredicate.allPredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=NullPointerException.class)
    public void testAllPredicateEx2() {
        AllPredicate.<Object>allPredicate(new Predicate[] { null });
    }

    @Test(expected=NullPointerException.class)
    public void testAllPredicateEx3() {
        AllPredicate.allPredicate(null, null);
    }

    @Test(expected=NullPointerException.class)
    public void testAllPredicateEx4() {
        AllPredicate.allPredicate((Collection<Predicate<Object>>) null);
    }

    @Test
    public void testAllPredicateEx5() {
        AllPredicate.allPredicate(Collections.emptyList());
    }

    @Test(expected=NullPointerException.class)
    public void testAllPredicateEx6() {
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(null);
        coll.add(null);
        AllPredicate.allPredicate(coll);
    }

    // orPredicate
    //------------------------------------------------------------------

    @Test
    public void testOrPredicate() {
        assertTrue(PredicateUtils.orPredicate(truePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.orPredicate(truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertTrue(PredicateUtils.orPredicate(FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.orPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=NullPointerException.class)
    public void testOrPredicateEx() {
        PredicateUtils.orPredicate(null, null);
    }

    // anyPredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    public void testAnyPredicate() {
        assertPredicateFalse(PredicateUtils.anyPredicate(), null);

        assertTrue(PredicateUtils.anyPredicate(truePredicate(), truePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.anyPredicate(truePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.anyPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.anyPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertTrue(PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertTrue(PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertTrue(PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertFalse(PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertPredicateFalse(PredicateUtils.anyPredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertPredicateTrue(PredicateUtils.anyPredicate(coll), null);
        coll.clear();
        assertPredicateFalse(PredicateUtils.anyPredicate(coll), null);
    }

    @Test(expected=NullPointerException.class)
    public void testAnyPredicateEx1() {
        PredicateUtils.anyPredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=NullPointerException.class)
    public void testAnyPredicateEx2() {
        PredicateUtils.anyPredicate(new Predicate[] {null});
    }

    @Test(expected=NullPointerException.class)
    public void testAnyPredicateEx3() {
        PredicateUtils.anyPredicate(null, null);
    }

    @Test(expected=NullPointerException.class)
    public void testAnyPredicateEx4() {
        PredicateUtils.anyPredicate((Collection<Predicate<Object>>) null);
    }

    @Test
    public void testAnyPredicateEx5() {
        PredicateUtils.anyPredicate(Collections.emptyList());
    }

    @Test(expected=NullPointerException.class)
    public void testAnyPredicateEx6() {
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(null);
        coll.add(null);
        PredicateUtils.anyPredicate(coll);
    }

    // eitherPredicate
    //------------------------------------------------------------------

    @Test
    public void testEitherPredicate() {
        assertFalse(PredicateUtils.eitherPredicate(truePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.eitherPredicate(truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertTrue(PredicateUtils.eitherPredicate(FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.eitherPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=NullPointerException.class)
    public void testEitherPredicateEx() {
        PredicateUtils.eitherPredicate(null, null);
    }

    // onePredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    public void testOnePredicate() {
        assertPredicateFalse(PredicateUtils.onePredicate((Predicate<Object>[]) new Predicate[] {}), null);
        assertFalse(PredicateUtils.onePredicate(truePredicate(), truePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.onePredicate(truePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.onePredicate(truePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertTrue(PredicateUtils.onePredicate(FalsePredicate.falsePredicate(), truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertTrue(PredicateUtils.onePredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.onePredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertTrue(PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertFalse(PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertPredicateFalse(PredicateUtils.onePredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertPredicateTrue(PredicateUtils.onePredicate(coll), null);
        coll.clear();
        assertPredicateFalse(PredicateUtils.onePredicate(coll), null);
    }

    @Test(expected=NullPointerException.class)
    public void testOnePredicateEx1() {
        PredicateUtils.onePredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=NullPointerException.class)
    public void testOnePredicateEx2() {
        PredicateUtils.onePredicate(new Predicate[] {null});
    }

    @Test(expected=NullPointerException.class)
    public void testOnePredicateEx3() {
        PredicateUtils.onePredicate(null, null);
    }

    @Test(expected=NullPointerException.class)
    public void testOnePredicateEx4() {
        PredicateUtils.onePredicate((Collection<Predicate<Object>>) null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnePredicateEx5() {
        PredicateUtils.onePredicate(Collections.EMPTY_LIST);
    }

    @Test(expected=NullPointerException.class)
    public void testOnePredicateEx6() {
        PredicateUtils.onePredicate(Arrays.asList(null, null));
    }

    // neitherPredicate
    //------------------------------------------------------------------

    @Test
    public void testNeitherPredicate() {
        assertFalse(PredicateUtils.neitherPredicate(truePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.neitherPredicate(truePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        assertFalse(PredicateUtils.neitherPredicate(FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.neitherPredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
    }

    @Test(expected=NullPointerException.class)
    public void testNeitherPredicateEx() {
        PredicateUtils.neitherPredicate(null, null);
    }

    // nonePredicate
    //------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    public void testNonePredicate() {
        assertPredicateTrue(PredicateUtils.nonePredicate(), null);
        assertFalse(PredicateUtils.nonePredicate(truePredicate(), truePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.nonePredicate(truePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertFalse(PredicateUtils.nonePredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.nonePredicate(FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate(), FalsePredicate.falsePredicate()).evaluate(null));
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(TruePredicate.truePredicate());
        assertFalse(PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        coll.add(FalsePredicate.falsePredicate());
        assertTrue(PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(FalsePredicate.falsePredicate());
        assertPredicateTrue(PredicateUtils.nonePredicate(coll), null);
        coll.clear();
        coll.add(TruePredicate.truePredicate());
        assertPredicateFalse(PredicateUtils.nonePredicate(coll), null);
        coll.clear();
        assertPredicateTrue(PredicateUtils.nonePredicate(coll), null);
    }

    @Test(expected=NullPointerException.class)
    public void testNonePredicateEx1() {
        PredicateUtils.nonePredicate((Predicate<Object>[]) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected=NullPointerException.class)
    public void testNonePredicateEx2() {
        PredicateUtils.nonePredicate(new Predicate[] {null});
    }

    @SuppressWarnings("unchecked")
    @Test(expected=NullPointerException.class)
    public void testNonePredicateEx3() {
        PredicateUtils.nonePredicate(null, null);
    }

    @Test(expected=NullPointerException.class)
    public void testNonePredicateEx4() {
        PredicateUtils.nonePredicate((Collection<Predicate<Object>>) null);
    }

    @Test
    public void testNonePredicateEx5() {
        PredicateUtils.nonePredicate(Collections.emptyList());
    }

    @Test(expected=NullPointerException.class)
    public void testNonePredicateEx6() {
        final Collection<Predicate<Object>> coll = new ArrayList<>();
        coll.add(null);
        coll.add(null);
        PredicateUtils.nonePredicate(coll);
    }

    // instanceofPredicate
    //------------------------------------------------------------------

    @Test
    public void testInstanceOfPredicate() {
        assertNotNull(PredicateUtils.instanceofPredicate(String.class));
        assertFalse(PredicateUtils.instanceofPredicate(String.class).evaluate(null));
        assertFalse(PredicateUtils.instanceofPredicate(String.class).evaluate(cObject));
        assertTrue(PredicateUtils.instanceofPredicate(String.class).evaluate(cString));
        assertFalse(PredicateUtils.instanceofPredicate(String.class).evaluate(cInteger));
    }

    // uniquePredicate
    //------------------------------------------------------------------

    @Test
    public void testUniquePredicate() {
        final Predicate<Object> p = PredicateUtils.uniquePredicate();
        assertTrue(p.evaluate(new Object()));
        assertTrue(p.evaluate(new Object()));
        assertTrue(p.evaluate(new Object()));
        assertTrue(p.evaluate(cString));
        assertFalse(p.evaluate(cString));
        assertFalse(p.evaluate(cString));
    }

    // asPredicate(Transformer)
    //------------------------------------------------------------------

    @Test
    public void testAsPredicateTransformer() {
        assertFalse(PredicateUtils.asPredicate(TransformerUtils.nopTransformer()).evaluate(false));
        assertTrue(PredicateUtils.asPredicate(TransformerUtils.nopTransformer()).evaluate(true));
    }

    @Test(expected=NullPointerException.class)
    public void testAsPredicateTransformerEx1() {
        PredicateUtils.asPredicate(null);
    }

    @Test(expected=FunctorException.class)
    public void testAsPredicateTransformerEx2() {
        PredicateUtils.asPredicate(TransformerUtils.nopTransformer()).evaluate(null);
    }

    // invokerPredicate
    //------------------------------------------------------------------

    @Test
    public void testInvokerPredicate() {
        final List<Object> list = new ArrayList<>();
        assertTrue(PredicateUtils.invokerPredicate("isEmpty").evaluate(list));
        list.add(new Object());
        assertFalse(PredicateUtils.invokerPredicate("isEmpty").evaluate(list));
    }

    @Test(expected=NullPointerException.class)
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

    @Test
    public void testInvokerPredicate2() {
        final List<String> list = new ArrayList<>();
        assertFalse(PredicateUtils.invokerPredicate(
                "contains", new Class[]{Object.class}, new Object[]{cString}).evaluate(list));
        list.add(cString);
        assertTrue(PredicateUtils.invokerPredicate(
                "contains", new Class[]{Object.class}, new Object[]{cString}).evaluate(list));
    }

    @Test(expected=NullPointerException.class)
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
        assertTrue(PredicateUtils.nullIsExceptionPredicate(truePredicate()).evaluate(new Object()));
        PredicateUtils.nullIsExceptionPredicate(TruePredicate.truePredicate()).evaluate(null);
    }

    @Test(expected=NullPointerException.class)
    public void testNullIsExceptionPredicateEx1() {
        PredicateUtils.nullIsExceptionPredicate(null);
    }

    // nullIsTrue
    //------------------------------------------------------------------

    @Test
    public void testNullIsTruePredicate() {
        assertTrue(PredicateUtils.nullIsTruePredicate(truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.nullIsTruePredicate(truePredicate()).evaluate(new Object()));
        assertFalse(PredicateUtils.nullIsTruePredicate(FalsePredicate.falsePredicate()).evaluate(new Object()));
    }

    @Test(expected=NullPointerException.class)
    public void testNullIsTruePredicateEx1() {
        PredicateUtils.nullIsTruePredicate(null);
    }

    // nullIsFalse
    //------------------------------------------------------------------

    @Test
    public void testNullIsFalsePredicate() {
        assertFalse(PredicateUtils.nullIsFalsePredicate(truePredicate()).evaluate(null));
        assertTrue(PredicateUtils.nullIsFalsePredicate(truePredicate()).evaluate(new Object()));
        assertFalse(PredicateUtils.nullIsFalsePredicate(FalsePredicate.falsePredicate()).evaluate(new Object()));
    }

    @Test(expected=NullPointerException.class)
    public void testNullIsFalsePredicateEx1() {
        PredicateUtils.nullIsFalsePredicate(null);
    }

    // transformed
    //------------------------------------------------------------------

    @Test
    public void testTransformedPredicate() {
        assertTrue(PredicateUtils.transformedPredicate(
                TransformerUtils.nopTransformer(),
                truePredicate()).evaluate(new Object()));

        final Map<Object, Object> map = new HashMap<>();
        map.put(Boolean.TRUE, "Hello");
        final Transformer<Object, Object> t = TransformerUtils.mapTransformer(map);
        final Predicate<Object> p = EqualPredicate.<Object>equalPredicate("Hello");
        assertFalse(PredicateUtils.transformedPredicate(t, p).evaluate(null));
        assertTrue(PredicateUtils.transformedPredicate(t, p).evaluate(Boolean.TRUE));
        Exception exception = assertThrows(NullPointerException.class, () -> {
            PredicateUtils.transformedPredicate(null, null);
        });
        assertTrue(exception.getMessage().contains("transformer"));
    }

    // misc tests
    //------------------------------------------------------------------

    /**
     * Test that all Predicate singletones hold singleton pattern in
     * serialization/deserialization process.
     */
    @Test
    public void testSingletonPatternInSerialization() {
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
