/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/bidimap/AbstractTestOrderedBidiMap.java,v 1.1 2003/11/16 20:35:46 scolebourne Exp $
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
package org.apache.commons.collections.bidimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.iterators.MapIterator;
import org.apache.commons.collections.iterators.OrderedMapIterator;

/**
 * Abstract test class for {@link OrderedBidiMap} methods and contracts.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/11/16 20:35:46 $
 * 
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public abstract class AbstractTestOrderedBidiMap extends AbstractTestBidiMap {

    public AbstractTestOrderedBidiMap(String testName) {
        super(testName);
    }

    public AbstractTestOrderedBidiMap() {
        super();
    }

    //-----------------------------------------------------------------------
    public void testFirstKey() {
        resetEmpty();
        OrderedBidiMap bidi = (OrderedBidiMap) map;
        try {
            bidi.firstKey();
            fail();
        } catch (NoSuchElementException ex) {}
        
        resetFull();
        bidi = (OrderedBidiMap) map;
        Object confirmedFirst = confirmed.keySet().iterator().next();
        assertEquals(confirmedFirst, bidi.firstKey());
    }
    
    public void testLastKey() {
        resetEmpty();
        OrderedBidiMap bidi = (OrderedBidiMap) map;
        try {
            bidi.lastKey();
            fail();
        } catch (NoSuchElementException ex) {}
        
        resetFull();
        bidi = (OrderedBidiMap) map;
        Object confirmedLast = null;
        for (Iterator it = confirmed.keySet().iterator(); it.hasNext();) {
            confirmedLast = it.next();
        }
        assertEquals(confirmedLast, bidi.lastKey());
    }

    //-----------------------------------------------------------------------    
    public void testNextKey() {
        resetEmpty();
        OrderedBidiMap bidi = (OrderedBidiMap) map;
        assertEquals(null, bidi.nextKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, bidi.nextKey(null)); // this is allowed too
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, bidi.nextKey(null));
        }
        
        resetFull();
        bidi = (OrderedBidiMap) map;
        Iterator it = confirmed.keySet().iterator();
        Object confirmedLast = it.next();
        while (it.hasNext()) {
            Object confirmedObject = it.next();
            assertEquals(confirmedObject, bidi.nextKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, bidi.nextKey(confirmedLast));
        
        if (isAllowNullKey() == false) {
            try {
                bidi.nextKey(null);
                fail();
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, bidi.nextKey(null));
        }
    }
    
    public void testPreviousKey() {
        resetEmpty();
        OrderedBidiMap bidi = (OrderedBidiMap) map;
        assertEquals(null, bidi.previousKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, bidi.previousKey(null)); // this is allowed too
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, bidi.previousKey(null));
        }
        
        resetFull();
        bidi = (OrderedBidiMap) map;
        List list = new ArrayList(confirmed.keySet());
        Collections.reverse(list);
        Iterator it = list.iterator();
        Object confirmedLast = it.next();
        while (it.hasNext()) {
            Object confirmedObject = it.next();
            assertEquals(confirmedObject, bidi.previousKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, bidi.previousKey(confirmedLast));
        
        if (isAllowNullKey() == false) {
            try {
                bidi.previousKey(null);
                fail();
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, bidi.previousKey(null));
        }
    }
    
    //-----------------------------------------------------------------------
    public void testMapIteratorOrder() {
        resetFull();
        OrderedBidiMap bidi = (OrderedBidiMap) map;
        List ordered = new ArrayList(map.keySet());
        List ordered2 = new ArrayList(map.keySet());
        assertEquals("KeySet iterator is not consistent", ordered, ordered2);
        
        int i = 0;
        for (MapIterator it = bidi.mapIterator(); it.hasNext(); i++) {
            Object key = (Object) it.next();
            assertEquals("Inconsistent order", ordered.get(i), key);
            assertEquals("Incorrect value for key", bidi.get(key), it.getValue());
        }
        i = 0;
        OrderedMapIterator it = bidi.orderedMapIterator();
        for (; it.hasNext(); i++) {
            Object key = (Object) it.next();
            assertEquals("Inconsistent order", ordered.get(i), key);
            assertEquals("Incorrect value for key", bidi.get(key), it.getValue());
            assertEquals(true, it.hasPrevious());
        }
        i--;
        for (; it.hasPrevious(); i--) {
            Object key = (Object) it.previous();
            assertEquals("Inconsistent order", ordered.get(i), key);
            assertEquals("Incorrect value for key", bidi.get(key), it.getValue());
            assertEquals(true, it.hasNext());
        }
    }
    
}
