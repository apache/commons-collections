/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestBagUtils.java,v 1.5 2003/12/24 17:31:41 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import junit.framework.Test;

import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.bag.PredicatedBag;
import org.apache.commons.collections.bag.PredicatedSortedBag;
import org.apache.commons.collections.bag.SynchronizedBag;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.collections.bag.TransformedBag;
import org.apache.commons.collections.bag.TransformedSortedBag;
import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.collections.bag.UnmodifiableBag;
import org.apache.commons.collections.bag.UnmodifiableSortedBag;

/**
 * Tests for BagUtils factory methods.
 * 
 * @version $Revision: 1.5 $ $Date: 2003/12/24 17:31:41 $
 *
 * @author Phil Steitz
 */
public class TestBagUtils extends BulkTest {

    public TestBagUtils(String name) {
        super(name);
    }


    public static Test suite() {
        return BulkTest.makeSuite(TestBagUtils.class);
    }
    
    //----------------------------------------------------------------------

    protected Class stringClass = this.getName().getClass();
    protected Predicate truePredicate = PredicateUtils.truePredicate();
    protected Transformer nopTransformer = TransformerUtils.nopTransformer();
    
    //----------------------------------------------------------------------
    
    public void testSynchronizedBag() {
        Bag bag = BagUtils.synchronizedBag(new HashBag());
        assertTrue("Returned object should be a SynchronizedBag.",
            bag instanceof SynchronizedBag);
        try {
            bag = BagUtils.synchronizedBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testUnmodifiableBag() {
        Bag bag = BagUtils.unmodifiableBag(new HashBag());
        assertTrue("Returned object should be an UnmodifiableBag.",
            bag instanceof UnmodifiableBag);
        try {
            bag = BagUtils.unmodifiableBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testPredicatedBag() {
        Bag bag = BagUtils.predicatedBag(new HashBag(), truePredicate);
        assertTrue("Returned object should be a PredicatedBag.",
            bag instanceof PredicatedBag);
        try {
            bag = BagUtils.predicatedBag(null,truePredicate);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            bag = BagUtils.predicatedBag(new HashBag(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testTypedBag() {
        Bag bag = BagUtils.typedBag(new HashBag(), stringClass);      
        assertTrue("Returned object should be a TypedBag.",
            bag instanceof PredicatedBag);
        try {
            bag = BagUtils.typedBag(null, stringClass);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            bag = BagUtils.typedBag(new HashBag(), null);
            fail("Expecting IllegalArgumentException for null type.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
     public void testTransformedBag() {
        Bag bag = BagUtils.transformedBag(new HashBag(), nopTransformer);      
        assertTrue("Returned object should be an TransformedBag.",
            bag instanceof TransformedBag);
        try {
            bag = BagUtils.transformedBag(null, nopTransformer);      
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            bag = BagUtils.transformedBag(new HashBag(), null);  
            fail("Expecting IllegalArgumentException for null transformer.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
     
    public void testSynchronizedSortedBag() {
        Bag bag = BagUtils.synchronizedSortedBag(new TreeBag());
        assertTrue("Returned object should be a SynchronizedSortedBag.",
            bag instanceof SynchronizedSortedBag);
        try {
            bag = BagUtils.synchronizedSortedBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testUnmodifiableSortedBag() {
        Bag bag = BagUtils.unmodifiableSortedBag(new TreeBag());
        assertTrue("Returned object should be an UnmodifiableSortedBag.",
            bag instanceof UnmodifiableSortedBag);
        try {
            bag = BagUtils.unmodifiableSortedBag(null);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testPredicatedSortedBag() {
        Bag bag = BagUtils.predicatedSortedBag(new TreeBag(), truePredicate);
        assertTrue("Returned object should be a PredicatedSortedBag.",
            bag instanceof PredicatedSortedBag);
        try {
            bag = BagUtils.predicatedSortedBag(null, truePredicate);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            bag = BagUtils.predicatedSortedBag(new TreeBag(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testTypedSortedBag() {
        Bag bag = BagUtils.typedSortedBag(new TreeBag(), stringClass);      
        assertTrue("Returned object should be a TypedSortedBag.",
            bag instanceof PredicatedSortedBag);
        try {
            bag = BagUtils.typedSortedBag(null, stringClass);
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            bag = BagUtils.typedSortedBag(new TreeBag(), null);
            fail("Expecting IllegalArgumentException for null type.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    
    public void testTransformedSortedBag() {
        Bag bag = BagUtils.transformedSortedBag(new TreeBag(), nopTransformer);      
        assertTrue("Returned object should be an TransformedSortedBag",
            bag instanceof TransformedSortedBag);
        try {
            bag = BagUtils.transformedSortedBag(null, nopTransformer);      
            fail("Expecting IllegalArgumentException for null bag.");
        } catch (IllegalArgumentException ex) {
            // expected
        } 
        try {
            bag = BagUtils.transformedSortedBag(new TreeBag(), null);  
            fail("Expecting IllegalArgumentException for null transformer.");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
}


