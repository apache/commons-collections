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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.functors.BasicPredicateTestBase;
import org.apache.commons.collections.functors.NullPredicate;
import org.junit.Test;

/**
 * Tests the org.apache.commons.collections.PredicateUtils class.
 * 
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Matt Benson
 */
public class TestPredicateUtils extends BasicPredicateTestBase {
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

    // equalPredicate
    //------------------------------------------------------------------

    @Test public void testEqualPredicate() {
        assertSame(nullPredicate(), PredicateUtils.equalPredicate(null));
        assertNotNull(PredicateUtils.equalPredicate(new Integer(6)));
        assertEquals(false, PredicateUtils.equalPredicate(new Integer(6)).evaluate(null));
        assertEquals(false, PredicateUtils.equalPredicate(new Integer(6)).evaluate(cObject));
        assertEquals(false, PredicateUtils.equalPredicate(new Integer(6)).evaluate(cString));
        assertEquals(true, PredicateUtils.equalPredicate(new Integer(6)).evaluate(cInteger));
    }

    // identityPredicate
    //------------------------------------------------------------------

    @Test public void testIdentityPredicate() {
        assertSame(nullPredicate(), PredicateUtils.identityPredicate(null));
        assertNotNull(PredicateUtils.identityPredicate(new Integer(6)));
        assertEquals(false, PredicateUtils.identityPredicate(new Integer(6)).evaluate(null));
        assertEquals(false, PredicateUtils.identityPredicate(new Integer(6)).evaluate(cObject));
        assertEquals(false, PredicateUtils.identityPredicate(new Integer(6)).evaluate(cString));
        assertEquals(false, PredicateUtils.identityPredicate(new Integer(6)).evaluate(cInteger));
        assertEquals(true, PredicateUtils.identityPredicate(cInteger).evaluate(cInteger));
    }

    // truePredicate
    //------------------------------------------------------------------

    @Test public void testTruePredicate() {
        assertNotNull(PredicateUtils.truePredicate());
        assertSame(PredicateUtils.truePredicate(), PredicateUtils.truePredicate());
        assertEquals(true, PredicateUtils.truePredicate().evaluate(null));
        assertEquals(true, PredicateUtils.truePredicate().evaluate(cObject));
        assertEquals(true, PredicateUtils.truePredicate().evaluate(cString));
        assertEquals(true, PredicateUtils.truePredicate().evaluate(cInteger));
    }

    // falsePredicate
    //------------------------------------------------------------------

    @Test public void testFalsePredicate() {
        assertNotNull(PredicateUtils.falsePredicate());
        assertSame(PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate());
        assertEquals(false, PredicateUtils.falsePredicate().evaluate(null));
        assertEquals(false, PredicateUtils.falsePredicate().evaluate(cObject));
        assertEquals(false, PredicateUtils.falsePredicate().evaluate(cString));
        assertEquals(false, PredicateUtils.falsePredicate().evaluate(cInteger));
    }

    // notPredicate
    //------------------------------------------------------------------

