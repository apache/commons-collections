/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestPredicatedBag.java,v 1.4 2003/10/02 22:35:31 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.decorators;

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.HashBag;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.AbstractTestBag;

/**
 * Extension of {@link TestBag} for exercising the {@link PredicatedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/10/02 22:35:31 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedBag extends AbstractTestBag {
    
    public TestPredicatedBag(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPredicatedBag.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedBag.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //--------------------------------------------------------------------------

    protected Predicate stringPredicate() {
        return new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
    }   
    
    protected Predicate truePredicate = PredicateUtils.truePredicate();
    
    protected Bag decorateBag(HashBag bag, Predicate predicate) {
        return PredicatedBag.decorate(bag, predicate);
    }

    protected Bag makeBag() {
        return decorateBag(new HashBag(), truePredicate);
    }
    
    protected Bag makeTestBag() {
        return decorateBag(new HashBag(), stringPredicate());
    }
    
    //--------------------------------------------------------------------------

    public void testlegalAddRemove() {
        Bag bag = makeTestBag();
        assertEquals(0, bag.size());
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "1"};
        for (int i = 0; i < els.length; i++) {
            bag.add(els[i]);
            assertEquals(i + 1, bag.size());
            assertEquals(true, bag.contains(els[i]));
        }
        Set set = ((PredicatedBag) bag).uniqueSet();
        assertTrue("Unique set contains the first element",set.contains(els[0]));
        assertEquals(true, bag.remove(els[0])); 
        set = ((PredicatedBag) bag).uniqueSet();
        assertTrue("Unique set now does not contain the first element",
            !set.contains(els[0])); 
    }
 
    public void testIllegalAdd() {
        Bag bag = makeTestBag();
        Integer i = new Integer(3);
        try {
            bag.add(i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !bag.contains(i));   
    }

    public void testIllegalDecorate() {
        HashBag elements = new HashBag();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            Bag bag = decorateBag(elements, stringPredicate());
            fail("Bag contains an element that should fail the predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            Bag bag = decorateBag(new HashBag(), null);
            fail("Expectiing IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }              
    }
}
