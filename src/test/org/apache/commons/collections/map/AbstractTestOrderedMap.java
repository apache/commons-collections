/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/AbstractTestOrderedMap.java,v 1.1 2003/11/20 22:34:49 scolebourne Exp $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.iterators.AbstractTestMapIterator;
import org.apache.commons.collections.iterators.AbstractTestOrderedMapIterator;
import org.apache.commons.collections.iterators.MapIterator;

/**
 * Abstract test class for {@link OrderedMap} methods and contracts.
 *
 * @version $Revision: 1.1 $ $Date: 2003/11/20 22:34:49 $
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractTestOrderedMap extends AbstractTestMap {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test name
     */
    public AbstractTestOrderedMap(String testName) {
        super(testName);
    }
    
    //-----------------------------------------------------------------------
    /**
     * OrderedMap uses TreeMap as its known comparison.
     * 
     * @return a map that is known to be valid
     */
    public Map makeConfirmedMap() {
        return new TreeMap(new NullComparator());
    }
    
    /**
     * The only confirmed collection we have that is ordered is the sorted one.
     * Thus, sort the keys.
     */
    public Object[] getSampleKeys() {
        List list = new ArrayList(Arrays.asList(super.getSampleKeys()));
        Collections.sort(list, new NullComparator());
        return list.toArray();
    }

    //-----------------------------------------------------------------------
    public void testFirstKey() {
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        try {
            ordered.firstKey();
            fail();
        } catch (NoSuchElementException ex) {}
        
        resetFull();
        ordered = (OrderedMap) map;
        Object confirmedFirst = confirmed.keySet().iterator().next();
        assertEquals(confirmedFirst, ordered.firstKey());
    }
    
    public void testLastKey() {
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        try {
            ordered.lastKey();
            fail();
        } catch (NoSuchElementException ex) {}
        
        resetFull();
        ordered = (OrderedMap) map;
        Object confirmedLast = null;
        for (Iterator it = confirmed.keySet().iterator(); it.hasNext();) {
            confirmedLast = it.next();
        }
        assertEquals(confirmedLast, ordered.lastKey());
    }

    //-----------------------------------------------------------------------    
    public void testNextKey() {
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        assertEquals(null, ordered.nextKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, ordered.nextKey(null)); // this is allowed too
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.nextKey(null));
        }
        
        resetFull();
        ordered = (OrderedMap) map;
        Iterator it = confirmed.keySet().iterator();
        Object confirmedLast = it.next();
        while (it.hasNext()) {
            Object confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.nextKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, ordered.nextKey(confirmedLast));
        
        if (isAllowNullKey() == false) {
            try {
                ordered.nextKey(null);
                fail();
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.nextKey(null));
        }
    }
    
    public void testPreviousKey() {
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        assertEquals(null, ordered.previousKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, ordered.previousKey(null)); // this is allowed too
            } catch (NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.previousKey(null));
        }
        
        resetFull();
        ordered = (OrderedMap) map;
        List list = new ArrayList(confirmed.keySet());
        Collections.reverse(list);
        Iterator it = list.iterator();
        Object confirmedLast = it.next();
        while (it.hasNext()) {
            Object confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.previousKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, ordered.previousKey(confirmedLast));
        
        if (isAllowNullKey() == false) {
            try {
                ordered.previousKey(null);
                fail();
            } catch (NullPointerException ex) {}
        } else {
            if (isAllowNullKey() == false) {
                assertEquals(null, ordered.previousKey(null));
            }
        }
    }
    
    //-----------------------------------------------------------------------
    public BulkTest bulkTestMapIterator() {
        return new InnerTestOrderedMapIterator();
    }
    
    // TODO: Test mapIterator() and orderedMapIterator() separately
    public class InnerTestMapIterator extends AbstractTestMapIterator {
        public InnerTestMapIterator() {
            super("InnerTestMapIterator");
        }
        
        public boolean supportsRemove() {
            return AbstractTestOrderedMap.this.isRemoveSupported();
        }

        public boolean supportsSetValue() {
            return AbstractTestOrderedMap.this.isSetValueSupported();
        }

        public MapIterator makeEmptyMapIterator() {
            resetEmpty();
            return ((OrderedMap) AbstractTestOrderedMap.this.map).mapIterator();
        }

        public MapIterator makeFullMapIterator() {
            resetFull();
            return ((OrderedMap) AbstractTestOrderedMap.this.map).mapIterator();
        }
        
        public Map getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestOrderedMap.this.map;
        }
        
        public Map getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestOrderedMap.this.confirmed;
        }
        
        public void verify() {
            super.verify();
            AbstractTestOrderedMap.this.verify();
        }
    }
    
    //-----------------------------------------------------------------------
    public BulkTest bulkTestOrderedMapIterator() {
        return new InnerTestOrderedMapIterator();
    }
    
    // TODO: Test mapIterator() and orderedMapIterator() separately
    public class InnerTestOrderedMapIterator extends AbstractTestOrderedMapIterator {
        public InnerTestOrderedMapIterator() {
            super("InnerTestOrderedMapIterator");
        }
        
        public boolean supportsRemove() {
            return AbstractTestOrderedMap.this.isRemoveSupported();
        }

        public boolean supportsSetValue() {
            return AbstractTestOrderedMap.this.isSetValueSupported();
        }

        public MapIterator makeEmptyMapIterator() {
            resetEmpty();
            return ((OrderedMap) AbstractTestOrderedMap.this.map).orderedMapIterator();
        }

        public MapIterator makeFullMapIterator() {
            resetFull();
            return ((OrderedMap) AbstractTestOrderedMap.this.map).orderedMapIterator();
        }
        
        public Map getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestOrderedMap.this.map;
        }
        
        public Map getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestOrderedMap.this.confirmed;
        }
        
        public void verify() {
            super.verify();
            AbstractTestOrderedMap.this.verify();
        }
    }
    
}
