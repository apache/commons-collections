/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/MapPerformance.java,v 1.3 2003/12/08 22:05:10 scolebourne Exp $
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
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.map.Flat3Map;

/** 
 * <code>TestMapPerformance</code> is designed to perform basic Map performance tests.
 *
 * @author Stephen Colebourne
 */
public class MapPerformance {

    /** The total number of runs for each test */    
    private static final int RUNS = 20000000;
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        testAll();
    }
    
    private static void testAll() {
        Map dummyMap = new DummyMap();
        Map hashMap = new HashMap();
//        hashMap.put("Alpha", "A");
//        hashMap.put("Beta", "B");
//        hashMap.put("Gamma", "C");
//        hashMap.put("Delta", "D");
        Map flatMap = new Flat3Map(hashMap);
        System.out.println(flatMap);
        Map unmodHashMap = Collections.unmodifiableMap(new HashMap(hashMap));
        Map fastHashMap = new FastHashMap(hashMap);
        Map treeMap = new TreeMap(hashMap);
        Map seqMap = new SequencedHashMap(hashMap);
//        Map linkedMap = new LinkedHashMap(hashMap);
//        Map syncMap = Collections.unmodifiableMap(new HashMap(hashMap));
//        Map bucketMap = new StaticBucketMap();
//        bucketMap.putAll(hashMap);
//        Map doubleMap = new DoubleOrderedMap(hashMap);
        
        // dummy is required as the VM seems to hotspot the first call to the
        // test method with the given type
        test(dummyMap,      "         Dummy ");
        test(dummyMap,      "         Dummy ");
        test(dummyMap,      "         Dummy ");
        test(flatMap,       "         Flat3 ");
        test(hashMap,       "       HashMap ");
        
        test(flatMap,       "         Flat3 ");
        test(flatMap,       "         Flat3 ");
        test(flatMap,       "         Flat3 ");
        
        test(hashMap,       "       HashMap ");
        test(hashMap,       "       HashMap ");
        test(hashMap,       "       HashMap ");
        
//        test(treeMap,       "       TreeMap ");
//        test(treeMap,       "       TreeMap ");
//        test(treeMap,       "       TreeMap ");
        
//        test(unmodHashMap,  "Unmod(HashMap) ");
//        test(unmodHashMap,  "Unmod(HashMap) ");
//        test(unmodHashMap,  "Unmod(HashMap) ");
//        
//        test(syncMap,       " Sync(HashMap) ");
//        test(syncMap,       " Sync(HashMap) ");
//        test(syncMap,       " Sync(HashMap) ");
//        
//        test(fastHashMap,   "   FastHashMap ");
//        test(fastHashMap,   "   FastHashMap ");
//        test(fastHashMap,   "   FastHashMap ");
//        
//        test(seqMap,        "    SeqHashMap ");
//        test(seqMap,        "    SeqHashMap ");
//        test(seqMap,        "    SeqHashMap ");
//        
//        test(linkedMap,     " LinkedHashMap ");
//        test(linkedMap,     " LinkedHashMap ");
//        test(linkedMap,     " LinkedHashMap ");
//        
//        test(bucketMap,     "     BucketMap ");
//        test(bucketMap,     "     BucketMap ");
//        test(bucketMap,     "     BucketMap ");
//        
//        test(doubleMap,     "     DoubleMap ");
//        test(doubleMap,     "     DoubleMap ");
//        test(doubleMap,     "     DoubleMap ");
    }

    private static void test(Map map, String name) {
        long start = 0, end = 0;
        int total = 0;
        start = System.currentTimeMillis();
        for (int i = RUNS; i > 0; i--) {
//            if (map.get("Alpha") != null) total++;
//            if (map.get("Beta") != null) total++;
//            if (map.get("Gamma") != null) total++;
            map.put("Alpha", "A");
            map.put("Beta", "B");
            map.put("Beta", "C");
            map.put("Gamma", "D");
//            map.remove("Gamma");
//            map.remove("Beta");
//            map.remove("Alpha");
            map.put("Delta", "E");
            map.clear();
        }
        end = System.currentTimeMillis();
        System.out.println(name + (end - start));
    }

    // ----------------------------------------------------------------------

    private static class DummyMap implements Map {
        public void clear() {
        }
        public boolean containsKey(Object key) {
            return false;
        }
        public boolean containsValue(Object value) {
            return false;
        }
        public Set entrySet() {
            return null;
        }
        public Object get(Object key) {
            return null;
        }
        public boolean isEmpty() {
            return false;
        }
        public Set keySet() {
            return null;
        }
        public Object put(Object key, Object value) {
            return null;
        }
        public void putAll(Map t) {
        }
        public Object remove(Object key) {
            return null;
        }
        public int size() {
            return 0;
        }
        public Collection values() {
            return null;
        }
    }
    
}

