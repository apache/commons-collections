/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/AbstractTestIterableMap.java,v 1.2 2003/12/07 01:21:51 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.iterators.AbstractTestMapIterator;

/**
 * Abstract test class for {@link IterableMap} methods and contracts.
 *
 * @version $Revision: 1.2 $ $Date: 2003/12/07 01:21:51 $
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractTestIterableMap extends AbstractTestMap {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test name
     */
    public AbstractTestIterableMap(String testName) {
        super(testName);
    }
    
    //-----------------------------------------------------------------------
    public void testFailFastEntrySet() {
        if (isRemoveSupported() == false) return;
        resetFull();
        Iterator it = map.entrySet().iterator();
        Map.Entry val = (Map.Entry) it.next();
        map.remove(val.getKey());
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
        
        resetFull();
        it = map.entrySet().iterator();
        it.next();
        map.clear();
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
    }
    
    public void testFailFastKeySet() {
        if (isRemoveSupported() == false) return;
        resetFull();
        Iterator it = map.keySet().iterator();
        Object val = it.next();
        map.remove(val);
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
        
        resetFull();
        it = map.keySet().iterator();
        it.next();
        map.clear();
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
    }
    
    public void testFailFastValues() {
        if (isRemoveSupported() == false) return;
        resetFull();
        Iterator it = map.values().iterator();
        it.next();
        map.remove(map.keySet().iterator().next());
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
        
        resetFull();
        it = map.values().iterator();
        it.next();
        map.clear();
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
    }
    
    //-----------------------------------------------------------------------
    public BulkTest bulkTestMapIterator() {
        return new InnerTestMapIterator();
    }
    
    public class InnerTestMapIterator extends AbstractTestMapIterator {
        public InnerTestMapIterator() {
            super("InnerTestMapIterator");
        }
        
        public Object[] addSetValues() {
            return AbstractTestIterableMap.this.getNewSampleValues();
        }
        
        public boolean supportsRemove() {
            return AbstractTestIterableMap.this.isRemoveSupported();
        }
        
        public boolean isGetStructuralModify() {
            return AbstractTestIterableMap.this.isGetStructuralModify();
        }

        public boolean supportsSetValue() {
            return AbstractTestIterableMap.this.isSetValueSupported();
        }

        public MapIterator makeEmptyMapIterator() {
            resetEmpty();
            return ((IterableMap) AbstractTestIterableMap.this.map).mapIterator();
        }

        public MapIterator makeFullMapIterator() {
            resetFull();
            return ((IterableMap) AbstractTestIterableMap.this.map).mapIterator();
        }
        
        public Map getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestIterableMap.this.map;
        }
        
        public Map getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestIterableMap.this.confirmed;
        }
        
        public void verify() {
            super.verify();
            AbstractTestIterableMap.this.verify();
        }
    }
    
//  public void testCreate() throws Exception {
//      resetEmpty();
//      writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/HashedMap.emptyCollection.version3.obj");
//      resetFull();
//      writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/HashedMap.fullCollection.version3.obj");
//  }
}
