/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/AbstractTestSortedBidiMap.java,v 1.1 2003/10/31 01:26:25 scolebourne Exp $
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.pairs.DefaultMapEntry;

/**
 * Abstract test class for {@link BidiMap} methods and contracts.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/10/31 01:26:25 $
 * 
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public abstract class AbstractTestSortedBidiMap extends AbstractTestBidiMap {

    protected List sortedKeys = new ArrayList();
    protected List sortedValues = new ArrayList();
    protected SortedSet sortedNewValues = new TreeSet();

    public AbstractTestSortedBidiMap(String testName) {
        super(testName);
        sortedKeys.addAll(Arrays.asList(getSampleKeys()));
        Collections.sort(sortedKeys);
        sortedKeys = Collections.unmodifiableList(sortedKeys);
        
        Map map = new TreeMap();
        for (int i = 0; i < getSampleKeys().length; i++) {
            map.put(getSampleKeys()[i], getSampleValues()[i]);
        }
        sortedValues.addAll(map.values());
        sortedValues = Collections.unmodifiableList(sortedValues);
        
        sortedNewValues.addAll(Arrays.asList(getNewSampleValues()));
    }

    public AbstractTestSortedBidiMap() {
        super();
        sortedKeys.addAll(Arrays.asList(getSampleValues()));
        Collections.sort(sortedKeys);
        sortedKeys = Collections.unmodifiableList(sortedKeys);
        
        Map map = new TreeMap();
        for (int i = 0; i < getSampleKeys().length; i++) {
            map.put(getSampleValues()[i], getSampleKeys()[i]);
        }
        sortedValues.addAll(map.values());
        sortedValues = Collections.unmodifiableList(sortedValues);
        
        sortedNewValues.addAll(Arrays.asList(getNewSampleValues()));
    }

    //-----------------------------------------------------------------------
    protected boolean isAllowNullKey() {
        return false;
    }
    protected boolean isAllowNullValue() {
        return false;
    }
    protected Map makeConfirmedMap() {
        return new TreeMap();
    }

    //-----------------------------------------------------------------------    
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
    public void testBidiHeadMapContains() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        Object first = it.next();
        Object toKey = it.next();
        Object second = it.next();
        Object firstValue = sm.get(first);
        Object secondValue = sm.get(second);
        
        SortedMap head = sm.headMap(toKey);
        assertEquals(1, head.size());
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, head.containsKey(first));
        assertEquals(true, sm.containsValue(firstValue));
        assertEquals(true, head.containsValue(firstValue));
        assertEquals(true, sm.containsKey(second));
        assertEquals(false, head.containsKey(second));
        assertEquals(true, sm.containsValue(secondValue));
        assertEquals(false, head.containsValue(secondValue));
    }
                
    //-----------------------------------------------------------------------
    public void testBidiRemoveByHeadMap() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        Object first = it.next();
        Object second = it.next();
        Object toKey = it.next();
        
        SortedMap head = sm.headMap(toKey);
        assertEquals(2, head.size());
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, head.containsKey(first));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, head.containsKey(second));
        
        Object firstValue = head.remove(first);
        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, head.containsKey(first));
        assertEquals(false, head.containsValue(firstValue));
        assertEquals(1, head.size());
        
        Object secondValue = head.remove(second);
        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, head.containsKey(second));
        assertEquals(false, head.containsValue(secondValue));
        assertEquals(0, head.size());
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByHeadMapEntrySet() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        it.next();
        it.next();
        Object fromKey = it.next();
        Object first = it.next();
        Object second = it.next();
        Object toKey = it.next();
        
        SortedMap head = sm.headMap(toKey);
        Set set = head.entrySet();
        Iterator it2 = set.iterator();
        Object fromEntry = it2.next();
        Map.Entry firstEntry = new DefaultMapEntry((Map.Entry) it2.next());
        Map.Entry secondEntry = new DefaultMapEntry((Map.Entry) it2.next());
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, head.containsKey(first));
        assertEquals(true, set.contains(firstEntry));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, head.containsKey(second));
        assertEquals(true, set.contains(secondEntry));
        
        set.remove(firstEntry);
        assertEquals(false, sm.containsKey(firstEntry.getKey()));
        assertEquals(false, sm.containsValue(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertEquals(false, head.containsKey(firstEntry.getKey()));
        assertEquals(false, head.containsValue(firstEntry.getValue()));
        assertEquals(false, set.contains(firstEntry));
        
        set.remove(secondEntry);
        assertEquals(false, sm.containsKey(secondEntry.getKey()));
        assertEquals(false, sm.containsValue(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertEquals(false, head.containsKey(secondEntry.getKey()));
        assertEquals(false, head.containsValue(secondEntry.getValue()));
        assertEquals(false, set.contains(secondEntry));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByTailMap() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        it.next();
        it.next();
        Object fromKey = it.next();
        Object first = it.next();
        Object second = it.next();
        
        SortedMap tail = sm.tailMap(fromKey);
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, tail.containsKey(first));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, tail.containsKey(second));
        
        Object firstValue = tail.remove(first);
        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, tail.containsKey(first));
        assertEquals(false, tail.containsValue(firstValue));
        
        Object secondValue = tail.remove(second);
        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, tail.containsKey(second));
        assertEquals(false, tail.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByTailMapEntrySet() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        it.next();
        it.next();
        Object fromKey = it.next();
        Object first = it.next();
        Object second = it.next();
        Object toKey = it.next();
        
        SortedMap tail = sm.tailMap(fromKey);
        Set set = tail.entrySet();
        Iterator it2 = set.iterator();
        Object fromEntry = it2.next();
        Map.Entry firstEntry = new DefaultMapEntry((Map.Entry) it2.next());
        Map.Entry secondEntry = new DefaultMapEntry((Map.Entry) it2.next());
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, tail.containsKey(first));
        assertEquals(true, set.contains(firstEntry));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, tail.containsKey(second));
        assertEquals(true, set.contains(secondEntry));
        
        set.remove(firstEntry);
        assertEquals(false, sm.containsKey(firstEntry.getKey()));
        assertEquals(false, sm.containsValue(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertEquals(false, tail.containsKey(firstEntry.getKey()));
        assertEquals(false, tail.containsValue(firstEntry.getValue()));
        assertEquals(false, set.contains(firstEntry));
        
        set.remove(secondEntry);
        assertEquals(false, sm.containsKey(secondEntry.getKey()));
        assertEquals(false, sm.containsValue(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertEquals(false, tail.containsKey(secondEntry.getKey()));
        assertEquals(false, tail.containsValue(secondEntry.getValue()));
        assertEquals(false, set.contains(secondEntry));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveBySubMap() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        it.next();
        it.next();
        Object fromKey = it.next();
        Object first = it.next();
        Object second = it.next();
        Object toKey = it.next();
        
        SortedMap sub = sm.subMap(fromKey, toKey);
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        
        Object firstValue = sub.remove(first);
        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));
        
        Object secondValue = sub.remove(second);
        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveBySubMapEntrySet() {
        // extra test as other tests get complex
        SortedBidiMap sm = (SortedBidiMap) makeFullMap();
        Iterator it = sm.keySet().iterator();
        it.next();
        it.next();
        Object fromKey = it.next();
        Object first = it.next();
        Object second = it.next();
        Object toKey = it.next();
        
        SortedMap sub = sm.subMap(fromKey, toKey);
        Set set = sub.entrySet();
        assertEquals(3, set.size());
        Iterator it2 = set.iterator();
        Object fromEntry = it2.next();
        Map.Entry firstEntry = new DefaultMapEntry((Map.Entry) it2.next());
        Map.Entry secondEntry = new DefaultMapEntry((Map.Entry) it2.next());
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, set.contains(firstEntry));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        assertEquals(true, set.contains(secondEntry));
        
        set.remove(firstEntry);
        assertEquals(false, sm.containsKey(firstEntry.getKey()));
        assertEquals(false, sm.containsValue(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertEquals(false, sub.containsKey(firstEntry.getKey()));
        assertEquals(false, sub.containsValue(firstEntry.getValue()));
        assertEquals(false, set.contains(firstEntry));
        
        set.remove(secondEntry);
        assertEquals(false, sm.containsKey(secondEntry.getKey()));
        assertEquals(false, sm.containsValue(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertEquals(false, sub.containsKey(secondEntry.getKey()));
        assertEquals(false, sub.containsValue(secondEntry.getValue()));
        assertEquals(false, set.contains(secondEntry));
    }

    //-----------------------------------------------------------------------    
    public BulkTest bulkTestHeadMap() {
        return new AbstractTestSortedMap.TestHeadMap(this);
    }

    public BulkTest bulkTestTailMap() {
        return new AbstractTestSortedMap.TestTailMap(this);
    }

    public BulkTest bulkTestSubMap() {
        return new AbstractTestSortedMap.TestSubMap(this);
    }

}
