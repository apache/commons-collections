/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestClosureUtils.java,v 1.6 2003/11/27 23:57:09 scolebourne Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.collections.functors.FunctorException;
import org.apache.commons.collections.functors.NOPClosure;

/**
 * Tests the org.apache.commons.collections.ClosureUtils class.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.6 $ $Date: 2003/11/27 23:57:09 $
 *
 * @author Stephen Colebourne
 */
public class TestClosureUtils extends junit.framework.TestCase {

    private static final Object cObject = new Object();
    private static final Object cString = "Hello";
    private static final Object cInteger = new Integer(6);

    /**
     * Construct
     */
    public TestClosureUtils(String name) {
        super(name);
    }

    /**
     * Main.
     * @param args
     */    
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Return class as a test suite.
     */
    public static Test suite() {
        return new TestSuite(TestClosureUtils.class);
    }

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
    
    static class MockClosure implements Closure {
        int count = 0;
        
        public void execute(Object object) {
            count++;
        }
    }
    static class MockTransformer implements Transformer {
        int count = 0;
        
        public Object transform(Object object) {
            count++;
            return object;
        }
    }

    // exceptionClosure
    //------------------------------------------------------------------

    public void testExceptionClosure() {
        assertNotNull(ClosureUtils.exceptionClosure());
        assertSame(ClosureUtils.exceptionClosure(), ClosureUtils.exceptionClosure());
        try {
            ClosureUtils.exceptionClosure().execute(null);
        } catch (FunctorException ex) {
            try {
                ClosureUtils.exceptionClosure().execute(cString);
            } catch (FunctorException ex2) {
                return;
            }
        }
        fail();
    }
    
    // nopClosure
    //------------------------------------------------------------------

    public void testNopClosure() {
        StringBuffer buf = new StringBuffer("Hello");
        ClosureUtils.nopClosure().execute(null);
        assertEquals("Hello", buf.toString());
        ClosureUtils.nopClosure().execute("Hello");
        assertEquals("Hello", buf.toString());
    }

    // invokeClosure
    //------------------------------------------------------------------

    public void testInvokeClosure() {
        StringBuffer buf = new StringBuffer("Hello");
        ClosureUtils.invokerClosure("reverse").execute(buf);
        assertEquals("olleH", buf.toString());
        buf = new StringBuffer("Hello");
        ClosureUtils.invokerClosure("setLength", new Class[] {Integer.TYPE}, new Object[] {new Integer(2)}).execute(buf);
        assertEquals("He", buf.toString());
    }

    // forClosure
    //------------------------------------------------------------------

