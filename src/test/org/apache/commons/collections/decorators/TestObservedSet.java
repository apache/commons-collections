/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestObservedSet.java,v 1.2 2003/08/31 17:28:42 scolebourne Exp $
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.TestSet;
import org.apache.commons.collections.event.ModificationEventType;
import org.apache.commons.collections.event.StandardModificationHandler;

/**
 * Extension of {@link TestSet} for exercising the
 * {@link ObservedSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:28:42 $
 * 
 * @author Stephen Colebourne
 */
public class TestObservedSet extends TestSet {
    
    private static Integer SIX = new Integer(6);
    private static Integer SEVEN = new Integer(7);
    private static Integer EIGHT = new Integer(8);
    private static final ObservedTestHelper.Listener LISTENER = ObservedTestHelper.LISTENER;
    
    public TestObservedSet(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestObservedSet.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestObservedSet.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //-----------------------------------------------------------------------
    public Set makeEmptySet() {
        return ObservedSet.decorate(new HashSet(), LISTENER);
    }

    protected Set makeFullSet() {
        Set set = new HashSet();
        set.addAll(Arrays.asList(getFullElements()));
        return ObservedSet.decorate(set, LISTENER);
    }
    
    //-----------------------------------------------------------------------
    public void testObservedSet() {
        ObservedSet coll = ObservedSet.decorate(new HashSet());
        ObservedTestHelper.doTestFactoryPlain(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestFactoryWithListener(coll);
        
        coll = ObservedSet.decoratePostEventsOnly(new HashSet(), LISTENER);
        ObservedTestHelper.doTestFactoryPostEvents(coll);
        
        coll = ObservedSet.decorate(new HashSet());
        ObservedTestHelper.doTestAddRemoveGetListeners(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestAdd(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestAddAll(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestClear(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestRemove(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestRemoveAll(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestRetainAll(coll);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER);
        ObservedTestHelper.doTestIteratorRemove(coll);
    }

    //-----------------------------------------------------------------------    
    public void testFactoryWithHandler() {
        StandardModificationHandler handler = new StandardModificationHandler();
        ObservedSet coll = ObservedSet.decorate(new HashSet(), handler);
        
        assertSame(handler, coll.getHandler());
        assertEquals(0, coll.getModificationListeners().length);
    }
    
    public void testFactoryWithMasks() {
        ObservedSet coll = ObservedSet.decorate(new HashSet(), LISTENER, -1, 0);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent != null);
        assertTrue(LISTENER.postEvent == null);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER, 0, -1);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent == null);
        assertTrue(LISTENER.postEvent != null);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER, -1, -1);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent != null);
        assertTrue(LISTENER.postEvent != null);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER, 0, 0);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent == null);
        assertTrue(LISTENER.postEvent == null);
        
        coll = ObservedSet.decorate(new HashSet(), LISTENER, ModificationEventType.ADD, ModificationEventType.ADD_ALL);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent != null);
        assertTrue(LISTENER.postEvent == null);
    }
    
}
