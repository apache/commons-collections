/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/TestUnmodifiableOrderedMapIterator.java,v 1.1 2003/11/20 21:45:35 scolebourne Exp $
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
package org.apache.commons.collections.iterators;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.OrderedMap;

/**
 * Tests the UnmodifiableOrderedMapIterator.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/11/20 21:45:35 $
 * 
 * @author Stephen Colebourne
 */
public class TestUnmodifiableOrderedMapIterator extends AbstractTestOrderedMapIterator {

    public static Test suite() {
        return new TestSuite(TestUnmodifiableOrderedMapIterator.class);
    }

    public TestUnmodifiableOrderedMapIterator(String testName) {
        super(testName);
    }

    public MapIterator makeEmptyMapIterator() {
        return UnmodifiableOrderedMapIterator.decorate(
            ListOrderedMap.decorate(new HashMap()).orderedMapIterator());
    }

    public MapIterator makeFullMapIterator() {
        return UnmodifiableOrderedMapIterator.decorate(
            ((OrderedMap) getMap()).orderedMapIterator());
    }
    
    public Map getMap() {
        Map testMap = ListOrderedMap.decorate(new HashMap());
        testMap.put("A", "a");
        testMap.put("B", "b");
        testMap.put("C", "c");
        return testMap;
    }

    public Map getConfirmedMap() {
        Map testMap = new TreeMap();
        testMap.put("A", "a");
        testMap.put("B", "b");
        testMap.put("C", "c");
        return testMap;
    }

    public boolean supportsRemove() {
        return false;
    }

    public boolean supportsSetValue() {
        return false;
    }
    
    //-----------------------------------------------------------------------
    public void testOrderedMapIterator() {
        assertTrue(makeEmptyOrderedMapIterator() instanceof Unmodifiable);
    }
    
    public void testDecorateFactory() {
        OrderedMapIterator it = makeFullOrderedMapIterator();
        assertSame(it, UnmodifiableOrderedMapIterator.decorate(it));
        
        it = ((OrderedMap) getMap()).orderedMapIterator() ;
        assertTrue(it != UnmodifiableOrderedMapIterator.decorate(it));
        
        try {
            UnmodifiableOrderedMapIterator.decorate(null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

}
