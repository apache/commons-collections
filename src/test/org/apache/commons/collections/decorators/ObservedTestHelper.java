/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/ObservedTestHelper.java,v 1.2 2003/08/31 17:28:42 scolebourne Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.event.ModificationEventType;
import org.apache.commons.collections.event.ModificationListener;
import org.apache.commons.collections.event.StandardModificationEvent;
import org.apache.commons.collections.event.StandardModificationHandler;
import org.apache.commons.collections.event.StandardModificationListener;

/**
 * Helper for testing
 * {@link ObservedCollection} implementations.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:28:42 $
 * 
 * @author Stephen Colebourne
 */
public class ObservedTestHelper {
    
    public static Integer SIX = new Integer(6);
    public static Integer SEVEN = new Integer(7);
    public static Integer EIGHT = new Integer(8);
    public static List SIX_SEVEN_LIST = new ArrayList();
    static {
        SIX_SEVEN_LIST.add(SIX);
        SIX_SEVEN_LIST.add(SEVEN);
    }
    
    public static class Listener implements StandardModificationListener {
        public StandardModificationEvent preEvent = null;
        public StandardModificationEvent postEvent = null;
        
        public void modificationOccurring(StandardModificationEvent event) {
            this.preEvent = event;
        }

        public void modificationOccurred(StandardModificationEvent event) {
            this.postEvent = event;
        }
    }
    
    public static final Listener LISTENER = new Listener();
    public static final Listener LISTENER2 = new Listener();
    
    public ObservedTestHelper() {
        super();
    }

    //-----------------------------------------------------------------------
    public static void doTestFactoryPlain(ObservedCollection coll) {
        Assert.assertEquals(StandardModificationHandler.class, coll.getHandler().getClass());
        Assert.assertEquals(0, coll.getModificationListeners().length);
    }
    
    public static void doTestFactoryWithListener(ObservedCollection coll) {
        Assert.assertEquals(StandardModificationHandler.class, coll.getHandler().getClass());
        Assert.assertEquals(1, coll.getModificationListeners().length);
        Assert.assertSame(LISTENER, coll.getModificationListeners()[0]);
    }
    
    public static void doTestFactoryPostEvents(ObservedCollection coll) {
        Assert.assertEquals(StandardModificationHandler.class, coll.getHandler().getClass());
        Assert.assertEquals(1, coll.getModificationListeners().length);
        Assert.assertSame(LISTENER, coll.getModificationListeners()[0]);
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        coll.add(SIX);
        Assert.assertTrue(LISTENER.preEvent == null);
        Assert.assertTrue(LISTENER.postEvent != null);
    }
    
    //-----------------------------------------------------------------------
    public static void doTestAddRemoveGetListeners(ObservedCollection coll) {
        Assert.assertEquals(0, coll.getModificationListeners().length);
        coll.addModificationListener(LISTENER);
        Assert.assertEquals(1, coll.getModificationListeners().length);
        Assert.assertSame(LISTENER, coll.getModificationListeners()[0]);
        
        coll.addModificationListener(LISTENER2);
        Assert.assertEquals(2, coll.getModificationListeners().length);
        Assert.assertSame(LISTENER, coll.getModificationListeners()[0]);
        Assert.assertSame(LISTENER2, coll.getModificationListeners()[1]);
        
        coll.removeModificationListener(LISTENER);
        Assert.assertEquals(1, coll.getModificationListeners().length);
        Assert.assertSame(LISTENER2, coll.getModificationListeners()[0]);
        
        coll.removeModificationListener(LISTENER);  // check no error if not present
        Assert.assertEquals(1, coll.getModificationListeners().length);
        Assert.assertSame(LISTENER2, coll.getModificationListeners()[0]);
        
        coll.removeModificationListener(LISTENER2);
        Assert.assertEquals(0, coll.getModificationListeners().length);
        
        try {
            coll.addModificationListener(new ModificationListener() {});
            Assert.fail();
        } catch (ClassCastException ex) {
        }
    }
    