    public void testForClosure() {
        MockClosure cmd = new MockClosure();
        ClosureUtils.forClosure(5, cmd).execute(null);
        assertEquals(5, cmd.count);
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(0, new MockClosure()));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(-1, new MockClosure()));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(1, null));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.forClosure(3, null));
        assertSame(cmd, ClosureUtils.forClosure(1, cmd));
    }

    // whileClosure
    //------------------------------------------------------------------

    public void testWhileClosure() {
        MockClosure cmd = new MockClosure();
        ClosureUtils.whileClosure(PredicateUtils.falsePredicate(), cmd).execute(null);
        assertEquals(0, cmd.count);
        
        cmd = new MockClosure();
        ClosureUtils.whileClosure(PredicateUtils.uniquePredicate(), cmd).execute(null);
        assertEquals(1, cmd.count);
        
        try {
            ClosureUtils.whileClosure(null, ClosureUtils.nopClosure());
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.whileClosure(PredicateUtils.falsePredicate(), null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.whileClosure(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

    // doWhileClosure
    //------------------------------------------------------------------

    public void testDoWhileClosure() {
        MockClosure cmd = new MockClosure();
        ClosureUtils.doWhileClosure(cmd, PredicateUtils.falsePredicate()).execute(null);
        assertEquals(1, cmd.count);
        
        cmd = new MockClosure();
        ClosureUtils.doWhileClosure(cmd, PredicateUtils.uniquePredicate()).execute(null);
        assertEquals(2, cmd.count);
        
        try {
            ClosureUtils.doWhileClosure(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

    // chainedClosure
    //------------------------------------------------------------------

    public void testChainedClosure() {
        MockClosure a = new MockClosure();
        MockClosure b = new MockClosure();
        ClosureUtils.chainedClosure(a, b).execute(null);
        assertEquals(1, a.count);
        assertEquals(1, b.count);
        
        a = new MockClosure();
        b = new MockClosure();
        ClosureUtils.chainedClosure(new Closure[] {a, b, a}).execute(null);
        assertEquals(2, a.count);
        assertEquals(1, b.count);
        
        a = new MockClosure();
        b = new MockClosure();
        Collection coll = new ArrayList();
        coll.add(b);
        coll.add(a);
        coll.add(b);
        ClosureUtils.chainedClosure(coll).execute(null);
        assertEquals(1, a.count);
        assertEquals(2, b.count);
        
        assertSame(NOPClosure.INSTANCE, ClosureUtils.chainedClosure(new Closure[0]));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.chainedClosure(Collections.EMPTY_LIST));
        
        try {
            ClosureUtils.chainedClosure(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.chainedClosure((Closure[]) null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.chainedClosure((Collection) null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.chainedClosure(new Closure[] {null, null});
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            coll = new ArrayList();
            coll.add(null);
            coll.add(null);
            ClosureUtils.chainedClosure(coll);
            fail();
        } catch (IllegalArgumentException ex) {}
    }
    
    // switchClosure
    //------------------------------------------------------------------

    public void testSwitchClosure() {
        MockClosure a = new MockClosure();
        MockClosure b = new MockClosure();
        ClosureUtils.ifClosure(PredicateUtils.truePredicate(), a, b).execute(null);
        assertEquals(1, a.count);
        assertEquals(0, b.count);
        
        a = new MockClosure();
        b = new MockClosure();
        ClosureUtils.ifClosure(PredicateUtils.falsePredicate(), a, b).execute(null);
        assertEquals(0, a.count);
        assertEquals(1, b.count);
        
        a = new MockClosure();
        b = new MockClosure();
        ClosureUtils.switchClosure(
            new Predicate[] {PredicateUtils.equalPredicate("HELLO"), PredicateUtils.equalPredicate("THERE")}, 
            new Closure[] {a, b}).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        
        a = new MockClosure();
        b = new MockClosure();
        ClosureUtils.switchClosure(
            new Predicate[] {PredicateUtils.equalPredicate("HELLO"), PredicateUtils.equalPredicate("THERE")}, 
            new Closure[] {a, b}).execute("HELLO");
        assertEquals(1, a.count);
        assertEquals(0, b.count);
        
        a = new MockClosure();
        b = new MockClosure();
        MockClosure c = new MockClosure();
        ClosureUtils.switchClosure(
            new Predicate[] {PredicateUtils.equalPredicate("HELLO"), PredicateUtils.equalPredicate("THERE")}, 
            new Closure[] {a, b}, c).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        assertEquals(1, c.count);
        
        a = new MockClosure();
        b = new MockClosure();
        Map map = new HashMap();
        map.put(PredicateUtils.equalPredicate("HELLO"), a);
        map.put(PredicateUtils.equalPredicate("THERE"), b);
        ClosureUtils.switchClosure(map).execute(null);
        assertEquals(0, a.count);
        assertEquals(0, b.count);

        a = new MockClosure();
        b = new MockClosure();
        map = new HashMap();
        map.put(PredicateUtils.equalPredicate("HELLO"), a);
        map.put(PredicateUtils.equalPredicate("THERE"), b);
        ClosureUtils.switchClosure(map).execute("THERE");
        assertEquals(0, a.count);
        assertEquals(1, b.count);

        a = new MockClosure();
        b = new MockClosure();
        c = new MockClosure();
        map = new HashMap();
        map.put(PredicateUtils.equalPredicate("HELLO"), a);
        map.put(PredicateUtils.equalPredicate("THERE"), b);
        map.put(null, c);
        ClosureUtils.switchClosure(map).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        assertEquals(1, c.count);
        
        assertSame(NOPClosure.INSTANCE, ClosureUtils.switchClosure(new Predicate[0], new Closure[0]));
        assertSame(NOPClosure.INSTANCE, ClosureUtils.switchClosure(new HashMap()));
        map = new HashMap();
        map.put(null, null);
        assertSame(NOPClosure.INSTANCE, ClosureUtils.switchClosure(map));

        try {
            ClosureUtils.switchClosure(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.switchClosure((Predicate[]) null, (Closure[]) null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.switchClosure((Map) null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.switchClosure(new Predicate[2], new Closure[2]);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ClosureUtils.switchClosure(
                    new Predicate[] {PredicateUtils.truePredicate()},
                    new Closure[] {a,b});
            fail();
        } catch (IllegalArgumentException ex) {}
    }
    
    // switchMapClosure
    //------------------------------------------------------------------

    public void testSwitchMapClosure() {
        MockClosure a = new MockClosure();
        MockClosure b = new MockClosure();
        Map map = new HashMap();
        map.put("HELLO", a);
        map.put("THERE", b);
        ClosureUtils.switchMapClosure(map).execute(null);
        assertEquals(0, a.count);
        assertEquals(0, b.count);

        a = new MockClosure();
        b = new MockClosure();
        map = new HashMap();
        map.put("HELLO", a);
        map.put("THERE", b);
        ClosureUtils.switchMapClosure(map).execute("THERE");
        assertEquals(0, a.count);
        assertEquals(1, b.count);

        a = new MockClosure();
        b = new MockClosure();
        MockClosure c = new MockClosure();
        map = new HashMap();
        map.put("HELLO", a);
        map.put("THERE", b);
        map.put(null, c);
        ClosureUtils.switchMapClosure(map).execute("WELL");
        assertEquals(0, a.count);
        assertEquals(0, b.count);
        assertEquals(1, c.count);

        assertSame(NOPClosure.INSTANCE, ClosureUtils.switchMapClosure(new HashMap()));
        
        try {
            ClosureUtils.switchMapClosure(null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }
    
    // asClosure
    //------------------------------------------------------------------

    public void testTransformerClosure() {
        MockTransformer mock = new MockTransformer();
        Closure closure = ClosureUtils.asClosure(mock);
        closure.execute(null);
        assertEquals(1, mock.count);
        closure.execute(null);
        assertEquals(2, mock.count);
        
        assertSame(ClosureUtils.nopClosure(), ClosureUtils.asClosure(null));
    }
    
}
