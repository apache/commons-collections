/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestObservedList.java,v 1.1 2003/08/28 18:31:13 scolebourne Exp $
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.TestList;
import org.apache.commons.collections.event.ModificationEventType;
import org.apache.commons.collections.event.StandardModificationHandler;

/**
 * Extension of {@link TestList} for exercising the
 * {@link ObservedList} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/08/28 18:31:13 $
 * 
 * @author Stephen Colebourne
 */
public class TestObservedList extends TestList {
    
    private static Integer SIX = new Integer(6);
    private static Integer SEVEN = new Integer(7);
    private static Integer EIGHT = new Integer(8);
    private static final ObservedTestHelper.Listener LISTENER = ObservedTestHelper.LISTENER;
    
    public TestObservedList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestObservedList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestObservedList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //-----------------------------------------------------------------------
    public List makeEmptyList() {
        return ObservedList.decorate(new ArrayList(), LISTENER);
    }

    protected List makeFullList() {
        List set = new ArrayList();
        set.addAll(Arrays.asList(getFullElements()));
        return ObservedList.decorate(set, LISTENER);
    }
    
    //-----------------------------------------------------------------------
    public void testObservedList() {
        ObservedList coll = ObservedList.decorate(new ArrayList());
        ObservedTestHelper.doTestFactoryPlain(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestFactoryWithListener(coll);
        
        coll = ObservedList.decoratePostEventsOnly(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestFactoryPostEvents(coll);
        
        coll = ObservedList.decorate(new ArrayList());
        ObservedTestHelper.doTestAddRemoveGetListeners(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestAdd(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestAddIndexed(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestAddAll(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestAddAllIndexed(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestClear(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestRemove(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestRemoveIndexed(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestRemoveAll(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestRetainAll(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestIteratorRemove(coll);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER);
        ObservedTestHelper.doTestSetIndexed(coll);
    }

    //-----------------------------------------------------------------------    
    public void testFactoryWithHandler() {
        StandardModificationHandler handler = new StandardModificationHandler();
        ObservedList coll = ObservedList.decorate(new ArrayList(), handler);
        
        assertSame(handler, coll.getHandler());
        assertEquals(0, coll.getModificationListeners().length);
    }
    
    public void testFactoryWithMasks() {
        ObservedList coll = ObservedList.decorate(new ArrayList(), LISTENER, -1, 0);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent != null);
        assertTrue(LISTENER.postEvent == null);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER, 0, -1);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent == null);
        assertTrue(LISTENER.postEvent != null);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER, -1, -1);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent != null);
        assertTrue(LISTENER.postEvent != null);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER, 0, 0);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent == null);
        assertTrue(LISTENER.postEvent == null);
        
        coll = ObservedList.decorate(new ArrayList(), LISTENER, ModificationEventType.ADD, ModificationEventType.ADD_ALL);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        assertTrue(LISTENER.preEvent != null);
        assertTrue(LISTENER.postEvent == null);
    }
    
}