    //-----------------------------------------------------------------------
    public static void doTestAdd(ObservedCollection coll) {
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(0, coll.size());
        coll.add(SIX);
        Assert.assertEquals(1, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SIX, LISTENER.preEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SIX, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(0, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(0, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SIX, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeCollection().size());
        Assert.assertSame(SIX, LISTENER.postEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(0, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(1, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(1, coll.size());
        coll.add(SEVEN);
        Assert.assertEquals(2, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SEVEN, LISTENER.preEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SEVEN, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(1, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(1, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SEVEN, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeCollection().size());
        Assert.assertSame(SEVEN, LISTENER.postEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(1, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.add(SIX_SEVEN_LIST);
        Assert.assertEquals(3, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.preEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeCollection().size());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(3, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
    }

    //-----------------------------------------------------------------------
    public static void doTestAddIndexed(ObservedList coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.add(1, EIGHT);
        Assert.assertEquals(3, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD_INDEXED, LISTENER.preEvent.getType());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(EIGHT, LISTENER.preEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(EIGHT, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD_INDEXED, LISTENER.postEvent.getType());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(EIGHT, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeCollection().size());
        Assert.assertSame(EIGHT, LISTENER.postEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(3, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
    }

    //-----------------------------------------------------------------------
    public static void doTestAddAll(ObservedCollection coll) {
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(0, coll.size());
        coll.addAll(SIX_SEVEN_LIST);
        Assert.assertEquals(2, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD_ALL, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.preEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.preEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(0, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(0, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD_ALL, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.preEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(0, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(2, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
    }

    //-----------------------------------------------------------------------
    public static void doTestAddAllIndexed(ObservedList coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.addAll(1, SIX_SEVEN_LIST);
        Assert.assertEquals(4, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD_ALL_INDEXED, LISTENER.preEvent.getType());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.preEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.ADD_ALL_INDEXED, LISTENER.postEvent.getType());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(4, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(2, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
    }

    //-----------------------------------------------------------------------
    public static void doTestClear(ObservedCollection coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.clear();
        Assert.assertEquals(0, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.CLEAR, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(null, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(0, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.CLEAR, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(null, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(0, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(0, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(-2, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(0, coll.size());
        coll.clear();  // already done this
        Assert.assertEquals(0, coll.size());
        Assert.assertTrue(LISTENER.preEvent != null);
        Assert.assertTrue(LISTENER.postEvent == null);
    }

    //-----------------------------------------------------------------------
    public static void doTestRemove(ObservedCollection coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.remove(SEVEN);
        Assert.assertEquals(1, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SEVEN, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SEVEN, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SEVEN, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SEVEN, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(1, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(-1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(1, coll.size());
        coll.remove(SEVEN);  // already removed
        Assert.assertEquals(1, coll.size());
        Assert.assertTrue(LISTENER.preEvent != null);
        Assert.assertTrue(LISTENER.postEvent == null);
    }

    //-----------------------------------------------------------------------
    public static void doTestRemoveIndexed(ObservedList coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.remove(0);
        Assert.assertEquals(1, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE_INDEXED, LISTENER.preEvent.getType());
        Assert.assertEquals(0, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(null, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(0, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE_INDEXED, LISTENER.postEvent.getType());
        Assert.assertEquals(0, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(null, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(0, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(SIX, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(1, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(-1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
    }

    //-----------------------------------------------------------------------
    public static void doTestRemoveAll(ObservedCollection coll) {
        coll.add(EIGHT);
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(3, coll.size());
        coll.removeAll(SIX_SEVEN_LIST);
        Assert.assertEquals(1, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE_ALL, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(3, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(3, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE_ALL, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(3, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(1, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(-2, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(1, coll.size());
        coll.removeAll(SIX_SEVEN_LIST);  // already done this
        Assert.assertEquals(1, coll.size());
        Assert.assertTrue(LISTENER.preEvent != null);
        Assert.assertTrue(LISTENER.postEvent == null);
    }

    //-----------------------------------------------------------------------
    public static void doTestRetainAll(ObservedCollection coll) {
        coll.add(EIGHT);
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(3, coll.size());
        coll.retainAll(SIX_SEVEN_LIST);
        Assert.assertEquals(2, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.RETAIN_ALL, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(3, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(3, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.RETAIN_ALL, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeObject());
        Assert.assertSame(SIX_SEVEN_LIST, LISTENER.postEvent.getChangeCollection());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(3, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(-1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.retainAll(SIX_SEVEN_LIST);  // already done this
        Assert.assertEquals(2, coll.size());
        Assert.assertTrue(LISTENER.preEvent != null);
        Assert.assertTrue(LISTENER.postEvent == null);
    }

    //-----------------------------------------------------------------------
    public static void doTestIteratorRemove(ObservedCollection coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        Iterator it = coll.iterator();
        it.next();
        it.next();
        it.remove();
        Assert.assertEquals(1, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE, LISTENER.preEvent.getType());
        Assert.assertEquals(-1, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(SEVEN, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SEVEN, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.REMOVE, LISTENER.postEvent.getType());
        Assert.assertEquals(-1, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(SEVEN, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(SEVEN, LISTENER.preEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(Boolean.TRUE, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(1, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(-1, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(true, LISTENER.postEvent.isSizeChanged());
        
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(1, coll.size());
        coll.remove(SEVEN);  // already removed
        Assert.assertEquals(1, coll.size());
        Assert.assertTrue(LISTENER.preEvent != null);
        Assert.assertTrue(LISTENER.postEvent == null);
    }

    //-----------------------------------------------------------------------
    public static void doTestSetIndexed(ObservedList coll) {
        coll.addAll(SIX_SEVEN_LIST);
        LISTENER.preEvent = null;
        LISTENER.postEvent = null;
        Assert.assertEquals(2, coll.size());
        coll.set(0, EIGHT);
        Assert.assertEquals(2, coll.size());
        // pre
        Assert.assertSame(coll, LISTENER.preEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.preEvent.getHandler());
        Assert.assertEquals(ModificationEventType.SET_INDEXED, LISTENER.preEvent.getType());
        Assert.assertEquals(0, LISTENER.preEvent.getChangeIndex());
        Assert.assertSame(EIGHT, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(EIGHT, LISTENER.postEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeRepeat());
        Assert.assertSame(null, LISTENER.preEvent.getResult());
        Assert.assertEquals(2, LISTENER.preEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.preEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.preEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.preEvent.isSizeChanged());
        // post
        Assert.assertSame(coll, LISTENER.postEvent.getSourceCollection());
        Assert.assertSame(coll.getHandler(), LISTENER.postEvent.getHandler());
        Assert.assertEquals(ModificationEventType.SET_INDEXED, LISTENER.postEvent.getType());
        Assert.assertEquals(0, LISTENER.postEvent.getChangeIndex());
        Assert.assertSame(EIGHT, LISTENER.postEvent.getChangeObject());
        Assert.assertEquals(1, LISTENER.preEvent.getChangeCollection().size());
        Assert.assertSame(EIGHT, LISTENER.postEvent.getChangeCollection().iterator().next());
        Assert.assertEquals(1, LISTENER.postEvent.getChangeRepeat());
        Assert.assertSame(SIX, LISTENER.postEvent.getResult());
        Assert.assertEquals(2, LISTENER.postEvent.getPreSize());
        Assert.assertEquals(2, LISTENER.postEvent.getPostSize());
        Assert.assertEquals(0, LISTENER.postEvent.getSizeChange());
        Assert.assertEquals(false, LISTENER.postEvent.isSizeChanged());
    }

}
