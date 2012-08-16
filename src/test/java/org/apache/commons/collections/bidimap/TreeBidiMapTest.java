/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.bidimap;

import java.util.TreeMap;

import junit.framework.Test;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.BulkTest;

/**
 * JUnit tests.
 *
 * @version $Id$
 */
public class TreeBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractOrderedBidiMapTest<K, V> {

    public static Test suite() {
        return BulkTest.makeSuite(TreeBidiMapTest.class);
    }

    public TreeBidiMapTest(String testName) {
        super(testName);
    }

    @Override
    public BidiMap<K, V> makeObject() {
        return new TreeBidiMap<K, V>();
    }
    
    @Override
    public TreeMap<K, V> makeConfirmedMap() {
        return new TreeMap<K, V>();
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    @Override
    public String[] ignoredTests() {
        return new String[] {"TreeBidiMapTest.bulkTestInverseMap.bulkTestInverseMap"};
    }
    
    @Override
    public boolean isAllowNullKey() {
        return false;
    }
    
    @Override
    public boolean isAllowNullValue() {
        return false;
    }
    
    @Override
    public boolean isSetValueSupported() {
        return false;
    }
    
    @Override
    public String getCompatibilityVersion() {
        return "3.3";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "/tmp/TreeBidiMap.emptyCollection.version3.3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "/tmp/TreeBidiMap.fullCollection.version3.3.obj");
//    }

}