    @Test public void testNotPredicate() {
        assertNotNull(PredicateUtils.notPredicate(PredicateUtils.truePredicate()));
        assertEquals(false, PredicateUtils.notPredicate(PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.notPredicate(PredicateUtils.truePredicate()).evaluate(cObject));
        assertEquals(false, PredicateUtils.notPredicate(PredicateUtils.truePredicate()).evaluate(cString));
        assertEquals(false, PredicateUtils.notPredicate(PredicateUtils.truePredicate()).evaluate(cInteger));
    }
    
    @Test public void testNotPredicateEx() {
        try {
            PredicateUtils.notPredicate(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // andPredicate
    //------------------------------------------------------------------

    @Test public void testAndPredicate() {
        assertEquals(true, PredicateUtils.andPredicate(PredicateUtils.truePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.andPredicate(PredicateUtils.truePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.andPredicate(PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.andPredicate(PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
    }

    @Test public void testAndPredicateEx() {
        try {
            PredicateUtils.andPredicate(null, null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // allPredicate
    //------------------------------------------------------------------

    @Test public void testAllPredicate() {
        assertTrue(PredicateUtils.allPredicate(
            new Predicate[] {}).evaluate(null));
        assertEquals(true, PredicateUtils.allPredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.truePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.allPredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.allPredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.allPredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()}).evaluate(null));
        Collection coll = new ArrayList();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(true, PredicateUtils.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        assertEquals(false, PredicateUtils.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        assertFalse(PredicateUtils.allPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        assertTrue(PredicateUtils.allPredicate(coll).evaluate(null));
        coll.clear();
        assertTrue(PredicateUtils.allPredicate(coll).evaluate(null));
    }

    @Test public void testAllPredicateEx1() {
        try {
            PredicateUtils.allPredicate((Predicate[]) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAllPredicateEx2() {
        try {
            PredicateUtils.allPredicate(new Predicate[] {null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAllPredicateEx3() {
        try {
            PredicateUtils.allPredicate(new Predicate[] {null, null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAllPredicateEx4() {
        try {
            PredicateUtils.allPredicate((Collection) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAllPredicateEx5() {
        PredicateUtils.allPredicate(Collections.EMPTY_LIST);
    }
    
    @Test public void testAllPredicateEx6() {
        try {
            Collection coll = new ArrayList();
            coll.add(null);
            coll.add(null);
            PredicateUtils.allPredicate(coll);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // orPredicate
    //------------------------------------------------------------------

    @Test public void testOrPredicate() {
        assertEquals(true, PredicateUtils.orPredicate(PredicateUtils.truePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.orPredicate(PredicateUtils.truePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.orPredicate(PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.orPredicate(PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
    }
    
    @Test public void testOrPredicateEx() {
        try {
            PredicateUtils.orPredicate(null, null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // anyPredicate
    //------------------------------------------------------------------

    @Test public void testAnyPredicate() {
        assertFalse(PredicateUtils.anyPredicate(
            new Predicate[] {}).evaluate(null));
        assertEquals(true, PredicateUtils.anyPredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.truePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.anyPredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.anyPredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.anyPredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()}).evaluate(null));
        Collection coll = new ArrayList();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(true, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(true, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(true, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        assertEquals(false, PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        assertFalse(PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        assertTrue(PredicateUtils.anyPredicate(coll).evaluate(null));
        coll.clear();
        assertFalse(PredicateUtils.anyPredicate(coll).evaluate(null));
    }

    @Test public void testAnyPredicateEx1() {
        try {
            PredicateUtils.anyPredicate((Predicate[]) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAnyPredicateEx2() {
        try {
            PredicateUtils.anyPredicate(new Predicate[] {null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAnyPredicateEx3() {
        try {
            PredicateUtils.anyPredicate(new Predicate[] {null, null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAnyPredicateEx4() {
        try {
            PredicateUtils.anyPredicate((Collection) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAnyPredicateEx5() {
        PredicateUtils.anyPredicate(Collections.EMPTY_LIST);
    }
    
    @Test public void testAnyPredicateEx6() {
        try {
            Collection coll = new ArrayList();
            coll.add(null);
            coll.add(null);
            PredicateUtils.anyPredicate(coll);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // eitherPredicate
    //------------------------------------------------------------------

    @Test public void testEitherPredicate() {
        assertEquals(false, PredicateUtils.eitherPredicate(PredicateUtils.truePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.eitherPredicate(PredicateUtils.truePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.eitherPredicate(PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.eitherPredicate(PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
    }

    @Test public void testEitherPredicateEx() {
        try {
            PredicateUtils.eitherPredicate(null, null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // onePredicate
    //------------------------------------------------------------------

    @Test public void testOnePredicate() {
        assertFalse(PredicateUtils.onePredicate(
            new Predicate[] {}).evaluate(null));
        assertEquals(false, PredicateUtils.onePredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.truePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.onePredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.onePredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.onePredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.truePredicate(), PredicateUtils.falsePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.onePredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.onePredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()}).evaluate(null));
        Collection coll = new ArrayList();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(true, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        assertEquals(false, PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        assertFalse(PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        assertTrue(PredicateUtils.onePredicate(coll).evaluate(null));
        coll.clear();
        assertFalse(PredicateUtils.onePredicate(coll).evaluate(null));
    }

    @Test public void testOnePredicateEx1() {
        try {
            PredicateUtils.onePredicate((Predicate[]) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testOnePredicateEx2() {
        try {
            PredicateUtils.onePredicate(new Predicate[] {null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testOnePredicateEx3() {
        try {
            PredicateUtils.onePredicate(new Predicate[] {null, null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testOnePredicateEx4() {
        try {
            PredicateUtils.onePredicate((Collection) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testOnePredicateEx5() {
        PredicateUtils.onePredicate(Collections.EMPTY_LIST);
    }
    
    @Test public void testOnePredicateEx6() {
        try {
            Collection coll = new ArrayList();
            coll.add(null);
            coll.add(null);
            PredicateUtils.onePredicate(coll);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // neitherPredicate
    //------------------------------------------------------------------

    @Test public void testNeitherPredicate() {
        assertEquals(false, PredicateUtils.neitherPredicate(PredicateUtils.truePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.neitherPredicate(PredicateUtils.truePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
        assertEquals(false, PredicateUtils.neitherPredicate(PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.neitherPredicate(PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()).evaluate(null));
    }

    @Test public void testNeitherPredicateEx() {
        try {
            PredicateUtils.neitherPredicate(null, null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // nonePredicate
    //------------------------------------------------------------------

    @Test public void testNonePredicate() {
        assertTrue(PredicateUtils.nonePredicate(
            new Predicate[] {}).evaluate(null));
        assertEquals(false, PredicateUtils.nonePredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.truePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.nonePredicate(new Predicate[] {
            PredicateUtils.truePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(false, PredicateUtils.nonePredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.truePredicate()}).evaluate(null));
        assertEquals(true, PredicateUtils.nonePredicate(new Predicate[] {
            PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate(), PredicateUtils.falsePredicate()}).evaluate(null));
        Collection coll = new ArrayList();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.truePredicate());
        assertEquals(false, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        coll.add(PredicateUtils.falsePredicate());
        assertEquals(true, PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.falsePredicate());
        assertTrue(PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        coll.add(PredicateUtils.truePredicate());
        assertFalse(PredicateUtils.nonePredicate(coll).evaluate(null));
        coll.clear();
        assertTrue(PredicateUtils.nonePredicate(coll).evaluate(null));
    }

    @Test public void testNonePredicateEx1() {
        try {
            PredicateUtils.nonePredicate((Predicate[]) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testNonePredicateEx2() {
        try {
            PredicateUtils.nonePredicate(new Predicate[] {null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testNonePredicateEx3() {
        try {
            PredicateUtils.nonePredicate(new Predicate[] {null, null});
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testNonePredicateEx4() {
        try {
            PredicateUtils.nonePredicate((Collection) null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testNonePredicateEx5() {
        PredicateUtils.nonePredicate(Collections.EMPTY_LIST);
    }
    
    @Test public void testNonePredicateEx6() {
        try {
            Collection coll = new ArrayList();
            coll.add(null);
            coll.add(null);
            PredicateUtils.nonePredicate(coll);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
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
        Predicate p = PredicateUtils.uniquePredicate();
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
        assertEquals(false, PredicateUtils.asPredicate(TransformerUtils.nopTransformer()).evaluate(Boolean.FALSE));
        assertEquals(true, PredicateUtils.asPredicate(TransformerUtils.nopTransformer()).evaluate(Boolean.TRUE));
    }

    @Test public void testAsPredicateTransformerEx1() {
        try {
            PredicateUtils.asPredicate(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testAsPredicateTransformerEx2() {
        try {
            PredicateUtils.asPredicate(TransformerUtils.nopTransformer()).evaluate(null);
        } catch (FunctorException ex) {
            return;
        }
        fail();
    }
    
    // invokerPredicate
    //------------------------------------------------------------------

    @Test public void testInvokerPredicate() {
        List list = new ArrayList();
        assertEquals(true, PredicateUtils.invokerPredicate("isEmpty").evaluate(list));
        list.add(new Object());
        assertEquals(false, PredicateUtils.invokerPredicate("isEmpty").evaluate(list));
    }

    @Test public void testInvokerPredicateEx1() {
        try {
            PredicateUtils.invokerPredicate(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testInvokerPredicateEx2() {
        try {
            PredicateUtils.invokerPredicate("isEmpty").evaluate(null);
        } catch (FunctorException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testInvokerPredicateEx3() {
        try {
            PredicateUtils.invokerPredicate("noSuchMethod").evaluate(new Object());
        } catch (FunctorException ex) {
            return;
        }
        fail();
    }
    
    // invokerPredicate2
    //------------------------------------------------------------------

    @Test public void testInvokerPredicate2() {
        List list = new ArrayList();
        assertEquals(false, PredicateUtils.invokerPredicate(
            "contains", new Class[] {Object.class}, new Object[] {cString}).evaluate(list));
        list.add(cString);
        assertEquals(true, PredicateUtils.invokerPredicate(
            "contains", new Class[] {Object.class}, new Object[] {cString}).evaluate(list));
    }

    @Test public void testInvokerPredicate2Ex1() {
        try {
            PredicateUtils.invokerPredicate(null, null, null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testInvokerPredicate2Ex2() {
        try {
            PredicateUtils.invokerPredicate("contains", new Class[] {Object.class}, new Object[] {cString}).evaluate(null);
        } catch (FunctorException ex) {
            return;
        }
        fail();
    }
    
    @Test public void testInvokerPredicate2Ex3() {
        try {
            PredicateUtils.invokerPredicate(
                "noSuchMethod", new Class[] {Object.class}, new Object[] {cString}).evaluate(new Object());
        } catch (FunctorException ex) {
            return;
        }
        fail();
    }
    
    // nullIsException
    //------------------------------------------------------------------

    @Test public void testNullIsExceptionPredicate() {
        assertEquals(true, PredicateUtils.nullIsExceptionPredicate(PredicateUtils.truePredicate()).evaluate(new Object()));
        try {
            PredicateUtils.nullIsExceptionPredicate(PredicateUtils.truePredicate()).evaluate(null);
        } catch (FunctorException ex) {
            return;
        }
        fail();
    }

    @Test public void testNullIsExceptionPredicateEx1() {
        try {
            PredicateUtils.nullIsExceptionPredicate(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // nullIsTrue
    //------------------------------------------------------------------

    @Test public void testNullIsTruePredicate() {
        assertEquals(true, PredicateUtils.nullIsTruePredicate(PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.nullIsTruePredicate(PredicateUtils.truePredicate()).evaluate(new Object()));
        assertEquals(false, PredicateUtils.nullIsTruePredicate(PredicateUtils.falsePredicate()).evaluate(new Object()));
    }

    @Test public void testNullIsTruePredicateEx1() {
        try {
            PredicateUtils.nullIsTruePredicate(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // nullIsFalse
    //------------------------------------------------------------------

    @Test public void testNullIsFalsePredicate() {
        assertEquals(false, PredicateUtils.nullIsFalsePredicate(PredicateUtils.truePredicate()).evaluate(null));
        assertEquals(true, PredicateUtils.nullIsFalsePredicate(PredicateUtils.truePredicate()).evaluate(new Object()));
        assertEquals(false, PredicateUtils.nullIsFalsePredicate(PredicateUtils.falsePredicate()).evaluate(new Object()));
    }

    @Test public void testNullIsFalsePredicateEx1() {
        try {
            PredicateUtils.nullIsFalsePredicate(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    // transformed
    //------------------------------------------------------------------

    @Test public void testTransformedPredicate() {
        assertEquals(true, PredicateUtils.transformedPredicate(
                TransformerUtils.nopTransformer(),
                PredicateUtils.truePredicate()).evaluate(new Object()));
                
        Map map = new HashMap();
        map.put(Boolean.TRUE, "Hello");
        Transformer t = TransformerUtils.mapTransformer(map);
        Predicate p = PredicateUtils.equalPredicate("Hello");
        assertEquals(false, PredicateUtils.transformedPredicate(t, p).evaluate(null));
        assertEquals(true, PredicateUtils.transformedPredicate(t, p).evaluate(Boolean.TRUE));
        try {
            PredicateUtils.transformedPredicate(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

}
