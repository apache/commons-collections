/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/AbstractTestSortedMap.java,v 1.2 2003/11/18 22:37:17 scolebourne Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.BulkTest;

/**
 * Abstract test class for {@link java.util.SortedMap} methods and contracts.
 *
 * @version $Revision: 1.2 $ $Date: 2003/11/18 22:37:17 $
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractTestSortedMap extends AbstractTestMap {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test name
     */
    public AbstractTestSortedMap(String testName) {
        super(testName);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Can't sort null keys.
     * 
     * @return false
     */
    public boolean isAllowNullKey() {
        return false;
    }

    /**
     * SortedMap uses TreeMap as its known comparison.
     * 
     * @return a map that is known to be valid
     */
    public Map makeConfirmedMap() {
        return new TreeMap();
    }

    //-----------------------------------------------------------------------
    public void testComparator() {
        SortedMap sm = (SortedMap) makeFullMap();
        // no tests I can think of
    }
    
    public void testFirstKey() {
        SortedMap sm = (SortedMap) makeFullMap();
        assertSame(sm.keySet().iterator().next(), sm.firstKey());
    }
    
    public void testLastKey() {
        SortedMap sm = (SortedMap) makeFullMap();
        Object obj = null;
        for (Iterator it = sm.keySet().iterator(); it.hasNext();) {
            obj = (Object) it.next();
        }
        assertSame(obj, sm.lastKey());
    }
    
    //-----------------------------------------------------------------------    
    public BulkTest bulkTestHeadMap() {
        return new TestHeadMap(this);
    }

    public BulkTest bulkTestTailMap() {
        return new TestTailMap(this);
    }

    public BulkTest bulkTestSubMap() {
        return new TestSubMap(this);
    }

    public static abstract class TestViewMap extends AbstractTestSortedMap {
        protected final AbstractTestMap main;
        protected final List subSortedKeys = new ArrayList();
        protected final List subSortedValues = new ArrayList();
        protected final List subSortedNewValues = new ArrayList();
        
        public TestViewMap(String name, AbstractTestMap main) {
            super(name);
            this.main = main;
        }
        public void resetEmpty() {
            // needed to init verify correctly
            main.resetEmpty();
            super.resetEmpty();
        }
        public void resetFull() {
            // needed to init verify correctly
            main.resetFull();
            super.resetFull();
        }
        public void verify() {
            // cross verify changes on view with changes on main map
            super.verify();
            main.verify();
        }
        public BulkTest bulkTestHeadMap() {
            return null;  // block infinite recursion
        }
        public BulkTest bulkTestTailMap() {
            return null;  // block infinite recursion
        }
        public BulkTest bulkTestSubMap() {
            return null;  // block infinite recursion
        }
        
        public Object[] getSampleKeys() {
            return subSortedKeys.toArray();
        }
        public Object[] getSampleValues() {
            return subSortedValues.toArray();
        }
        public Object[] getNewSampleValues() {
            return subSortedNewValues.toArray();
        }
        
        public String getCompatibilityVersion() {
            return main.getCompatibilityVersion();
        }
        public boolean isAllowNullKey() {
            return main.isAllowNullKey();
        }
        public boolean isAllowNullValue() {
            return main.isAllowNullValue();
        }
        public boolean isPutAddSupported() {
            return main.isPutAddSupported();
        }
        public boolean isPutChangeSupported() {
            return main.isPutChangeSupported();
        }
        public boolean isRemoveSupported() {
            return main.isRemoveSupported();
        }
        public boolean supportsEmptyCollections() {
            return false;
        }
        public boolean supportsFullCollections() {
            return false;
        }
    }
    
    public static class TestHeadMap extends TestViewMap {
        static final int SUBSIZE = 6;
        final Object toKey;
        
        public TestHeadMap(AbstractTestMap main) {
            super("SortedMap.HeadMap", main);
            SortedMap sm = (SortedMap) main.makeFullMap();
            for (Iterator it = sm.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                this.subSortedKeys.add(entry.getKey());
                this.subSortedValues.add(entry.getValue());
            }
            this.toKey = this.subSortedKeys.get(SUBSIZE);
            this.subSortedKeys.subList(SUBSIZE, this.subSortedKeys.size()).clear();
            this.subSortedValues.subList(SUBSIZE, this.subSortedValues.size()).clear();
            this.subSortedNewValues.addAll(Arrays.asList(main.getNewSampleValues()).subList(0, SUBSIZE));
        }
        public Map makeEmptyMap() {
            // done this way so toKey is correctly set in the returned map
            return ((SortedMap) main.makeEmptyMap()).headMap(toKey);
        }
        public Map makeFullMap() {
            return ((SortedMap) main.makeFullMap()).headMap(toKey);
        }
        public void testHeadMapOutOfRange() {
            if (isPutAddSupported() == false) return;
            resetEmpty();
            try {
                ((SortedMap) map).put(toKey, subSortedValues.get(0));
                fail();
            } catch (IllegalArgumentException ex) {}
            verify();
        }
    }
    
    public static class TestTailMap extends TestViewMap {
        static final int SUBSIZE = 6;
        final Object fromKey;
        final Object invalidKey;
        
        public TestTailMap(AbstractTestMap main) {
            super("SortedMap.TailMap", main);
            SortedMap sm = (SortedMap) main.makeFullMap();
            for (Iterator it = sm.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                this.subSortedKeys.add(entry.getKey());
                this.subSortedValues.add(entry.getValue());
            }
            this.fromKey = this.subSortedKeys.get(this.subSortedKeys.size() - SUBSIZE);
            this.invalidKey = this.subSortedKeys.get(this.subSortedKeys.size() - SUBSIZE - 1);
            this.subSortedKeys.subList(0, this.subSortedKeys.size() - SUBSIZE).clear();
            this.subSortedValues.subList(0, this.subSortedValues.size() - SUBSIZE).clear();
            this.subSortedNewValues.addAll(Arrays.asList(main.getNewSampleValues()).subList(0, SUBSIZE));
        }
        public Map makeEmptyMap() {
            // done this way so toKey is correctly set in the returned map
            return ((SortedMap) main.makeEmptyMap()).tailMap(fromKey);
        }
        public Map makeFullMap() {
            return ((SortedMap) main.makeFullMap()).tailMap(fromKey);
        }
        public void testTailMapOutOfRange() {
            if (isPutAddSupported() == false) return;
            resetEmpty();
            try {
                ((SortedMap) map).put(invalidKey, subSortedValues.get(0));
                fail();
            } catch (IllegalArgumentException ex) {}
            verify();
        }
    }
    
    public static class TestSubMap extends TestViewMap {
        static final int SUBSIZE = 3;
        final Object fromKey;
        final Object toKey;
        
        public TestSubMap(AbstractTestMap main) {
            super("SortedMap.SubMap", main);
            SortedMap sm = (SortedMap) main.makeFullMap();
            for (Iterator it = sm.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                this.subSortedKeys.add(entry.getKey());
                this.subSortedValues.add(entry.getValue());
            }
            this.fromKey = this.subSortedKeys.get(SUBSIZE);
            this.toKey = this.subSortedKeys.get(this.subSortedKeys.size() - SUBSIZE);
            
            this.subSortedKeys.subList(0, SUBSIZE).clear();
            this.subSortedKeys.subList(this.subSortedKeys.size() - SUBSIZE, this.subSortedKeys.size()).clear();
            
            this.subSortedValues.subList(0, SUBSIZE).clear();
            this.subSortedValues.subList(this.subSortedValues.size() - SUBSIZE, this.subSortedValues.size()).clear();
            
            this.subSortedNewValues.addAll(Arrays.asList(main.getNewSampleValues()).subList(
                SUBSIZE, this.main.getNewSampleValues().length - SUBSIZE));
        }
        
        public Map makeEmptyMap() {
            // done this way so toKey is correctly set in the returned map
            return ((SortedMap) main.makeEmptyMap()).subMap(fromKey, toKey);
        }
        public Map makeFullMap() {
            return ((SortedMap) main.makeFullMap()).subMap(fromKey, toKey);
        }
        public void testSubMapOutOfRange() {
            if (isPutAddSupported() == false) return;
            resetEmpty();
            try {
                ((SortedMap) map).put(toKey, subSortedValues.get(0));
                fail();
            } catch (IllegalArgumentException ex) {}
            verify();
        }
    }
    
}
